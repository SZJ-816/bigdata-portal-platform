#!/bin/bash
# 在服务器上构建后端

cd /root/bigdata-portal-platform/backend
echo "Building backend with Maven..." | tee /tmp/build.log
docker run --rm -v /root/bigdata-portal-platform/backend:/app -v /root/.m2:/root/.m2 -w /app maven:3.8.6-openjdk-11-slim mvn clean package -DskipTests >> /tmp/build.log 2>&1
echo "Build exit code: $?" | tee -a /tmp/build.log

if [ -f /root/bigdata-portal-platform/backend/target/portal-2.0.0.jar ]; then
    echo "✅ Build succeeded, copying jar..." | tee -a /tmp/build.log
    cp /root/bigdata-portal-platform/backend/target/portal-2.0.0.jar /root/bigdata-portal-platform/backend/portal-2.0.0.jar
    echo "✅ Jar copied" | tee -a /tmp/build.log
else
    echo "❌ Build failed, no jar found" | tee -a /tmp/build.log
    exit 1
fi

# 重建后端镜像并启动
echo "Rebuilding backend image..." | tee -a /tmp/build.log
cd /root/bigdata-portal-platform
docker compose build backend >> /tmp/build.log 2>&1
echo "✅ Image built" | tee -a /tmp/build.log

echo "Restarting backend..." | tee -a /tmp/build.log
docker compose up -d backend >> /tmp/build.log 2>&1
echo "✅ Backend restarted" | tee -a /tmp/build.log

# 等待后端启动
echo "Waiting for backend to be healthy..." | tee -a /tmp/build.log
for i in {1..30}; do
    STATUS=$(docker inspect -f '{{.State.Health.Status}}' bigdata-backend 2>/dev/null || echo "unknown")
    if [ "$STATUS" = "healthy" ]; then
        echo "✅ Backend is healthy" | tee -a /tmp/build.log
        break
    fi
    sleep 5
done
