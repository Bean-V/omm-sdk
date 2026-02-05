-optimizationpasses 5                                           # 指定代码的压缩级别
-dontwarn
-dontusemixedcaseclassnames                            # 是否使用大小写混合
-dontskipnonpubliclibraryclasses                        # 是否混淆第三方jar
-dontpreverify                                                        # 混淆时是否做预校验
-verbose                                                                  # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/* # 混淆时所采用的算法

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep class  com.huawei.android.util.** { *;}
-keep class  com.tdtech.devicemanager.** { *;}
-keep class  ga.dualview.** { *;}
-keep class  ga.mdm.** { *;}

-keep class  com.oort.weichat.fragment.entity.** { *;}

# React Native 相关
-keep class com.facebook.react.** { *; }
-keep class com.facebook.soloader.** { *; }
-keep class com.facebook.jni.** { *; }
-dontwarn com.facebook.react.**
-dontwarn com.facebook.soloader.**

# Jitsi Meet 相关
-keep class org.jitsi.meet.sdk.** { *; }
-keep class org.jitsi.react.** { *; }
-dontwarn org.jitsi.**

# 保留原生库引用
-keepclasseswithmembernames class * {
    native <methods>;
}



# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
 # 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
 # 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
 # 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
 # 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# 保持自己定义的类不被混淆
-keep class MyClass

#如果有引用v4包可以添加下面这行
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

#如果引用了v4或者v7包，可以忽略警告，因为用不到android.support
-dontwarn android.support.**

#保持自定义组件不被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
-keepclassmembers enum * {
  public static **[] values();
 public static ** valueOf(java.lang.String);
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#xUtils(保持注解，及使用注解的Activity不被混淆，不然会影响Activity中你使用注解相关的代码无法使用)
-keep class * extends java.lang.annotation.Annotation {*;}
-keep class com.oort.weichat.activity.** {*;}

# 以libaray的形式引用的图片加载框架,不想混淆（注意，此处不是jar包形式，想混淆去掉"#"）
-keep class org.doubango.ngn.events.** { *; }
-keep class org.doubango.ngn.events.**
-keep class org.doubango.ngn.media.** { *; }
-keep class org.doubango.ngn.media.**
-keep class org.doubango.ngn.model.** { *; }
-keep class org.doubango.ngn.model.**
-keep class org.doubango.ngn.services.** { *; }
-keep class org.doubango.ngn.services.**
-keep class org.doubango.ngn.sip.** { *; }
-keep class org.doubango.ngn.sip.**
-keep class org.doubango.ngn.utils.** { *; }
-keep class org.doubango.ngn.utils.**
-keep class org.doubango.ngn.** { *; }
-keep class org.doubango.ngn.**
-keep class org.doubango.tinyWRAP.** { *; }
-keep class org.doubango.tinyWRAP.**
-keep class org.greenrobot.eventbus.** { *; }
-keep class org.greenrobot.eventbus.**
-keep class org.greenrobot.eventbus.util.** { *; }
-keep class org.greenrobot.eventbus.util.**
-keep class org.jivesoftware.smack.initializer**
-keep class org.jivesoftware.smack.**
-keep class org.jivesoftware.smackx.**
-keep class org.jivesoftware.smackx.**{*;}
-keep class org.jivesoftware.smack.compression.**
#-keep class com.oortcloud.job.push.utils.GsonUtil
-keepattributes EnclosingMethod
-keep class org.jivesoftware.smack.initializer.** { *; }
-keep class org.jivesoftware.smack.** { *; }
-keep class com.github.siyamed.shapeimageview.**
-dontwarn  com.github.siyamed.shapeimageview.**
-keep class org.kxml2.io.**
-keep class org.kxml2.io.KXmlParser
-dontwarn  org.kxml2.io.KXmlParser
-keep class org.kxml2.io.**{*;}
-keep class com.github.siyamed.shapeimageview.**{*;}
# ormlite
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
###-------- Gson 相关的混淆配置--------
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}

###-------- pulltorefresh 相关的混淆配置---------
-dontwarn com.handmark.pulltorefresh.library.**
-keep class com.handmark.pulltorefresh.library.** { *;}
-dontwarn com.handmark.pulltorefresh.library.extras.**
-keep class com.handmark.pulltorefresh.library.extras.** { *;}
-dontwarn com.handmark.pulltorefresh.library.internal.**
-keep class com.handmark.pulltorefresh.library.internal.** { *;}

###-------- ShareSDK 相关的混淆配置---------
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-keep class m.framework.**{*;}
-dontwarn cn.sharesdk.**
-dontwarn com.sina.**
-dontwarn com.mob.**
-dontwarn **.R$*

