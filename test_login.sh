#!/bin/bash
echo "=== Test Login ==="
curl -s -X POST http://localhost:8090/api/users/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'
echo ""
echo "=== Test Register ==="
curl -s -X POST http://localhost:8090/api/users/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"newuser","password":"pass123","email":"new@test.com"}'
echo ""
echo "=== Test Login (test) ==="
curl -s -X POST http://localhost:8090/api/users/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"test","password":"test123"}'
