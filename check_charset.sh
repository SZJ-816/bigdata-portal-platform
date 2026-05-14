#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 -e "
SHOW VARIABLES LIKE 'character_set%';
SHOW VARIABLES LIKE 'collation%';
SHOW CREATE TABLE bigdata_portal.news\G
" 2>/dev/null
