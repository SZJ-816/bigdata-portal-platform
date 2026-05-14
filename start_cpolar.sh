#!/bin/bash
pkill -9 -f cpolar 2>/dev/null
sleep 2
rm -f /root/cpolar_output.log
nohup /root/cpolar http 80 -region=cn -log=stdout > /root/cpolar_output.log 2>&1 &
echo "cpolar started, waiting..."
sleep 12
echo "=== Log ==="
cat /root/cpolar_output.log
