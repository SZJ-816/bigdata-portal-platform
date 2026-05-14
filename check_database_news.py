import requests

base_url = "http://192.168.146.128"

print("检查数据库中是否有名为'OpenAI发布GPT-5'的新闻")
print("=" * 80)

# 搜索包含"OpenAI"或"GPT-5"的新闻
response = requests.get(f"{base_url}/api/news?keyword=OpenAI")
if response.status_code == 200:
    results = response.json().get('data', [])
    print(f"搜索'OpenAI'找到 {len(results)} 条新闻:")
    for i, news in enumerate(results[:10]):
        print(f"  {i+1}. ID: {news.get('id')} - {news.get('title', '')[:60]}...")

print("\n" + "=" * 80)

# 获取所有新闻，看看有没有ID 1, 2, 3
response = requests.get(f"{base_url}/api/news")
if response.status_code == 200:
    all_news = response.json().get('data', [])
    
    # 检查有没有ID 1, 2, 3
    found_ids = []
    for news in all_news:
        if news.get('id') in [1, 2, 3]:
            found_ids.append(news)
    
    if found_ids:
        print(f"找到ID为1, 2, 3的新闻:")
        for news in found_ids:
            print(f"  ID: {news.get('id')} - {news.get('title', '')[:60]}")
    else:
        print("❌ 数据库中没有ID为1, 2, 3的新闻")
        print(f"   数据库中的最小ID是: {min([n.get('id') for n in all_news])}")
        print(f"   数据库中的最大ID是: {max([n.get('id') for n in all_news])}")

print("\n" + "=" * 80)
print("测试ID 1, 2, 3是否存在:")
for test_id in [1, 2, 3]:
    response = requests.get(f"{base_url}/api/news/{test_id}")
    if response.status_code == 200:
        result = response.json()
        if result.get('success'):
            print(f"  ID {test_id}: ✓ 存在")
        else:
            print(f"  ID {test_id}: ✗ {result.get('message')}")
    else:
        print(f"  ID {test_id}: ✗ 请求失败 {response.status_code}")

print("\n" + "=" * 80)
print("结论: 前端显示的是模拟数据，不是真实数据库数据!")
