package com.quvideo.application.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ErrorDialog extends DialogFragment {

  private static final String ARG_MESSAGE = "message";

  static ErrorDialog newInstance(String message) {
    ErrorDialog dialog = new ErrorDialog();
    Bundle args = new Bundle();
    args.putString(ARG_MESSAGE, message);
    dialog.setArguments(args);
    return dialog;
  }

  @NonNull @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Activity activity = getActivity();
    AlertDialog.Builder builder = new AlertDialog.Builder(activity)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            if (activity != null) {
              activity.finish();
            }
          }
        });
    if (getArguments() != null) {
      builder.setMessage(getArguments().getString(ARG_MESSAGE));
    }
    return builder.create();
  }
}
