#!/bin/bash
echo "Starting cpolar with China region..."
cd /root
nohup /root/cpolar http 80 -region=cn -log=stdout > /root/cpolar_output.log 2>&1 &
CPOLAR_PID=$!
echo "CPolar started with PID: $CPOLAR_PID"
echo "Waiting 10 seconds..."
sleep 10
echo ""
echo "===== CPolar Output ====="
cat /root/cpolar_output.log
