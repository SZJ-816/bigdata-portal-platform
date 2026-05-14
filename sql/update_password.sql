-- 更新用户密码的SQL脚本
-- 执行方式: docker exec -i bigdata-mysql mysql -uroot -proot123 bigdata_portal < sql/update_password.sql

UPDATE `user` SET password = '$2a$10$Ec7Cmc6ZCm3PN736c2uST.BPoxHfp8gy5aTbuYdyvR3C2wYDmv/bO' WHERE username = 'admin';

UPDATE `user` SET password = '$2a$10$Ec7Cmc6ZCm3PN736c2uST.BPoxHfp8gy5aTbuYdyvR3C2wYDmv/bO' WHERE username = 'test';

SELECT id, username, email FROM `user`;