# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the SDK tools proguard-defaults.txt file.

# Keep WebView JavaScript interfaces
-keepclassmembers class com.zhixun.portal.MainActivity$AndroidBridge {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep BuildConfig fields
-keepclassmembers class com.zhixun.portal.BuildConfig {
    *;
}
