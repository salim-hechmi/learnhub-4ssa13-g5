# =====================================================================
#  Enregistre marketplace-service sur le serveur Eureka d'un AUTRE PC du groupe (même Wi-Fi).
#  Exemple : Eureka lancé chez Nawres (IP 192.168.0.124) ->
#     powershell -ExecutionPolicy Bypass -File .\start-marketplace-remote.ps1 -EurekaHost 192.168.0.124
#
#  Paramètres :
#    -EurekaHost   IP du PC qui fait tourner Eureka (ipconfig chez lui, ligne « Carte réseau sans fil Wi-Fi »)
#    -EurekaPorts  port(s) Eureka sur ce PC (8761, ou 8761,8762 s'il lance les deux instances)
#    -Port         port de ce marketplace (8085 par défaut)
#    -Mode         local (H2 + catalogue simulé, rien à installer) ou integration (MySQL + vrai course-service)
#
#  Arrêt : .\stop-all.ps1   (ou fermer le processus java sur le port choisi)
# =====================================================================
param(
    [Parameter(Mandatory = $true)][string]$EurekaHost,
    [int[]]$EurekaPorts = @(8761),
    [int]$Port = 8085,
    [ValidateSet('local', 'integration')][string]$Mode = 'local'
)
$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$logs = Join-Path $root 'logs'
New-Item -ItemType Directory -Force $logs | Out-Null

# 1. L'IP de CE PC sur le réseau qui mène à Eureka (évite les cartes VirtualBox / VMware,
#    sinon le service s'annoncerait avec une IP que les autres PC ne peuvent pas joindre)
$myIp = (Find-NetRoute -RemoteIPAddress $EurekaHost -ErrorAction SilentlyContinue | Where-Object { $_.IPAddress } | Select-Object -First 1).IPAddress
if (-not $myIp) { Write-Warning "Impossible de trouver une route vers $EurekaHost : êtes-vous sur le même Wi-Fi ?"; exit 1 }
Write-Host "IP de ce PC sur le réseau : $myIp"

# 2. Eureka joignable ?
foreach ($p in $EurekaPorts) {
    if (-not (Test-NetConnection $EurekaHost -Port $p -InformationLevel Quiet -WarningAction SilentlyContinue)) {
        Write-Warning "Eureka ne répond pas sur http://${EurekaHost}:$p : il doit être lancé, et son pare-feu doit autoriser le port $p."
        exit 1
    }
}
$zones = ($EurekaPorts | ForEach-Object { "http://${EurekaHost}:$_/eureka/" }) -join ','

# 3. JAR du marketplace (compilé si absent)
$dir = Join-Path $root 'marketplace-service\target'
$jar = Get-ChildItem $dir -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Name -notlike '*.original' } | Select-Object -First 1
if (-not $jar) {
    Push-Location (Join-Path $root 'marketplace-service'); mvn -q -DskipTests package; Pop-Location
    $jar = Get-ChildItem $dir -Filter '*.jar' | Where-Object { $_.Name -notlike '*.original' } | Select-Object -First 1
}

# 4. Lancement
$arguments = @('-Xms32m', '-Xmx192m', '-XX:+UseSerialGC', '-XX:TieredStopAtLevel=1', '-jar', "`"$($jar.FullName)`"",
    "--spring.profiles.active=$Mode", "--server.port=$Port",
    "--eureka.client.service-url.defaultZone=$zones",
    '--eureka.instance.prefer-ip-address=true', "--eureka.instance.ip-address=$myIp")
Start-Process java -ArgumentList $arguments -WindowStyle Hidden `
    -RedirectStandardOutput (Join-Path $logs "marketplace-remote-$Port.log") -RedirectStandardError (Join-Path $logs "marketplace-remote-$Port.err.log") | Out-Null

for ($i = 0; $i -lt 90; $i++) {
    try { Invoke-WebRequest "http://localhost:$Port/actuator/health" -UseBasicParsing -TimeoutSec 2 | Out-Null; break } catch { Start-Sleep 2 }
}
Write-Host ''
Write-Host "marketplace-service:$Port démarré ($myIp). Il apparaît en moins de 30 s sur :"
$EurekaPorts | ForEach-Object { Write-Host "  http://${EurekaHost}:$_" }
Write-Host ''
Write-Host "Pour que l'autre PC puisse ouvrir ce service (lien du dashboard, appels OpenFeign), autorisez le port $Port"
Write-Host "dans le pare-feu de CE PC (PowerShell en administrateur) :"
Write-Host "  New-NetFirewallRule -DisplayName 'LearnHub marketplace $Port' -Direction Inbound -Protocol TCP -LocalPort $Port -Action Allow -Profile Private"
