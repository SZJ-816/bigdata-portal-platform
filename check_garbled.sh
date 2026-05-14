#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e "SELECT id, LEFT(title,40) as title, LEFT(summary,60) as summary FROM news WHERE content LIKE '%&lt;%' OR content LIKE '%&amp;%' OR content LIKE '%<p>%' OR content LIKE '%<a %' LIMIT 10" 2>/dev/null
