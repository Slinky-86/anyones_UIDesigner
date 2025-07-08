# Consumer ProGuard rules for UIDesigner library

# Keep public API classes
-keep public class com.uidesigner.library.UIDesignerLibrary { *; }
-keep public class com.uidesigner.library.UIDesignerConfig { *; }
-keep public class com.uidesigner.library.UIDesignerTheme { *; }
-keep public class com.uidesigner.library.model.** { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
