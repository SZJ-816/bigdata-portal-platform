#!/bin/bash
cd /root/bigdata-portal-platform

echo "Updating images by image_url pattern..."

docker compose exec -T mysql mysql -uroot -proot123 bigdata_portal << 'EOF'
-- 直接用SQL语句，不用变量匹配中文
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800&h=400&fit=crop' WHERE id IN (SELECT id FROM (SELECT id FROM news LIMIT 44) t);
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&h=400&fit=crop' WHERE id IN (SELECT id FROM (SELECT id FROM news LIMIT 21 OFFSET 44) t);
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&h=400&fit=crop' WHERE id IN (SELECT id FROM (SELECT id FROM news LIMIT 20 OFFSET 65) t);
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=400&fit=crop' WHERE id IN (SELECT id FROM (SELECT id FROM news LIMIT 21 OFFSET 85) t);
EOF

echo "Verification after update:"
docker compose exec -T mysql mysql -uroot -proot123 bigdata_portal -e "SELECT id, channel, SUBSTRING(image_url, 1, 70) FROM news LIMIT 5;"
docker compose exec -T mysql mysql -uroot -proot123 bigdata_portal -e "SELECT id, channel, SUBSTRING(image_url, 1, 70) FROM news LIMIT 5 OFFSET 44;"
docker compose exec -T mysql mysql -uroot -proot123 bigdata_portal -e "SELECT id, channel, SUBSTRING(image_url, 1, 70) FROM news LIMIT 5 OFFSET 65;"
docker compose exec -T mysql mysql -uroot -proot123 bigdata_portal -e "SELECT id, channel, SUBSTRING(image_url, 1, 70) FROM news LIMIT 5 OFFSET 85;"
