package com.quvideo.application.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.IntDef;
import androidx.annotation.RequiresApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wangjieming on 28/12/2017.
 * 申请SystemAlertWindow权限.signature权限
 */

public class SignaturePermissionRequest {

  private Activity mTarget;//目标页面,只有有context就可以.
  private VivaSignaturePermissionListener mSignaturePermissionListener;
  private int permissionType = 0;

  public SignaturePermissionRequest(Activity target) {
    mTarget = target;
  }

  public static boolean hasSystemAlertDialogPermission(Context context) {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
        || PermissionHelper.targetSdkVersionBelowAndroidM(context)
        || Settings.canDrawOverlays(context);
  }

  public static boolean hasWriteSettingPermission(Context context) {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
        || PermissionHelper.targetSdkVersionBelowAndroidM(context)
        || Settings.System.canWrite(context);
  }

  public SignaturePermissionRequest setSignaturePermissionListener(
      VivaSignaturePermissionListener listener) {
    this.mSignaturePermissionListener = listener;
    return this;
  }

  /**
   * 装填请求类型
   */
  public SignaturePermissionRequest permission(@PermissionType int permissionType) {
    this.permissionType = permissionType;
    return this;
  }

  /**
   * 发动请求
   */
  public void request() {
    if (permissionType == 0) {
      throw new IllegalArgumentException(
          "Miss Call permission(SignaturePermissionRequest.MODE_SYSTEM_ALERT_WINDOWS) or permission(SignaturePermissionRequest.MODE_WRITE_SETTING)");
    }

    Context context = mTarget;
    if (PermissionHelper.targetSdkVersionBelowAndroidM(context)) {
      //targetSdkVersion以下版本，暂时失效。
      signaturePermissionListener.onPermissionGrant();
      return;
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      //手机版本在M以下，无需申请
      signaturePermissionListener.onPermissionGrant();
    } else {
      startPermissionProxyActivity(mTarget);
    }
  }

  /**
   * 启动权限申请代理页面.
   */
  @RequiresApi(api = Build.VERSION_CODES.M) private void startPermissionProxyActivity(
      Activity target) {
    PermissionProxyActivity.setSignaturePermissionListener(signaturePermissionListener);
    Intent intent = new Intent(target, PermissionProxyActivity.class);
    int modeType = 0;
    if (permissionType == MODE_SYSTEM_ALERT_WINDOWS) {
      modeType = PermissionProxyActivity.MODE_PERMISSION_SYSTEM_ALERT_WINDOWS;
    } else if (permissionType == MODE_WRITE_SETTING) {
      modeType = PermissionProxyActivity.MODE_PERMISSION_WRITE_SETTING;
    }
    intent.putExtra(PermissionProxyActivity.KEY_MODE_TYPE, modeType);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    target.startActivity(intent);
  }

  private PermissionProxyActivity.SignaturePermissionListener signaturePermissionListener =
      new PermissionProxyActivity.SignaturePermissionListener() {
        @Override public void onPermissionGrant() {
          if (mSignaturePermissionListener != null) {
            mSignaturePermissionListener.onPermissionGrant();
          }
        }

        @Override public void onPermissionDenied() {
          if (mSignaturePermissionListener != null) {
            mSignaturePermissionListener.onPermissionDenied();
          }
        }
      };

  public static final int MODE_SYSTEM_ALERT_WINDOWS = 1;
  public static final int MODE_WRITE_SETTING = 2;

  @IntDef({
      MODE_SYSTEM_ALERT_WINDOWS, MODE_WRITE_SETTING
  }) @Retention(RetentionPolicy.SOURCE) private @interface PermissionType {

  }
}
