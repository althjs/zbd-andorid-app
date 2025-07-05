# Add project specific ProGuard rules here.

# WebView JavaScript 인터페이스 보호
-keepclassmembers class kr.zbd.android.MainActivity$WebAppInterface {
    public *;
}

# WebView 관련 클래스 보호
-keep class android.webkit.** { *; }
-keep class androidx.webkit.** { *; }

# JavaScript 인터페이스 메서드 보호
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Glide 이미지 로더 보호
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
    *** rewind();
}

# PhotoView 보호
-keep class com.github.chrisbanes.photoview.** { *; }

# 일반적인 Android 컴포넌트 보호
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# 애니메이션 관련 보호
-keep class androidx.core.splashscreen.** { *; }
-keep class androidx.swiperefreshlayout.** { *; }

# 네트워크 보안 설정 보호
-keep class javax.net.ssl.** { *; }
-keep class org.apache.http.** { *; }

# 리플렉션 사용 클래스 보호
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# 크래시 로그를 위한 라인 정보 보존
-keepattributes SourceFile,LineNumberTable

# 열거형 보호
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Parcelable 구현체 보호
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# 일반적인 성능 최적화
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# 경고 억제
-dontwarn org.apache.http.**
-dontwarn android.webkit.**
-dontwarn javax.annotation.**