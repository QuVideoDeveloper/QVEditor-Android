package com.quvideo.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import java.util.List;

public class EditorApplication extends Application {

  /** license文件路径 */
  private static final String ASSETS_ANDROID_XIAOYING_LICENCE =
      "assets_android://qvlicense/license.txt";

  private static volatile Application sApplication;

  public static volatile boolean initApplicationOver = false;

  @Override public void onCreate() {
    super.onCreate();
    String processName = getCurProcessName(this);
    String appPkgName = getPackageName();
    init(this);
    if (!TextUtils.isEmpty(processName) && !TextUtils.equals(appPkgName, processName)) {
      // 非主进程啥也不做
      return;
    }
    //
    EditorApp.Companion.getInstance().init(this, ASSETS_ANDROID_XIAOYING_LICENCE);
    initApplicationOver = true;
  }

  /**
   * 获取当前进程名字
   */
  public static String getCurProcessName(Context ctx) {
    if (ctx == null) {
      return null;
    }
    int pid = android.os.Process.myPid();
    ActivityManager mActivityManager =
        (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
    if (mActivityManager != null) {
      List<ActivityManager.RunningAppProcessInfo> appProcesses =
          mActivityManager.getRunningAppProcesses();
      if (appProcesses != null && appProcesses.size() > 0) {
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
          if (appProcess.pid == pid) {
            return appProcess.processName;
          }
        }
      }
    }
    return null;
  }

  public static void init(Application application) {
    sApplication = application;
  }

  public static Application getIns() {
    return sApplication;
  }
}
