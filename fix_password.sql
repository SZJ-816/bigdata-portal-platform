-- 修复用户密码 - 使用正确的BCrypt哈希
-- admin 用户密码: 123456
UPDATE `user` SET password = '$2a$10$Ec7Cmc6ZCm3PN736c2uST.BPoxHfp8gy5aTbuYdyvR3C2wYDmv/bO' WHERE username = 'admin';

-- test 用户密码: 123456  
UPDATE `user` SET password = '$2a$10$Ec7Cmc6ZCm3PN736c2uST.BPoxHfp8gy5aTbuYdyvR3C2wYDmv/bO' WHERE username = 'test';

SELECT username, password FROM `user`;