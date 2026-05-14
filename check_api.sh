#!/bin/bash
curl -s http://localhost:8090/api/news | python3 -c "
import sys, json
data = json.load(sys.stdin)['data'][:5]
for d in data:
    print(d['channel'], '->', d.get('imageUrl', ''))
"
