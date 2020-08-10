package com.quvideo.application.editor.effect.plugin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import java.util.List;

/**
 * Created by santa on 2020-04-17.
 */
public class EffectPluginAttriAdapter extends RecyclerView.Adapter<EffectPluginAttriAdapter.Holder> {

  private List<PluginEditItem> mlist;
  private IPluginEditClickListener mIPluginEditClickListener;

  public EffectPluginAttriAdapter(List<PluginEditItem> list, IPluginEditClickListener listener) {
    mlist = list;
    mIPluginEditClickListener = listener;
  }

  public void updateList(List<PluginEditItem> list) {
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
    holder.mTextView.setText(mlist.get(position).getName());
    holder.mImageView.setImageResource(mlist.get(position).getResId());
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mIPluginEditClickListener.onClick(v, mlist.get(position));
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

  public static class PluginEditItem {

    private int resId;
    private String name;

    public PluginEditItem(int resId, String name) {
      this.resId = resId;
      this.name = name;
    }

    public int getResId() {
      return resId;
    }

    public String getName() {
      return name;
    }
  }

  public interface IPluginEditClickListener {
    void onClick(View view, PluginEditItem operate);
  }
}
