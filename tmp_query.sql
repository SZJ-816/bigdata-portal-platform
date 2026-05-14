#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "
SELECT COUNT(*) AS total,
  SUM(CASE WHEN image_url LIKE '%unsplash%' THEN 1 ELSE 0 END) AS unsplash,
  SUM(CASE WHEN image_url LIKE '%ithome%' THEN 1 ELSE 0 END) AS ithome,
  SUM(CASE WHEN image_url LIKE '%leiphone%' THEN 1 ELSE 0 END) AS leiphone,
  SUM(CASE WHEN image_url IS NULL OR image_url = '' THEN 1 ELSE 0 END) AS no_img
FROM news;
"
