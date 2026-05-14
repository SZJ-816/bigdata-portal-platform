import requests

base_url = "http://192.168.146.128"

print("测试新闻详情API")
print("=" * 80)

# 1. 获取首页新闻列表
response = requests.get(f"{base_url}/api/news")
all_news = []
if response.status_code == 200:
    all_news = response.json().get('data', [])
    print(f"✓ 获取到 {len(all_news)} 条新闻")

if not all_news:
    print("✗ 没有获取到新闻，退出测试")
    exit(1)

# 2. 测试前5条新闻的详情
print("\n测试新闻详情:")
for i, news in enumerate(all_news[:5]):
    news_id = news.get('id')
    title = news.get('title', '')[:40]
    response = requests.get(f"{base_url}/api/news/{news_id}")
    
    if response.status_code == 200:
        result = response.json()
        if result.get('success') and result.get('data'):
            print(f"  {i+1}. ID {news_id}: ✓ 正常")
        else:
            print(f"  {i+1}. ID {news_id}: ✗ {result.get('message')}")
            print(f"     标题: {title}")
    else:
        print(f"  {i+1}. ID {news_id}: ✗ 请求失败 {response.status_code}")

# 3. 查看一条新闻的详情数据
if all_news:
    print("\n" + "=" * 80)
    print("查看一条新闻详情完整数据:")
    news_id = all_news[0].get('id')
    response = requests.get(f"{base_url}/api/news/{news_id}")
    if response.status_code == 200:
        print(f"完整响应:")
        print(response.text)
