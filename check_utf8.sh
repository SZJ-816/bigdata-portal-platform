#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 -e "
SET NAMES utf8mb4;
SELECT channel, COUNT(*) AS cnt FROM news GROUP BY channel;
" 2>/dev/null
