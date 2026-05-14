import requests

base_url = "http://192.168.146.128"

print("检查首页新闻列表的ID")
print("=" * 80)

response = requests.get(f"{base_url}/api/news")
if response.status_code == 200:
    all_news = response.json().get('data', [])
    print(f"首页返回的新闻数量: {len(all_news)}\n")
    
    print("前20条新闻的ID和标题:")
    for i, news in enumerate(all_news[:20]):
        news_id = news.get('id')
        title = news.get('title', '')[:50]
        print(f"  {i+1}. ID: {news_id} - {title}...")
    
    print("\n" + "=" * 80)
    print("检查前10条新闻的详情是否存在:")
    
    for i, news in enumerate(all_news[:10]):
        news_id = news.get('id')
        title = news.get('title', '')[:40]
        
        response = requests.get(f"{base_url}/api/news/{news_id}")
        if response.status_code == 200:
            result = response.json()
            if result.get('success'):
                print(f"  {i+1}. ID {news_id}: ✓ 存在")
            else:
                print(f"  {i+1}. ID {news_id}: ✗ {result.get('message')}")
                print(f"     标题: {title}")
        else:
            print(f"  {i+1}. ID {news_id}: ✗ 请求失败 {response.status_code}")
