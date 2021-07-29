package com.quvideo.application.superedit;

import android.app.Activity;
import android.widget.Toast;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.ToastUtils;

/**
 *
 */
public class ZXingManager {

  public static String ZXING_RESULT_QRMSG = "result_qrmsg";

  public static boolean isHadSuperZXing() {
    return false;
  }

  public static void go2CaptureActivity(Activity activity, int requestCode) {
    ToastUtils.show(activity.getApplicationContext(), R.string.mn_edit_tips_no_support, Toast.LENGTH_LONG);
  }
}
