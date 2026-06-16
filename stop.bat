@echo off
chcp 65001 >nul
title 智讯平台 - 停止服务
color 0C

echo ============================================
echo     智讯新闻资讯平台 - 停止所有服务
echo ============================================
echo.

:: ====== 配置区 ======
set SSH_HOST=192.168.146.128
set SSH_USER=root
set CPOLAR_EXE=D:\cpolar\cpolar.exe

:: ====== 停止 cpolar ======
echo [1/3] 停止 cpolar...
taskkill /f /im cpolar.exe >nul 2>&1
if %errorlevel% equ 0 (
    echo       cpolar 已停止
) else (
    echo       cpolar 未在运行
)

:: ====== 停止 Vite 开发服务器 ======
echo [2/3] 停止 Vite 开发服务器...
taskkill /f /fi "WINDOWTITLE eq Vite-DevServer*" >nul 2>&1
:: 也通过进程名杀 node
wmic process where "commandline like '%%vite%%'" call terminate >nul 2>&1
echo       Vite 已停止

:: ====== 停止远程 Docker（可选） ======
echo [3/3] 远程 Docker 服务...
echo       提示: 远程 Docker 服务默认保持运行
echo       如需停止，请手动 SSH 执行:
echo         ssh %SSH_USER%@%SSH_HOST% "cd /opt/bigdata-portal-platform && docker compose stop"
echo.

echo ============================================
echo   本地服务已全部停止
echo ============================================
echo.
pause
