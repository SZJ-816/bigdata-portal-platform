#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "
SELECT channel, LEFT(image_url, 50) AS img FROM news ORDER BY id DESC LIMIT 10;
SELECT COUNT(*) AS unsplash_count FROM news WHERE image_url LIKE '%unsplash%';
"
