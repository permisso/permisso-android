# Consumer ProGuard rules for the library
# These rules will be automatically applied to projects using this library

-keep class io.permisso.android.** { *; }
-keepclassmembers class io.permisso.android.PermissoJavaScriptInterface {
    @android.webkit.JavascriptInterface <methods>;
}
