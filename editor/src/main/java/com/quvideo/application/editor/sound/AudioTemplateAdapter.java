package com.quvideo.application.editor.sound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by santa on 2020-04-17.
 */
public class AudioTemplateAdapter extends RecyclerView.Adapter<AudioTemplateAdapter.TemplateHolder> {

  private List<AudioTemplate> mTemplates = new ArrayList<>();

  private Context mContext;

  private BaseMenuView mBaseMenuView;

  private OnItemSelectListener mOnItemClickListener;

  public interface OnItemSelectListener {
    void onItemSelected(AudioTemplate template);
  }

  public AudioTemplateAdapter(Context context, BaseMenuView baseMenuView) {
    this.mContext = context;
    this.mBaseMenuView = baseMenuView;
  }

  public void updateList(List<AudioTemplate> templates) {
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
    final AudioTemplate item = mTemplates.get(position);
    holder.mTextView.setText(item.getTitle());
    Glide.with(holder.mImageView).load(item.getThumbnailResId()).into(holder.mImageView);
    holder.mImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mOnItemClickListener != null) {
          mOnItemClickListener.onItemSelected(item);
          mBaseMenuView.dismissMenu();
        }
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

    public TemplateHolder(@NonNull View itemView) {
      super(itemView);
      mTextView = itemView.findViewById(R.id.home_template_item_text);
      mImageView = itemView.findViewById(R.id.home_template_item_image);
    }
  }
}
