#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e "SELECT id, LEFT(title,40) as title, channel FROM news WHERE id > 100 LIMIT 10" 2>/dev/null
