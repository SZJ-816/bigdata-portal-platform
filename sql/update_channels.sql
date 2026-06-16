-- 更新现有新闻的频道，使其与前端频道一致
-- 执行方式: docker exec -i bigdata-mysql mysql -uroot -proot123 bigdata_portal < sql/update_channels.sql

-- 查看当前各频道的新闻数量
SELECT channel, COUNT(*) as count FROM news GROUP BY channel;

-- 更新 雷锋网 的新闻为 AI 频道
UPDATE news SET channel = 'AI' WHERE source = '雷锋网';

-- 更新 机器之心 的新闻为 AI 频道
UPDATE news SET channel = 'AI' WHERE source = '机器之心';

-- 更新 InfoQ 的新闻为 大数据 频道
UPDATE news SET channel = '大数据' WHERE source = 'InfoQ';

-- 更新 虎嗅 的新闻为 互联网 频道（而不是"科技"）
UPDATE news SET channel = '互联网' WHERE source = '虎嗅';

-- 查看更新后的各频道新闻数量
SELECT channel, COUNT(*) as count FROM news GROUP BY channel;