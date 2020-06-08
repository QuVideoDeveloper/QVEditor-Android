package com.quvideo.application.gallery.board;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.utils.GSizeUtil;
import com.quvideo.application.gallery.widget.SpannableTextView;
import com.quvideo.application.utils.pop.Pop;
import com.quvideo.application.utils.pop.PopCallback;
import com.quvideo.application.utils.rx.RxViewUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by zhengjunfei on 2020-03-11
 */
public class BaseMediaBoardView extends RelativeLayout {
  public static final int DEF_NEGATIVE_ONE = -1;
  public static final int DEF_CHOOSE_POS = -2;
  public static final int DEF_FULL_CHOOSE_POS = Integer.MAX_VALUE;
  protected View mRootView;
  protected TextView mNextBtn;
  protected SpannableTextView mClipCountTv;

  protected MediaBoardCallback mMediaBoardCallback;
  protected boolean isExpand = true;


  public BaseMediaBoardView(Context context) {
    super(context);
    init();
  }

  public BaseMediaBoardView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public BaseMediaBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public BaseMediaBoardView(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  protected void init(){
    mRootView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
    mClipCountTv = mRootView.findViewById(R.id.txt_clip_count);
    mNextBtn = mRootView.findViewById(R.id.btn_next);
    RxViewUtil.setOnClickListener(view -> {
      Pop.showQuietly(view);
      ArrayList<MediaModel> mediaMissionList = getMediaMissionList();
      if (mMediaBoardCallback != null) {
        mMediaBoardCallback.onMediaSelectDone(mediaMissionList);
      }
    }, mNextBtn);
  }

  protected ArrayList<MediaModel> getMediaMissionList() {
    return null;
  }

  public void addMediaItem(List<MediaModel> list, int choosePos) {

  }

  public void addMediaItem(MediaModel model, boolean replace) {

  }

  public void removeMediaItem(MediaModel model) {

  }

  public boolean isMissionExist(MediaModel model) {
    return false;
  }

  public void updateOnlyMission(MediaModel model) {

  }

  public int getMediaBoardIndex(MediaModel model) {
    return 0;
  }

  public int getSelectedMediaCount() {
    return 0;
  }

  public void setMediaBoardCallback(MediaBoardCallback callback) {
    this.mMediaBoardCallback = callback;
  }

  protected void updateClipCount(int count) {
    GallerySettings gallerySettings = GalleryClient.getInstance().getGallerySettings();
    int minSelectCount = gallerySettings.getMinSelectCount();
    int maxSelectCount = gallerySettings.getMaxSelectCount();
    //1,无配置（至少1个）或 配置了Min（至少N个）
    //2,配置了最多或者配置了a~b个（请选择a~b个）
    //3,配置的最小==最大（只能选择N个）
    if (maxSelectCount == GallerySettings.NO_LIMIT_UP_FLAG) {
      String text = getContext().getString(
          R.string.mn_gallery_template_selected_count_no_max_description, minSelectCount);
      String tmpText = getContext().getString(
          R.string.mn_gallery_template_selected_count_no_max_description);

      int start = tmpText.indexOf("%d");
      int end = start + String.valueOf(minSelectCount).length();

      if (tmpText.contains("%d") && end <= text.length()) {
        mClipCountTv.setSpanText(text, start, end, Color.parseColor("#ff5e13"), null);
      } else {
        mClipCountTv.setText(text);
      }
    } else {
      if (minSelectCount == maxSelectCount) {
        String text = getContext().getString(
            R.string.mn_gallery_template_selected_count_fixed_description, minSelectCount);
        String tmpText = getContext().getString(
            R.string.mn_gallery_template_selected_count_fixed_description);

        int start = tmpText.indexOf("%d");
        int end = start + String.valueOf(minSelectCount).length();

        if (tmpText.contains("%d") && end <= text.length()) {
          mClipCountTv.setSpanText(text, start, end, Color.parseColor("#ff5e13"), null);
        } else {
          mClipCountTv.setText(text);
        }
      } else {
        String countStr = minSelectCount + "~" + maxSelectCount;
        String text =
            getContext().getString(R.string.mn_gallery_template_selected_count_description,
                countStr);
        String tmpText =
            getContext().getString(R.string.mn_gallery_template_selected_count_description);

        int start = tmpText.indexOf("%s");
        int end = start + countStr.length();

        if (tmpText.contains("%s") && end <= text.length()) {
          mClipCountTv.setSpanText(text, start, end, Color.parseColor("#ff5e13"), null);
        } else {
          mClipCountTv.setText(text);
        }
      }
    }

    mNextBtn.setAlpha(count >= minSelectCount ? 1.f : 0.5f);

    if (count > 0) {
      expand();
    } else {
      collapse();
    }
  }

  public void collapse() {
    if (!isExpand) {
      return;
    }
    isExpand = false;
    Pop.fallDown(this, 0.f, GSizeUtil.getFitPxFromDp(getContext(), 94.f), new PopCallback() {
      @Override public void onFinish() {

      }
    });
  }

  public void updateClipCountInSpped(List<MediaModel> list){
    int count;
    if(null == list || 0 == list.size()){
      count = 1;
    } else{
      count = list.size();
    }
    String tmpText =
        getContext().getString(R.string.mn_gallery_template_selected_count_description);

    String text =
        getContext().getResources().getString(R.string.mn_gallery_template_selected_count_description, String.valueOf(count));

    int start = tmpText.indexOf("%s");
    int end = start + String.valueOf(count).length();
    if (tmpText.contains("%s") && end <= text.length()) {
      mClipCountTv.setSpanText(text, start, end, Color.parseColor("#ff5e13"), null);
    } else {
      mClipCountTv.setText(text);
    }
  }

  public void expand() {
    if (isExpand) {
      return;
    }
    isExpand = true;
    Pop.raiseUp(this, GSizeUtil.getFitPxFromDp(getContext(), 94.f), 0.f, new PopCallback() {
      @Override public void onFinish() {

      }
    });
  }


  protected int getLayoutId(){
    return 0;
  }




}
