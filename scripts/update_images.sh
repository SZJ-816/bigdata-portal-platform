#!/bin/bash
cd /root/bigdata-portal-platform

# 更新每个频道的图片
docker compose exec -T mysql mysql -uroot -proot123 bigdata_portal << 'EOF'
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&h=400&fit=crop' WHERE channel = '互联网';
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=400&fit=crop' WHERE channel = '大数据';
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&h=400&fit=crop' WHERE channel = '硬件';
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800&h=400&fit=crop' WHERE channel = 'AI';
EOF

echo "Update completed!"
docker compose exec -T mysql mysql -uroot -proot123 bigdata_portal -e "SELECT channel, image_url, COUNT(*) FROM news GROUP BY channel, image_url;"
