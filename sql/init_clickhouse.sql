-- ============================================================
--  智讯科技资讯聚合平台 - ClickHouse 数据库初始化脚本
--  执行方式：
--    方式一：docker exec -i bigdata-clickhouse clickhouse-client \
--              --user default --password bigdata123 < init_clickhouse.sql
--    方式二：cat init_clickhouse.sql | docker exec -i bigdata-clickhouse clickhouse-client
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS bigdata_portal COMMENT '大数据门户 - 行为分析与统计';

-- ------------------------------------------------------------
--  1. 用户行为事件表 behavior_events (MergeTree 引擎)
--  用途：存储用户浏览、点赞、分享、收藏等全量行为事件
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bigdata_portal.behavior_events (
    event_date Date DEFAULT toDate(event_time),
    event_time DateTime DEFAULT now(),
    user_id UInt64,
    event_type String,
    target_id Nullable(UInt64),
    target_type Nullable(String),
    extra Nullable(String)
) ENGINE = MergeTree()
    PARTITION BY toYYYYMM(event_date)
    ORDER BY (event_date, user_id, event_time)
    TTL event_date + INTERVAL 1 YEAR
    SETTINGS index_granularity = 8192;

-- ------------------------------------------------------------
--  2. 新闻内容统计表 news_stats (SummingMergeTree 引擎)
--  用途：按新闻聚合浏览量、点赞数、评论数
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bigdata_portal.news_stats (
    news_id UInt64,
    news_title String,
    view_count UInt64 DEFAULT 0,
    like_count UInt64 DEFAULT 0,
    comment_count UInt64 DEFAULT 0,
    stat_date Date DEFAULT today()
) ENGINE = SummingMergeTree()
    PARTITION BY toYYYYMM(stat_date)
    ORDER BY (stat_date, news_id)
    SETTINGS index_granularity = 8192;

-- ------------------------------------------------------------
--  3. 频道运营统计表 channel_stats (SummingMergeTree 引擎)
--  用途：按频道聚合流量，支持大屏分析
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bigdata_portal.channel_stats (
    channel String,
    view_count UInt64 DEFAULT 0,
    stat_date Date DEFAULT today()
) ENGINE = SummingMergeTree()
    PARTITION BY toYYYYMM(stat_date)
    ORDER BY (stat_date, channel)
    SETTINGS index_granularity = 8192;

-- ============================================================
--  建表完成。以下是常用的查询示例
-- ============================================================

-- 示例 1：统计今日 PV（页面浏览量）
-- SELECT count() AS pv FROM bigdata_portal.behavior_events WHERE event_date = today();

-- 示例 2：统计今日 UV（独立访客数）
-- SELECT countDistinct(user_id) AS uv FROM bigdata_portal.behavior_events WHERE event_date = today();

-- 示例 3：行为类型分布
-- SELECT event_type, count() AS count
-- FROM bigdata_portal.behavior_events
-- WHERE event_date = today()
-- GROUP BY event_type
-- ORDER BY count DESC;

-- 示例 4：热门新闻 TOP 10
-- SELECT news_id, any(news_title) AS title,
--        sum(view_count) AS total_views,
--        sum(like_count) AS total_likes
-- FROM bigdata_portal.news_stats
-- GROUP BY news_id
-- ORDER BY total_views DESC
-- LIMIT 10;

-- 示例 5：近 7 天流量趋势
-- SELECT toDate(event_time) AS date,
--        count() AS pv,
--        countDistinct(user_id) AS uv
-- FROM bigdata_portal.behavior_events
-- WHERE event_date >= today() - 7
-- GROUP BY date
-- ORDER BY date;

-- ============================================================
--  数据导入（从 MySQL 同步历史行为数据）
--  建议在 MySQL 数据初始化完成后执行
-- ============================================================

-- 导入用户行为数据（通过 MySQL 表函数从 MySQL 同步）
INSERT INTO bigdata_portal.behavior_events
    (event_date, event_time, user_id, event_type, target_id, target_type, extra)
SELECT
    DATE(create_time) AS event_date,
    create_time AS event_time,
    user_id,
    behavior_type AS event_type,
    article_id AS target_id,
    'article' AS target_type,
    NULL AS extra
FROM mysql(
    'bigdata-mysql:3306',
    'bigdata_portal',
    'user_behavior',
    'root',
    'root123456'
);

-- 验证导入数据量
SELECT count() AS total_events FROM bigdata_portal.behavior_events;

-- ============================================================
--  ClickHouse 初始化完成！
-- ============================================================
