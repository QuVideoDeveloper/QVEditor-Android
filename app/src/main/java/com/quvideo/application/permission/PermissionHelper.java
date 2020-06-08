package com.quvideo.application.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.AppOpsManagerCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionHelper {

  /**
   * 启动权限申请代理页面.
   *
   * @param deniedPermissions 查询到的没有获取到的权限.
   */
  @RequiresApi(api = Build.VERSION_CODES.M) public static void startPermissionProxyActivity(Activity activity,
      String[] deniedPermissions, @PermissionProxyActivity.ModeType int modeType,
      PermissionProxyActivity.PermissionListener permissionListener) {
    PermissionProxyActivity.setPermissionListener(permissionListener);
    Intent intent = new Intent(activity, PermissionProxyActivity.class);
    intent.putExtra(PermissionProxyActivity.KEY_INPUT_PERMISSIONS, deniedPermissions);
    intent.putExtra(PermissionProxyActivity.KEY_MODE_TYPE, modeType);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    activity.startActivity(intent);
  }

  /**
   *
   */
  @RequiresApi(api = Build.VERSION_CODES.M) public static String[] getDeniedPermissions(Context context,
      @NonNull String... permissions) {
    List<String> deniedList = new ArrayList<>(1);
    for (String permission : permissions)
      if (ContextCompat.checkSelfPermission(context, permission)
          != PackageManager.PERMISSION_GRANTED) {
        deniedList.add(permission);
      }
    return deniedList.toArray(new String[deniedList.size()]);
  }

  /**
   * 检测是否已授权这些权限
   */
  public static boolean hasPermission(@NonNull Context context, @NonNull String... permissionsArgs) {
    List<String> permissions = Arrays.asList(permissionsArgs);
    if (targetSdkVersionBelowAndroidM(context) || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      //App target < 23 手机的版本小于23 都不会工作。
      return true;
    }
    for (String permission : permissions) {
      int result = ContextCompat.checkSelfPermission(context, permission);
      if (result == PackageManager.PERMISSION_DENIED) return false;

      String op = AppOpsManagerCompat.permissionToOp(permission);
      if (TextUtils.isEmpty(op)) continue;
      result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
      if (result != AppOpsManagerCompat.MODE_ALLOWED) return false;
    }
    return true;
  }

  /**
   * targetSdkVersion 小于23，暂时不处理这个逻辑。兼容过渡时期用的。
   */
  public static boolean targetSdkVersionBelowAndroidM(Context context) {
    if (context == null) {
      return true;
    }
    int targetSdkVersion = 0;
    try {
      ApplicationInfo applicationInfo =
          context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
      if (applicationInfo != null) {
        targetSdkVersion = applicationInfo.targetSdkVersion;
        //Log.d(VivaPermission.TAG, "targetSdkVersion = " + targetSdkVersion);
      }
    } catch (Exception ignore) {

    }
    //targetSdkVersion以下版本，暂时失效。
    return targetSdkVersion < Build.VERSION_CODES.M;
  }
}
