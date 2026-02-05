
# ================================================
# 合并所有依赖的混淆规则
# ================================================

# 核心优化配置
-optimizationpasses 5
-allowaccessmodification
-overloadaggressively
-mergeinterfacesaggressively

# 保持所有必要的类

# 核心 SDK 类
-keep class com.oort.weichat.** { *; }
-keep class com.oort.provider.** { *; }
-keep class com.oort.web.** { *; }
-keep class com.oort.basemodule.** { *; }
-keep class com.oort.tools.** { *; }
-keep class com.oort.image.** { *; }
-keep class com.oort.contacts.** { *; }
-keep class com.oort.store.** { *; }
-keep class com.oort.media.** { *; }
-keep class com.oort.ai.** { *; }

# 第三方库关键类
-keep class com.tencent.** { *; }
-keep class com.huawei.** { *; }
-keep class com.xiaomi.** { *; }
-keep class com.google.** { *; }
-keep class com.github.bumptech.glide.** { *; }
-keep class org.greenrobot.eventbus.** { *; }
-keep class com.squareup.** { *; }
-keep class io.reactivex.** { *; }
-keep class org.bouncycastle.** { *; }
-keep class com.tencent.bugly.** { *; }
-keep class com.baidu.** { *; }
-keep class org.igniterealtime.smack.** { *; }

# 序列化类
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
}

# 资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

# BuildConfig
-keep class **.BuildConfig

# 注解
-keepattributes *Annotation*, Signature, InnerClasses

# 移除日志（减小体积）
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# 不要警告任何类（确保编译通过）
-dontwarn **
