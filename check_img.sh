#!/bin/bash
curl -s http://localhost:8090/api/news | grep -o '"imageUrl":"[^"]*"' | head -10
