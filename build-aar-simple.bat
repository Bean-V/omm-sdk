@echo off
REM 简单的 AAR 构建脚本

echo ========================================
echo 构建 omm-lib AAR
echo ========================================
echo.

echo [步骤 1/2] 清理...
call .\gradlew.bat clean
echo.

echo [步骤 2/2] 构建 Release AAR...
call .\gradlew.bat :omm-lib:bundleReleaseAar
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ 构建失败！
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✅ 构建完成！
echo ========================================
echo.
echo 输出文件位置:
dir omm-lib\build\outputs\aar\*.aar
echo.
pause
