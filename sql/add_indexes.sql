ALTER TABLE news ADD INDEX idx_publish_time (publish_time DESC);
ALTER TABLE news ADD INDEX idx_channel_publish (channel, publish_time DESC);
ALTER TABLE news ADD INDEX idx_is_breaking (is_breaking, publish_time DESC);
ALTER TABLE news ADD INDEX idx_source_url (source_url);
