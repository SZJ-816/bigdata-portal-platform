#!/usr/bin/env python3
import subprocess
import json

def run(cmd):
    r = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    return r.stdout.decode('utf-8', errors='ignore').strip(), r.returncode

print("=== ClickHouse 事件类型分布 ===")
out, _ = run("docker exec bigdata-clickhouse clickhouse-client --user=default --password=bigdata123 --database=bigdata_portal --query 'SELECT event_type, count() as cnt FROM user_behavior GROUP BY event_type ORDER BY cnt DESC'")
print(out)

print("\n=== ClickHouse 频道分布 ===")
out, _ = run("docker exec bigdata-clickhouse clickhouse-client --user=default --password=bigdata123 --database=bigdata_portal --query 'SELECT channel, count() as cnt FROM user_behavior WHERE length(channel) > 0 AND channel != ''  GROUP BY channel ORDER BY cnt DESC'")
print(out)

print("\n=== ClickHouse region分布 ===")
out, _ = run("docker exec bigdata-clickhouse clickhouse-client --user=default --password=bigdata123 --database=bigdata_portal --query 'SELECT region, count() as cnt FROM user_behavior WHERE length(region) > 0 AND region != '' GROUP BY region ORDER BY cnt DESC'")
print(out)

print("\n=== MySQL 新闻频道分布 ===")
out, _ = run("docker exec bigdata-mysql mysql -uroot -proot123456 bigdata_portal -e 'SELECT channel, count(*) as cnt FROM news GROUP BY channel ORDER BY cnt DESC'")
print(out)

print("\n=== 数据库新闻总数 ===")
out, _ = run("docker exec bigdata-mysql mysql -uroot -proot123456 bigdata_portal -e 'SELECT count(*) as total FROM news'")
print(out)

print("\n=== /api/news/hot API 数据 ===")
out, _ = run("curl -s http://localhost:8090/api/news/hot")
print(out)

print("\n=== /api/news?size=500 频道统计 (frontend fallback) ===")
out, _ = run("curl -s 'http://localhost:8090/api/news?page=1&size=500'")
try:
    data = json.loads(out)
    records = data.get('data', {}).get('records', data.get('data', []))
    channel_map = {}
    for item in records:
        ch = item.get('channel', '其他')
        channel_map[ch] = channel_map.get(ch, 0) + 1
    for k, v in sorted(channel_map.items(), key=lambda x: -x[1]):
        print(f"  {k}: {v}")
except Exception as e:
    print(f"  Error: {e}")
    print(f"  Raw: {out[:500]}")
