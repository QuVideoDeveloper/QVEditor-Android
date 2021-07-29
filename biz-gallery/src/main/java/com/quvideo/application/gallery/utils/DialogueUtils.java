package com.quvideo.application.gallery.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.quvideo.application.gallery.BuildConfig;
import com.quvideo.application.gallery.R;

public class DialogueUtils {

  private static ProgressDialog mDialog = null;


  public static synchronized void showModalProgressDialogue(Context context, int strID,
      DialogInterface.OnCancelListener listener, boolean cancelable) {
    if (mDialog != null) {
      dismissModalProgressDialogue();
    }
    if (!isActivityAlive(context)) {
      return;
    }

    mDialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    try {
      mDialog.show();
    } catch (Throwable e) {
      if (BuildConfig.DEBUG) {
        e.printStackTrace();
      }
      return;
    }
    try {
      mDialog.setContentView(R.layout.viva_gallery_simple_dialogue_content);

      TextView txtView = mDialog.findViewById(R.id.check_update_dialog_txt);
      if (strID == -1) {
        txtView.setVisibility(View.GONE);
      } else {
        txtView.setVisibility(View.VISIBLE);
        txtView.setText(strID);
      }
      txtView.setVisibility(View.GONE);
      mDialog.setCancelable(cancelable);
      mDialog.setCanceledOnTouchOutside(false);
      if (listener != null && cancelable) {
        mDialog.setOnCancelListener(listener);
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  public static synchronized void dismissModalProgressDialogue() {
    if (null != mDialog) {
      try {
        mDialog.dismiss();
      } catch (Exception e) {
      }
      mDialog = null;
    }
  }

  private static boolean isActivityAlive(Context context) {
    return !(!(context instanceof Activity)
        || ((Activity) context).getWindow() == null
        || ((Activity) context).isFinishing());
  }


}
