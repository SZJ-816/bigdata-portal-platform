#!/bin/bash
# cpolar启动脚本

# 清理旧的进程
pkill -9 -f cpolar
sleep 2

# 启动cpolar隧道
echo "Starting cpolar..."
cd /root

# 启动前端隧道
nohup /root/cpolar http -subdomain=bigdata-portal 80 > /root/cpolar.log 2>&1 &
CPOLAR_PID=$!
echo $CPOLAR_PID > /root/cpolar.pid
sleep 3

# 检查进程
ps aux | grep cpolar

# 等待日志
echo "Waiting 5 seconds..."
sleep 5
echo ""
echo "---- cpolar output:"
cat /root/cpolar.log
