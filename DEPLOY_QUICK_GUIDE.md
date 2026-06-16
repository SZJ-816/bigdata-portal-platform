# 后端重新部署快速指南

## 服务器信息
- **地址**: 192.168.146.128
- **用户**: root
- **密码**: 123456

## 本次修复内容
✅ 添加了 `/images/**` 静态资源映射到 WebMvcConfig.java，用于显示新闻图片

## 快速部署步骤

### 方式一：使用自动部署脚本（推荐）

```bash
# 1. 连接到服务器
ssh root@192.168.146.128
# 输入密码：123456

# 2. 进入项目目录
cd /root/bigdata-portal-platform  # 或项目实际目录

# 3. 赋予脚本执行权限
chmod +x deploy_backend.sh

# 4. 运行部署脚本
./deploy_backend.sh
```

---

### 方式二：手动执行部署

如果脚本无法使用，请按以下步骤手动操作：

```bash
# 1. 连接服务器
ssh root@192.168.146.128

# 2. 进入项目目录
cd /root/bigdata-portal-platform

# 3. 停止旧的后端容器
docker-compose stop backend
docker-compose rm -f backend

# 4. 清理旧镜像
docker images | grep bigdata-portal-backend | awk '{print $3}' | xargs -r docker rmi -f

# 5. 确保后端 JAR 包是最新的
# （如果本地修改了代码，需要先在本地构建并上传到服务器）

# 6. 重新构建并启动后端
docker-compose up -d --build backend

# 7. 查看日志确认启动成功
docker-compose logs -f backend
```

---

## 上传本地修改到服务器

如果您在本地修改了后端代码（如 WebMvcConfig.java），需要构建并上传：

### 在本地 Windows 机器（当前环境）：

```bash
# 1. 重新构建后端（如果有 Maven）
cd d:\workspace\bigdata-portal-platform\backend
mvn clean package -DskipTests

# 2. 上传 JAR 包到服务器
scp d:\workspace\bigdata-portal-platform\backend\target\portal-2.0.0.jar root@192.168.146.128:/root/bigdata-portal-platform/backend/target/

# 3. 也可以上传整个项目（如果需要）
# scp -r d:\workspace\bigdata-portal-platform root@192.168.146.128:/root/
```

---

## 验证部署

部署完成后，在浏览器或命令行验证：

```bash
# 检查容器状态
docker-compose ps

# 检查图片资源访问（测试用一个新闻图片）
curl -v http://192.168.146.128:8090/images/news/test.jpg

# 查看后端日志
docker-compose logs backend
```

---

## 访问服务

部署成功后可以访问：
- **前端**: http://192.168.146.128
- **后端API**: http://192.168.146.128:8090
- **管理面板**: http://192.168.146.128:3000

---

## 故障排查

### 后端启动失败
```bash
# 查看详细日志
docker-compose logs --tail=100 backend
```

### 图片还是无法显示
1. 确认 WebMvcConfig.java 的修改已包含在 JAR 包中
2. 检查 /app/news-images 目录是否有图片文件
3. 检查浏览器控制台是否有 404 错误

### 镜像文件位置
```bash
# 检查容器内的图片目录
docker-compose exec backend ls -la /app/news-images

# 检查 Docker volume
docker volume ls | grep news-images
```
