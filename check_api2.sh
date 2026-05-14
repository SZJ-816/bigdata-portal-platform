#!/bin/bash
curl -s http://localhost:8090/api/news | docker exec -i bigdata-mysql mysql -uroot -proot123 -N -e "SELECT 1" 2>/dev/null || true
curl -s http://localhost:8090/api/news | grep -o '"imageUrl":"[^"]*"' | head -10
