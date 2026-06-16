#!/bin/bash
set -e

/init-entrypoint.sh "$@" &

until clickhouse-client --query "SELECT 1" 2>/dev/null; do
  echo "Waiting for ClickHouse to start..."
  sleep 2
done

echo "ClickHouse is ready, running init SQL..."
clickhouse-client --queries-file /docker-entrypoint-initdb.d/init.sql || echo "Init SQL may have already been applied"
echo "ClickHouse initialization complete."

wait
