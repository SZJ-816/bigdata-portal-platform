#!/bin/bash
echo "=== AI ==="
curl -s 'http://localhost:8090/api/news?channel=AI' | grep -o '"imageUrl":"[^"]*"' | head -2
echo ""
echo "=== 大数据 ==="
curl -s 'http://localhost:8090/api/news?channel=大数据' | grep -o '"imageUrl":"[^"]*"' | head -2
echo ""
echo "=== 云计算 ==="
curl -s 'http://localhost:8090/api/news?channel=云计算' | grep -o '"imageUrl":"[^"]*"' | head -2
echo ""
echo "=== 硬件 ==="
curl -s 'http://localhost:8090/api/news?channel=硬件' | grep -o '"imageUrl":"[^"]*"' | head -2
