package com.quvideo.application.gallery.preview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.viewpager.widget.ViewPager;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.GalleryStatus;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.preview.adapter.PhotoPagerAdapter;
import com.quvideo.application.gallery.preview.utils.AnimUtil;
import com.quvideo.application.gallery.utils.GalleryFile;
import com.quvideo.application.gallery.utils.GalleryToast;
import com.quvideo.application.gallery.widget.photo.PhotoView;
import com.quvideo.application.utils.pop.Pop;
import com.quvideo.application.utils.rx.RxViewUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by zhengjunfei on 2019/9/12
 */
public class PhotoActivity extends AppCompatActivity
    implements PhotoPagerAdapter.PhotoCropperCallback {
  public static final String INTENT_PHOTO_LIST_KEY = "intent_photo_list_key";
  static final String INTENT_KEY_PHOTO_PREVIEW_POS = "intent_key_photo_preview_pos";
  static final String INTENT_KEY_PHOTO_PREVIEW_LIMIT = "intent_key_photo_preview_limit";

  private ViewPager mViewPager;
  private RelativeLayout mTitleLayout, mOpsLayout;
  private TextView mPhotoIndexTv, mTotalCountTv, mDoneTv;
  private ImageButton mSelectBtn, mBackBtn, mRotateBtn;

  private PhotoPagerAdapter mPagerAdapter;

  private Integer mPreviewPosition = 0;
  private int mLimitCount;
  private List<MediaModel> mPhotoList = new ArrayList<>();
  private SparseArray<Float> mSelectPhotoSparseArray = new SparseArray<>();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gallery_media_activity_photo_cropper);
    initView();

    mLimitCount = getIntent().getIntExtra(INTENT_KEY_PHOTO_PREVIEW_LIMIT, Integer.MAX_VALUE / 2);
    initPhotoData();
    initViewPager();

    addListener();
  }

  private void initView() {
    mTitleLayout = findViewById(R.id.title_layout);
    mOpsLayout = findViewById(R.id.ops_layout);
    mDoneTv = findViewById(R.id.btn_done);
    mPhotoIndexTv = findViewById(R.id.tv_curr_index);
    mTotalCountTv = findViewById(R.id.tv_count);
    mSelectBtn = findViewById(R.id.btn_select);
    mViewPager = findViewById(R.id.viewpager);
    mBackBtn = findViewById(R.id.btn_back);
    mRotateBtn = findViewById(R.id.btn_rotate);
  }

  private void initPhotoData() {
    int startPos = getIntent().getIntExtra(INTENT_KEY_PHOTO_PREVIEW_POS, 0);
    mPhotoList = GalleryStatus.getInstance().getPhotoList();
    if (mPhotoList == null || mPhotoList.size() == 0) {
      this.finish();
      return;
    }
    mPreviewPosition = startPos;
    mPhotoIndexTv.setText(String.valueOf(startPos + 1));
    mTotalCountTv.setText(String.valueOf(mPhotoList.size()));
    updateDoneStatus();
    updateButtonStatus(startPos);
    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    mSelectBtn.setVisibility(View.VISIBLE);
  }

  private void initViewPager() {
    mPagerAdapter = new PhotoPagerAdapter(this);
    mPagerAdapter.setData(GalleryStatus.getInstance().getPhotoList());
    mViewPager.setAdapter(mPagerAdapter);
    mViewPager.addOnPageChangeListener(new PhotoPagerChangeListener());
    if (mPhotoList.size() > 2) {
      mViewPager.setOffscreenPageLimit(3);
    }
    mViewPager.setCurrentItem(mPreviewPosition);
    mPagerAdapter.notifyDataSetChanged();
  }

  private class PhotoPagerChangeListener extends ViewPager.SimpleOnPageChangeListener {

    @Override public void onPageSelected(int position) {
      if (position == mPreviewPosition) {
        //same page
        return;
      }

      mPreviewPosition = position;
      mPhotoIndexTv.setText(String.valueOf(position + 1));
      updateButtonStatus(position);

      PhotoView currentPhotoView = mPagerAdapter.getCurrentPhotoView();
      if (currentPhotoView != null) {
        currentPhotoView.resetZoom();
        currentPhotoView.postInvalidate();
      }
    }
  }

  @SuppressLint("ClickableViewAccessibility") private void addListener() {
    RxViewUtil.setOnClickListener(view -> {
      Pop.show(view);
      boolean btnSelected = mSelectBtn.isSelected();
      if (!btnSelected) {
        //check limit when select,except un_select
        int selectedCount = mSelectPhotoSparseArray.size();
        if (selectedCount >= mLimitCount) {
          GalleryToast.show(this, getString(R.string.mn_gallery_template_enough_tip_text));
          return;
        }
      }

      mSelectBtn.setSelected(!btnSelected);
      if (mSelectBtn.isSelected()) {
        float rotation = 0f;
        PhotoView currentPhotoView = mPagerAdapter.getCurrentPhotoView();
        if (currentPhotoView != null) {
          rotation = currentPhotoView.getRotation();
        }
        mSelectPhotoSparseArray.put(mPreviewPosition, rotation);
      } else {
        mSelectPhotoSparseArray.remove(mPreviewPosition);
      }
      updateDoneStatus();
    }, mSelectBtn);

    RxViewUtil.setOnClickListener(view -> {
      setResult(Activity.RESULT_CANCELED);
      PhotoActivity.this.finish();
    }, mBackBtn);

    RxViewUtil.setOnClickListener(view -> {
      Pop.showQuietly(view);
      PhotoView currentPhotoView = mPagerAdapter.getCurrentPhotoView();
      if (currentPhotoView != null) {
        float rotation = (currentPhotoView.getRotation() + 90) % 360;
        currentPhotoView.setRotation(rotation);
        //update value
        if (isSeletedPhoto(mPreviewPosition)) {
          updateSeletedPhotoParams(mPreviewPosition, rotation);
        }
      }
    }, mRotateBtn);

    RxViewUtil.setOnClickListener(view -> {
      Pop.showQuietly(view);

      onPhotoSelectDone();
    }, mDoneTv);

    mTitleLayout.setOnTouchListener((v, event) -> true);
    mOpsLayout.setOnTouchListener((v, event) -> true);
  }

  private void updateDoneStatus() {
    String text;
    if (mSelectPhotoSparseArray.size() > 0) {
      text = getString(R.string.mn_gallery_preview_ok_title, mSelectPhotoSparseArray.size());
    } else {
      text = getString(R.string.mn_gallery_preview_confirm_title);
    }
    mDoneTv.setText(text);
  }

  private void updateButtonStatus(int index) {
    mSelectBtn.setSelected(isSeletedPhoto(index));
    if (mPhotoList.size() <= index) {
      return;
    }
    MediaModel itemInfo = mPhotoList.get(index);
    if (GalleryFile.isGifFile(itemInfo.getFilePath())) {
      mRotateBtn.setVisibility(View.GONE);
    } else {
      mRotateBtn.setVisibility(View.VISIBLE);
    }
  }

  private boolean isSeletedPhoto(int index) {
    float value = mSelectPhotoSparseArray.get(index, -1.f);
    return value >= 0;
  }

  private void updateSeletedPhotoParams(int index, float rotation) {
    if (isSeletedPhoto(index)) {
      mSelectPhotoSparseArray.put(index, rotation);
    }
  }

  private void onPhotoSelectDone() {
    if (mSelectPhotoSparseArray.size() == 0) {
      float rotation = 0f;
      PhotoView currentPhotoView = mPagerAdapter.getCurrentPhotoView();
      if (currentPhotoView != null) {
        rotation = currentPhotoView.getRotation();
      }
      mSelectPhotoSparseArray.put(mPreviewPosition, rotation);
    }
    ArrayList<Integer> selectIndexList = new ArrayList<>();
    for (int i = 0; i < mSelectPhotoSparseArray.size(); i++) {
      int index = mSelectPhotoSparseArray.keyAt(i);
      selectIndexList.add(index);
    }

    List<MediaModel> photoList = GalleryStatus.getInstance().getPhotoList();
    if (photoList != null && !photoList.isEmpty()) {
      for (Integer index : selectIndexList) {
        if (index >= 0 && index < photoList.size()) {
          MediaModel model = photoList.get(index);
          if (model != null) {
            float rotation = mSelectPhotoSparseArray.get(index, 0.f);
            model.setRotation((int) rotation);
          }
        }
      }
    }

    Intent data = getIntent();
    data.putExtra(INTENT_PHOTO_LIST_KEY, selectIndexList);
    setResult(RESULT_OK, data);
    PhotoActivity.this.finish();
  }

  public static void launchPhoto(Activity activity, int startPosition, int limit,
      View scaleUpSourceView, int requestCode) {
    Intent intent = new Intent(activity, PhotoActivity.class);
    intent.putExtra(INTENT_KEY_PHOTO_PREVIEW_POS, startPosition);
    intent.putExtra(INTENT_KEY_PHOTO_PREVIEW_LIMIT, limit);
    try {
      if (scaleUpSourceView != null) {
        ActivityOptionsCompat compat =
            ActivityOptionsCompat.makeScaleUpAnimation(scaleUpSourceView,
                scaleUpSourceView.getWidth() / 2, scaleUpSourceView.getHeight(), 0, 0);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, compat.toBundle());
      } else {
        activity.startActivityForResult(intent, requestCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 照片点击回调
   * 展示或者隐藏上下操作栏
   */
  @Override public void onPhotoItemClick() {
    if (mTitleLayout.getVisibility() == View.VISIBLE) {
      AnimUtil.topViewAnim(mTitleLayout, false);
      AnimUtil.bottomViewAnim(mOpsLayout, false);
    } else {
      AnimUtil.topViewAnim(mTitleLayout, true);
      AnimUtil.bottomViewAnim(mOpsLayout, true);
    }
  }
}
