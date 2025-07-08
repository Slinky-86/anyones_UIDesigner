# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

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

# Keep UI Designer Library classes
-keep class com.uidesigner.library.** { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }

# Keep Hilt classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep serialization classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep component model classes
-keep class com.uidesigner.library.model.** { *; }

# Keep view model classes
-keep class com.uidesigner.library.ui.viewmodel.** { *; }

# Keep custom views
-keep class com.uidesigner.library.ui.components.** { *; }

# Prevent obfuscation of drag and drop functionality
-keep class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep drag and drop related classes
-keep class android.view.DragEvent { *; }
-keep class android.content.ClipData { *; }
-keep class android.content.ClipDescription { *; }

# Keep reflection-based classes
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep XML parsing classes
-keep class org.xmlpull.** { *; }
-keep class org.kxml2.** { *; }

# Keep font resources
-keep class **.R$font { *; }

# Keep drawable resources
-keep class **.R$drawable { *; }

# Optimization settings
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# Keep generic signatures
-keepattributes Signature

# Keep annotations
-keepattributes *Annotation*

# Keep inner classes
-keepattributes InnerClasses,EnclosingMethod
