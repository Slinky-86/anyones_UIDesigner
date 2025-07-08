# Consumer ProGuard rules for UIDesigner library

# Keep all public API classes
-keep public class com.uidesigner.library.** { *; }

# Keep Hilt components
-keep class * extends dagger.hilt.android.AndroidEntryPoint
-keep class dagger.hilt.** { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }

# Keep serialization
-keep class kotlinx.serialization.** { *; }
-keep class com.uidesigner.library.**$$serializer { *; }
