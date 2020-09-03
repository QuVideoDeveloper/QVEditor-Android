package com.quvideo.application.draft;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.EditorConst;
import com.quvideo.application.db.DraftInfoDao;
import com.quvideo.application.editor.EditorActivity;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.DateUtils;
import com.quvideo.mobile.engine.QEEngineClient;
import java.util.ArrayList;
import java.util.List;

public class DraftAdapter extends RecyclerView.Adapter {

  private Activity mActivity;
  private LayoutInflater mLayoutInflater;
  private List<DraftModel> mDraftList = new ArrayList<>();

  public DraftAdapter(Activity activity) {
    this.mActivity = activity;
    mLayoutInflater = LayoutInflater.from(activity);
  }

  @NonNull @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = mLayoutInflater.inflate(R.layout.draft_item_layout, parent, false);
    return new ItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
    ItemViewHolder holder = (ItemViewHolder) viewHolder;
    DraftModel itemData = mDraftList.get(position);

    if (itemData == null) {
      return;
    }
    holder.mTvName.setText(DateUtils.formatFullDate(itemData.createTime));
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(mActivity, EditorActivity.class);
        intent.putExtra(EditorConst.INTENT_EXT_KEY_DRAFT, itemData.projectUrl);
        mActivity.startActivity(intent);
        mActivity.finish();
      }
    });
    holder.mIvDelete.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DraftInfoDao draftInfoDao = new DraftInfoDao();
        draftInfoDao.removeItem(itemData);
        QEEngineClient.deleteProject(itemData.projectUrl);
        removeItem(itemData, position);
        notifyDataSetChanged();
      }
    });
  }

  //默认有头部
  @Override public int getItemCount() {
    return mDraftList.size();
  }

  public void setData(List<DraftModel> list) {
    mDraftList.clear();
    if (list != null) {
      mDraftList.addAll(list);
    }
  }

  /**
   * 因为有头部布局
   * 实际删除的是pos+1
   **/
  public void removeItem(DraftModel draftModel, int position) {
    if (mDraftList.size() > position && mDraftList.contains(draftModel)) {
      mDraftList.remove(position);
      notifyItemRemoved(position);
    }
  }

  public List<DraftModel> getData() {
    return mDraftList;
  }

  protected class ItemViewHolder extends RecyclerView.ViewHolder {

    private final ImageView mIvDelete;
    private final TextView mTvName;

    public ItemViewHolder(View view) {
      super(view);
      mTvName = view.findViewById(R.id.draft_tv_title);
      mIvDelete = view.findViewById(R.id.draft_iv_delete);
    }
  }
}
