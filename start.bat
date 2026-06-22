@echo off
chcp 65001 >nul
title 智讯平台 - 一键启动
color 0A

echo ============================================
echo     智讯新闻资讯平台 - 一键启动脚本
echo ============================================
echo.

:: ====== 配置区 ======
set SSH_HOST=192.168.146.128
set SSH_USER=root
set SSH_PASS=123456
set FRONTEND_DIR=d:\workspace\bigdata-portal-platform\frontend
set CPOLAR_EXE=D:\cpolar\cpolar.exe
set NODE_MODULES=%FRONTEND_DIR%\node_modules

:: ====== Step 1: 启动远程 Docker 后端服务 ======
echo [1/3] 启动远程服务器 Docker 服务...
echo       目标: %SSH_HOST%

:: 检查 SSH 密钥是否已配置（免密登录）
ssh -o StrictHostKeyChecking=no -o ConnectTimeout=5 -o BatchMode=yes %SSH_USER%@%SSH_HOST% "echo ok" >nul 2>&1
if %errorlevel% equ 0 (
    echo       SSH 密钥认证成功，启动 Docker 服务...
    ssh %SSH_USER%@%SSH_HOST% "cd /opt/bigdata-portal-platform && docker compose up -d"
) else (
    :: 尝试用 plink（PuTTY）
    where plink >nul 2>&1
    if %errorlevel% equ 0 (
        echo       使用 plink 连接，启动 Docker 服务...
        echo y | plink -ssh %SSH_USER%@%SSH_HOST% -pw %SSH_PASS% "cd /opt/bigdata-portal-platform && docker compose up -d"
    ) else (
        echo       [!] 无 plink，尝试手动 SSH...
        echo       请在弹出的终端中输入密码: %SSH_PASS%
        start cmd /c "ssh %SSH_USER%@%SSH_HOST% -t 'cd /opt/bigdata-portal-platform && docker compose up -d && read -p Press_Enter_to_close...'"
    )
)

:: 等待后端启动
echo       等待后端服务启动（30秒）...
timeout /t 10 /nobreak >nul

:: 验证后端
echo       验证后端 API...
curl -s -o nul -w "      HTTP %%{http_code}" http://%SSH_HOST%:8090/api/news >nul 2>&1
echo       后端服务检查完成
echo.

:: ====== Step 2: 启动本地前端开发服务器 ======
echo [2/3] 启动本地前端开发服务器...

:: 检查 node_modules
if not exist "%NODE_MODULES%" (
    echo       安装前端依赖...
    cd /d "%FRONTEND_DIR%"
    call npm install
)

:: 启动 Vite（后台运行）
echo       启动 Vite dev server (端口 3000)...
cd /d "%FRONTEND_DIR%"
start "Vite-DevServer" /min cmd /c "cd /d %FRONTEND_DIR% && npx vite --host 0.0.0.0 --port 3000"

:: 等待 Vite 启动
echo       等待 Vite 启动（5秒）...
timeout /t 5 /nobreak >nul

:: 验证前端
echo       验证前端服务...
curl -s -o nul -w "      HTTP %%{http_code}" http://localhost:3000 >nul 2>&1
echo       前端服务检查完成
echo.

:: ====== Step 3: 启动 cpolar 内网穿透 ======
echo [3/3] 启动 cpolar 内网穿透...

if not exist "%CPOLAR_EXE%" (
    echo       [!] cpolar 未找到: %CPOLAR_EXE%
    echo       请手动安装 cpolar 或修改脚本中的路径
    echo.
    echo ============================================
    echo   前端已启动: http://localhost:3000
    echo   但 cpolar 未启动，无法公网访问
    echo ============================================
    pause
    exit /b 1
)

:: 启动 cpolar（后台运行）
echo       启动 cpolar (端口 3000, region=cn_vip)...
start "Cpolar-Tunnel" /min cmd /c "%CPOLAR_EXE% http 3000 -region=cn_vip"

:: 等待 cpolar 获取公网地址
echo       等待 cpolar 建立隧道（8秒）...
timeout /t 8 /nobreak >nul

:: 获取 cpolar 公网地址
echo       查询 cpolar 公网地址...
for /f "tokens=*" %%i in ('curl -s http://127.0.0.1:4040/api/tunnels 2^>nul ^| findstr /i "public_url"') do (
    echo       %%i
)
echo.

:: ====== 启动完成 ======
echo ============================================
echo   启动完成！
echo ============================================
echo.
echo   本地前端:   http://localhost:3000
echo   远程后端:   http://%SSH_HOST%:8090
echo   cpolar 管理: http://127.0.0.1:4040
echo.
echo   查看 cpolar 公网地址:
echo     curl http://127.0.0.1:4040/api/tunnels
echo.
echo   停止所有服务: 运行 stop.bat
echo ============================================
echo.

:: 打开浏览器
start http://localhost:3000

pause
