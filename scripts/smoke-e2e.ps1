param(
    [string]$BaseUrl = "http://localhost:7529",
    [string]$UserAccount = "tengyun",
    [string]$UserPassword = "12345678",
    [int64]$InterfaceId = 1,
    [int]$GrantNum = 2
)

$ErrorActionPreference = 'Stop'

function Assert-CodeZero($resp, $name) {
    if ($null -eq $resp -or $resp.code -ne 0) {
        throw "[FAIL] $name => $($resp.message)"
    }
    Write-Output "[OK] $name"
}

$loginBody = @{
    userAccount = $UserAccount
    userPassword = $UserPassword
} | ConvertTo-Json

$loginResp = Invoke-RestMethod -Uri "$BaseUrl/user/login" -Method Post -ContentType "application/json" -Body $loginBody -SessionVariable session
Assert-CodeZero $loginResp "login"

$currentResp = Invoke-RestMethod -Uri "$BaseUrl/user/current" -WebSession $session
Assert-CodeZero $currentResp "currentUser"
$userId = [int64]$currentResp.data.id
Write-Output "[INFO] userId=$userId role=$($currentResp.data.userRole)"

$beforeResp = Invoke-RestMethod -Uri "$BaseUrl/userInterfaceInfo/get?userId=$userId&interfaceInfoId=$InterfaceId" -WebSession $session -ErrorAction SilentlyContinue
$leftBefore = 0
if ($beforeResp -and $beforeResp.code -eq 0) {
    $leftBefore = [int]$beforeResp.data.leftNum
}
Write-Output "[INFO] leftBefore=$leftBefore"

$grantBody = @{
    userId = $userId
    interfaceInfoId = $InterfaceId
    grantNum = $GrantNum
    description = "smoke grant"
} | ConvertTo-Json
$grantResp = Invoke-RestMethod -Uri "$BaseUrl/userInterfaceInfo/grant" -Method Post -ContentType "application/json" -Body $grantBody -WebSession $session
Assert-CodeZero $grantResp "grantQuota"

$invokeBody = @{
    id = $InterfaceId
    userRequestParams = '{"name":"tengyun"}'
} | ConvertTo-Json
$invokeResp = Invoke-RestMethod -Uri "$BaseUrl/interfaceInfo/invoke" -Method Post -ContentType "application/json" -Body $invokeBody -WebSession $session
Assert-CodeZero $invokeResp "invokeInterface"

for ($i = 0; $i -lt 6; $i++) {
    Start-Sleep -Seconds 1
    $logResp = Invoke-RestMethod -Uri "$BaseUrl/interfaceInvokeLog/list/page?userId=$userId&interfaceInfoId=$InterfaceId&pageNum=1&pageSize=5" -WebSession $session
    Assert-CodeZero $logResp "queryInvokeLog"
    $records = @($logResp.data.records)
    if ($records.Count -eq 0) {
        continue
    }
    if ([int]$records[0].status -ne 2) {
        break
    }
}
if ($records.Count -gt 0) {
    Write-Output "[INFO] latestInvoke status=$($records[0].status) http=$($records[0].responseStatus)"
}

$afterResp = Invoke-RestMethod -Uri "$BaseUrl/userInterfaceInfo/get?userId=$userId&interfaceInfoId=$InterfaceId" -WebSession $session
Assert-CodeZero $afterResp "queryQuotaAfterInvoke"
$leftAfter = [int]$afterResp.data.leftNum
Write-Output "[INFO] leftAfter=$leftAfter"
Write-Output "[INFO] expectedDelta=$(($GrantNum - 1)) actualDelta=$(($leftAfter - $leftBefore))"

$auditResp = Invoke-RestMethod -Uri "$BaseUrl/adminOperateLog/list/page?action=AUTH_GRANT_QUOTA&pageNum=1&pageSize=5" -WebSession $session
Assert-CodeZero $auditResp "queryAuditLog"
Write-Output "[INFO] auditGrantCount=$(@($auditResp.data.records).Count)"

Write-Output "[DONE] smoke e2e finished."
