#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal -e "
SELECT source, COUNT(*) AS cnt FROM news GROUP BY source;
SELECT COUNT(*) AS total FROM news;
"
