import requests
import json

base_url = "http://192.168.146.128"

print("测试API对不同频道名称的响应")
print("=" * 80)

test_channels = [
    'AI', 
    '人工智能', 
    '大数据', 
    '云计算', 
    '互联网', 
    '硬件', 
    '创业'
]

for channel in test_channels:
    response = requests.get(f"{base_url}/api/news?channel={channel}")
    if response.status_code == 200:
        result = response.json()
        news_list = result.get('data', [])
        print(f"channel='{channel}': {len(news_list)} 条新闻")
    else:
        print(f"channel='{channel}': 请求失败 {response.status_code}")

print("\n" + "=" * 80)
print("检查数据库中实际存储的频道名称")
print("=" * 80)

response = requests.get(f"{base_url}/api/news")
if response.status_code == 200:
    all_news = response.json().get('data', [])
    print(f"总新闻数: {len(all_news)}")
    
    channels_found = set()
    for news in all_news:
        channels_found.add(news.get('channel'))
    
    print(f"数据库中实际的频道值: {sorted(list(channels_found))}")
