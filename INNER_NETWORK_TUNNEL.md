# 内网穿透配置指南

本指南提供多种内网穿透方案，用于将本地部署的服务暴露到公网访问。

## 方案一：frp（推荐）

### 1. 服务器端（公网服务器）配置

如果您有公网服务器，按以下步骤配置：

```bash
# 在公网服务器上下载 frp
wget https://github.com/fatedier/frp/releases/download/v0.52.3/frp_0.52.3_linux_amd64.tar.gz
tar -zxvf frp_0.52.3_linux_amd64.tar.gz
cd frp_0.52.3_linux_amd64

# 创建 frps.toml
cat > frps.toml << 'EOF'
[common]
bindPort = 7000
vhostHTTPPort = 8080
vhostHTTPSPort = 8443
EOF

# 启动服务端
./frps -c frps.toml
```

### 2. 客户端（本地服务器 192.168.146.128）配置

```bash
# 下载 frp
cd /root
wget https://github.com/fatedier/frp/releases/download/v0.52.3/frp_0.52.3_linux_amd64.tar.gz
tar -zxvf frp_0.52.3_linux_amd64.tar.gz
cd frp_0.52.3_linux_amd64

# 创建 frpc.toml
cat > frpc.toml << 'EOF'
[common]
serverAddr = "YOUR_PUBLIC_SERVER_IP"  # 替换为您的公网服务器IP
serverPort = 7000

[web]
type = "http"
localPort = 80
customDomains = "your-domain.com"  # 替换为您的域名

[backend]
type = "http"
localPort = 8090
customDomains = "api.your-domain.com"  # 替换为API域名

[dashboard]
type = "http"
localPort = 3000
customDomains = "admin.your-domain.com"  # 替换为后台域名
EOF
```

### 3. 启动 frp 客户端

```bash
# 前台启动测试
./frpc -c frpc.toml

# 后台运行（使用 systemd）
cat > /etc/systemd/system