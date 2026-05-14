import requests

base_url = "http://192.168.146.128"

# 1. 测试基本连接
print("=" * 50)
print("测试基本连接...")
try:
    response = requests.get(f"{base_url}/api/news", timeout=5)
    print(f"GET /api/news - Status: {response.status_code}")
    if response.status_code == 200:
        data = response.json()
        print(f"新闻数量: {len(data.get('data', []))}")
except Exception as e:
    print(f"Error: {str(e)}")

# 2. 尝试调用更新摘要接口
print("\n" + "=" * 50)
print("尝试更新摘要...")
try:
    response = requests.post(f"{base_url}/api/tools/update-summaries", timeout=10)
    print(f"POST /api/tools/update-summaries - Status: {response.status_code}")
    print(f"Response: {response.text}")
except Exception as e:
    print(f"Error: {str(e)}")

# 3. 尝试调用抓取新闻接口
print("\n" + "=" * 50)
print("尝试抓取新闻...")
try:
    response = requests.post(f"{base_url}/api/tools/fetch-news", timeout=30)
    print(f"POST /api/tools/fetch-news - Status: {response.status_code}")
    print(f"Response: {response.text}")
except Exception as e:
    print(f"Error: {str(e)}")

# 4. 测试登录功能
print("\n" + "=" * 50)
print("测试登录功能...")
try:
    response = requests.post(
        f"{base_url}/api/users/login",
        json={"username": "admin", "password": "123456"},
        timeout=10
    )
    print(f"POST /api/users/login - Status: {response.status_code}")
    print(f"Response: {response.text}")
    
    if response.status_code == 200:
        result = response.json()
        if result.get("success"):
            print("\n✓ 登录成功!")
            print(f"  Username: {result['data']['username']}")
        else:
            print(f"\n✗ 登录失败: {result.get('error')}")
except Exception as e:
    print(f"Error: {str(e)}")

print("\n" + "=" * 50)
print("测试完成")