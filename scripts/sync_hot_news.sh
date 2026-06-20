#!/bin/bash
# sync_hot_news.sh - 从 ClickHouse 同步热门新闻数据到 MySQL

set -e

# 配置
MYSQL_HOST="localhost"
MYSQL_PORT="3306"
MYSQL_USER="root"
MYSQL_PASS="root123456"
MYSQL_DB="bigdata_portal"

CLICKHOUSE_HOST="localhost"
CLICKHOUSE_PORT="9000"
CLICKHOUSE_USER="default"
CLICKHOUSE_PASSWORD="bigdata123"
CLICKHOUSE_DB="bigdata_portal"

echo "[$(date)] 开始同步热门新闻数据..."

# 从 ClickHouse 查询每个新闻的阅读次数
docker exec bigdata-clickhouse clickhouse-client \
  --host ${CLICKHOUSE_HOST} \
  --port ${CLICKHOUSE_PORT} \
  --user ${CLICKHOUSE_USER} \
  --password ${CLICKHOUSE_PASSWORD} \
  --database ${CLICKHOUSE_DB} \
  --query "SELECT news_id, count() as cnt FROM user_behavior WHERE length(news_id) > 0 AND news_id != '' GROUP BY news_id" \
  > /tmp/clickhouse_news_counts.txt

# 检查是否有数据
if [ ! -s /tmp/clickhouse_news_counts.txt ]; then
    echo "[$(date)] ClickHouse 没有阅读数据，跳过同步"
    exit 0
fi

# 更新 MySQL 中的 view_count
while IFS=$'\t' read -r news_id cnt; do
    if [ -n "$news_id" ] && [ -n "$cnt" ]; then
        # 检查是否是有效数字
        if [[ "$cnt" =~ ^[0-9]+$ ]]; then
            mysql -h${MYSQL_HOST} -P${MYSQL_PORT} -u${MYSQL_USER} -p${MYSQL_PASS} ${MYSQL_DB} -e \
                "UPDATE news SET view_count = ${cnt} WHERE id = ${news_id};" 2>/dev/null || true
        fi
    fi
done < /tmp/clickhouse_news_counts.txt

echo "[$(date)] 同步完成！共处理 $(wc -l < /tmp/clickhouse_news_counts.txt) 条记录"
