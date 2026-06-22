@echo off
REM ============================================
REM  智讯 Android App - 构建脚本（Release 签名版）
REM ============================================

set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "ANDROID_HOME=C:\Users\Administrator\AppData\Local\Android\Sdk"

echo ========================================
echo   智讯 Android App 构建 (Release)
echo ========================================
echo JAVA_HOME:    %JAVA_HOME%
echo ANDROID_HOME: %ANDROID_HOME%
echo.

REM 清理之前的构建
call "%~dp0gradlew.bat" clean

REM 构建 Release APK
call "%~dp0gradlew.bat" assembleRelease

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   构建成功！
    echo   APK: app\build\outputs\apk\release\app-release.apk
    echo ========================================
    copy /Y "app\build\outputs\apk\release\app-release.apk" "..\bigdata-portal.apk" >nul 2>&1
    echo 已复制到项目根目录 bigdata-portal.apk
) else (
    echo.
    echo 构建失败，请检查错误信息。
)

pause
