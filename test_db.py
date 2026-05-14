import requests

base_url = "http://192.168.146.128"

# 检查数据库中新闻的频道名称
print("=" * 60)
print("检查API返回的新闻频道名称")
print("=" * 60)

# 获取一些新闻详情，看看频道名称
response = requests.get(f"{base_url}/api/news?channel=AI")
if response.status_code == 200:
    result = response.json()
    news_list = result.get('data', [])
    print(f"\nAI频道新闻数量: {len(news_list)}")
    
    if news_list:
        # 检查第一条新闻的完整字段
        first_news = news_list[0]
        print(f"\n第一条新闻详情:")
        print(f"  ID: {first_news.get('id')}")
        print(f"  Title: {first_news.get('title', '')[:40]}...")
        print(f"  Channel: {first_news.get('channel')}")
        print(f"  Source: {first_news.get('source')}")

# 测试各个频道
print("\n" + "=" * 60)
print("测试各频道API")
print("=" * 60)

test_channels = ['AI', '人工智能', '大数据', '云计算', '互联网', '硬件', '创业']

for ch in test_channels:
    response = requests.get(f"{base_url}/api/news?channel={ch}")
    if response.status_code == 200:
        result = response.json()
        count = len(result.get('data', []))
        print(f"  {ch}: {count} 条新闻")
    else:
        print(f"  {ch}: 请求失败 (Status: {response.status_code})")

print("\n" + "=" * 60)
print("测试完成")
print("=" * 60)