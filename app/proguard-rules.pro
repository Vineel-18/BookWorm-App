# Add project specific ProGuard rules here.
-keep class com.bookworm.app.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
