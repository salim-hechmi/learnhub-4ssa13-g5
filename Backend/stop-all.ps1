# Arrête les processus lancés par start-all.ps1 (repérés par leur port d'écoute).
$ports = 8761, 8762, 8082, 8092, 8085, 8095
foreach ($port in $ports) {
    Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue |
        ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue; Write-Host "Arrêté : port $port" }
}
