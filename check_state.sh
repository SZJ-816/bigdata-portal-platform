#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "
SELECT channel, COUNT(*) AS cnt FROM news GROUP BY channel;
SELECT COUNT(*) AS unsplash FROM news WHERE image_url LIKE '%unsplash%';
SELECT COUNT(*) AS svg FROM news WHERE image_url LIKE '%svg%';
SELECT COUNT(*) AS ithome FROM news WHERE image_url LIKE '%ithome%';
"
