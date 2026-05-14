import requests

base_url = "http://192.168.146.128"

print("检查新闻详情页问题")
print("=" * 80)

# 1. 获取首页新闻列表
response = requests.get(f"{base_url}/api/news")
all_news = []
if response.status_code == 200:
    all_news = response.json().get('data', [])
    print(f"✓ 获取到 {len(all_news)} 条新闻\n")

if not all_news:
    print("✗ 没有获取到新闻，退出")
    exit(1)

# 2. 测试前10条新闻的详情
print("测试新闻详情API:")
for i, news in enumerate(all_news[:10]):
    news_id = news.get('id')
    title = news.get('title', '')[:40]
    
    response = requests.get(f"{base_url}/api/news/{news_id}")
    
    if response.status_code == 200:
        result = response.json()
        if result.get('success') and result.get('data'):
            print(f"  {i+1}. ID {news_id}: ✓ 正常 - {title}...")
        else:
            print(f"  {i+1}. ID {news_id}: ✗ {result.get('message')}")
            print(f"     原始响应: {response.text[:200]}")
    else:
        print(f"  {i+1}. ID {news_id}: ✗ 请求失败 {response.status_code}")

# 3. 查看一条完整新闻详情
print("\n" + "=" * 80)
print("查看完整新闻详情响应:")
if all_news:
    news_id = all_news[0].get('id')
    print(f"\n新闻ID: {news_id}")
    response = requests.get(f"{base_url}/api/news/{news_id}")
    print(f"状态码: {response.status_code}")
    print(f"响应内容:")
    import json
    print(json.dumps(response.json(), indent=2, ensure_ascii=False))
