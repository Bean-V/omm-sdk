# OMM-SDK 打包和依赖问题分析

## 问题概述

用户在使用 JitPack 依赖 `com.github.Bean-V.omm-sdk:omm-sdk:2.0.9` 时遇到 401 错误，提示无法找到 `Levc-lib:2.0.5`。但使用本地项目依赖 `implementation project(':omm-lib')` 时一切正常。

## 根本原因

### 1. 版本不匹配问题

- **根配置文件** (`build.gradle`) 中定义: `PUBLISH_VERSION = '2.0.5'`
- **用户尝试使用**: `implementation 'com.github.Bean-V.omm-sdk:omm-sdk:2.0.9'`
- **POM 文件中引用**: `Levc-lib:2.0.5`（因为 POM 使用的是 `PUBLISH_VERSION`）

版本 2.0.9 可能不存在，或者 JitPack 构建失败。

### 2. Levc-lib 模块已被注释

在 `settings.gradle` 中，`Levc-lib` 模块已被注释掉：

```gradle
//include ':Levc-lib' 执法记录仪暂时不用
```

但在 `omm-lib/build_publishing.gradle` 的 POM 生成逻辑中，仍然包含了 `Levc-lib` 依赖：

```gradle
def subModules = [
    'oort_imagepick_sdk', 'ToolsMain', 'provider_lib', 'pullToRefershLibraryMy',
    'jcvideoplayer-lib', 'YZxing-lib', 'OpenGLlibrary', 'MPChartLib',
    'liveLibrary', 'OortWebframeLib', 'basemodule', 'AppStore',
    'Contacts', 'screenrecorder', 'AIDICert', 'sound_recon',
    'FrameLibrary', 'Levc-lib', 'giraffeplayer', 'floatview', 'offline',  // ❌ Levc-lib 仍在列表中
    'matisse', 'Utilities3-1.0.3', 'ooortCloudDisk', 'IDCardCheck'
]
```

这导致生成的 POM 文件中包含了一个不存在的依赖。

### 3. JitPack 多模块发布机制

JitPack 在构建多模块项目时：
1. 会为每个模块生成独立的 Maven 坐标
2. 主模块的 POM 文件会引用所有子模块
3. 如果子模块不存在或构建失败，下载主模块时会报 401 错误

## 解决方案

### 方案 1: 修复 POM 生成逻辑（推荐）

从 POM 生成逻辑中移除 `Levc-lib`：

```gradle
// omm-lib/build_publishing.gradle
def subModules = [
    'oort_imagepick_sdk', 'ToolsMain', 'provider_lib', 'pullToRefershLibraryMy',
    'jcvideoplayer-lib', 'YZxing-lib', 'OpenGLlibrary', 'MPChartLib',
    'liveLibrary', 'OortWebframeLib', 'basemodule', 'AppStore',
    'Contacts', 'screenrecorder', 'AIDICert', 'sound_recon',
    'FrameLibrary', 'giraffeplayer', 'floatview', 'offline',  // ✅ 移除 Levc-lib
    'matisse', 'Utilities3-1.0.3', 'ooortCloudDisk', 'IDCardCheck'
]
```

### 方案 2: 使用正确的版本号

确保使用的版本号与 `PUBLISH_VERSION` 一致：

```gradle
// 使用 2.0.5 而不是 2.0.9
implementation 'com.github.Bean-V.omm-sdk:omm-sdk:2.0.5'
```

### 方案 3: 更新版本号并重新发布

如果确实需要发布 2.0.9 版本：

1. 更新 `build.gradle` 中的版本号：
```gradle
PUBLISH_VERSION = '2.0.9'
```

2. 更新 `omm-lib/build_publishing.gradle` 中的版本号：
```gradle
PUBLISH_VERSION = '2.0.9'
```

3. 提交代码并打 tag：
```bash
git add .
git commit -m "Release version 2.0.9"
git tag 2.0.9
git push origin main
git push origin 2.0.9
```

4. 等待 JitPack 自动构建

## 验证步骤

### 1. 检查 JitPack 构建状态

访问 JitPack 构建日志：
```
https://jitpack.io/com/github/Bean-V/omm-sdk/2.0.5/build.log
```

