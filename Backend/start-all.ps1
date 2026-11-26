# =====================================================================
#  LearnHub - démarre l'architecture sur un PC Windows
#
#  Mode "integration" (par défaut) : 2 Eureka + 2 course-service + 2 marketplace-service
#     eureka-server      : http://localhost:8761  et  http://127.0.0.1:8762
#     course-service     : 8082 et 8092   (Nawres Soua, MySQL learnhub_course_db)
#     marketplace-service: 8085 et 8095   (Selim Hechmi, MySQL db_marketplace)
#     -> le marketplace lit les prix sur course-service via Eureka + OpenFeign
#     api-gateway        : 8080   (point d'accès unique, Swagger unifié, load balancing, circuit breaker)
#     Prérequis : Java 17+, Maven, MySQL démarré sur le port 3306 (Laragon / XAMPP, root sans mot de passe)
#
#  Mode "local" : 2 Eureka + 2 marketplace seulement, base H2 en mémoire, aucun MySQL nécessaire
#
#  Utilisation (depuis le dossier Backend) :
#     powershell -ExecutionPolicy Bypass -File .\start-all.ps1
#     powershell -ExecutionPolicy Bypass -File .\start-all.ps1 -Mode local
#  Arrêt : .\stop-all.ps1        Journaux : dossier Backend\logs
# =====================================================================
param([ValidateSet('integration', 'local')][string]$Mode = 'integration')

$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$logs = Join-Path $root 'logs'
New-Item -ItemType Directory -Force $logs | Out-Null

# Mémoire réduite par JVM pour pouvoir lancer beaucoup de services sur un PC de 8 Go
$jvm = @('-Xms32m', '-Xmx192m', '-XX:MaxMetaspaceSize=192m', '-XX:+UseSerialGC', '-XX:TieredStopAtLevel=1', '-Xss512k')

function Get-Jar($service) {
    $dir = Join-Path $root "$service\target"
    $jar = Get-ChildItem $dir -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Name -notlike '*.original' } | Select-Object -First 1
    if (-not $jar) {
        Write-Host "Compilation de $service..."
        Push-Location (Join-Path $root $service); mvn -q -DskipTests package; Pop-Location
        $jar = Get-ChildItem $dir -Filter '*.jar' | Where-Object { $_.Name -notlike '*.original' } | Select-Object -First 1
    }
    return $jar.FullName
}

function Start-Jar($service, $name, [string[]]$extra) {
    $arguments = $jvm + @('-jar', "`"$(Get-Jar $service)`"") + $extra
    Start-Process java -ArgumentList $arguments -WindowStyle Hidden `
        -RedirectStandardOutput (Join-Path $logs "$name.log") -RedirectStandardError (Join-Path $logs "$name.err.log") | Out-Null
    Write-Host "  -> $name"
}

function Wait-Url($url, $label) {
    for ($i = 0; $i -lt 90; $i++) {
        try { Invoke-WebRequest $url -UseBasicParsing -TimeoutSec 2 | Out-Null; Write-Host "  $label prêt"; return } catch { Start-Sleep 2 }
    }
    Write-Warning "$label ne répond pas (voir le dossier logs)"
}

if ($Mode -eq 'integration' -and -not (Test-NetConnection localhost -Port 3306 -InformationLevel Quiet -WarningAction SilentlyContinue)) {
    Write-Warning 'MySQL ne répond pas sur le port 3306 : démarrez Laragon / XAMPP, ou lancez avec -Mode local.'
    exit 1
}

Write-Host 'Démarrage des serveurs Eureka...'
Start-Jar 'eureka-server' 'eureka-peer1' @('--spring.profiles.active=peer1')
Start-Jar 'eureka-server' 'eureka-peer2' @('--spring.profiles.active=peer2')
Wait-Url 'http://localhost:8761/actuator/health' 'Eureka 8761'
Wait-Url 'http://127.0.0.1:8762/actuator/health' 'Eureka 8762'

if ($Mode -eq 'integration') {
    Write-Host 'Démarrage de course-service (2 instances)...'
    # RabbitMQ n'est pas lancé en local : on ne le compte pas dans /actuator/health
    Start-Jar 'course-service' 'course-8082' @('--management.health.rabbit.enabled=false')
    Start-Jar 'course-service' 'course-8092' @('--server.port=8092', '--management.health.rabbit.enabled=false')
    Wait-Url 'http://localhost:8082/actuator/health' 'Course 8082'
    Wait-Url 'http://localhost:8092/actuator/health' 'Course 8092'
    $profile = 'integration'
} else {
    $profile = 'local'
}

Write-Host "Démarrage de marketplace-service (2 instances, profil $profile)..."
# INSTANCE_WEIGHT : poids annoncé dans Eureka, utilisé par l'algorithme Weighted de la Gateway (75 % / 25 %)
Start-Jar 'marketplace-service' 'marketplace-8085' @("--spring.profiles.active=$profile", '--INSTANCE_WEIGHT=3')
Start-Jar 'marketplace-service' 'marketplace-8095' @("--spring.profiles.active=$profile", '--server.port=8095', '--INSTANCE_WEIGHT=1')
Wait-Url 'http://localhost:8085/actuator/health' 'Marketplace 8085'
Wait-Url 'http://localhost:8095/actuator/health' 'Marketplace 8095'

Write-Host 'Démarrage de l''API Gateway (port 8080)...'
Start-Jar 'api-gateway' 'api-gateway-8080' @()
Wait-Url 'http://localhost:8080/actuator/health' 'API Gateway 8080'

Write-Host ''
Write-Host 'Les instances apparaissent dans Eureka en moins d''une minute :'
Write-Host '  http://localhost:8761   et   http://127.0.0.1:8762'
Write-Host 'Point d''accès unique (Postman, frontend) : http://localhost:8080'
Write-Host 'Swagger unifié                           : http://localhost:8080/swagger-ui.html'
