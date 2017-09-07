# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\pcdalao\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#指定代码的压缩级别
#-optimizationpasses 5

#混淆时不会产生形形色色的类名 包明不混合大小写
#-dontusemixedcaseclassnames

#不去忽略非公共的库类
#-dontskipnonpubliclibraryclasses

#优化  不优化输入的类文件
#-dontoptimize

#预校验
#-dontpreverify

#混淆时是否记录日志
#-verbose

# 混淆时所采用的算法
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class net.translives.app.bean.** { *; }

-keepattributes EnclosingMethod

##---------------End: proguard configuration for Gson  ----------

-keep class net.translives.app.BuildConfig { *; }
-keep class net.translives.app.util.** { *; }

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-dontwarn com.thoughtworks.xstream.**
-keep class com.thoughtworks.xstream.** { *; }

-dontwarn com.tencent.weibo.sdk.android.**
-keep class com.tencent.weibo.sdk.android.** { *; }

-dontwarn android.support.v7.widget.**
-keep class android.support.v7.widget.**{*;}

-dontshrink
-dontoptimize
-dontwarn android.webkit.WebView
-dontwarn com.tencent.weibo.sdk.**

-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.tencent.**
-keep public class javax.**
-keep public class android.webkit.**
-keep public class com.tencent.** {*;}

-keep class com.sina.weibo.** {*;}
-keep class com.tencent.mm.sdk.** {*;}
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

-keep class com.wangjie.** {*; }

# Glide Start
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
# Glide End

# 友盟Start
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class net.translives.app.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-dontwarn com.umeng.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep public class com.umeng.socialize.* {*;}
-keep class com.umeng.scrshot.**
-keep class com.umeng.socialize.sensor.**
-keep class pub.devrel.easypermissions.**
# 友盟End


