package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.error.SDKErrCode;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.ClipBgData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPBackground;
import java.util.ArrayList;

public class EditClipBGDialog extends BaseMenuView {

  private CustomSeekbarPop mCustomSeekbarPop;

  private View btnNone;
  private View btnBlur;
  private View btnCustom;
  private View btnColor1;
  private View btnColor2;
  private View btnGradual;

  private int clipIndex;

  public EditClipBGDialog(Context context, MenuContainer container, IQEWorkSpace workSpace, int clipIndex) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipBG;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_clip_background;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    mCustomSeekbarPop.setVisibility(INVISIBLE);

    btnNone = view.findViewById(R.id.btnNone);
    btnBlur = view.findViewById(R.id.btnBlur);
    btnCustom = view.findViewById(R.id.btnCustom);
    btnColor1 = view.findViewById(R.id.btnColor1);
    btnColor2 = view.findViewById(R.id.btnColor2);
    btnGradual = view.findViewById(R.id.btnGradual);

    btnNone.setOnClickListener(mOnClickListener);
    btnBlur.setOnClickListener(mOnClickListener);
    btnCustom.setOnClickListener(mOnClickListener);
    btnColor1.setOnClickListener(mOnClickListener);
    btnColor2.setOnClickListener(mOnClickListener);
    btnGradual.setOnClickListener(mOnClickListener);

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
            ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
            if (clipData != null) {
              ClipBgData clipBgData = clipData.getClipBgData();
              if (clipBgData != null) {
                if (clipBgData.getClipBgType() == ClipBgData.ClipBgType.COLOR) {
                  clipBgData.colorAngle = progress;
                } else {
                  clipBgData.blurLen = progress;
                }
                ClipOPBackground clipOPBackground = new ClipOPBackground(clipIndex, clipBgData);
                mWorkSpace.handleOperation(clipOPBackground);
              }
            }
          }
        }));

    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    if (clipData.getClipBgData() != null) {
      if (clipData.getClipBgData().getClipBgType() == ClipBgData.ClipBgType.BLUR) {
        mCustomSeekbarPop.updateRange("0", "100", new CustomSeekbarPop.SeekRange(0, 100));
        mCustomSeekbarPop.setProgress(clipData.getClipBgData().blurLen);
        mCustomSeekbarPop.setVisibility(VISIBLE);
      } else if (clipData.getClipBgData().getClipBgType() == ClipBgData.ClipBgType.PICTURE) {
        mCustomSeekbarPop.updateRange("0", "100", new CustomSeekbarPop.SeekRange(0, 100));
        mCustomSeekbarPop.setProgress(clipData.getClipBgData().blurLen);
        mCustomSeekbarPop.setVisibility(VISIBLE);
      } else if (clipData.getClipBgData().getClipBgType() == ClipBgData.ClipBgType.COLOR) {
        if (clipData.getClipBgData().colorArray.length > 1) {
          mCustomSeekbarPop.updateRange("0", "360", new CustomSeekbarPop.SeekRange(0, 360));
          mCustomSeekbarPop.setProgress(clipData.getClipBgData().colorAngle);
          mCustomSeekbarPop.setVisibility(VISIBLE);
        }
      } else {

      }
    }
  }

  private OnClickListener mOnClickListener = new OnClickListener() {
    @Override public void onClick(View v) {
      if (v.equals(btnNone)) {
        mCustomSeekbarPop.setVisibility(INVISIBLE);
        ClipOPBackground clipOPBackground = new ClipOPBackground(clipIndex, null);
        mWorkSpace.handleOperation(clipOPBackground);
      } else if (v.equals(btnBlur)) {
        ClipBgData clipBgData = new ClipBgData(50);
        ClipOPBackground clipOPBackground = new ClipOPBackground(clipIndex, clipBgData);
        mWorkSpace.handleOperation(clipOPBackground);
        mCustomSeekbarPop.updateRange("0", "100", new CustomSeekbarPop.SeekRange(0, 100));
        mCustomSeekbarPop.setProgress(50);
        mCustomSeekbarPop.setVisibility(VISIBLE);
      } else if (v.equals(btnCustom)) {
        replaceBGPic();
      } else if (v.equals(btnColor1)) {
        mCustomSeekbarPop.setVisibility(INVISIBLE);
        ClipBgData clipBgData = new ClipBgData(new int[] {
            ContextCompat.getColor(getContext(), R.color.color_fe3d42),
        }, 0);
        ClipOPBackground clipOPBackground = new ClipOPBackground(clipIndex, clipBgData);
        mWorkSpace.handleOperation(clipOPBackground);
      } else if (v.equals(btnColor2)) {
        mCustomSeekbarPop.setVisibility(INVISIBLE);
        ClipBgData clipBgData = new ClipBgData(new int[] {
            ContextCompat.getColor(getContext(), R.color.color_3493f2),
        }, 0);
        ClipOPBackground clipOPBackground = new ClipOPBackground(clipIndex, clipBgData);
        mWorkSpace.handleOperation(clipOPBackground);
      } else if (v.equals(btnGradual)) {
        ClipBgData clipBgData = new ClipBgData(new int[] {
            ContextCompat.getColor(getContext(), R.color.color_fe3d42),
            ContextCompat.getColor(getContext(), R.color.color_3493f2),
        }, 0);
        ClipOPBackground clipOPBackground = new ClipOPBackground(clipIndex, clipBgData);
        mWorkSpace.handleOperation(clipOPBackground);
        mCustomSeekbarPop.updateRange("0", "360", new CustomSeekbarPop.SeekRange(0, 360));
        mCustomSeekbarPop.setProgress(0);
        mCustomSeekbarPop.setVisibility(VISIBLE);
      } else {
        return;
      }
    }
  };

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
    GalleryClient.getInstance().performLaunchGallery(getActivity());

    GalleryClient.getInstance().initProvider(new IGalleryProvider() {
      @Override
      public boolean checkFileEditAble(String filePath) {
        int res = MediaFileUtils.checkFileEditAble(filePath);
        return res == SDKErrCode.RESULT_OK;
      }

      @Override
      public void onGalleryFileDone(ArrayList<MediaModel> mediaList) {
        super.onGalleryFileDone(mediaList);
        if (mediaList != null && mediaList.size() > 0 && mWorkSpace != null) {
          ClipBgData clipBgData = new ClipBgData(mediaList.get(0).getFilePath(), 50);
          ClipOPBackground clipOPBackground = new ClipOPBackground(clipIndex, clipBgData);
          mWorkSpace.handleOperation(clipOPBackground);
          mCustomSeekbarPop.updateRange("0", "100", new CustomSeekbarPop.SeekRange(0, 100));
          mCustomSeekbarPop.setProgress(50);
          mCustomSeekbarPop.setVisibility(VISIBLE);
        }
      }
    });
  }

  @Override protected void releaseAll() {
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_background);
  }
}
