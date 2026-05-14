import requests

base_url = "http://192.168.146.128"

print("=" * 60)
print("测试新闻ID是否正确")
print("=" * 60)

# 获取所有新闻（首页用）
response = requests.get(f"{base_url}/api/news")
if response.status_code == 200:
    all_news = response.json().get('data', [])
    print(f"\n首页新闻总数: {len(all_news)}")
    
    # 测试前5条新闻的详情
    print("\n测试首页前5条新闻的详情:")
    for i, news in enumerate(all_news[:5]):
        news_id = news.get('id')
        title = news.get('title', '')[:30]
        
        # 获取详情
        detail_response = requests.get(f"{base_url}/api/news/{news_id}")
        if detail_response.status_code == 200:
            result = detail_response.json()
            if result.get('success'):
                print(f"  {i+1}. ID {news_id}: ✓ 存在")
            else:
                print(f"  {i+1}. ID {news_id}: ✗ {result.get('message')}")
        else:
            print(f"  {i+1}. ID {news_id}: ✗ 请求失败")

# 获取AI频道的新闻
response = requests.get(f"{base_url}/api/news?channel=AI")
if response.status_code == 200:
    ai_news = response.json().get('data', [])
    print(f"\nAI频道新闻数量: {len(ai_news)}")
    
    # 测试前5条新闻的详情
    print("\n测试AI频道前5条新闻的详情:")
    for i, news in enumerate(ai_news[:5]):
        news_id = news.get('id')
        title = news.get('title', '')[:30]
        
        # 获取详情
        detail_response = requests.get(f"{base_url}/api/news/{news_id}")
        if detail_response.status_code == 200:
            result = detail_response.json()
            if result.get('success'):
                print(f"  {i+1}. ID {news_id}: ✓ 存在")
            else:
                print(f"  {i+1}. ID {news_id}: ✗ {result.get('message')}")
        else:
            print(f"  {i+1}. ID {news_id}: ✗ 请求失败")

# 测试大数据频道
response = requests.get(f"{base_url}/api/news?channel=大数据")
if response.status_code == 200:
    bigdata_news = response.json().get('data', [])
    print(f"\n大数据频道新闻数量: {len(bigdata_news)}")
    
    # 测试前5条新闻的详情
    print("\n测试大数据频道前5条新闻的详情:")
    for i, news in enumerate(bigdata_news[:5]):
        news_id = news.get('id')
        title = news.get('title', '')[:30]
        
        # 获取详情
        detail_response = requests.get(f"{base_url}/api/news/{news_id}")
        if detail_response.status_code == 200:
            result = detail_response.json()
            if result.get('success'):
                print(f"  {i+1}. ID {news_id}: ✓ 存在")
            else:
                print(f"  {i+1}. ID {news_id}: ✗ {result.get('message')}")
        else:
            print(f"  {i+1}. ID {news_id}: ✗ 请求失败")

print("\n" + "=" * 60)
print("测试完成")
print("=" * 60)