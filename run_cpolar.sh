#!/bin/bash
pkill -9 -f cpolar 2>/dev/null
sleep 2

# Start frontend tunnel
nohup /root/cpolar http -subdomain=bigdata-portal 80 > /root/cpolar_frontend.log 2>&1 < /dev/null &
echo $! > /root/cpolar_frontend.pid
sleep 3

# Start backend tunnel  
nohup /root/cpolar http -subdomain=bigdata-api 8090 > /root/cpolar_backend.log 2>&1 < /dev/null &
echo $! > /root/cpolar_backend.pid
sleep 3

echo "======= CPolar Status ======="
ps aux | grep cpolar
echo ""
echo "======= Frontend Log ======="
cat /root/cpolar_frontend.log
echo ""
echo "======= Backend Log ======="
cat /root/cpolar_backend.log
