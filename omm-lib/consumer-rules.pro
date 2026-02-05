
# ================================================
# 消费者混淆规则
# ================================================

# 保持 SDK 所有公开 API
-keep class com.oort.weichat.** {
    public *;
}

# 保持所有接口
-keep interface com.oort.weichat.** {
    *;
}

# 保持所有注解
-keepattributes *Annotation*

# 不要警告 SDK 内部代码
-dontwarn com.oort.**
-dontwarn com.tencent.**
-dontwarn com.huawei.**
-dontwarn com.google.**
