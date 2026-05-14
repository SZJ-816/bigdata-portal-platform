import requests

base_url = "http://192.168.146.128"

# 测试频道API
print("=" * 60)
print("测试频道API返回的新闻数据")
print("=" * 60)

# 获取所有新闻
response = requests.get(f"{base_url}/api/news")
if response.status_code == 200:
    result = response.json()
    news_list = result.get('data', [])
    print(f"\n总新闻数量: {len(news_list)}")
    
    # 按频道分组显示
    channels = {}
    for news in news_list:
        channel = news.get('channel', 'Unknown')
        if channel not in channels:
            channels[channel] = []
        channels[channel].append({
            'id': news.get('id'),
            'title': news.get('title', '')[:30],
            'channel': channel
        })
    
    print("\n各频道新闻分布:")
    for channel, news_list in channels.items():
        print(f"\n【{channel}】- {len(news_list)} 条")
        for news in news_list[:3]:
            print(f"  ID: {news['id']} - {news['title']}...")

# 测试特定频道
print("\n" + "=" * 60)
print("测试 AI 频道数据")
print("=" * 60)

response = requests.get(f"{base_url}/api/news?channel=AI")
if response.status_code == 200:
    result = response.json()
    news_list = result.get('data', [])
    print(f"\nAI频道新闻数量: {len(news_list)}")
    for news in news_list[:5]:
        print(f"  ID: {news.get('id')} - {news.get('title', '')[:40]}...")

# 测试新闻详情API
print("\n" + "=" * 60)
print("测试新闻详情API")
print("=" * 60)

# 获取一个新闻ID
response = requests.get(f"{base_url}/api/news")
if response.status_code == 200:
    news_list = response.json().get('data', [])
    if news_list:
        first_news = news_list[0]
        news_id = first_news.get('id')
        print(f"\n测试新闻 ID: {news_id}")
        
        response = requests.get(f"{base_url}/api/news/{news_id}")
        print(f"GET /api/news/{news_id} - Status: {response.status_code}")
        if response.status_code == 200:
            result = response.json()
            if result.get('success'):
                print("✓ 新闻详情获取成功")
                print(f"  标题: {result['data'].get('title', '')[:40]}...")
            else:
                print(f"✗ 新闻不存在: {result.get('message')}")
        else:
            print(f"✗ 请求失败")

print("\n" + "=" * 60)
print("测试完成")
print("=" * 60)