-- 逐个频道更新图片URL
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800&h=400&fit=crop' WHERE channel = 'AI';
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&h=400&fit=crop' WHERE channel = '硬件';
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&h=400&fit=crop' WHERE channel = '互联网';
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=400&fit=crop' WHERE channel = '大数据';
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&h=400&fit=crop' WHERE channel = '云计算';
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=800&h=400&fit=crop' WHERE channel = '软件';
UPDATE news SET image_url = 'https://images.unsplash.com/photo-1488590528505-98d2b5aba04b?w=800&h=400&fit=crop' WHERE channel NOT IN ('AI', '硬件', '互联网', '大数据', '云计算', '软件');
