package com.quvideo.application.slide;

import android.app.Activity;
import android.content.Intent;
import com.quvideo.application.EditorConst;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.mobile.engine.error.SDKErrCode;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import java.util.ArrayList;

public class SlideTemplate extends SimpleTemplate {

  private int minCount;
  private int maxCount;

  public SlideTemplate(long templateId, String title, int resId, int minCount, int maxCount) {
    super(templateId, title, resId);
    this.minCount = minCount;
    this.maxCount = maxCount;
  }

  public int getMinCount() {
    return minCount;
  }

  public int getMaxCount() {
    return maxCount;
  }

  @Override public void onClick(Activity activity) {
    gotoSlide(activity);
  }

  public void gotoSlide(Activity activity) {
    //update settings
    GallerySettings settings = new GallerySettings.Builder()
        .minSelectCount(minCount)
        .maxSelectCount(maxCount)
        .showMode(GalleryDef.MODE_PHOTO)
        .build();

    GalleryClient.getInstance().initSetting(settings);
    //enter gallery
    GalleryClient.getInstance().performLaunchGallery(activity);

    GalleryClient.getInstance().initProvider(new IGalleryProvider() {
      @Override
      public boolean checkFileEditAble(String filePath) {
        int res = MediaFileUtils.checkFileEditAble(filePath);
        return res == SDKErrCode.RESULT_OK;
      }

      @Override
      public void onGalleryFileDone(ArrayList<MediaModel> mediaList) {
        super.onGalleryFileDone(mediaList);
        ArrayList<String> albumChoose = new ArrayList<>();
        if (mediaList != null && mediaList.size() > 0) {
          for (MediaModel item : mediaList) {
            albumChoose.add(item.getFilePath());
          }
        }
        Intent intent = new Intent(activity, SlideShowActivity.class);
        intent.putExtra(EditorConst.INTENT_EXT_KEY_ALBUM, albumChoose);
        intent.putExtra(EditorConst.INTENT_EXT_KEY_SLIDE_THEMEID, getTemplateId());
        activity.startActivity(intent);
      }
    });
  }
}
