import requests
import random
import pymysql

base_url = "http://192.168.146.128"
db_config = {
    'host': '192.168.146.128',
    'user': 'root',
    'password': 'root123',
    'database': 'bigdata_portal',
    'port': 3306
}

print("=" * 70)
print("重新分配新闻到各个频道")
print("=" * 70)

# 获取所有新闻
print("\n获取现有新闻...")
response = requests.get(f"{base_url}/api/news")
all_news = []
if response.status_code == 200:
    all_news = response.json().get('data', [])
    print(f"获取到 {len(all_news)} 条新闻")

if not all_news:
    print("没有获取到新闻，退出")
    exit(1)

# 定义目标频道和分配比例
target_channels = ['AI', '大数据', '云计算', '互联网', '硬件', '创业']
channel_keywords = {
    'AI': ['ai', 'gpt', 'llm', '大语言模型', '人工智能', '机器学习', '深度学习', 'robot', '机器人', '算法', '模型'],
    '大数据': ['big data', '大数据', '数据', 'spark', 'hadoop', '数据库', '数据分析'],
    '云计算': ['cloud', '云', 'aws', 'azure', 'aliyun', '阿里云', '云原生', 'docker', 'kubernetes'],
    '互联网': ['互联网', 'web', 'app', '应用', '平台', '产品', '运营', '36氪', '虎嗅', '钛媒体', '极客公园'],
    '硬件': ['硬件', '芯片', 'cpu', 'gpu', '手机', 'iphone', 'pc', '电脑', '设备', '爱范儿', '半导体'],
    '创业': ['创业', '投资', '融资', '公司', '企业', 'techcrunch', '创业公司'],
}

# 连接数据库
print("\n连接数据库...")
conn = None
try:
    conn = pymysql.connect(**db_config)
    cursor = conn.cursor()
    
    # 先查看当前频道分布
    print("\n当前频道分布:")
    cursor.execute("SELECT channel, COUNT(*) as count FROM news GROUP BY channel")
    for row in cursor:
        print(f"  {row[0]}: {row[1]} 条")
    
    # 开始重新分配
    print(f"\n开始重新分配 {len(all_news)} 条新闻...")
    updated_count = 0
    
    for news in all_news:
        news_id = news.get('id')
        title = news.get('title', '').lower()
        summary = news.get('summary', '').lower()
        
        # 根据关键词分配
        new_channel = None
        for channel, keywords in channel_keywords.items():
            for keyword in keywords:
                if keyword in title or keyword in summary:
                    new_channel = channel
                    break
            if new_channel:
                break
        
        # 如果没有匹配到，随机分配
        if not new_channel:
            new_channel = random.choice(target_channels)
        
        # 更新数据库
        sql = "UPDATE news SET channel = %s WHERE id = %s"
        cursor.execute(sql, (new_channel, news_id))
        updated_count += 1
        
        if updated_count % 10 == 0:
            print(f"  已更新 {updated_count} 条...")
    
    conn.commit()
    print(f"\n✓ 成功重新分配 {updated_count} 条新闻!")
    
    # 查看新的频道分布
    print("\n新的频道分布:")
    cursor.execute("SELECT channel, COUNT(*) as count FROM news GROUP BY channel")
    for row in cursor:
        count = row[1]
        status = "✓" if count > 0 else "✗"
        print(f"  {status} {row[0]}: {count} 条")
    
    print("\n" + "=" * 70)
    print("分配完成!")
    print("=" * 70)
    
except Exception as e:
    print(f"\n✗ 错误: {e}")
    if conn:
        conn.rollback()
finally:
    if conn:
        conn.close()
