#!/bin/bash
cd /root/bigdata-portal-platform

echo "Clearing database..."
docker compose exec -T mysql mysql -uroot -proot123 bigdata_portal -e "TRUNCATE TABLE news;"

echo "Database cleared!"

echo "Checking backend status..."
docker compose ps backend
docker compose logs --tail 20 backend

echo "Done. News will be re-fetched in next scheduled run (every 5 minutes)."
