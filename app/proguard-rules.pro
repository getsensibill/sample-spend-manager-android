# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# TODO: Remove these as we fix up the rules of each module
-keep class com.getsensibill.capturestandalone** { *; }
-keep class com.getsensibill.core** { *; }
-keep class com.getsensibill.rest** { *; }
-keep class com.getsensibill.utilities** { *; }
-keep class com.getsensibill.tokenprovider** { *; }
-keep class com.getsensibill.encryptor** { *; }
-keep class com.getsensibill.filesystem** { *; }
-keep class com.getsensibill.utils** { *; }
-keep class com.getsensibill.oauthclient** { *; }
-keep class com.getsensibill.sensibillauth** { *; }
-keep class com.getsensibill.web** { *; }

# Webview
-keepattributes JavascriptInterface
-keepclassmembers class com.getsensibill.web** {
    @android.webkit.JavascriptInterface <methods>;
}

# Gson
-keepclassmembers,allowobfuscation class com.getsensibill.web** {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep public class com.getsensibill.web.data.configuration** { *; }

# Models used in setup for SM
-keep public class com.getsensibill.web.data.models.Brand { *; }
-keep public class com.getsensibill.web.data.models.Colors { *; }
-keep public class com.getsensibill.web.data.models.Fonts { *; }

# UI Finish Reasons
-keep public class com.getsensibill.web.data.UiFinishReason { *; }

# UI Activity and Fragments
-keep public class com.getsensibill.web.ui.WebUiActivity { *; }
-keep public class com.getsensibill.web.ui.WebUiFragment { *; }
-keep public class com.getsensibill.web.ui.WebUiFragment$Listener { *; }
-keep public class com.getsensibill.web.ui.WebUiNetworkErrorFragment { *; }
-keep public class com.getsensibill.web.ui.WebUiNetworkErrorFragment$Listener { *; }

# Webview
-keepattributes JavascriptInterface
-keepclassmembers class com.getsensibill.web** {
    @android.webkit.JavascriptInterface <methods>;
}

# Gson
-keepclassmembers,allowobfuscation class com.getsensibill.web** {
  @com.google.gson.annotations.SerializedName <fields>;
}