###--------------umeng 社会化分享相关的混淆配置-----------
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-dontwarn java.lang.invoke**
-dontwarn org.apache.lang.**
-dontwarn org.apache.commons.**
-dontwarn com.nhaarman.**
-dontwarn se.emilsjolander.**

-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

-dontwarn twitter4j.**
-keep class twitter4j.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep public class com.umeng.soexample.R$*{
    public static final int *;
}
-keep public class com.umeng.soexample.R$*{
    public static final int *;
}
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}

-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}
###--------------umeng 统计相关的混淆配置-----------
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class **.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
###-------Parcelable------------
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
###--------百度地图----------
#-libraryjars libs/BaiduLBS_Android.jar
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}
-keep class com.baidu.location.** { *; }
-keep class com.linkedin.** { *; }
-keep class mapsdkvi.com.** {*;}

-keepattributes Signature
 ###--------百度推送----------
#-libraryjars libs/pushservice-6.1.1.21.jar
-dontwarn com.baidu.**
-keep class com.baidu.**{*; }
###--------eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
###-----------okhttp
-dontwarn com.squareup.okhttp.**
-dontwarn org.xbill.DNS.spi.**
-dontwarn org.simpleframework.xml.stream.**
-dontwarn demo.**
-dontwarn com.alibaba.fastjson.util.**
-dontwarn com.alibaba.fastjson.support.spring.**
-dontwarn com.alibaba.fastjson.serializer.**

-keep class com.squareup.okhttp.** { }
-dontwarn okio.*
-keep class com.squareup.okhttp3.** {
*;
}
###------------twitter
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn rx.**
-dontwarn retrofit.**
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keepclasseswithmembers class * { @retrofit.http.* <methods>; }
-ignorewarnings

#部分library不混淆
-keep class org.wysaid.** {*;}
-keep class com.dubu.livecamera.** {*;}
-keep class com.coremedia.** {*;}
-keep class com.github.** {*;}
-keep class com.googlecode.** {*;}
-keep class com.seu.** {*;}
-keep class net.ossrs.** {*;}
-keep class com.dou361.** {*;}
-keep class tv.** {*;}

# bean类，主要用于json to bean, 不能混淆，
-keep class com.xuan.xuanhttplibrary.okhttp.result.** { *; }
-keep class com.oort.weichat.util.SkinUtils.Skin { *; }
-keep class com.oort.weichat.bean.** { *; }
-keep class com.oort.weichat.ui.live.bean.** { *; }
-keep class com.oort.weichat.ui.mucfile.bean.** { *; }

-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
    @com.facebook.common.internal.DoNotStrip *;
}

-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}

-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }
-keep class * extends com.facebook.react.bridge.NativeModule { *; }
-keepclassmembers,includedescriptorclasses class * { native <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.UIProp <fields>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }

-dontwarn com.facebook.react.**

# TextLayoutBuilder uses a non-public Android constructor within StaticLayout.
# See libs/proxy/src/main/java/com/facebook/fbui/textlayoutbuilder/proxy for details.
-dontwarn android.text.StaticLayout

# okhttp

-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# okio

-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

# WebRTC

-keep class org.webrtc.** { *; }
-dontwarn org.chromium.build.BuildHooksAndroid

# Jisti Meet SDK

-keep class org.jitsi.meet.sdk.** { *; }

# 新jitsi依赖的一个东西，
-keep class com.horcrux.** { *; }

# jpush, https://docs.jiguang.cn/jpush/client/Android/android_guide/
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

# 华为推送，https://developer.huawei.com/consumer/cn/service/hms/catalog/huaweipush_agent.html?page=hmssdk_huaweipush_devprepare_agent#6%20%E9%85%8D%E7%BD%AE%E6%B7%B7%E6%B7%86%E8%84%9A%E6%9C%AC
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.huawei.android.hms.agent.**{*;}

# mipush, https://dev.mi.com/console/doc/detail?pId=41#_3_3
-keep public class * extends com.xiaomi.mipush.sdk.PushMessageReceiver {*;}
#可以防止一个误报的 warning 导致无法成功编译，如果编译使用的 Android 版本是 23。
-dontwarn com.xiaomi.push.**

#滤镜库， 没有提供混淆方式
-keep class com.xiaojigou.**{*;}
-keep class org.bouncycastle.**{*;}

# 支付宝，https://docs.open.alipay.com/54/104509/
-keep class com.oort.weichat.ui.me.redpacket.alipay.** { *; }
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

