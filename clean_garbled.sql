#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "
SELECT COUNT(*) AS clean_count FROM news WHERE channel IN ('AI','大数据','云计算','互联网','硬件','创业');
DELETE FROM news WHERE channel NOT IN ('AI','大数据','云计算','互联网','硬件','创业');
SELECT COUNT(*) AS total_after FROM news;
"
