# 前端重新部署指南

## 服务器信息
- **地址**: 192.168.146.128
- **用户**: root
- **密码**: 123456

## 部署步骤

### 1. 连接到服务器
```bash
ssh root@192.168.146.128
# 输入密码: 123456
```

### 2. 进入项目目录
```bash
# 查找项目位置
find / -name "docker-compose.yml" -path "*bigdata*" 2>/dev/null

# 进入项目目录（假设在某个位置，比如 /root/bigdata-portal-platform）
cd /path/to/bigdata-portal-platform
```

### 3. 更新配置文件（如果本地修改了）

如果您在本地修改了 `frontend/default.conf`，需要先将文件上传到服务器：

```bash
# 在您的本地 Windows 机器上（另一个终端）：
scp d:\workspace\bigdata-portal-platform\frontend\default.conf root@192.168.146.128:/path/to/project/frontend/default.conf
```

### 4. 执行重新部署
```bash
# 方式一：使用 docker-compose 直接构建
docker-compose up -d --build frontend

# 方式二：或者分步执行
docker-compose stop frontend
docker-compose rm -f frontend
docker-compose up -d --build frontend
```

### 5. 检查部署状态
```bash
# 查看容器状态
docker-compose ps

# 查看前端日志
docker-compose logs -f frontend
```

### 6. 验证修复
访问 http://192.168.146.128，检查：
- 首页新闻是否正常显示
- 点击新闻能否正常打开详情页
- 浏览器控制台是否还有错误

## 快速命令（一次性执行）

如果您想一次性执行所有操作，可以在 SSH 连接后运行：

```bash
cd /path/to/bigdata-portal-platform && \
docker-compose stop frontend && \
docker-compose rm -f frontend && \
docker images | grep bigdata-portal-frontend | awk '{print $3}' | xargs -r docker rmi -f && \
docker-compose up -d --build frontend && \
sleep 5 && \
docker-compose ps
```

## 故障排查

### 后端连接问题
如果前端还是无法连接后端，检查：
```bash
# 检查后端容器状态
docker-compose ps backend

# 检查后端日志
docker-compose logs backend

# 测试后端是否响应
curl http://localhost:8090/api/news
```

### 网络问题
```bash
# 检查 Docker 网络
docker network ls
docker network inspect bigdata-portal-platform_bigdata-net
```
