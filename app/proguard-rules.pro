-target 1.6
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,Exceptions,InnerClasses
-renamesourcefileattribute SourceFile
-adaptresourcefilenames **.properties
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF
-verbose
-dontwarn android.support.**,org.springframework.**,eclipse.local.sdk.**,org.apache.commons.**,org.apache.commons.**
-keepattributes Signature
-keepattributes *Annotation*

#指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses
#指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

-keep class java.lang.** {*;}
-dontwarn java.lang.**
-keep class java.** {*;}
-dontwarn java.**

-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes SourceFile,LineNumberTable

-keep class com.quvideo.application.gallery.** { *; }

#==================gson && protobuf==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep class com.android.vending.billing.**
-dontwarn android.app.**

# Preserve all fundamental application classes.
-keep public class * extends android.app.Fragment

-keep public class * extends android.app.Activity

-keep public class * extends android.app.Application

-keep public class * extends android.app.Service

-keep public class * extends android.content.BroadcastReceiver

-keep public class * extends android.content.ContentProvider

-keep public class * extends android.app.backup.BackupAgentHelper

-keep public class * extends android.preference.Preference

-keep public class com.android.vending.licensing.ILicensingService

# Preserve all View implementations, their special context constructors, and
# their setters.
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context,android.util.AttributeSet);
    public <init>(android.content.Context,android.util.AttributeSet,int);
    public void set*(...);
}

# Preserve all classes that have special context constructors, and the
# constructors themselves.
-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}

# Preserve all classes that have special context constructors, and the
# constructors themselves.
-keepclasseswithmembers class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}

# Preserve all possible onClick handlers.
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

# Preserve the special fields of all Parcelable implementations.
-keepclassmembers class * extends android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Preserve the special static methods that are required in all enumeration
# classes.
-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class *,* {
    native <methods>;
}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keepattributes EnclosingMethod

-keep,allowobfuscation @interface android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {
  *;
}

-dontwarn java.lang.reflect.Executable

####################for green dao
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties { *; }

# If you DO use SQLCipher:
-keep class org.greenrobot.greendao.database.SqlCipherEncryptedHelper { *; }

# If you do NOT use SQLCipher:
-dontwarn net.sqlcipher.database.**
# If you do NOT use RxJava:
-dontwarn rx.**

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
