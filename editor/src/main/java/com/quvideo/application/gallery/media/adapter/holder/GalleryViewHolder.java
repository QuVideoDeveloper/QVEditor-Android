package com.quvideo.application.gallery.media.adapter.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.adapterhelper.BaseViewHolder;
import com.quvideo.application.gallery.media.adapter.MediaAdapter;
import com.quvideo.application.gallery.media.adapter.PinnedHeaderEntity;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.utils.GSizeUtil;
import com.quvideo.application.gallery.utils.GalleryUtil;

public class GalleryViewHolder extends BaseViewHolder {

  private LinearLayout mOrderLayout;
  private TextView mOrderTv;

  public GalleryViewHolder(View view) {
    super(view);
  }

  public void setData(PinnedHeaderEntity<MediaModel> data) {
    if (data == null) {
      return;
    }

    switch (this.getItemViewType()) {
      case MediaAdapter.TYPE_HEADER:
        setHeaderTitle(data.getPinnedHeaderName());
        break;
      case MediaAdapter.TYPE_DATA:
        if (data.getData() != null) {
          setNormalData(data.getData());
        }
        break;
      default:
        break;
    }
  }

  private void setHeaderTitle(String title) {
    TextView tvTitle = itemView.findViewById(R.id.tv_header_title);
    if (tvTitle != null && !TextUtils.isEmpty(title)) {
      tvTitle.setText(title);
    }
  }

  private void setNormalData(MediaModel model) {
    if (model == null) {
      return;
    }
    Context context = this.itemView.getContext();
    ImageView ivCover = itemView.findViewById(R.id.iv_cover);
    ImageButton previewBtn = itemView.findViewById(R.id.preview_icon);
    LinearLayout durationLayout = itemView.findViewById(R.id.layout_duration);
    TextView tvDuration = itemView.findViewById(R.id.tv_video_duration);
    mOrderLayout = itemView.findViewById(R.id.order_layout);
    mOrderTv = itemView.findViewById(R.id.tv_order);

    this.addOnClickListener(R.id.preview_icon);
    updateOrder(model.getOrder());

    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    if (null != settings
        && GalleryDef.TYPE_VIDEO == model.getSourceType()) { //如果是变速模板类型的gallery中的视频就隐藏修剪入口按钮
      previewBtn.setVisibility(View.GONE);
    }

    int screenWidth = GSizeUtil.getsScreenWidth(context)
        - (GallerySettings.SHOWN_COLUMNS - 1) * GSizeUtil.getFitPxFromDp(context, 2.f);
    int itemSize = screenWidth / GallerySettings.SHOWN_COLUMNS;
    if (model.getSourceType() == GalleryDef.TYPE_VIDEO) {
      GalleryUtil.loadImage(itemSize / 2, itemSize / 2, R.drawable.gallery_default_pic_cover,
          model.getFilePath(), ivCover);
      previewBtn.setImageResource(R.drawable.gallery_media_video_trim_icon);
      durationLayout.setVisibility(View.VISIBLE);
      tvDuration.setText(GalleryUtil.getFormatDuration(model.getDuration()));
    } else {
      GalleryUtil.loadImage(itemSize / 2, itemSize / 2, R.drawable.gallery_default_pic_cover,
          model.getFilePath(), ivCover);
      previewBtn.setImageResource(R.drawable.gallery_media_photo_preview_icon);
      durationLayout.setVisibility(View.GONE);
    }
    //如果是instgram回来的数据，是没有时长的，这个时候需要隐藏时间view
    tvDuration.setVisibility(View.VISIBLE);
  }

  public void updateOrder(int order) {
    if (mOrderLayout == null || mOrderTv == null) {
      return;
    }
    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    if (null == settings) {//只有默认模式下才显示序号
      return;
    }

    if (order > 0) {
      mOrderLayout.setVisibility(View.VISIBLE);
      mOrderTv.setText(GalleryUtil.getCommonIndex(order));
    } else {
      mOrderLayout.setVisibility(View.GONE);
    }
  }
}
