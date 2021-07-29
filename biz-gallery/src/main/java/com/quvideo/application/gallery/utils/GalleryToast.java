package com.quvideo.application.gallery.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.quvideo.application.gallery.R;

/**
 * @desc gallery toast util
 */
public class GalleryToast {

  private static Toast toast;

  public static void show(@NonNull Context context, String name) {
    View view = null;
    if (toast != null) {
      view = toast.getView();
      toast.cancel();
    }
    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.gallery_toast_layout, null);
    }

    toast = new Toast(context);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.setDuration(Toast.LENGTH_SHORT);

    TextView contentTv = view.findViewById(R.id.toast_content);
    if (contentTv != null && name != null) {
      contentTv.setText(name);
    }
    toast.setView(view);
    toast.show();
  }
}
