package com.quvideo.application.editor.base;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.quvideo.application.editor.EditorActivity;
import com.quvideo.application.editor.R;
import com.quvideo.application.glidedecoder.EffectThumbParams;
import com.quvideo.application.superedit.ZXingManager;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.utils.FileUtils;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import java.util.ArrayList;
import java.util.List;

public class SimpleTemplateAdapter
    extends RecyclerView.Adapter<SimpleTemplateAdapter.TemplateHolder> {

  private List<SimpleTemplate> mTemplates = new ArrayList<>();

  private Activity mActivity;

  private BaseMenuView mBaseMenuView;

  private OnItemSelectListener mOnItemClickListener;

  private boolean isNeedScan = ZXingManager.isHadSuperZXing();

  private int mSelectedIndex = 0;
  private int addOffset = isNeedScan ? 1 : 0;

  public interface OnItemSelectListener {
    void onItemSelected(SimpleTemplate template);
  }

  public SimpleTemplateAdapter(AppCompatActivity activity, BaseMenuView baseMenuView) {
    this.mActivity = activity;
    this.mBaseMenuView = baseMenuView;
    mSelectedIndex = 0;
  }

  public void updateList(List<SimpleTemplate> templates) {
    this.mTemplates = templates;
    notifyDataSetChanged();
  }

  public void changeFocus(int position) {
    int oldIndex = mSelectedIndex;
    mSelectedIndex = position;
    if (oldIndex >= 0 && oldIndex < getItemCount() - addOffset) {
      notifyItemChanged(oldIndex + addOffset);
    }
    notifyItemChanged(mSelectedIndex + addOffset);
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
    if (isNeedScan && position == 0) {
      holder.mTextView.setText(R.string.mn_edit_qrcode_scan);
      holder.mImgFocus.setVisibility(View.GONE);
      holder.mImageView.setImageResource(R.drawable.editor_tool_qrcode_scan);
      holder.mImageView.setOnClickListener(v -> {
        ZXingManager.go2CaptureActivity(mActivity, EditorActivity.INTENT_REQUEST_QRCODE);
      });
      return;
    }
    final SimpleTemplate item = mTemplates.get(position - addOffset);
    XytInfo xytInfo = XytManager.getXytInfo(item.getTemplateId());
    if (!TextUtils.isEmpty(item.getTitle())) {
      holder.mTextView.setText(item.getTitle());
    } else {
      holder.mTextView.setText(
          xytInfo.getTitle(mActivity.getResources().getConfiguration().locale));
    }
    boolean isSelected = (position - addOffset) == mSelectedIndex;
    if (item.getThumbnailResId() <= 0) {
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
        int thumbHeight = DPUtils.dpToPixel(holder.mImageView.getContext(), 68);
        EffectThumbParams effectThumbParams =
            new EffectThumbParams(filterPath, thumbWidth, thumbHeight);
        Glide.with(holder.mImageView)
            .load(effectThumbParams)
            .into(holder.mImageView);
      }
    } else {
      Glide.with(mActivity)
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
      if (mOnItemClickListener != null) {
        mOnItemClickListener.onItemSelected(item);
        changeFocus(position - addOffset);
      } else {
        item.onClick(mActivity);
        mBaseMenuView.dismissMenu();
      }
    });
  }

  @Override
  public int getItemCount() {
    return mTemplates.size() + addOffset;
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
