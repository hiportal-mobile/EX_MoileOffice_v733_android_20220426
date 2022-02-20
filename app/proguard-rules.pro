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
-keep class com.airbnb.lottie.LottieAnimationView
-keep class com.google.android.gms.flags.impl.FlagProviderImpl
-keep class com.google.android.gms.measurement.AppMeasurement
-keep class com.google.firebase.iid.FirebaseInstanceId
-keep class com.skt.pe.widget.wheelview.WheelView
-keep class com.skt.pe.common.data.SKTWebUtil
-keep class retrofit2.*
-dontwarn com.google.gms.googleservices.**
-dontwarn com.samsung.android.sdk.pass.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn com.skt.pe.common.data.**

