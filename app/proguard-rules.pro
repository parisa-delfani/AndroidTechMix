# Keep ProGuard / R8 rules lean; add keeps only when needed.
-keepattributes SourceFile,LineNumberTable
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn kotlinx.serialization.**
