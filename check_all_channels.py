import requests

base_url = "http://192.168.146.128"

print("=" * 70)
print("检查所有频道的新闻情况")
print("=" * 70)

# 前端支持的频道
front_channels = [
    {'name': 'AI', 'label': '人工智能'},
    {'name': '大数据', 'label': '大数据'},
    {'name': '云计算', 'label': '云计算'},
    {'name': '互联网', 'label': '互联网'},
    {'name': '硬件', 'label': '硬件'},
    {'name': '创业', 'label': '创业'},
]

print("\n各频道新闻数量:")
print("-" * 50)

all_channel_data = {}
for ch in front_channels:
    response = requests.get(f"{base_url}/api/news?channel={ch['name']}")
    if response.status_code == 200:
        result = response.json()
        count = len(result.get('data', []))
        all_channel_data[ch['name']] = count
        status = "✓" if count > 0 else "✗"
        print(f"  {status} {ch['label']} ({ch['name']}): {count} 条")
    else:
        print(f"  ✗ {ch['label']} ({ch['name']}): 请求失败")

print("\n" + "=" * 70)
print("检查首页新闻列表")
print("=" * 70)

response = requests.get(f"{base_url}/api/news")
if response.status_code == 200:
    result = response.json()
    news_list = result.get('data', [])
    print(f"\n首页新闻总数: {len(news_list)}")
    
    if news_list:
        print("\n前5条新闻:")
        for i, news in enumerate(news_list[:5]):
            print(f"  {i+1}. [{news.get('channel')}] {news.get('title', '')[:50]}...")

print("\n" + "=" * 70)
print("检查数据库中所有新闻的频道分布")
print("=" * 70)

# 获取所有新闻并统计
if news_list:
    channel_counts = {}
    for news in news_list:
        ch = news.get('channel', 'Unknown')
        channel_counts[ch] = channel_counts.get(ch, 0) + 1
    
    print("\n数据库中所有新闻的频道分布:")
    for ch, count in sorted(channel_counts.items()):
        print(f"  {ch}: {count} 条")

print("\n" + "=" * 70)
print("测试新闻详情页面")
print("=" * 70)

if news_list:
    test_news = news_list[0]
    news_id = test_news.get('id')
    response = requests.get(f"{base_url}/api/news/{news_id}")
    if response.status_code == 200:
        result = response.json()
        if result.get('success'):
            print(f"\n✓ 新闻ID {news_id} 存在")
            print(f"  标题: {result['data'].get('title', '')[:50]}...")
            print(f"  频道: {result['data'].get('channel')}")
        else:
            print(f"\n✗ 新闻ID {news_id}: {result.get('message')}")

print("\n" + "=" * 70)
print("测试完成")
print("=" * 70)