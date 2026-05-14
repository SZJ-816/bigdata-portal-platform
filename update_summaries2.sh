#!/bin/bash
docker exec bigdata-mysql mysql -uroot -proot123 bigdata_portal << 'EOF'
UPDATE news 
SET summary = CONCAT('本文介绍了', title, '相关内容，点击查看详情了解更多信息。')
WHERE summary LIKE '%点击查看%' OR summary LIKE '%阅读全文%';
EOF

echo "Updated summaries!"
