package com.quvideo.application.frame.view;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import androidx.core.content.ContextCompat;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.error.SDKErrCode;
import com.quvideo.mobile.engine.process.param.BGParam;
import com.quvideo.mobile.engine.process.param.BGPosParam;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class FrameBGMenuPopwin extends PopupWindow {
  private Activity mActivity;

  private CustomSeekbarPop mCustomSeekbarPop;

  private View btnRotation;
  private View btnZoomOut;
  private View btnZoomIn;

  private View btnNone;
  private View btnBlur;
  private View btnCustom;
  private View btnColor1;
  private View btnColor2;
  private View btnGradual;

  private BGParam mBGParam;

  private OnParamSelectCallback mCallback;

  public FrameBGMenuPopwin(Activity activity, OnParamSelectCallback callback) {
    this.mActivity = activity;
    this.mCallback = callback;
    mBGParam = new BGParam(new int[] {
        ContextCompat.getColor(mActivity, R.color.black),
    }, 0);
    initView();
  }

  private void initView() {
    View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_frame_bg_menu_pop, null, false);

    view.findViewById(R.id.rootView).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dismiss();
      }
    });

    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    mCustomSeekbarPop.setVisibility(INVISIBLE);
    mCustomSeekbarPop.setShowPop(false);

    btnNone = view.findViewById(R.id.btnNone);
    btnBlur = view.findViewById(R.id.btnBlur);
    btnCustom = view.findViewById(R.id.btnCustom);
    btnColor1 = view.findViewById(R.id.btnColor1);
    btnColor2 = view.findViewById(R.id.btnColor2);
    btnGradual = view.findViewById(R.id.btnGradual);

    btnRotation = view.findViewById(R.id.btnRotation);
    btnZoomOut = view.findViewById(R.id.btnFitIn);
    btnZoomIn = view.findViewById(R.id.btnFitOut);

    btnNone.setOnClickListener(mOnClickListener);
    btnBlur.setOnClickListener(mOnClickListener);
    btnCustom.setOnClickListener(mOnClickListener);
    btnColor1.setOnClickListener(mOnClickListener);
    btnColor2.setOnClickListener(mOnClickListener);
    btnGradual.setOnClickListener(mOnClickListener);

    btnRotation.setOnClickListener(mOnClickListener);
    btnZoomOut.setOnClickListener(mOnClickListener);
    btnZoomIn.setOnClickListener(mOnClickListener);

    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("100")
        .progress(50)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 100))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            if (mBGParam.bgType == BGParam.BgType.COLOR) {
              mBGParam.colorAngle = progress;
            } else {
              mBGParam.blurLen = progress;
            }
            if (mCallback != null) {
              mCallback.onParamChange(mBGParam);
            }
          }
        }));
    initPopWindow(view);
  }

  /**
   * 替换数据源
   */
  public void replaceBGPic() {
    //update settings
    GallerySettings settings = new GallerySettings.Builder()
        .minSelectCount(1)
        .maxSelectCount(1)
        .showMode(GalleryDef.MODE_PHOTO)
        .build();

    GalleryClient.getInstance().initSetting(settings);
    //enter gallery
    GalleryClient.getInstance().performLaunchGallery(mActivity);

    GalleryClient.getInstance().initProvider(new IGalleryProvider() {
      @Override
      public boolean checkFileEditAble(String filePath) {
        int res = MediaFileUtils.checkFileEditAble(filePath);
        return res == SDKErrCode.RESULT_OK;
      }

      @Override
      public void onGalleryFileDone(ArrayList<MediaModel> mediaList) {
        super.onGalleryFileDone(mediaList);
        if (mediaList != null && mediaList.size() > 0) {
          mBGParam.bgType = BGParam.BgType.PICTURE;
          mBGParam.imagePath = mediaList.get(0).getFilePath();
          mCustomSeekbarPop.updateRange("0", "100", new CustomSeekbarPop.SeekRange(0, 100));
          mCustomSeekbarPop.setProgress(mBGParam.blurLen);
          mCustomSeekbarPop.setVisibility(VISIBLE);
          if (mCallback != null) {
            mCallback.onParamChange(mBGParam);
          }
        }
      }
    });
  }

  private View.OnClickListener mOnClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      if (v.equals(btnNone)) {
        mCustomSeekbarPop.setVisibility(INVISIBLE);
        mBGParam.bgType = BGParam.BgType.COLOR;
        mBGParam.colorArray = new int[] {
            ContextCompat.getColor(mActivity, R.color.black)
        };
        mBGParam.colorAngle = 0;
      } else if (v.equals(btnBlur)) {
        mBGParam.bgType = BGParam.BgType.BLUR;
        mBGParam.imagePath = null;
        mCustomSeekbarPop.updateRange("0", "100", new CustomSeekbarPop.SeekRange(0, 100));
        mCustomSeekbarPop.setProgress(mBGParam.blurLen);
        mCustomSeekbarPop.setVisibility(VISIBLE);
      } else if (v.equals(btnCustom)) {
        //
        replaceBGPic();
        return;
      } else if (v.equals(btnColor1)) {
        mCustomSeekbarPop.setVisibility(INVISIBLE);
        mBGParam.bgType = BGParam.BgType.COLOR;
        mBGParam.imagePath = null;
        mBGParam.colorArray = new int[] {
            ContextCompat.getColor(mActivity, R.color.color_fe3d42),
        };
      } else if (v.equals(btnColor2)) {
        mCustomSeekbarPop.setVisibility(INVISIBLE);
        mBGParam.bgType = BGParam.BgType.COLOR;
        mBGParam.imagePath = null;
        mBGParam.colorArray = new int[] {
            ContextCompat.getColor(mActivity, R.color.color_3493f2),
        };
      } else if (v.equals(btnGradual)) {
        mBGParam.bgType = BGParam.BgType.COLOR;
        mBGParam.imagePath = null;
        mBGParam.colorArray = new int[] {
            ContextCompat.getColor(mActivity, R.color.color_fe3d42),
            ContextCompat.getColor(mActivity, R.color.color_3493f2),
        };
        mCustomSeekbarPop.updateRange("0", "360", new CustomSeekbarPop.SeekRange(0, 360));
        mCustomSeekbarPop.setProgress(mBGParam.colorAngle);
        mCustomSeekbarPop.setVisibility(VISIBLE);
      } else if (v.equals(btnRotation)) {
        if (mBGParam.posParam == null) {
          mBGParam.posParam = new BGPosParam();
        }
        mBGParam.posParam.degree = (mBGParam.posParam.degree + 90) % 360;
      } else if (v.equals(btnZoomOut)) {
        if (mBGParam.posParam == null) {
          mBGParam.posParam = new BGPosParam();
        }
        mBGParam.posParam.widthScale *= 0.9f;
        mBGParam.posParam.heightScale *= 0.9f;
      } else if (v.equals(btnZoomIn)) {
        if (mBGParam.posParam == null) {
          mBGParam.posParam = new BGPosParam();
        }
        mBGParam.posParam.widthScale /= 0.9f;
        mBGParam.posParam.heightScale /= 0.9f;
      }
      if (mCallback != null) {
        mCallback.onParamChange(mBGParam);
      }
    }
  };

  private void initPopWindow(View view) {
    this.setContentView(view);
    this.setOutsideTouchable(true);
    this.setTouchable(true);
    this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
    this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
    this.setInputMethodMode(INPUT_METHOD_NOT_NEEDED);
    this.setFocusable(false);
    this.setBackgroundDrawable(new BitmapDrawable());
  }

  /**
   * 背景选择处理
   */
  public interface OnParamSelectCallback {
    void onParamChange(BGParam bgParam);
  }
}
