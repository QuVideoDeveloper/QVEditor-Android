package com.quvideo.application.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.quvideo.application.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 代理申请权限的页面.透明的。
 * shouldShowRequestPermissionRationale 关键方法说明:
 * 申请权限前 false -->  申请后 true  第一次被拒绝
 * 申请权限前 true -->  申请后 false  这次拒绝点击了NeverAskAgain
 * 申请权限前 true -->   申请后 true  第二次及以上拒绝,但是未点击NeverAskAgain --> onNeverAskAgain
 * 申请权限前 false -->  申请后 false 请求前,就已经点击过了NeverAskAgain.  --> onAlwaysDenied
 */
@RequiresApi(api = Build.VERSION_CODES.M) public class PermissionProxyActivity extends Activity {

  public static final int MODE_RATIONALE = 1; //请求前先检查是否会弹出NeverAskAgain.UI层决定是否有必要弹出告知权限重要性的UI
  static final int MODE_PERMISSION = 2;//不检查.直接申请权限,
  static final int MODE_PERMISSION_WRITE_SETTING = 3;//代理跳转到WRITE_SETTING权限修改页面
  static final int MODE_PERMISSION_SYSTEM_ALERT_WINDOWS = 4;//代理跳转到SYSTEM_ALERT_WINDOWS权限修改页面。

  static final String KEY_INPUT_PERMISSIONS = "KEY_INPUT_PERMISSIONS";
  static final String KEY_MODE_TYPE = "KEY_MODE_TYPE";

  public static final int REQUEST_CODE_REQUEST_WRITE_SETTING = 1024;
  public static final int REQUEST_CODE_REQUEST_SYSTEM_ALERT_WINDOW = 2048;

  //只要有一个拒绝过就要通知UI层是否弹出提示.可能会有继续拒绝的风险.
  private boolean mRationale = false;

  private static PermissionListener sPermissionListener;
  private static SignaturePermissionListener sSignaturePermissionListener;

  public static void setPermissionListener(PermissionListener permissionListener) {
    sPermissionListener = permissionListener;
  }

  public static void setSignaturePermissionListener(
      SignaturePermissionListener signaturePermissionListener) {
    sSignaturePermissionListener = signaturePermissionListener;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String[] permissions = getIntent().getStringArrayExtra(KEY_INPUT_PERMISSIONS);
    int modeType = getIntent().getIntExtra(KEY_MODE_TYPE, 0);

    //Signature权限。
    if (signaturePermissionRequest(modeType)) {
      return;
    }
    //危险权限。
    if (permissions == null || sPermissionListener == null) {
      sPermissionListener = null;
      finish();
      return;
    }
    boolean rationale = shouldShowRequestPermissionRationale(Arrays.asList(permissions));
    if (modeType == MODE_RATIONALE && rationale) {
      //是否会出现NeverAskAgain.如果=true说明会出现,这时通知逻辑层,决定是否弹出提示告知权限的重要性.
      //回调UI层,处理提示之后.,UI层决定是否继续resumeRequest
      finish();
      sPermissionListener.onPermissionRationaleResult();
    } else {
      requestPermissions(permissions, 1);
    }
  }

  /**
   * Signature权限，要跳转到设置页面
   *
   * @return true
   */
  private boolean signaturePermissionRequest(@ModeType int modeType) {
    if (modeType == MODE_PERMISSION_WRITE_SETTING) {
      //Setting读写权限
      Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
          Uri.parse("package:" + getPackageName()));
      startActivityForResult(intent, REQUEST_CODE_REQUEST_WRITE_SETTING);
      return true;
    } else if (modeType == MODE_PERMISSION_SYSTEM_ALERT_WINDOWS) {
      //系统悬浮窗权限
      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
          Uri.parse("package:" + getPackageName()));
      startActivityForResult(intent, REQUEST_CODE_REQUEST_SYSTEM_ALERT_WINDOW);
      return true;
    }
    return false;
  }

  /**
   * 权限框中会出现不再提醒,提前告知用户重要性.
   */
  private boolean shouldShowRequestPermissionRationale(@NonNull List<String> permissions) {
    mRationale = false;
    for (String permission : permissions) {
      mRationale = shouldShowRequestPermissionRationale(permission);
      if (mRationale) {
        break;
      }
    }
    return mRationale;
  }

  /**
   * 危险权限请求的回调
   */
  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {

    if (sPermissionListener == null) {
      finish();
      return;
    }

    List<String> deniedList = new ArrayList<>();
    for (int i = 0; i < permissions.length; i++) {
      if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
        deniedList.add(permissions[i]);
      }
    }

    if (deniedList.isEmpty()) {
      sPermissionListener.onPermissionGrant();
    } else {
      if (!mRationale && !shouldShowRequestPermissionRationale(deniedList)) {
        sPermissionListener.onAlwaysDenied();
      } else {
        sPermissionListener.onPermissionDenied(deniedList);
      }
    }
    sPermissionListener = null;
    finish();
  }

  /**
   * 透明动画
   */
  @Override public void finish() {
    super.finish();
    overridePendingTransition(R.anim.viva_permission_activity_alpha,
        R.anim.viva_permission_activity_alpha_out);
  }

  /**
   * Signature权限，请求是依赖startActivityForResult的方式。
   * 返回结果之后。回调回去。
   */
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (sSignaturePermissionListener != null) {
      if (requestCode == REQUEST_CODE_REQUEST_SYSTEM_ALERT_WINDOW) {
        //如果是悬浮窗权限返回
        boolean hasSystemAlertWindowPermission =
            SignaturePermissionRequest.hasSystemAlertDialogPermission(this);
        if (hasSystemAlertWindowPermission) {
          sSignaturePermissionListener.onPermissionGrant();
        } else {
          sSignaturePermissionListener.onPermissionDenied();
        }
      } else if (requestCode == REQUEST_CODE_REQUEST_WRITE_SETTING) {
        //如果是系统设置写权限。返回
        boolean hasWriteSettingPermission =
            SignaturePermissionRequest.hasWriteSettingPermission(this);
        if (hasWriteSettingPermission) {
          sSignaturePermissionListener.onPermissionGrant();
        } else {
          sSignaturePermissionListener.onPermissionDenied();
        }
      }
    }

    finish();
  }

  /**
   * 危险权限
   */
  public interface PermissionListener {
    //void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults);

    void onPermissionGrant();

    void onPermissionDenied(List<String> deniedList);

    void onNeverAskAgain();

    void onAlwaysDenied();

    void onPermissionRationaleResult();
  }

  /**
   * WriteSetting和SystemAlertWindow权限
   */
  interface SignaturePermissionListener {

    void onPermissionGrant();

    void onPermissionDenied();
  }

  @IntDef({
      MODE_RATIONALE, MODE_PERMISSION, MODE_PERMISSION_SYSTEM_ALERT_WINDOWS,
      MODE_PERMISSION_WRITE_SETTING
  }) @Retention(RetentionPolicy.SOURCE) @interface ModeType {

  }
}
