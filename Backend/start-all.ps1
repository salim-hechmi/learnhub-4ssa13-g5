# =====================================================================
#  LearnHub - démarre les 2 serveurs Eureka et le marketplace (2 instances)
#    eureka-server  : http://localhost:8761  et  http://127.0.0.1:8762
#    marketplace    : http://localhost:8085  et  http://localhost:8095   (profil "local")
#
#  Prérequis : Java 17+ et Maven. Aucune base à installer (H2 en mémoire).
#  Utilisation (depuis le dossier Backend) :
#     powershell -ExecutionPolicy Bypass -File .\start-all.ps1
#  Arrêt : .\stop-all.ps1        Journaux : dossier Backend\logs
#
#  Chaque membre ajoute ici la ligne Start-Jar de son microservice une fois qu'il est client Eureka.
# =====================================================================
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

Write-Host 'Démarrage des serveurs Eureka...'
Start-Jar 'eureka-server' 'eureka-peer1' @('--spring.profiles.active=peer1')
Start-Jar 'eureka-server' 'eureka-peer2' @('--spring.profiles.active=peer2')
Wait-Url 'http://localhost:8761/actuator/health' 'Eureka 8761'
Wait-Url 'http://127.0.0.1:8762/actuator/health' 'Eureka 8762'

Write-Host 'Démarrage de marketplace-service (2 instances)...'
Start-Jar 'marketplace-service' 'marketplace-8085' @('--spring.profiles.active=local')
Start-Jar 'marketplace-service' 'marketplace-8095' @('--spring.profiles.active=local', '--server.port=8095')
Wait-Url 'http://localhost:8085/actuator/health' 'Marketplace 8085'
Wait-Url 'http://localhost:8095/actuator/health' 'Marketplace 8095'

Write-Host ''
Write-Host 'Les instances apparaissent dans Eureka en moins d''une minute :'
Write-Host '  http://localhost:8761   et   http://127.0.0.1:8762'
