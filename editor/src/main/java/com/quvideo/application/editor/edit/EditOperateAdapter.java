package com.quvideo.application.editor.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.EditOperate;
import com.quvideo.application.editor.base.ItemOnClickListener;
import java.util.List;

/**
 * Created by santa on 2020-04-17.
 */
public class EditOperateAdapter extends RecyclerView.Adapter<EditOperateAdapter.Holder> {

    private List<EditOperate> mlist;
    private ItemOnClickListener mItemOnClickListener;

    public EditOperateAdapter(List<EditOperate> list, ItemOnClickListener listener) {
        mlist = list;
        mItemOnClickListener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_edit_operate_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.mTextView.setText(mlist.get(position).getTitle());
        holder.mImageView.setImageResource(mlist.get(position).getResId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemOnClickListener.onClick(v, mlist.get(position));
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
