#!/bin/bash
# 更新数据库密码的脚本

# 生成BCrypt哈希
echo "Generating BCrypt hash for password '123456'..."

# 使用docker exec执行更新
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "
-- 生成新的BCrypt哈希并更新密码
UPDATE \`user\` SET password = '\$2a\$10\$Ec7Cmc6ZCm3PN736c2uST.BPoxHfp8gy5aTbuYdyvR3C2wYDmv/bO' WHERE username = 'admin';
UPDATE \`user\` SET password = '\$2a\$10\$Ec7Cmc6ZCm3PN736c2uST.BPoxHfp8gy5aTbuYdyvR3C2wYDmv/bO' WHERE username = 'test';

-- 验证更新结果
SELECT id, username, email FROM \`user\`;
"