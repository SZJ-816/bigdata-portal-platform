#!/bin/bash
echo "=== 测试真实图片URL ==="
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/news/77eaac62e704e8450ce22ac1024586cd.png
echo " - news image"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/channel-ai.svg
echo " - channel-ai.svg"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/channel-bigdata.svg
echo " - channel-bigdata.svg"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/channel-cloud.svg
echo " - channel-cloud.svg"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/channel-hardware.svg
echo " - channel-hardware.svg"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/channel-blockchain.svg
echo " - channel-blockchain.svg"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/channel-digital.svg
echo " - channel-digital.svg"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/channel-internet.svg
echo " - channel-internet.svg"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/channel-auto.svg
echo " - channel-auto.svg"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/channel-startup.svg
echo " - channel-startup.svg"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost:8090/images/channel-security.svg
echo " - channel-security.svg"

echo ""
echo "=== 前端图片 ==="
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost/images/channel-ai.svg
echo " - frontend channel-ai.svg"
curl -s -o /dev/null -w "HTTP %{http_code}" http://localhost/images/news/77eaac62e704e8450ce22ac1024586cd.png
echo " - frontend news image"
