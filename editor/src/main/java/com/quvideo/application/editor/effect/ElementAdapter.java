package com.quvideo.application.editor.effect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import java.util.List;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.Holder> {

  private List<ElementDialog.DataItem> mlist;
  private IDataClickListener mIDataClickListener;

  public ElementAdapter(List<ElementDialog.DataItem> list, IDataClickListener listener) {
    mlist = list;
    mIDataClickListener = listener;
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
    final ElementDialog.DataItem item = mlist.get(position);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mIDataClickListener.onClick(v, item);
      }
    });
  }

  public interface IDataClickListener {
    void onClick(View view, ElementDialog.DataItem operate);
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
