package com.quvideo.application.export;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import com.quvideo.application.editor.R;
import com.quvideo.mobile.engine.editor.utils.HDVideoUtils;
import com.quvideo.mobile.engine.model.export.ExportParams;

public class ExportChooseDialog extends Dialog implements View.OnClickListener {

  private View contentView;
  private View rootView;
  private RelativeLayout mLayout1080PItem, mLayout2K, mLayout4K;
  private RelativeLayout mLayout720HD;
  private RelativeLayout mLayoutNormal;

  public void setOnDialogItemListener(OnDialogItemListener mOnDialogItemListener) {
    this.mOnDialogItemListener = mOnDialogItemListener;
  }

  private OnDialogItemListener mOnDialogItemListener;

  /**
   *
   */
  public ExportChooseDialog(Context context) {
    super(context, R.style.editor_style_choose_dialog);
    this.setCancelable(true);
    contentView = LayoutInflater.from(context).inflate(R.layout.editor_export_hd_dialog_layout, null);
    rootView = contentView.findViewById(R.id.root_layout);
    mLayoutNormal = contentView.findViewById(R.id.normal_layout);
    mLayout720HD = contentView.findViewById(R.id.hd_layout);
    mLayout1080PItem = contentView.findViewById(R.id.hd_1080_layout);
    mLayout2K = contentView.findViewById(R.id.hd_2k_layout);
    mLayout4K = contentView.findViewById(R.id.hd_4k_layout);

    initItemShowState();
    rootView.setOnClickListener(this);
    mLayoutNormal.setOnClickListener(this);
  }

  private void initItemShowState() {
    mLayout720HD.setVisibility(View.VISIBLE);
    mLayout720HD.setOnClickListener(this);
    mLayout1080PItem.setVisibility(View.VISIBLE);
    mLayout1080PItem.setOnClickListener(this);
    if (HDVideoUtils.isHD2KSupport()) {
      mLayout2K.setVisibility(View.VISIBLE);
      mLayout2K.setOnClickListener(this);
    } else {
      mLayout2K.setVisibility(View.GONE);
    }
    if (HDVideoUtils.isHD4KSupport()) {
      mLayout4K.setVisibility(View.VISIBLE);
      mLayout4K.setOnClickListener(this);
    } else {
      mLayout4K.setVisibility(View.GONE);
    }
  }

  @Override public void show() {
    if (contentView != null) {
      this.setContentView(contentView);
    }
    super.show();
  }

  @Override public void onClick(View v) {
    dismiss();
    if (mOnDialogItemListener == null) {
      return;
    }
    if (v.equals(mLayout720HD)) {
      mOnDialogItemListener.onItemClick(ExportParams.VIDEO_EXP_TYPE_720P);
    } else if (v.equals(mLayout1080PItem)) {
      mOnDialogItemListener.onItemClick(ExportParams.VIDEO_EXP_TYPE_1080P);
    } else if (v.equals(mLayoutNormal)) {
      mOnDialogItemListener.onItemClick(ExportParams.VIDEO_EXP_TYPE_NORMAL);
    } else if (v.equals(mLayout2K)) {
      mOnDialogItemListener.onItemClick(ExportParams.VIDEO_EXP_TYPE_2KHD);
    } else if (v.equals(mLayout4K)) {
      mOnDialogItemListener.onItemClick(ExportParams.VIDEO_EXP_TYPE_4KHD);
    }
  }

  public interface OnDialogItemListener {

    void onItemClick(int expType);
  }
}
