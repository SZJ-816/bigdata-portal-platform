#!/bin/bash
# Clean up duplicate maven containers
DUP_IDS=$(docker ps -q --filter ancestor=maven:3.8.6-openjdk-11-slim | tail -n +2)
if [ -n "$DUP_IDS" ]; then
    echo "Killing duplicate maven containers..."
    docker kill $DUP_IDS 2>/dev/null || true
    docker rm $DUP_IDS 2>/dev/null || true
fi

# Check if a maven build is still running
RUNNING=$(docker ps -q --filter ancestor=maven:3.8.6-openjdk-11-slim | wc -l)
echo "Running maven containers: $RUNNING"

if [ "$RUNNING" -eq 0 ]; then
    echo "Starting a fresh maven build..."
    cd /root/bigdata-portal-platform/backend
    docker run --rm -v /root/bigdata-portal-platform/backend:/app -v /root/.m2:/root/.m2 -w /app maven:3.8.6-openjdk-11-slim mvn clean package -DskipTests -q > /tmp/build2.log 2>&1 &
    MVN_PID=$!
    echo "Maven build PID: $MVN_PID"
fi