# 魅族推送，https://github.com/MEIZUPUSH/PushDemo/blob/3367bbc290de17487fdeba66e44c56cacaa6141f/PushdemoInternal/artifactory-proguard-rules.pro#L40-L39
-keep public class * extends com.google.protobuf.GeneratedMessage { *; }
-keep class com.google.protobuf.** { *; }
-keep public class * extends com.google.protobuf.** { *; }
-keep class com.meizu.cloud.pushsdk.** { *; }
-dontwarn  com.meizu.cloud.pushsdk.**
-keep class com.meizu.nebula.** { *; }
-dontwarn com.meizu.nebula.**
-keep class com.meizu.push.** { *; }
-dontwarn com.meizu.push.**

# VIVO推送，
-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*; }
-keep class com.vivo.vms.**{*; }
-keep  class * extends com.vivo.push.sdk.OpenClientPushMessageReceiver {*;}

# Oppo推送，看起来是多余的，https://open.oppomobile.com/wiki/doc#id=10196
-keep public class * extends android.app.Service

# slf4j, 有出现过slf4j混淆导致slf4j-android崩溃的情况，
#-keep class org.slf4j.**{*; }

# firebase,
-keep class com.google.firebase.**

# androidx, https://stackoverflow.com/a/56055731
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keep class androidx.annotation.Keep
-keep @androidx.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

-keep class androidx.core.app.CoreComponentFactory { *; }

# pinyin4j, http://youmu178.com/android-development/proguard-sample.html
-dontwarn net.soureceforge.pinyin4j.**
-dontwarn demo.**
-keep class net.sourceforge.pinyin4j.** { *;}
-keep class demo.** { *;}

# QQ登录，https://wiki.connect.qq.com/android%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98
-keep class * extends android.app.Dialog

# 屏幕共享，
-keep class com.oney.WebRTCModule.VideoCaptureController { *;}

# usb摄像头，
-keep class com.relywisdom.** { *; }
-keep class com.serenegiant.** { *; }
#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}

#OkHttp3混淆配置
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

#Fastjson混淆配置
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }

#Gson混淆配置
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#cordova
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes Signature
-keepattributes *Annotation*

-keep class org.apache.cordova.**{*;}
-keep interface org.apache.cordova.**{*;}

-keep public class * extends org.apache.cordova.CordovaPlugin
-keep class com.plugins.**{*;}

#Glide3
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#----------------------------------------------------------------------------
#----------------------------------------------------------------------------
#-------------------------------------------------------------------------------
#https 需要
-keep public class android.net.http.SslError
#------------------------------------------------------------------------------


#-------bugly-------start-----------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}

#-----------------------end----------
-keep class com.oortcloud.bean.**
-keep class com.oortcloud.bean.** { *; }


-keep class com.oortcloud.clouddisk.**
-keep class com.oortcloud.clouddisk.** { *; }


#安全认证混淆配置
-keep class cn.com.jit.mctk.log.**
-keep class cn.com.jit.mctk.log.** { *; }

-keep class cn.com.jit.provider.lib.**
-keep class cn.com.jit.provider.lib.** { *; }

#nfc刷身份证混淆配置
-keep class cn.com.senter.**
-keep class cn.com.senter.** { *; }
-keep class com.senter.**
-keep class com.senter.** { *; }

-keep class com.zkteco.android.biometric.module.idcard.meta.**
-keep class com.zkteco.android.biometric.module.idcard.meta.** { *; }



# 保留 SEServiceCallback 类及其所在包
-keep class com.wt.se.SEServiceCallback { *; }
-keep class com.wt.se.** { *; }
-keep class com.citc.simkeylibrary.**
-keep class com.citc.simkeylibrary.** { *; }

-keep class kl.cds.constant.** { *; }
-keep class koal.cert.tools.** { *; }
-keep class koal.cert.tools.** { *; }

-keep class com.oortcloud.coo.** { *; }

# 1. 保留 Cordova 解析核心类（避免混淆）
-keep class org.apache.cordova.ConfigXmlParser { *; }
-keep class org.apache.cordova.PluginEntry { *; }
-keep class org.apache.cordova.CordovaPreferences { *; }

# 2. 保留 XML Pull 解析器相关类和方法（核心，避免触发 getNamespacePrefix() 异常）
-keep interface org.xmlpull.v1.XmlPullParser { *; }
-keep class org.xmlpull.v1.** { *; }
-keep class android.content.res.XmlBlock$Parser {
    <methods>;
    <fields>;
}
# 禁止优化 XmlPullParser 的方法调用
-keepclassmembers class * implements org.xmlpull.v1.XmlPullParser {
    public <methods>;
}

# 3. 保留自定义插件类（避免解析时找不到插件包名）
-keep class com.plugins.oortcloud.** { *; }
-keep class com.plugins.** { *; }

# 4. 禁用可能破坏解析逻辑的优化
-dontoptimize # 关闭代码优化（避免内联解析方法）
-dontshrink # 关闭代码缩减（避免删除解析依赖）