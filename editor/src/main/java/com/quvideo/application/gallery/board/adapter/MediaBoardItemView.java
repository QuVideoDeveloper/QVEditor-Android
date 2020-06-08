package com.quvideo.application.gallery.board.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 9/4/2019
 * 普通样式的BoardView
 */
public class MediaBoardItemView extends BaseMediaBoardItemView {

  public MediaBoardItemView(Context context) {
    super(context);
  }

  public MediaBoardItemView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MediaBoardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void init() {
    super.init();
  }

  @Override
  public void update(MediaModel model, int pos) {
    super.update(model, pos);
    if (model == null) {
      return;
    }

    if (model.getSourceType() != GalleryDef.TYPE_VIDEO) {
      tvDuration.setVisibility(GONE);
    }
    mHoverStrokeView.setVisibility(View.GONE);
  }

  @Override protected int getLayoutId() {
    return R.layout.gallery_board_item_view_layout;
  }
}
