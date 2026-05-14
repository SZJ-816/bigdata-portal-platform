#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "
DELETE FROM news WHERE channel NOT IN ('AI','大数据','云计算','互联网','硬件','创业');
UPDATE news SET image_url = '/images/channel-ai.svg' WHERE channel = 'AI' AND image_url LIKE '%unsplash%';
UPDATE news SET image_url = '/images/channel-bigdata.svg' WHERE channel = '大数据' AND image_url LIKE '%unsplash%';
UPDATE news SET image_url = '/images/channel-cloud.svg' WHERE channel = '云计算' AND image_url LIKE '%unsplash%';
UPDATE news SET image_url = '/images/channel-internet.svg' WHERE channel = '互联网' AND image_url LIKE '%unsplash%';
UPDATE news SET image_url = '/images/channel-hardware.svg' WHERE channel = '硬件' AND image_url LIKE '%unsplash%';
UPDATE news SET image_url = '/images/channel-startup.svg' WHERE channel = '创业' AND image_url LIKE '%unsplash%';
UPDATE news SET image_url = '/images/channel-internet.svg' WHERE image_url LIKE '%unsplash%';
SELECT channel, COUNT(*) AS cnt FROM news GROUP BY channel;
SELECT COUNT(*) AS total FROM news;
"
