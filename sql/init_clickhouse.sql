CREATE DATABASE IF NOT EXISTS analytics;

USE analytics;

CREATE TABLE IF NOT EXISTS behavior_events (
    event_date Date,
    event_time DateTime,
    user_id UInt64,
    event_type String,
    target_id UInt64,
    target_type String,
    extra String
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(event_date)
ORDER BY (event_date, event_time, user_id)
TTL event_date + INTERVAL 90 DAY;

CREATE TABLE IF NOT EXISTS realtime_stats (
    stat_time DateTime,
    metric_name String,
    metric_value UInt64
) ENGINE = MergeTree()
ORDER BY (stat_time, metric_name);

CREATE TABLE IF NOT EXISTS news_stats (
    stat_date Date,
    news_id UInt64,
    news_title String,
    view_count UInt64,
    comment_count UInt64,
    share_count UInt64
) ENGINE = SummingMergeTree()
ORDER BY (stat_date, news_id);

CREATE TABLE IF NOT EXISTS channel_stats (
    stat_date Date,
    channel String,
    view_count UInt64,
    comment_count UInt64
) ENGINE = SummingMergeTree()
ORDER BY (stat_date, channel);
