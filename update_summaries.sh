#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal << 'EOF'
UPDATE news 
SET summary = CONCAT('本文介绍了', title, '相关内容，点击查看详情了解更多信息。')
WHERE LENGTH(summary) < 20;
EOF

echo "Updated summaries!"