查看是否有构建错误。

### 2. 检查 POM 文件

访问生成的 POM 文件：
```
https://jitpack.io/com/github/Bean-V/omm-sdk/2.0.5/omm-sdk-2.0.5.pom
```

确认：
- 所有子模块依赖都存在
- 没有引用不存在的模块（如 Levc-lib）
- 版本号正确

### 3. 本地测试

在第三方项目中测试：

```gradle
dependencies {
    implementation 'com.github.Bean-V.omm-sdk:omm-sdk:2.0.5'
}

configurations.all {
    exclude group: 'com.google.android', module: 'support-v4'
}
```

清理缓存并重新构建：
```bash
./gradlew clean --refresh-dependencies
./gradlew assembleDebug
```

## 最佳实践建议

### 1. 版本号管理

在根 `build.gradle` 中统一管理版本号：

```gradle
ext {
    PUBLISH_VERSION = '2.0.5'  // 所有模块使用统一版本
}
```

所有子模块的 `build_publishing.gradle` 都引用这个版本号：

```gradle
ext {
    PUBLISH_VERSION = rootProject.ext.PUBLISH_VERSION
}
```

### 2. 自动化 POM 生成

使用动态方式生成子模块列表，避免手动维护：

```gradle
// 自动收集所有已启用的子模块
def subModules = []
rootProject.subprojects.each { subproject ->
    if (subproject.name != 'omm-lib' && subproject.name != 'app') {
        subModules << subproject.name
    }
}
```

### 3. CI/CD 集成

建议添加 GitHub Actions 自动化发布流程：

```yaml
name: Publish to JitPack
on:
  push:
    tags:
      - '*'
jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Build with Gradle
        run: ./gradlew build
      - name: Trigger JitPack build
        run: |
          curl https://jitpack.io/com/github/Bean-V/omm-sdk/${{ github.ref_name }}/build.log
```

## 常见问题

### Q: 为什么本地项目依赖可以工作，但 JitPack 依赖不行？

A: 本地项目依赖时，Gradle 直接使用源代码，不需要 POM 文件。但 JitPack 依赖时，Gradle 会解析 POM 文件中的所有依赖，如果 POM 中引用了不存在的模块，就会报错。

### Q: 如何强制 JitPack 重新构建？

A: 访问以下 URL 触发重新构建：
```
https://jitpack.io/com/github/Bean-V/omm-sdk/2.0.5/build.log
```

或者删除 tag 后重新创建：
```bash
git tag -d 2.0.5
git push origin :refs/tags/2.0.5
git tag 2.0.5
git push origin 2.0.5
```

### Q: 为什么会有 401 错误而不是 404？

A: JitPack 对于不存在的依赖返回 401 Unauthorized，这是 JitPack 的设计。实际上表示"依赖不存在或构建失败"。

### Q: 如何调试 JitPack 构建问题？

A: 
1. 查看构建日志：`https://jitpack.io/com/github/Bean-V/omm-sdk/VERSION/build.log`
2. 检查 POM 文件：`https://jitpack.io/com/github/Bean-V/omm-sdk/VERSION/omm-sdk-VERSION.pom`
3. 本地模拟 JitPack 构建：`./gradlew clean build publishToMavenLocal`
4. 检查本地 Maven 仓库：`~/.m2/repository/com/github/Bean-V/`

## 立即行动清单

- [x] 从 `omm-lib/build_publishing.gradle` 中移除 `Levc-lib`
- [ ] 确认 `PUBLISH_VERSION` 与实际使用的版本一致
- [ ] 提交代码并打 tag (例如: 2.0.6)
- [ ] 等待 JitPack 构建完成
- [ ] 在第三方项目中测试新版本
- [ ] 更新第三方集成指南文档

## 临时解决方案

在新版本发布之前,请使用本地项目依赖:

```gradle
// app/build.gradle
dependencies {
    // 临时使用本地项目依赖
    implementation project(':omm-lib')
    
    // 等新版本发布后再使用 JitPack 依赖
    // implementation 'com.github.Bean-V:omm-sdk:2.0.6'
}
```

这样可以避免 JitPack 上旧版本 POM 文件中的 Levc-lib 依赖问题。

