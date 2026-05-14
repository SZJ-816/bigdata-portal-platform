#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "
SELECT COUNT(*) AS garbled_count FROM news WHERE channel NOT IN ('AI','大数据','云计算','互联网','硬件','创业');
SELECT DISTINCT channel, COUNT(*) AS cnt FROM news WHERE channel NOT IN ('AI','大数据','云计算','互联网','硬件','创业') GROUP BY channel;
"
