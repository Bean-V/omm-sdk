@echo off
chcp 65001 >nul
echo ========================================
echo 测试 JitPack 构建流程
echo ========================================
echo.

echo [步骤 1/5] 清理构建目录...
call gradlew.bat clean
if errorlevel 1 (
    echo ❌ 清理失败
    pause
    exit /b 1
)
echo ✅ 清理完成
echo.

echo [步骤 2/5] 发布本地 AAR 文件...
call gradlew.bat publishLocalAarsToMavenLocal --no-configuration-cache -x test -x lint
if errorlevel 1 (
    echo ❌ 发布本地 AAR 失败
    pause
    exit /b 1
)
echo ✅ 本地 AAR 发布完成
echo.

echo [步骤 3/5] 构建所有子模块...
call gradlew.bat assembleRelease -x test -x lint
if errorlevel 1 (
    echo ❌ 构建子模块失败
    pause
    exit /b 1
)
echo ✅ 所有子模块构建完成
echo.

echo [步骤 4/5] 合并所有依赖到完整 AAR...
call gradlew.bat :omm-lib:mergeReleaseDependencies -x test -x lint --no-configuration-cache
if errorlevel 1 (
    echo ❌ 合并依赖失败
    pause
    exit /b 1
)
echo ✅ 依赖合并完成
echo.

echo [步骤 5/5] 发布完整 AAR 到 Maven...
call gradlew.bat :omm-lib:publishToMavenLocal -x test -x lint
if errorlevel 1 (
    echo ❌ 发布失败
    pause
    exit /b 1
)
echo ✅ 发布完成
echo.

echo ========================================
echo 🎉 构建成功！
echo ========================================
echo.
echo 📦 输出文件:
echo   - 基础 AAR: omm-lib\build\outputs\aar\omm-lib-release.aar
echo   - 完整 AAR: omm-lib\build\outputs\aar\release-complete.aar
echo.
echo 📍 Maven 仓库:
echo   - %USERPROFILE%\.m2\repository\com\github\Bean-V\omm-sdk\1.0.0\
echo.
echo 🚀 下一步:
echo   1. 检查 release-complete.aar 文件大小（应该 50-100 MB）
echo   2. 提交代码: git add . ^&^& git commit -m "fix: 修复 JitPack 依赖问题"
echo   3. 创建 tag: git tag -a v1.0.8 -m "Release v1.0.8"
echo   4. 推送: git push origin main ^&^& git push origin v1.0.8
echo   5. 访问 JitPack: https://jitpack.io/#Bean-V/omm-sdk/v1.0.8
echo.
pause
