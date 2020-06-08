package com.quvideo.application.gallery.board.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GRange;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.utils.GSizeUtil;
import com.quvideo.application.gallery.utils.GalleryUtil;
import com.quvideo.application.gallery.utils.RotateTransformation;
import com.quvideo.application.utils.image.ImageLoader;

/**
 * Create by zhengjunfei on 2020-03-03
 */
public class BaseMediaBoardItemView extends ConstraintLayout {
  protected ImageView ivCover;
  protected TextView tvDuration, tvOrder;
  protected ImageButton deleteBtn;
  protected View shadowView;
  protected View mHoverView;
  protected View mHoverStrokeView;

  protected View mRootView;

  protected static int COVER_SIZE;

  public BaseMediaBoardItemView(Context context) {
    super(context);
    init();
  }

  public BaseMediaBoardItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public BaseMediaBoardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  protected void init() {
    mRootView = LayoutInflater.from(getContext())
        .inflate(getLayoutId(), this, true);

    ivCover = findViewById(R.id.iv_cover);
    tvDuration = findViewById(R.id.tv_duration);
    tvOrder = findViewById(R.id.tv_order);
    deleteBtn = findViewById(R.id.btn_delete);
    shadowView = findViewById(R.id.item_shadow);
    mHoverView = findViewById(R.id.item_hover);
    mHoverStrokeView = findViewById(R.id.item_hover_stroke);

    COVER_SIZE = GSizeUtil.getFitPxFromDp(getContext(), 27.5f);
  }

  protected int getLayoutId() {
    return 0;
  }

  public void update(MediaModel model, int pos) {
    if (model.getSourceType() == GalleryDef.TYPE_VIDEO) {
      shadowView.setVisibility(VISIBLE);

      long duration = model.getDuration();
      if (model.getRangeInFile() != null) {
        duration = model.getRangeInFile().getLength();
      }
      if (duration > 0) {
        tvDuration.setVisibility(VISIBLE);
        tvDuration.setText(GalleryUtil.getFormatDuration(duration));
      } else {
        tvDuration.setVisibility(GONE);
      }

      GRange rangeInFile = model.getRangeInFile();
      if (rangeInFile != null && rangeInFile.getLeftValue() != 0) {
        GalleryUtil.loadCover(getContext(), ivCover, model.getFilePath(),
            rangeInFile.getLeftValue() * 1000);
      } else {
        GalleryUtil.loadImage(COVER_SIZE, COVER_SIZE, R.drawable.gallery_default_pic_cover,
            model.getFilePath(), ivCover);
      }
    } else {
      shadowView.setVisibility(GONE);
      if (model.getRotation() > 0) {
        ImageLoader.loadImage(R.drawable.gallery_default_pic_cover, model.getFilePath(), ivCover,
            new RotateTransformation(model.getRotation()));
      } else {
        GalleryUtil.loadImage(COVER_SIZE, COVER_SIZE, R.drawable.gallery_default_pic_cover,
            model.getFilePath(), ivCover);
      }
    }
  }

  public ImageButton getDeleteBtn() {
    return deleteBtn;
  }

  public void updateOrderState(int index, boolean show) {
    if (show) {
      tvOrder.setVisibility(VISIBLE);
      tvOrder.setText(GalleryUtil.getCommonIndex(index));
    } else {
      tvOrder.setVisibility(GONE);
    }
  }

}
