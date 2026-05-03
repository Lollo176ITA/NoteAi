-keepattributes *Annotation*, InnerClasses
-keep,allowobfuscation,allowshrinking class kotlin.Metadata

# kotlinx.serialization
-keep,includedescriptorclasses class com.lorenzocensi.noteai.**$$serializer { *; }
-keepclassmembers class com.lorenzocensi.noteai.** {
    *** Companion;
}
-keepclasseswithmembers class com.lorenzocensi.noteai.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit
-keepattributes Signature, Exceptions
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Tink
-keep class com.google.crypto.tink.** { *; }
-keep class com.google.crypto.tink.proto.** { *; }
