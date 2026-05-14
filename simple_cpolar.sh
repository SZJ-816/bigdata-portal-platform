#!/bin/bash
pkill -9 -f cpolar 2>/dev/null
sleep 2
echo "Starting cpolar..."
nohup /root/cpolar start-all > /root/cpolar.log 2>&1 &
CPOLAR_PID=$!
echo $CPOLAR_PID > /root/cpolar.pid
sleep 5
echo ""
echo "=== CPolar Status ==="
ps aux | grep cpolar | grep -v grep
echo ""
echo "=== CPolar Output ==="
tail -30 /root/cpolar.log
