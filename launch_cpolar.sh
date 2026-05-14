#!/bin/bash
pkill -9 -f cpolar 2>/dev/null
sleep 1
rm -f /root/cpolar_output.log
nohup /root/cpolar http 80 -region=cn > /root/cpolar_output.log 2>&1 &
