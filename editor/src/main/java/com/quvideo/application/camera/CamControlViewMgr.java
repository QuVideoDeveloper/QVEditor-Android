package com.quvideo.application.camera;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.mobile.engine.camera.XYCameraConst;

class CamControlViewMgr {

  interface OnControlListener {

    void onFrontBtnClick();

    boolean onFlashBtnClick();

    void onZoomBtnClick();

    void onExposureBtnClick();

    XYCameraConst.RatioMode onRatioBtnClick();

    void onCaptureBtnClick();

    void onFilterBtnClick();

    void onFbModeBtnClick();

    void onRecordBtnClick();

    void onRecorderDoneBtnClick();

    void onClipDelBtnClick();

    void onBackBtnClick();

    void onAddMusicBtnClick();
  }

  private View rootView;
  private AppCompatImageView btnDel;
  private AppCompatTextView tvRecorderTime;
  private AppCompatTextView btnRecorderDone;
  private AppCompatTextView btnRecorder;

  private AppCompatTextView btnFront;
  private AppCompatTextView btnFlash;
  private AppCompatTextView btnZoom;
  private AppCompatTextView btnExposure;
  private AppCompatTextView btnRatio;
  private AppCompatTextView btnFilter;
  private AppCompatTextView btnFbMode;
  private AppCompatImageView btnCapture;
  private AppCompatTextView btnAddMusic;
  private AppCompatImageView btnBack;

  private boolean isRatioValid = true;

  void bindView(@NonNull final Activity activity,
      @NonNull final OnControlListener listener) {
    rootView = activity.findViewById(R.id.camControlView);
    if (rootView == null) {
      throw new RuntimeException("invalid camera control view");
    }

    btnFront = activity.findViewById(R.id.btnFront);
    btnFront.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onFrontBtnClick();
      }
    });

    btnFlash = activity.findViewById(R.id.btnFlash);
    btnFlash.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        boolean isFlashOn = listener.onFlashBtnClick();
        btnFlash.setSelected(isFlashOn);
      }
    });

    btnZoom = activity.findViewById(R.id.btnZoom);
    btnZoom.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onZoomBtnClick();
      }
    });

    btnExposure = activity.findViewById(R.id.btnExposure);
    btnExposure.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onExposureBtnClick();
      }
    });

    btnRatio = activity.findViewById(R.id.btnRatio);
    btnRatio.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        XYCameraConst.RatioMode ratioMode = listener.onRatioBtnClick();
        Drawable res;
        if (ratioMode == XYCameraConst.RatioMode.RATIO_4_3) {
          res = activity.getDrawable(R.drawable.cam_icon_ratio_4_3);
        } else if (ratioMode == XYCameraConst.RatioMode.RATIO_1_1) {
          res = activity.getDrawable(R.drawable.cam_icon_ratio_1_1);
        } else {
          res = activity.getDrawable(R.drawable.cam_icon_ratio_16_9);
        }

        if (res != null) {
          res.setBounds(0, 0, res.getIntrinsicWidth(), res.getIntrinsicHeight());
          btnRatio.setCompoundDrawables(null, res, null, null);
        }
      }
    });

    btnFilter = activity.findViewById(R.id.btnFilter);
    btnFilter.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onFilterBtnClick();
      }
    });

    btnCapture = activity.findViewById(R.id.btnCapture);
    btnCapture.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onCaptureBtnClick();
      }
    });
    if (!EditorApp.Companion.getInstance().getEditorConfig().isCaptureValid()) {
      btnCapture.setVisibility(View.GONE);
    }

    btnFbMode = activity.findViewById(R.id.btnBeautyFace);
    btnFbMode.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onFbModeBtnClick();
      }
    });

    btnRecorder = activity.findViewById(R.id.btnRecording);
    btnRecorder.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onRecordBtnClick();
      }
    });

    btnDel = activity.findViewById(R.id.btnDel);
    btnDel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onClipDelBtnClick();
      }
    });
    btnRecorderDone = activity.findViewById(R.id.btnRecorderDone);
    btnRecorderDone.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onRecorderDoneBtnClick();
      }
    });
    tvRecorderTime = activity.findViewById(R.id.tvRecorderTime);

    btnBack = activity.findViewById(R.id.btnBack);
    btnBack.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onBackBtnClick();
      }
    });

    btnAddMusic = activity.findViewById(R.id.btnAddMusic);
    btnAddMusic.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        listener.onAddMusicBtnClick();
      }
    });
    if (!EditorApp.Companion.getInstance().getEditorConfig().isMusicRecorderValid()) {
      btnAddMusic.setVisibility(View.GONE);
    }
  }

  void showView() {
    rootView.setVisibility(View.VISIBLE);
  }

  void hideView() {
    rootView.setVisibility(View.GONE);
  }

  void onRecording(String recorderTime) {
    btnDel.setVisibility(View.INVISIBLE);
    btnRecorderDone.setVisibility(View.INVISIBLE);

    btnFront.setVisibility(View.INVISIBLE);
    btnExposure.setVisibility(View.INVISIBLE);
    btnZoom.setVisibility(View.INVISIBLE);
    btnFlash.setVisibility(View.INVISIBLE);
    btnRatio.setVisibility(View.INVISIBLE);
    btnFilter.setVisibility(View.INVISIBLE);
    btnFbMode.setVisibility(View.INVISIBLE);
    btnCapture.setVisibility(View.INVISIBLE);
    btnBack.setVisibility(View.INVISIBLE);
    btnAddMusic.setVisibility(View.INVISIBLE);

    tvRecorderTime.setVisibility(View.VISIBLE);
    tvRecorderTime.setTextColor(Color.RED);
    tvRecorderTime.setText(recorderTime);
  }

  void setRecordTime(String recorderTime) {
    tvRecorderTime.setText(recorderTime);
  }

  void onRecordStop(int clipCount) {
    btnFront.setVisibility(View.VISIBLE);
    btnExposure.setVisibility(View.VISIBLE);
    btnZoom.setVisibility(View.VISIBLE);
    btnFlash.setVisibility(View.VISIBLE);
    if (isRatioValid) {
      btnRatio.setVisibility(View.VISIBLE);
    }
    btnFilter.setVisibility(View.VISIBLE);
    btnFbMode.setVisibility(View.VISIBLE);
    if (EditorApp.Companion.getInstance().getEditorConfig().isCaptureValid()) {
      btnCapture.setVisibility(View.VISIBLE);
    }
    btnBack.setVisibility(View.VISIBLE);
    if (EditorApp.Companion.getInstance().getEditorConfig().isMusicRecorderValid()) {
      btnAddMusic.setVisibility(View.VISIBLE);
    }

    if (clipCount > 0) {
      btnDel.setVisibility(View.VISIBLE);
      btnRecorderDone.setVisibility(View.VISIBLE);
      tvRecorderTime.setVisibility(View.VISIBLE);
      tvRecorderTime.setTextColor(Color.WHITE);

      btnRecorder.setText(String.valueOf(clipCount));
    } else {
      btnDel.setVisibility(View.INVISIBLE);
      btnRecorderDone.setVisibility(View.INVISIBLE);
      tvRecorderTime.setVisibility(View.INVISIBLE);

      btnRecorder.setText("");
    }
  }

  void setMusicTitle(String title) {
    btnAddMusic.setText(title);
  }

  void setBtnRatioValid(boolean isValid) {
    isRatioValid = isValid;
    btnRatio.setVisibility(isRatioValid ? View.VISIBLE : View.GONE);
  }
}
