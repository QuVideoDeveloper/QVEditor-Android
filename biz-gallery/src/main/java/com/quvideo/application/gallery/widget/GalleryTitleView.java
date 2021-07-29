package com.quvideo.application.gallery.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.quvideo.application.gallery.R;
import com.quvideo.application.gallery.magicindicator.MagicIndicator;
import com.quvideo.application.gallery.utils.GalleryUtil;
import com.quvideo.application.utils.pop.Pop;
import com.quvideo.application.utils.rx.RxViewUtil;

public class GalleryTitleView extends RelativeLayout {
  ImageButton backBtn;
  ImageView folderEntranceIv;
  MagicIndicator magicIndicator;
  public LinearLayout folderEntranceLayout;

  private TitleViewCallback mTitleViewCallback;

  public GalleryTitleView(Context context) {
    this(context, null);
  }

  public GalleryTitleView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public GalleryTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    init();
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.gallery_title_layout, this, true);
    backBtn = findViewById(R.id.btn_back);
    magicIndicator = findViewById(R.id.magic_indicator);
    folderEntranceLayout = findViewById(R.id.folder_entrance);
    folderEntranceIv = findViewById(R.id.iv_folder_entrance);

    RxViewUtil.setOnClickListener(view -> {
      if (GalleryUtil.isFastDoubleClick()) {
        return;
      }
      Pop.showQuietly(view);
      if (mTitleViewCallback != null) {
        mTitleViewCallback.onBack();
      }
    }, backBtn);

    RxViewUtil.setOnClickListener(view -> {
      if (GalleryUtil.isFastDoubleClick()) {
        return;
      }
      Pop.showQuietly(folderEntranceIv);
      if (mTitleViewCallback != null) {
        mTitleViewCallback.onFolderEntrance(folderEntranceIv);
      }
    }, folderEntranceLayout);
  }

  public void setTitleViewCallback(TitleViewCallback callback) {
    mTitleViewCallback = callback;
  }

  public MagicIndicator getMagicIndicator() {
    return magicIndicator;
  }

  public interface TitleViewCallback {
    void onBack();

    void onFolderEntrance(View anchor);
  }
}
