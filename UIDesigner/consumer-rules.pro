# Consumer ProGuard rules for UIDesigner Library

# Keep all public API classes
-keep public class com.uidesigner.library.UIDesigner { *; }
-keep public class com.uidesigner.library.UIDesignerConfig { *; }
-keep public class com.uidesigner.library.UIDesignerTheme { *; }
-keep public class com.uidesigner.library.ui.UIDesignerActivity { *; }
-keep public class com.uidesigner.library.ui.SplashActivity { *; }

# Keep model classes that might be used by consumers
-keep public class com.uidesigner.library.model.** { *; }

# Keep enums
-keepclassmembers enum com.uidesigner.library.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep callback interfaces
-keep interface com.uidesigner.library.callback.** { *; }

# Keep custom attributes
-keep class com.uidesigner.library.R$styleable { *; }
