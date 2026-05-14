#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e "SELECT channel, COUNT(*) as cnt, LEFT(image_url,70) as img FROM news GROUP BY channel" 2>/dev/null
