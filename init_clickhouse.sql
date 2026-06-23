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
ORDER BY (event_date, user_id, event_time);

CREATE TABLE IF NOT EXISTS bigdata_portal.news_stats (
    news_id UInt64,
    news_title String,
    view_count UInt64 DEFAULT 0,
    like_count UInt64 DEFAULT 0,
    comment_count UInt64 DEFAULT 0,
    stat_date Date DEFAULT today()
) ENGINE = SummingMergeTree()
ORDER BY (stat_date, news_id);

CREATE TABLE IF NOT EXISTS bigdata_portal.channel_stats (
    channel String,
    view_count UInt64 DEFAULT 0,
    stat_date Date DEFAULT today()
) ENGINE = SummingMergeTree()
ORDER BY (stat_date, channel);
