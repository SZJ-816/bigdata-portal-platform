ALTER TABLE `user` ADD COLUMN `role` VARCHAR(20) DEFAULT 'user' AFTER `email`;
ALTER TABLE `user` ADD COLUMN `is_active` TINYINT DEFAULT 1 AFTER `role`;
ALTER TABLE `user` ADD COLUMN `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `is_active`;
ALTER TABLE `user` ADD INDEX `idx_username` (`username`);
UPDATE `user` SET `role` = 'admin' WHERE `username` = 'admin';
UPDATE `user` SET `role` = 'user' WHERE `role` IS NULL OR `role` = '';
