# Configuration
$CONTAINER_NAME = "mongodb"
$DB_NAME = "catalog_db"
$BACKUP_DIR = "./backups"
$TIMESTAMP = Get-Date -Format "yyyyMMdd_HHmmss"
$FILENAME = "$BACKUP_DIR/backup_$TIMESTAMP.gzip"

# Create backup directory if needed
if (!(Test-Path $BACKUP_DIR)) {
    New-Item -ItemType Directory -Path $BACKUP_DIR | Out-Null
}

Write-Host "🔄 Starting Backup for $DB_NAME..."

# Build command
$cmd = "docker exec $CONTAINER_NAME sh -c ""mongodump --username admin --password password --authenticationDatabase admin --db $DB_NAME --archive"" > `"$FILENAME`""

# Run in CMD to preserve binary output
cmd.exe /c $cmd

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Backup Successful: $FILENAME"
} else {
    Write-Host "❌ Backup Failed!"
}
