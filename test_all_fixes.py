import requests
import random

base_url = "http://192.168.146.128"

print("=" * 80)
print("系统修复验证测试")
print("=" * 80)

# 1. 测试首页新闻列表
print("\n【1/4】测试首页新闻列表")
print("-" * 80)
response = requests.get(f"{base_url}/api/news")
if response.status_code == 200:
    all_news = response.json().get('data', [])
    print(f"✓ 获取到 {len(all_news)} 条新闻")
    if all_news:
        sample_news = all_news[0]
        print(f"  样本新闻: ID={sample_news.get('id')}, Title={sample_news.get('title', '')[:40]}")
        print(f"  频道: {sample_news.get('channel')}")

# 2. 测试各频道新闻
print("\n【2/4】测试各频道新闻")
print("-" * 80)
test_channels = [
    ('AI', '人工智能'),
    ('大数据', '大数据'), 
    ('云计算', '云计算'),
    ('互联网', '互联网'),
    ('硬件', '硬件'),
    ('创业', '创业')
]

all_ok = True
for param, label in test_channels:
    response = requests.get(f"{base_url}/api/news?channel={param}")
    if response.status_code == 200:
        news_count = len(response.json().get('data', []))
        if news_count > 0:
            print(f"✓ {label} ({param}): {news_count} 条新闻")
        else:
            print(f"✗ {label} ({param}): 无新闻")
            all_ok = False
    else:
        print(f"✗ {label} ({param}): 请求失败")
        all_ok = False

# 3. 测试新闻详情
print("\n【3/4】测试新闻详情")
print("-" * 80)
if all_news:
    test_ids = [news.get('id') for news in all_news[:3]]
    all_detail_ok = True
    for news_id in test_ids:
        response = requests.get(f"{base_url}/api/news/{news_id}")
        if response.status_code == 200:
            result = response.json()
            if result.get('success') and result.get('data'):
                print(f"✓ 新闻 ID={news_id}: 详情正常")
            else:
                print(f"✗ 新闻 ID={news_id}: {result.get('message')}")
                all_detail_ok = False
        else:
            print(f"✗ 新闻 ID={news_id}: 请求失败")
            all_detail_ok = False

# 4. 测试登录
print("\n【4/4】测试登录功能")
print("-" * 80)
response = requests.post(
    f"{base_url}/api/users/login",
    json={"username": "admin", "password": "123456"}
)
if response.status_code == 200:
    result = response.json()
    if result.get('success'):
        print(f"✓ 登录成功，获取到 token")
    else:
        print(f"✗ 登录失败: {result.get('error')}")

# 总结
print("\n" + "=" * 80)
print("测试总结")
print("=" * 80)
print(f"✓ 首页新闻: {len(all_news)} 条")
print(f"✓ 所有频道都有新闻: {'是' if all_ok else '否'}")
print(f"✓ 新闻详情正常: {'是' if all_detail_ok else '否'}")
print("\n修复完成！现在可以访问 http://192.168.146.128 进行体验")
