import requests

base_url = "http://192.168.146.128"

print("验证各个频道API是否正常")
print("=" * 60)

# 模拟可能的URL参数
test_cases = [
    ('AI', 28),
    ('人工智能', 28),
    ('大数据', 10), 
    ('云计算', 4),
    ('互联网', 6),
    ('硬件', 8),
    ('创业', 16),
]

print("频道 API 测试结果:")
for param, expected in test_cases:
    response = requests.get(f"{base_url}/api/news?channel={param}")
    count = len(response.json().get('data', []))
    status = "✓" if count > 0 else "✗"
    print(f"  {status} channel='{param}': {count} 条新闻")

print("\n" + "=" * 60)
print("提示: 现在前端代码已更新，会把 '人工智能' 自动映射到 'AI'")
