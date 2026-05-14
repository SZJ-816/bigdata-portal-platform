#!/bin/bash
# 重新构建前端Docker镜像的脚本

cd /path/to/bigdata-portal-platform

echo "停止nginx容器..."
docker-compose stop nginx

echo "删除旧nginx容器..."
docker-compose rm -f nginx

echo "重新构建并启动nginx..."
docker-compose up -d nginx

echo "等待服务启动..."
sleep 5

echo "检查nginx日志..."
docker-compose logs nginx | tail -20

echo "测试前端页面..."
curl -I http://localhost/

echo "构建完成!"
