package com.quvideo.application.frame.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quvideo.application.DPUtils;
import com.quvideo.application.editor.R;
import com.quvideo.application.glidedecoder.EffectThumbParams;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.utils.FileUtils;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import java.util.ArrayList;
import java.util.List;

public class FrameEffectAdapter extends RecyclerView.Adapter<FrameEffectAdapter.TemplateHolder> {

  private List<SimpleTemplate> mTemplates = new ArrayList<>();

  private Context mContext;

  private OnItemSelectListener mOnItemClickListener;

  private int mSelectedIndex = 0;

  public interface OnItemSelectListener {
    void onItemSelected(SimpleTemplate template);
  }

  public FrameEffectAdapter(Context context, List<SimpleTemplate> templates) {
    this.mContext = context;
    this.mTemplates = templates;
    mSelectedIndex = 0;
  }

  public void changeFocus(int position) {
    int oldIndex = mSelectedIndex;
    mSelectedIndex = position;
    if (oldIndex >= 0 && oldIndex < getItemCount()) {
      notifyItemChanged(oldIndex);
    }
    notifyItemChanged(mSelectedIndex);
  }

  public void setOnItemClickListener(OnItemSelectListener listener) {
    mOnItemClickListener = listener;
  }

  @NonNull
  @Override
  public TemplateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new TemplateHolder(
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.view_home_sample_template_list_item, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull TemplateHolder holder, final int position) {
    final SimpleTemplate item = mTemplates.get(position);
    boolean isSelected = position == mSelectedIndex;
    if (!TextUtils.isEmpty(item.getTitle())) {
      holder.mTextView.setText(item.getTitle());
    } else {
      XytInfo xytInfo = XytManager.getXytInfo(item.getTemplateId());
      holder.mTextView.setText(xytInfo.getTitle(mContext.getResources().getConfiguration().locale));
    }
    if (item.getThumbnailResId() <= 0) {
      XytInfo xytInfo = XytManager.getXytInfo(item.getTemplateId());
      String thumbnailAssetPath = xytInfo.filePath.replace(".xyt", "/thumbnail.webp");
      if (FileUtils.isFileExisted(thumbnailAssetPath)) {
        String thumbnail = xytInfo.filePath.replace("assets_android://", "file:///android_asset/")
            .replace(".xyt", "/thumbnail.webp");
        Uri uri = Uri.parse(thumbnail);
        Glide.with(holder.mImageView)
            .load(uri)
            .into(holder.mImageView);
      } else {
        final String filterPath = XytManager.getXytInfo(item.getTemplateId()).filePath;
        int thumbWidth = DPUtils.dpToPixel(holder.mImageView.getContext(), 60);
        int thumbHeight = DPUtils.dpToPixel(holder.mImageView.getContext(), 60);
        EffectThumbParams effectThumbParams =
            new EffectThumbParams(filterPath, thumbWidth, thumbHeight);
        Glide.with(holder.mImageView).load(effectThumbParams).into(holder.mImageView);
      }
    } else {
      Glide.with(mContext)
          .load(item.getThumbnailResId())
          .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable final GlideException e, final Object model,
                final Target<Drawable> target, final boolean isFirstResource) {
              return false;
            }

            @Override public boolean onResourceReady(final Drawable resource, final Object model,
                final Target<Drawable> target, final DataSource dataSource,
                final boolean isFirstResource) {
              if (resource instanceof WebpDrawable) {
                final WebpDrawable webpDrawable = ((WebpDrawable) resource);
                webpDrawable.start();
              }
              return false;
            }
          })
          .into(holder.mImageView);
    }
    holder.mImgFocus.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    holder.mImageView.setOnClickListener(v -> {
      changeFocus(position);
      if (mOnItemClickListener != null) {
        mOnItemClickListener.onItemSelected(item);
      }
    });
  }

  @Override
  public int getItemCount() {
    return mTemplates.size();
  }

  class TemplateHolder extends RecyclerView.ViewHolder {

    private AppCompatTextView mTextView;
    private AppCompatImageView mImageView;
    private AppCompatImageView mImgFocus;

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      mTextView = itemView.findViewById(R.id.home_template_item_text);
      mImageView = itemView.findViewById(R.id.home_template_item_image);
      mImgFocus = itemView.findViewById(R.id.imgFocus);
    }
  }
}
