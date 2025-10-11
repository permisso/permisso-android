# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep all public classes and methods in the SDK
-keep public class io.permisso.android.** { *; }

# Keep JavaScript interface methods
-keepclassmembers class io.permisso.android.PermissoJavaScriptInterface {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep WebView related classes
-keep class android.webkit.** { *; }
-keep class androidx.webkit.** { *; }

# Keep Custom Tabs classes
-keep class androidx.browser.customtabs.** { *; }

# Keep JSON related classes
-keep class org.json.** { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep enum classes
-keepclassmembers enum io.permisso.android.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep data classes
-keep @kotlin.Metadata class io.permisso.android.**
-keep class io.permisso.android.**$Companion { *; }

# Keep constructors for configuration classes
-keepclassmembers class io.permisso.android.PermissoConfig {
    <init>(...);
}

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
