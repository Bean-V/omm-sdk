# SDK -依赖问题
    1、fat-aar  
        com.github.kezong:fat-aar:1.3.8 
        问题：AGP（Android Gradle Plugin）版本有兼容性，依赖最高版本-> build:gradle:7.3.0
    2、aar2jar 
        只合并 .class 文件，不解决资源冲突
        资源问题（R 文件、Manifest）
        无法处理 native 库（.so 文件）
    3、R8/ProGuard 
        只能处理：
            .class 文件（Java/Kotlin 字节码） 
            代码优化和内联
            移除未使用的类和方法
        不能处理：
            res/ 目录下的资源文件
            AndroidManifest.xml 合并
            assets/ 文件
            jni/ native 库
            R.java 类合并




    3、maven-publish + api 依赖 是最标准、最推荐的方式：
        对第三方友好：一键集成，自动处理依赖
        维护简单：遵循标准 Maven/Gradle 规范
        灵活性强：支持依赖排除、版本控制
        社区认可：所有大厂 SDK 都采用这种方式
        长期可靠：不受 AGP 版本升级影响