#!/bin/bash
pkill -9 -f cpolar
sleep 2
rm -f /root/cpolar_output.log
nohup /root/cpolar http 80 -region=cn -log=stdout > /root/cpolar_output.log 2>&1 &
echo "Waiting 12 seconds..."
sleep 12
echo "=== Tunnel URLs ==="
strings /root/cpolar_output.log | grep "Tunnel established" | tail -4
