#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "
SELECT id, channel, LEFT(image_url, 40) AS img FROM news WHERE channel NOT IN ('AI','大数据','云计算','互联网','硬件','创业') LIMIT 20;
"
