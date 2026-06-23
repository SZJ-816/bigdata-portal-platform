INSERT INTO bigdata_portal.behavior_events (event_date, event_time, user_id, event_type, target_id, target_type, extra)
SELECT
    DATE(created_at) as event_date,
    created_at as event_time,
    user_id,
    event_type,
    target_id,
    target_type,
    extra
FROM mysql('bigdata-mysql:3306', 'bigdata_portal', 'user_behavior', 'root', 'BigData@2026');
