#!/bin/bash
curl -s 'http://localhost:8090/api/news' | grep -o '"channel":"[^"]*"' | sort | uniq -c | sort -rn
echo "---"
curl -s 'http://localhost:8090/api/news' | grep -o '"imageUrl":"[^"]*"' | sort | uniq -c | sort -rn
