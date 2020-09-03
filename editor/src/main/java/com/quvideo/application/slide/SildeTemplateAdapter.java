package com.quvideo.application.slide;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
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
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import java.util.ArrayList;
import java.util.List;

public class SildeTemplateAdapter
    extends RecyclerView.Adapter<SildeTemplateAdapter.TemplateHolder> {

  private List<SimpleTemplate> mTemplates = new ArrayList<>();

  private Activity mActivity;

  private DialogFragment mDialogFragment;

  private OnItemSelectListener mOnItemClickListener;

  private boolean isNeedScan = ZXingManager.isHadSuperZXing();
  private int addOffset = isNeedScan ? 1 : 0;

  public interface OnItemSelectListener {
    void onItemSelected(SimpleTemplate template);
  }

  public SildeTemplateAdapter(AppCompatActivity activity, DialogFragment dialogFragment) {
    this.mActivity = activity;
    this.mDialogFragment = dialogFragment;
  }

  public void updateList(List<SimpleTemplate> templates) {
    this.mTemplates = templates;
    notifyDataSetChanged();
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
      holder.mImageView.setImageResource(R.drawable.editor_tool_qrcode_scan);
      holder.mImageView.setOnClickListener(v -> {
        ZXingManager.go2CaptureActivity(mActivity, EditorActivity.INTENT_REQUEST_QRCODE);
      });
      return;
    }
    final SimpleTemplate item = mTemplates.get(position - addOffset);
    if (!TextUtils.isEmpty(item.getTitle())) {
      holder.mTextView.setText(item.getTitle());
    } else {
      XytInfo xytInfo = XytManager.getXytInfo(item.getTemplateId());
      holder.mTextView.setText(xytInfo.getTitle(mActivity.getResources().getConfiguration().locale));
    }
    if (item.getThumbnailResId() <= 0) {
      final String filterPath = XytManager.getXytInfo(item.getTemplateId()).filePath;
      int thumbWidth = DPUtils.dpToPixel(holder.mImageView.getContext(), 60);
      int thumbHeight = DPUtils.dpToPixel(holder.mImageView.getContext(), 60);
      EffectThumbParams effectThumbParams =
          new EffectThumbParams(filterPath, thumbWidth, thumbHeight);
      Glide.with(holder.mImageView).load(effectThumbParams).into(holder.mImageView);
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
    holder.mImageView.setSelected(false);
    holder.mImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnItemClickListener != null) {
          mOnItemClickListener.onItemSelected(item);
        } else {
          item.onClick(mActivity);
          mDialogFragment.dismissAllowingStateLoss();
        }
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

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      mTextView = itemView.findViewById(R.id.home_template_item_text);
      mImageView = itemView.findViewById(R.id.home_template_item_image);
    }
  }
}
