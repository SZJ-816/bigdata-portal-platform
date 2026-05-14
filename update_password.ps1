# 更新数据库密码的PowerShell脚本

# 检查MySQL客户端是否可用
Write-Host "Checking MySQL client..."

# 尝试使用MySQL客户端直接连接
$mysqlCmd = "mysql"
$mysqlHost = "192.168.146.128"
$mysqlUser = "root"
$mysqlPass = "root123"
$mysqlDb = "bigdata_portal"

# BCrypt哈希（由Java生成的）
$bcryptHash = "`$2a`$10`$Ec7Cmc6ZCm3PN736c2uST.BPoxHfp8gy5aTbuYdyvR3C2wYDmv/bO"

Write-Host "Updating passwords in database..."

$sql = @"
UPDATE `user` SET password = '$bcryptHash' WHERE username = 'admin';
UPDATE `user` SET password = '$bcryptHash' WHERE username = 'test';
SELECT id, username, email FROM `user`;
"@

# 如果mysql客户端存在，执行更新
if (Get-Command mysql -ErrorAction SilentlyContinue) {
    mysql -h $mysqlHost -u $mysqlUser -p$mysqlPass $mysqlDb -e $sql
} else {
    Write-Host "MySQL client not found. Trying alternative methods..."
    
    # 尝试使用docker exec
    if (Get-Command docker -ErrorAction SilentlyContinue) {
        Write-Host "Using docker exec to update passwords..."
        docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "UPDATE `user` SET password = '\$2a\$10\$Ec7Cmc6ZCm3PN736c2uST.BPoxHfp8gy5aTbuYdyvR3C2wYDmv/bO' WHERE username = 'admin';"
        docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "UPDATE `user` SET password = '\$2a\$10\$Ec7Cmc6ZCm3PN736c2uST.BPoxHfp8gy5aTbuYdyvR3C2wYDmv/bO' WHERE username = 'test';"
        Write-Host "Password update completed!"
    } else {
        Write-Host "Docker not found either. Please update passwords manually or restart the backend service."
    }
}