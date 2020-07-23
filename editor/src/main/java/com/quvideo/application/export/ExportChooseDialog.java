package com.quvideo.application.export;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.quvideo.application.editor.R;
import com.quvideo.mobile.engine.QEEngineClient;
import com.quvideo.mobile.engine.model.export.ExportParams;

public class ExportChooseDialog extends Dialog implements View.OnClickListener {

  private View contentView;
  private View rootView;
  private RadioGroup mRGFormat, mRGResolution, mRGHardware, mRGFps;
  private RadioButton mRB4k;
  private Button mBtnConfirm, mBtnCancel;
  private ExportParams exportParams = new ExportParams();

  private String fileExt = ".mp4";

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
    contentView = LayoutInflater.from(context).inflate(R.layout.editor_export_choose_dialog_layout, null);
    mRGFormat = contentView.findViewById(R.id.rg_format);
    mRGResolution = contentView.findViewById(R.id.rg_resolution);
    mRGHardware = contentView.findViewById(R.id.rg_hardware);
    mRGFps = contentView.findViewById(R.id.rg_fps);
    mRB4k = contentView.findViewById(R.id.rb_resolution_4k);

    mBtnConfirm = contentView.findViewById(R.id.btn_confirm);
    mBtnCancel = contentView.findViewById(R.id.btn_cancel);

    initItemShowState();
    mBtnConfirm.setOnClickListener(this);
    mBtnCancel.setOnClickListener(this);
    exportParams.customFps = 30;
    exportParams.expType = ExportParams.VIDEO_EXP_TYPE_720P;
  }

  private void initItemShowState() {
    if (QEEngineClient.isHD4KSupport()) {
      mRB4k.setVisibility(View.VISIBLE);
      mRB4k.setText("4K");
    } else if (QEEngineClient.isHD2KSupport()) {
      mRB4k.setVisibility(View.VISIBLE);
      mRB4k.setText("2K");
    } else {
      mRB4k.setVisibility(View.GONE);
    }

    mRGFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        exportParams.isGif = checkedId == R.id.rb_format_gif;
        exportParams.isWebp = checkedId == R.id.rb_format_webp;
        if (checkedId == R.id.rb_format_gif) {
          fileExt = ".gif";
        } else if (checkedId == R.id.rb_format_webp) {
          fileExt = ".webp";
        } else {
          fileExt = ".mp4";
        }
      }
    });
    mRGResolution.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_resolution_4k) {
          if (QEEngineClient.isHD4KSupport()) {
            exportParams.expType = ExportParams.VIDEO_EXP_TYPE_4KHD;
          } else {
            exportParams.expType = ExportParams.VIDEO_EXP_TYPE_2KHD;
          }
        } else if (checkedId == R.id.rb_resolution_1080) {
          exportParams.expType = ExportParams.VIDEO_EXP_TYPE_1080P;
        } else if (checkedId == R.id.rb_resolution_720) {
          exportParams.expType = ExportParams.VIDEO_EXP_TYPE_720P;
        } else {
          exportParams.expType = ExportParams.VIDEO_EXP_TYPE_NORMAL;
        }
      }
    });
    mRGFps.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        exportParams.customFps = checkedId == R.id.rb_fps_60 ? 30 : 60;
      }
    });
    mRGHardware.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        exportParams.isSoftwareCodec = checkedId == R.id.rb_hardware_soft;
      }
    });
  }

  @Override public void show() {
    if (contentView != null) {
      this.setContentView(contentView);
    }
    super.show();
  }

  @Override public void onClick(View v) {
    if (mOnDialogItemListener == null) {
      return;
    }
    if (v.equals(mBtnCancel)) {
      dismiss();
    } else if (v.equals(mBtnConfirm)) {
      long systemTime = System.currentTimeMillis();
      exportParams.outputPath = "/sdcard/ExportTest/Export_Video_Test_" + systemTime + fileExt;
      dismiss();
      mOnDialogItemListener.onConfirmExport(exportParams);
    }
  }

  public interface OnDialogItemListener {

    void onConfirmExport(ExportParams exportParams);
  }
}
