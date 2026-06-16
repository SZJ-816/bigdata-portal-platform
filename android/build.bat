@echo off
REM ============================================
REM  智讯 Android App - 构建脚本
REM  自动配置 JAVA_HOME 并执行 Gradle 构建
REM ============================================

set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "ANDROID_HOME=C:\Users\Administrator\AppData\Local\Android\Sdk"

REM 查找本地 Gradle（优先使用已缓存的 Gradle）
set "GRADLE_BIN="
for /d %%d in ("%USERPROFILE%\.gradle\wrapper\dists\gradle-8.5-bin\*\gradle-8.5\bin") do (
    if exist "%%d\gradle.bat" set "GRADLE_BIN=%%d\gradle.bat"
)

echo ========================================
echo   智讯 Android App 构建
echo ========================================
echo JAVA_HOME:    %JAVA_HOME%
echo ANDROID_HOME: %ANDROID_HOME%
if defined GRADLE_BIN (
    echo GRADLE:       %GRADLE_BIN%
) else (
    echo GRADLE:       gradlew.bat (需要网络下载)
)
echo.

if defined GRADLE_BIN (
    if "%~1"=="" (
        echo 正在构建 Debug APK...
        call "%GRADLE_BIN%" assembleDebug
    ) else (
        echo 执行: gradle %*
        call "%GRADLE_BIN%" %*
    )
) else (
    if "%~1"=="" (
        echo 正在构建 Debug APK (使用 wrapper)...
        call "%~dp0gradlew.bat" assembleDebug
    ) else (
        call "%~dp0gradlew.bat" %*
    )
)

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   构建成功！
    echo   APK: app\build\outputs\apk\debug\app-debug.apk
    echo ========================================
    copy /Y "app\build\outputs\apk\debug\app-debug.apk" "..\bigdata-portal.apk" >nul 2>&1
) else (
    echo.
    echo 构建失败，请检查错误信息。
)

pause
