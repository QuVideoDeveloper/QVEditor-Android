package com.quvideo.application.gallery.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.adapterhelper.BaseQuickAdapter;
import com.quvideo.application.gallery.adapterhelper.listener.OnItemClickListener;
import com.quvideo.application.gallery.media.adapter.FolderListAdapter;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaGroupItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by zhengjunfei on 2019/9/9
 */
public class FolderListPopwindow extends PopupWindow {
  private Activity mActivity;
  private TextView mTitle;
  private RecyclerView mRecyclerView;
  private int mDirection;
  private List<MediaGroupItem> mFolderList = new ArrayList<>();
  private FolderListAdapter mAdapter;
  private int mType;

  private OnFolderMultiSelectCallback mMultiCallback;
  private OnFolderSelectListener mSingleCallback;

  public FolderListPopwindow(Activity activity, int type, int direction,
      OnFolderMultiSelectCallback callback) {
    this.mActivity = activity;
    this.mType = type;
    this.mDirection = direction;
    this.mMultiCallback = callback;
    initView();
  }

  public FolderListPopwindow(Activity activity, int type, int direction,
      OnFolderSelectListener callback) {
    this.mActivity = activity;
    this.mType = type;
    this.mDirection = direction;
    this.mSingleCallback = callback;
    initView();
  }

  private void initView() {
    View view = LayoutInflater.from(mActivity)
        .inflate(R.layout.gallery_media_layout_folder_list, null, false);
    mTitle = view.findViewById(R.id.folder_list_title);
    mRecyclerView = view.findViewById(R.id.folder_list_recyclerview);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity.getApplicationContext()));
    initTitle();
    initAdapter();
    initPopWindow(view);
    initListener();
  }

  private void initTitle() {
    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    if (null == settings) {
      return;
    }

    String titleText;
    if (GalleryDef.MODE_BOTH == settings.getShowMode()) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(mActivity.getApplicationContext()
          .getResources()
          .getString(R.string.mn_gallery_select_video));
      stringBuilder.append("&");
      stringBuilder.append(mActivity.getApplicationContext()
          .getResources()
          .getString(R.string.mn_gallery_select_photo));
      titleText = stringBuilder.toString();
    } else if (GalleryDef.MODE_VIDEO == settings.getShowMode()) {
      titleText = mActivity.getApplicationContext()
          .getResources()
          .getString(R.string.mn_gallery_select_video);
    } else {
      titleText = mActivity.getApplicationContext()
          .getResources()
          .getString(R.string.mn_gallery_select_photo);
    }
    mTitle.setText(titleText);
  }

  private void initListener() {
    mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
      @Override
      public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (null == mAdapter || null == mAdapter.getItem(position)) {
          return;
        }
        mAdapter.updateItemCheckState(position, mType == FolderListAdapter.TYPE_SINGLE);
        if (FolderListAdapter.TYPE_MULTI == mType && null != mMultiCallback) {
          multiItemSelected(mAdapter.getItem(position));
        } else if (FolderListAdapter.TYPE_SINGLE == mType && null != mSingleCallback) {
          singleItemSelected(mAdapter.getItem(position), position);
        }
        dismiss();
      }
    });
  }

  private void multiItemSelected(MediaGroupItem curClickItemInfo) {
    if (null == curClickItemInfo || null == mMultiCallback) {
      return;
    }
    List<MediaGroupItem> selectFolderList = getSelectFolderList();
    mMultiCallback.onMultiSelectListener(null == selectFolderList ? null : selectFolderList);
  }

  private void singleItemSelected(MediaGroupItem curClickItemInfo, int pos) {
    if (null == curClickItemInfo || null == mSingleCallback || null == mAdapter) {
      return;
    }
    mSingleCallback.onSingleFolderSelected(
        mAdapter.posIsSingleCheck(pos) ? curClickItemInfo : null);
  }

  private void initAdapter() {
    mAdapter = new FolderListAdapter(new ArrayList<>(), mType);
    mAdapter.bindToRecyclerView(mRecyclerView);
  }

  private void initPopWindow(View view) {
    this.setContentView(view);
    this.setOutsideTouchable(true);
    this.setTouchable(true);
    this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
    this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
    this.setFocusable(true);
    this.setOutsideTouchable(true);
    if (View.LAYOUT_DIRECTION_RTL == mDirection) {
      this.setAnimationStyle(R.style.FolderChooseRltAnim);
    } else {
      this.setAnimationStyle(R.style.FolderChooseAnim);
    }
  }

  public void initData(List<MediaGroupItem> list) {
    if (null == list) {
      return;
    }
    mFolderList.clear();
    mFolderList.addAll(list);

    mAdapter.setNewData(mFolderList);
  }

  /**
   * 获取已选择列表
   */
  public List<MediaGroupItem> getSelectFolderList() {
    if (null == mAdapter || null == mAdapter.getData() || 0 == mAdapter.getData().size()) {
      return null;
    }

    List<MediaGroupItem> listSelecte = new ArrayList<>();
    for (MediaGroupItem info : mAdapter.getData()) {
      if (null != info && mAdapter.posIsNultiCheck(info.strGroupDisplayName)) {
        listSelecte.add(info);
      }
    }

    return listSelecte;
  }

  /**
   * 多选callback
   */
  public interface OnFolderMultiSelectCallback {
    void onMultiSelectListener(List<MediaGroupItem> selectedFolderList);
  }

  /**
   * 单选callback
   */
  public interface OnFolderSelectListener {
    void onSingleFolderSelected(MediaGroupItem groupItem);

    void onSnsLogin(MediaGroupItem groupItem);
  }
}
