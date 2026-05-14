import requests

base_url = "http://192.168.146.128"

# 测试登录功能
print("=" * 50)
print("1. 测试登录功能")
print("=" * 50)

# admin登录
response = requests.post(
    f"{base_url}/api/users/login",
    json={"username": "admin", "password": "123456"}
)
result = response.json()
if result.get("success"):
    print("✓ admin 登录成功!")
    token = result['data']['token']
    print(f"  Token: {token[:50]}...")
    
    # 测试收藏功能
    print("\n" + "=" * 50)
    print("2. 测试收藏功能")
    print("=" * 50)
    
    headers = {"Authorization": f"Bearer {token}"}
    
    # 添加收藏
    response = requests.post(
        f"{base_url}/api/users/favorites/64",
        headers=headers
    )
    print(f"POST /api/users/favorites/64 - Status: {response.status_code}")
    print(f"Response: {response.text}")
    
    # 获取收藏列表
    response = requests.get(
        f"{base_url}/api/users/favorites",
        headers=headers
    )
    print(f"\nGET /api/users/favorites - Status: {response.status_code}")
    if response.status_code == 200:
        fav_result = response.json()
        print(f"收藏数量: {len(fav_result.get('data', []))}")
    
    # 测试浏览历史
    print("\n" + "=" * 50)
    print("3. 测试浏览历史")
    print("=" * 50)
    
    # 添加浏览历史
    response = requests.post(
        f"{base_url}/api/users/history",
        headers=headers,
        json={"newsId": 64, "duration": 30}
    )
    print(f"POST /api/users/history - Status: {response.status_code}")
    print(f"Response: {response.text}")
    
    # 获取浏览历史
    response = requests.get(
        f"{base_url}/api/users/history",
        headers=headers
    )
    print(f"\nGET /api/users/history - Status: {response.status_code}")
    if response.status_code == 200:
        hist_result = response.json()
        print(f"历史记录数量: {len(hist_result.get('data', []))}")
        for item in hist_result.get('data', [])[:3]:
            print(f"  - News ID: {item.get('newsId')}, Duration: {item.get('duration')}s")
else:
    print(f"✗ admin 登录失败: {result.get('error')}")

# 测试test用户登录
print("\n" + "=" * 50)
print("4. 测试test用户登录")
print("=" * 50)

response = requests.post(
    f"{base_url}/api/users/login",
    json={"username": "test", "password": "123456"}
)
result = response.json()
if result.get("success"):
    print("✓ test 用户登录成功!")
else:
    print(f"✗ test 用户登录失败: {result.get('error')}")

print("\n" + "=" * 50)
print("所有测试完成!")
print("=" * 50)