#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e "SELECT channel, COUNT(*) as cnt FROM news GROUP BY channel" 2>/dev/null
echo "---"
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e "SELECT DISTINCT channel, LEFT(image_url,80) as img FROM news LIMIT 10" 2>/dev/null
