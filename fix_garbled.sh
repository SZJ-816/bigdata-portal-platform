#!/bin/bash
echo "=== 修复乱码数据 ==="

echo "1. 检查乱码数据数量..."
GARBLED_COUNT=$(docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -N -e "SELECT COUNT(*) FROM news WHERE id <= 54" 2>/dev/null)
echo "旧种子数据数量: $GARBLED_COUNT"

echo "2. 删除乱码的种子数据..."
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e "DELETE FROM news WHERE id <= 54" 2>/dev/null

echo "3. 检查剩余数据..."
TOTAL=$(docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -N -e "SELECT COUNT(*) FROM news" 2>/dev/null)
echo "剩余新闻数量: $TOTAL"

echo "4. 验证数据是否正常..."
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e "SELECT id, LEFT(title,30) as title, channel FROM news ORDER BY id ASC LIMIT 5" 2>/dev/null

echo "=== 修复完成 ==="
