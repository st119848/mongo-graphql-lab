param (
    [Parameter(Mandatory = $true)]
    [string]$NewVersion
)

$AppName = "catalog-service"
$HealthUrl = "http://localhost:8080/actuator/health"

Write-Host "Deploying ${AppName}:${NewVersion} ..."

# --- 1. Stop & Remove current container ---
docker stop $AppName 2>$null
docker rm $AppName 2>$null

# --- 2. Start New Version ---
docker run -d --name $AppName `
    -p 8080:8080 `
    --network mongo-graphql-lab_default `
    -e SPRING_DATA_MONGODB_URI="mongodb://admin:password@mongodb:27017/catalog_db?authSource=admin" `
    -e SPRING_DATA_REDIS_HOST="redis" `
    -e SPRING_KAFKA_BOOTSTRAP_SERVERS="kafka:9092" `
    "${AppName}:${NewVersion}"

Write-Host "Waiting for application to boot (15s)..."
Start-Sleep -Seconds 15

# --- 3. Health Check ---
try {
    $response = Invoke-WebRequest -Uri $HealthUrl -UseBasicParsing -TimeoutSec 5
    $httpStatus = $response.StatusCode
} catch {
    if ($_.Exception.Response) {
        $httpStatus = $_.Exception.Response.StatusCode.value__
    } else {
        $httpStatus = 0
    }
}

Write-Host "Health Check Status: $httpStatus"

if ($httpStatus -eq 200) {
    Write-Host "Deployment Successful! Version ${NewVersion} is live."
}
else {
    Write-Host "DEPLOYMENT FAILED! Health Check returned $httpStatus"
    Write-Host "Starting rollback to v1..."

    docker stop $AppName 2>$null
    docker rm $AppName 2>$null

    docker run -d --name $AppName `
        -p 8080:8080 `
        --network mongo-graphql-lab_default `
        -e SPRING_DATA_MONGODB_URI="mongodb://admin:password@mongodb:27017/catalog_db?authSource=admin" `
        -e SPRING_DATA_REDIS_HOST="redis" `
        -e SPRING_KAFKA_BOOTSTRAP_SERVERS="kafka:9092" `
        "${AppName}:v1"

    Write-Host "Rollback complete. Version 1 is back online."
}
