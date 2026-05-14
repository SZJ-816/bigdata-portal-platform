#!/bin/bash

# 大数据门户 - 前端重新部署脚本
echo "=========================================="
echo "开始重新部署前端服务..."
echo "=========================================="

# 检查是否在项目根目录
if [ ! -f "docker-compose.yml" ]; then
    echo "错误: 未找到 docker-compose.yml，请在项目根目录运行此脚本"
    exit 1
fi

# 1. 停止前端容器
echo "[1/4] 停止前端容器..."
docker-compose stop frontend

# 2. 删除旧的前端容器和镜像
echo "[2/4] 清理旧容器和镜像..."
docker-compose rm -f frontend
docker images | grep bigdata-portal-frontend | awk '{print $3}' | xargs -r docker rmi -f

# 3. 重新构建并启动前端
echo "[3/4] 重新构建并启动前端..."
docker-compose up -d --build frontend

# 4. 检查服务状态
echo "[4/4] 检查服务状态..."
sleep 5
docker-compose ps

echo ""
echo "=========================================="
echo "部署完成！"
echo "=========================================="
echo ""
echo "查看前端日志："
echo "  docker-compose logs -f frontend"
echo ""
echo "访问地址："
echo "  http://192.168.146.128"
echo ""
