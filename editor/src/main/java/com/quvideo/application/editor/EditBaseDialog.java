package com.quvideo.application.editor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.ItemOnClickListener;

/**
 * Created by santa on 2020-04-30.
 */
public abstract class EditBaseDialog extends DialogFragment implements View.OnClickListener, ItemOnClickListener {

    private ItemOnClickListener mItemOnClickListener;
    public EditBaseDialog(ItemOnClickListener l) {
        mItemOnClickListener = l;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        assert activity != null;
        View view = getDialogView(activity);

        ((TextView)view.findViewById(R.id.title)).setText(getBottomTitle());
        view.findViewById(R.id.confirm).setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(view);

        return builder.create();
    }

    protected abstract View getDialogView(Activity activity);

    protected abstract String getBottomTitle();

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        window.setAttributes(windowParams);

        WindowManager manager = window.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        getDialog().setCancelable(false);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().setOnKeyListener((dialog, keyCode, event) -> {;
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismissAllowingStateLoss();
                return true;
            }
            return false;
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        dismissAllowingStateLoss();
    }

    @Override
    public void onClick(View view, EditOperate operate) {
        if (mItemOnClickListener != null) {
            mItemOnClickListener.onClick(view, operate);
        }
        dismissAllowingStateLoss();
    }
}
