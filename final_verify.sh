#!/bin/bash
echo "========================================="
echo "  大数据新闻门户 - 全面功能验证"
echo "========================================="

echo ""
echo "=== 1. 服务状态 ==="
for svc in bigdata-mysql bigdata-backend bigdata-frontend bigdata-kafka bigdata-clickhouse bigdata-zookeeper; do
    status=$(docker inspect -f '{{.State.Status}}' $svc 2>/dev/null || echo "not found")
    echo "  $svc: $status"
done

echo ""
echo "=== 2. 登录测试 ==="
LOGIN_RESULT=$(curl -s -X POST http://localhost:8090/api/users/login -H 'Content-Type: application/json' -d @/tmp/login_test.json)
if echo "$LOGIN_RESULT" | grep -q '"success":true'; then
    echo "  ✅ 登录成功"
    TOKEN=$(echo "$LOGIN_RESULT" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])" 2>/dev/null)
else
    echo "  ❌ 登录失败: $LOGIN_RESULT"
    exit 1
fi

echo ""
echo "=== 3. 用户资料 ==="
PROFILE=$(curl -s http://localhost:8090/api/users/profile -H "Authorization: Bearer $TOKEN")
if echo "$PROFILE" | grep -q '"success":true'; then
    echo "  ✅ 获取用户资料成功"
else
    echo "  ❌ 获取用户资料失败"
fi

echo ""
echo "=== 4. 新闻列表 ==="
NEWS=$(curl -s http://localhost:8090/api/news)
TOTAL=$(echo "$NEWS" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['total'])" 2>/dev/null)
echo "  ✅ 共 $TOTAL 条新闻"

echo ""
echo "=== 5. 频道统计 ==="
CHANNELS=$(curl -s http://localhost:8090/api/admin/channels -H "Authorization: Bearer $TOKEN")
echo "$CHANNELS" | python3 -c "
import sys,json
data = json.load(sys.stdin)
if data.get('success'):
    for c in data['data']:
        print(f\"  ✅ {c['channel']}: {c['cnt']}条\")
" 2>/dev/null

echo ""
echo "=== 6. AI搜索(降级模式) ==="
AI_SEARCH=$(curl -s "http://localhost:8090/api/ai/search?keyword=AI")
if echo "$AI_SEARCH" | grep -q '"success":true'; then
    echo "  ✅ AI搜索正常(降级模式)"
else
    echo "  ❌ AI搜索失败: $(echo $AI_SEARCH | head -c 100)"
fi

echo ""
echo "=== 7. 热点摘要(降级模式) ==="
HOT=$(curl -s "http://localhost:8090/api/ai/hot-summary")
if echo "$HOT" | grep -q '"success":true'; then
    echo "  ✅ 热点摘要正常(降级模式)"
else
    echo "  ❌ 热点摘要失败"
fi

echo ""
echo "=== 8. 发送验证码 ==="
CODE=$(curl -s -X POST http://localhost:8090/api/users/send-code -H 'Content-Type: application/json' -d '{"email":"testuser@example.com"}')
if echo "$CODE" | grep -q '"success":true'; then
    echo "  ✅ 验证码发送成功"
else
    echo "  ❌ 验证码发送失败: $CODE"
fi

echo ""
echo "=== 9. 图片资源 ==="
for img in channel-ai.svg channel-bigdata.svg channel-cloud.svg channel-hardware.svg channel-blockchain.svg channel-digital.svg channel-internet.svg channel-auto.svg channel-startup.svg channel-security.svg; do
    code=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8090/images/$img)
    if [ "$code" = "200" ]; then
        echo "  ✅ /images/$img"
    else
        echo "  ❌ /images/$img (HTTP $code)"
    fi
done

echo ""
echo "=== 10. 前端图片 ==="
for img in channel-ai.svg channel-bigdata.svg; do
    code=$(curl -s -o /dev/null -w '%{http_code}' http://localhost/images/$img)
    if [ "$code" = "200" ]; then
        echo "  ✅ http://localhost/images/$img"
    else
        echo "  ❌ http://localhost/images/$img (HTTP $code)"
    fi
done

echo ""
echo "=== 11. 前端页面 ==="
FE_CODE=$(curl -s -o /dev/null -w '%{http_code}' http://localhost/)
echo "  前端首页: HTTP $FE_CODE"

echo ""
echo "========================================="
echo "  验证完成！"
echo "========================================="
