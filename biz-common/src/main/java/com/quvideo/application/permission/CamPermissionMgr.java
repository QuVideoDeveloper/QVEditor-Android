package com.quvideo.application.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import com.quvideo.application.common.R;

public class CamPermissionMgr {

  private static final int REQUEST_VIDEO_PERMISSIONS = 1;
  private static final String FRAGMENT_DIALOG = "permission_dialog";

  private static final String[] VIDEO_PERMISSIONS = {
      Manifest.permission.CAMERA,
      Manifest.permission.RECORD_AUDIO,
  };

  public static boolean hasPermissionsGranted(Activity activity) {
    for (String permission : VIDEO_PERMISSIONS) {
      if (ActivityCompat.checkSelfPermission(activity, permission)
          != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  /**
   * Requests permissions needed for recording video.
   */
  public static void requestVideoPermissions(AppCompatActivity activity) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, VIDEO_PERMISSIONS[0])
        && ActivityCompat.shouldShowRequestPermissionRationale(activity, VIDEO_PERMISSIONS[1])) {
      new ConfirmationDialog().show(activity.getSupportFragmentManager(), FRAGMENT_DIALOG);
    } else {
      ActivityCompat.requestPermissions(activity, VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
    }
  }

  public static boolean checkCamPermissionValid(AppCompatActivity activity, int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
      if (grantResults.length == VIDEO_PERMISSIONS.length) {
        for (int result : grantResults) {
          if (result != PackageManager.PERMISSION_GRANTED) {
            ErrorDialog.newInstance(activity.getString(R.string.mn_cam_permission_request))
                .show(activity.getSupportFragmentManager(), FRAGMENT_DIALOG);
            return false;
          }
        }

        return true;
      }

      ErrorDialog.newInstance(activity.getString(R.string.mn_cam_permission_request))
          .show(activity.getSupportFragmentManager(), FRAGMENT_DIALOG);
    }

    return false;
  }

  public static class ConfirmationDialog extends DialogFragment {

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      final Activity activity = getActivity();
      return new AlertDialog.Builder(getActivity())
          .setMessage(R.string.mn_cam_permission_request)
          .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              if (activity != null) {
                ActivityCompat.requestPermissions(activity, VIDEO_PERMISSIONS,
                    REQUEST_VIDEO_PERMISSIONS);
              }
            }
          })
          .setNegativeButton(android.R.string.cancel,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  if (activity != null) {
                    activity.finish();
                  }
                }
              })
          .create();
    }
  }
}
