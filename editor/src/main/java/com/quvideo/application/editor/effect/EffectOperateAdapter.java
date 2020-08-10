package com.quvideo.application.editor.effect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.IEffectEditClickListener;
import java.util.List;

/**
 * Created by santa on 2020-04-17.
 */
public class EffectOperateAdapter extends RecyclerView.Adapter<EffectOperateAdapter.Holder> {

  private List<EffectBarItem> mlist;
  private IEffectEditClickListener mIEffectEditClickListener;

  public EffectOperateAdapter(List<EffectBarItem> list, IEffectEditClickListener listener) {
    mlist = list;
    mIEffectEditClickListener = listener;
  }

  public void updateList(List<EffectBarItem> list) {
    this.mlist = list;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Holder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.view_edit_operate_item, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, final int position) {
    holder.mTextView.setText(mlist.get(position).getTitle());
    holder.mImageView.setImageResource(mlist.get(position).getResId());
    final EffectBarItem item = mlist.get(position);
    if (item.isEnabled()) {
      holder.mImageView.setAlpha(1f);
      holder.mTextView.setAlpha(1f);
    } else {
      holder.mImageView.setAlpha(0.1f);
      holder.mTextView.setAlpha(0.1f);
    }
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (item.isEnabled()) {
          mIEffectEditClickListener.onClick(v, item);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mlist.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    private ImageView mImageView;
    private TextView mTextView;

    public Holder(@NonNull View itemView) {
      super(itemView);
      mTextView = itemView.findViewById(R.id.textview);
      mImageView = itemView.findViewById(R.id.imageview);
    }
  }
}
