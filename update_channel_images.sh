#!/bin/bash

AI_IMG="https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800&h=400&fit=crop"
BIGDATA_IMG="https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800&h=400&fit=crop"
CLOUD_IMG="https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=800&h=400&fit=crop"
INTERNET_IMG="https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&h=400&fit=crop"
HARDWARE_IMG="https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&h=400&fit=crop"
STARTUP_IMG="https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=800&h=400&fit=crop"

echo "Updating images by channel..."

docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e \
  "UPDATE news SET image_url='${AI_IMG}' WHERE channel='AI'" 2>/dev/null
echo "AI: done"

docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e \
  "UPDATE news SET image_url='${BIGDATA_IMG}' WHERE channel='大数据'" 2>/dev/null
echo "大数据: done"

docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e \
  "UPDATE news SET image_url='${CLOUD_IMG}' WHERE channel='云计算'" 2>/dev/null
echo "云计算: done"

docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e \
  "UPDATE news SET image_url='${INTERNET_IMG}' WHERE channel='互联网'" 2>/dev/null
echo "互联网: done"

docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e \
  "UPDATE news SET image_url='${HARDWARE_IMG}' WHERE channel='硬件'" 2>/dev/null
echo "硬件: done"

docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e \
  "UPDATE news SET image_url='${STARTUP_IMG}' WHERE channel='创业'" 2>/dev/null
echo "创业: done"

echo ""
echo "=== Verify ==="
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal --default-character-set=utf8mb4 -e \
  "SELECT channel, COUNT(*) as cnt, LEFT(image_url,70) as img FROM news GROUP BY channel" 2>/dev/null
