# Configuration
$CONTAINER_NAME = "mongodb"
$DB_NAME = "catalog_db"
$BACKUP_FILE = $args[0]

# Check parameter
if (-not $BACKUP_FILE) {
    Write-Host "Missing backup file argument."
    Write-Host "Usage:"
    Write-Host "  powershell -ExecutionPolicy Bypass -File restore.ps1 .\\backups\\backup_xxx.gzip"
    exit 1
}

# Check file exists
if (!(Test-Path $BACKUP_FILE)) {
    Write-Host "Backup file not found: $BACKUP_FILE"
    exit 1
}

Write-Host "Starting Restore to DB: $DB_NAME ..."
Write-Host "Using Backup File: $BACKUP_FILE"

# Use CMD for binary-safe piping
$cmd = "type `"$BACKUP_FILE`" | docker exec -i $CONTAINER_NAME sh -c ""mongorestore --username admin --password password --authenticationDatabase admin --archive --nsInclude=$DB_NAME.* --drop"""

cmd.exe /c $cmd

# Check exit code
if ($LASTEXITCODE -eq 0) {
    Write-Host "Restore Completed Successfully."
} else {
    Write-Host "Restore Failed."
}
