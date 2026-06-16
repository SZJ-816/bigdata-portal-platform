UPDATE news SET image_url = CASE channel
  WHEN 'AI' THEN 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800&h=400&fit=crop'
  WHEN '人工智能' THEN 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800&h=400&fit=crop'
  WHEN '创业' THEN 'https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=800&h=400&fit=crop'
  WHEN '互联网' THEN 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&h=400&fit=crop'
  WHEN '大数据' THEN 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=400&fit=crop'
  WHEN '云计算' THEN 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&h=400&fit=crop'
  WHEN '硬件' THEN 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&h=400&fit=crop'
  WHEN '科技' THEN 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&h=400&fit=crop'
  WHEN '软件' THEN 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=800&h=400&fit=crop'
  ELSE 'https://images.unsplash.com/photo-1488590528505-98d2b5aba04b?w=800&h=400&fit=crop'
END;
