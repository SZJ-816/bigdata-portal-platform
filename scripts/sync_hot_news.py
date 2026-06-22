#!/usr/bin/env python3
"""
同步热门新闻数据：从 ClickHouse 读取真实阅读数据，更新到 MySQL
"""
import subprocess
import sys

def run_cmd(cmd):
    result = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    return result.stdout.decode('utf-8', errors='ignore').strip(), result.returncode

def main():
    print("[同步] 从 ClickHouse 获取新闻阅读数据...")

    # 从 ClickHouse 查询每个新闻的阅读次数
    query = "SELECT news_id, count() as cnt FROM user_behavior WHERE length(news_id) > 0 AND news_id != '' GROUP BY news_id"
    cmd = f'docker exec bigdata-clickhouse clickhouse-client --user=default --password=bigdata123 --database=bigdata_portal --query "{query}"'
    output, rc = run_cmd(cmd)

    if rc != 0 or not output:
        print("[同步] ClickHouse 查询失败或无数据")
        return

    # 解析数据并更新 MySQL
    lines = output.strip().split('\n')
    updated = 0

    for line in lines:
        parts = line.split('\t')
        if len(parts) >= 2:
            news_id = parts[0].strip()
            cnt = parts[1].strip()

            if news_id.isdigit() and cnt.isdigit():
                update_sql = f"UPDATE news SET view_count = {cnt} WHERE id = {news_id};"
                mysql_cmd = f'docker exec bigdata-mysql mysql -uroot -proot123456 bigdata_portal -e "{update_sql}"'
                run_cmd(mysql_cmd)
                updated += 1

    print(f"[同步] 完成！共更新 {updated} 条记录")

if __name__ == '__main__':
    main()
