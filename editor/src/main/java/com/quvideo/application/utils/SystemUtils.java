package com.quvideo.application.utils;

import android.app.Activity;
import android.os.Build;
import android.view.WindowManager;

public class SystemUtils {

  public static boolean isOppo() {
    return "oppo".equalsIgnoreCase(Build.MANUFACTURER) || Build.FINGERPRINT.contains("oppo");
  }

  public static boolean isVivo() {
    return "vivo".equalsIgnoreCase(Build.MANUFACTURER) || Build.FINGERPRINT.contains("vivo");
  }

  public static boolean isHuawei() {
    return "huawei".equalsIgnoreCase(Build.MANUFACTURER) || Build.FINGERPRINT.contains("huawei");
  }

  public static boolean isSanxing() {
    return "sanxing".equalsIgnoreCase(Build.MANUFACTURER) || "Samsung".equalsIgnoreCase(
        Build.MANUFACTURER) || Build.FINGERPRINT.contains("sanxing") || Build.FINGERPRINT.contains(
        "Samsung");
  }

  public static boolean isXiaomi(){
    return Build.MANUFACTURER.equals("Xiaomi");
  }

  public static void setHidenOfStatusBar(boolean hideStatusBar, Activity activity) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      // 小与16, 什么都不做
      return;
    }
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // 16 <= 当前版本 < 19 时,需要用此方法
            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            if (hideStatusBar) {
                visibility |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            } else {
                visibility |= View.SYSTEM_UI_FLAG_VISIBLE;
            }

            activity.getWindow().getDecorView().setSystemUiVisibility(visibility);
        }*/

    //隐藏状态栏
    if (hideStatusBar) {
      WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
      lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
      activity.getWindow().setAttributes(lp);
    } else {//显示状态栏
      WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
      attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
      activity.getWindow().setAttributes(attr);
    }
  }
}
