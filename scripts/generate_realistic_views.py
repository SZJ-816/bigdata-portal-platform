#!/usr/bin/env python3
"""
生成合理的新闻阅读数据
基于新闻发布时间和频道生成合理的 view_count
"""
import subprocess
import random
import time

def run_cmd(cmd):
    result = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    return result.stdout.decode('utf-8', errors='ignore').strip(), result.returncode

def main():
    print("[数据生成] 开始生成合理的热门新闻数据...")

    # 获取所有新闻
    query = "SELECT id, UNIX_TIMESTAMP(publish_time), channel FROM news ORDER BY publish_time DESC"
    cmd = f'docker exec bigdata-mysql mysql -uroot -proot123456 bigdata_portal -N -e "{query}"'
    output, rc = run_cmd(cmd)

    if rc != 0 or not output:
        print("[数据生成] 查询失败")
        return

    lines = output.strip().split('\n')
    now = int(time.time())
    updated = 0

    # 新闻越新，阅读量越可能高（基线更高）
    # 同时添加一些随机因素
    for line in lines:
        parts = line.split('\t')
        if len(parts) >= 3:
            news_id = parts[0].strip()
            publish_ts = int(parts[1].strip()) if parts[1].strip().isdigit() else 0
            channel = parts[2].strip()

            if not news_id.isdigit():
                continue

            # 计算新闻年龄（秒）
            age_hours = (now - publish_ts) / 3600 if publish_ts > 0 else 999

            # 根据年龄和频道生成阅读量
            if age_hours < 1:  # 1小时内
                base = random.randint(50, 500)
            elif age_hours < 6:  # 6小时内
                base = random.randint(200, 2000)
            elif age_hours < 24:  # 24小时内
                base = random.randint(500, 5000)
            elif age_hours < 72:  # 3天内
                base = random.randint(1000, 10000)
            else:  # 3天以上
                base = random.randint(50, 2000)

            # 根据频道调整
            channel_multiplier = {
                'AI': 1.5,
                '大数据': 1.3,
                '云计算': 1.2,
                '互联网': 1.1,
                '安全': 1.4,
                '区块链': 1.0,
                '硬件': 1.1,
                '创业': 0.9,
                '数码': 1.0,
                '汽车科技': 1.2,
            }.get(channel, 1.0)

            view_count = int(base * channel_multiplier * random.uniform(0.5, 1.5))
            comment_count = random.randint(0, min(view_count // 20, 500))

            update_sql = f"UPDATE news SET view_count = {view_count}, comment_count = {comment_count} WHERE id = {news_id};"
            mysql_cmd = f'docker exec bigdata-mysql mysql -uroot -proot123456 bigdata_portal -e "{update_sql}"'
            run_cmd(mysql_cmd)
            updated += 1

    print(f"[数据生成] 完成！共更新 {updated} 条记录")

if __name__ == '__main__':
    main()
