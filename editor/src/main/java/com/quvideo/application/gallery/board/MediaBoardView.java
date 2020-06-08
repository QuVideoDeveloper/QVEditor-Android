package com.quvideo.application.gallery.board;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.GalleryStatus;
import com.quvideo.application.gallery.board.adapter.ClipItemDecoration;
import com.quvideo.application.gallery.board.adapter.DragItemTouchCallback;
import com.quvideo.application.gallery.board.adapter.MediaBoardAdapter;
import com.quvideo.application.gallery.board.adapter.SmoothLayoutManager;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.utils.GSizeUtil;
import com.quvideo.application.utils.pop.Pop;
import com.quvideo.application.utils.rx.RxViewUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 8/30/2019
 * GalleryType ： GallerySettings.GalleryType.GALLERY_TYPE_BOARD_NORAML
 * 普通type样式对应的MediaBoardView
 */
public class MediaBoardView extends BaseMediaBoardView {

  public static final String TAG = MediaBoardView.class.getSimpleName();
  RecyclerView mRecyclerView;

  private MediaBoardAdapter mMediaBoardAdapter;
  private RecyclerView.SmoothScroller smoothScroller;

  /**
   * note:
   * key means the media model;
   * value SparseArray ,key means order in selected list ,and value means index in media total
   * list(specific media type)
   */
  private Map<MediaModel, SparseIntArray> mOrderedMediaMap = new LinkedHashMap<>();

  public MediaBoardView(Context context) {
    this(context, null);
  }

  public MediaBoardView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MediaBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void init() {
    super.init();
    initRecyclerView();
    updateClipCount(0);
    RxViewUtil.setOnClickListener(view -> {
      Pop.showQuietly(view);
      ArrayList<MediaModel> mediaMissionList = getMediaMissionList();
      if (mMediaBoardCallback != null) {
        mMediaBoardCallback.onMediaSelectDone(mediaMissionList);
      }
    }, mNextBtn);
  }

  @Override protected int getLayoutId() {
    return R.layout.gallery_media_board_view_layout;
  }

  @SuppressLint("ClickableViewAccessibility") @Override
  public boolean onTouchEvent(MotionEvent event) {
    return true;
  }

  private void initRecyclerView() {
    mRecyclerView = mRootView.findViewById(R.id.rc_view);
    mRecyclerView.setLayoutManager(
        new SmoothLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    mRecyclerView.addItemDecoration(
        new ClipItemDecoration(GSizeUtil.getFitPxFromDp(getContext(), 14.f)));
    mMediaBoardAdapter = new MediaBoardAdapter(getContext());
    mMediaBoardAdapter.setListener(position -> {
      deleteItem(position);
    });
    mRecyclerView.setAdapter(mMediaBoardAdapter);

    DragItemTouchCallback itemTouchCallback =
        new DragItemTouchCallback(mMediaBoardAdapter, true);
    itemTouchCallback.setOnDragListener(new DragItemTouchCallback.OnDragListener() {
      @Override public void onStartDrag(View view, int pos) {
        Vibrator vib = (Vibrator) view.getContext().getSystemService(Service.VIBRATOR_SERVICE);
        if (vib != null) {
          vib.vibrate(120L);
        }
        mMediaBoardAdapter.dragStateChanged(view, true);
      }

      @Override public void onFinishDrag(View view, int startPos, int endPos) {
        mMediaBoardAdapter.dragStateChanged(view, false);
        if (startPos != endPos && null != mRecyclerView) {
          GalleryStatus.getInstance().setFileOrdered(true);
          mMediaBoardAdapter.choosePos = endPos;
          mRecyclerView.post(new Runnable() {
            @Override public void run() {
              mMediaBoardAdapter.notifyDataSetChanged();
            }
          });
        }
      }
    });
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
    itemTouchHelper.attachToRecyclerView(mRecyclerView);

    smoothScroller = new LinearSmoothScroller(getContext()) {

      @Override protected int getHorizontalSnapPreference() {
        return LinearSmoothScroller.SNAP_TO_END;
      }
    };
    //add adapter date observer
    mMediaBoardAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override public void onChanged() {
        super.onChanged();
        updateMediaOrder();
      }

      @Override public void onItemRangeChanged(int positionStart, int itemCount) {
        super.onItemRangeChanged(positionStart, itemCount);
        updateMediaOrder();
      }

      @Override public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        updateMediaOrder();
      }

      @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        updateMediaOrder();
      }

      @Override public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount);
        updateMediaOrder();
      }

      void updateMediaOrder() {
        mOrderedMediaMap.clear();
        List<MediaModel> missionList = mMediaBoardAdapter.getMissionList();
        GalleryStatus.getInstance().setSelectedList(missionList);
        for (int i = 0; i < missionList.size(); i++) {
          MediaModel model = missionList.get(i);
          SparseIntArray sparseArray = new SparseIntArray();
          sparseArray.put(i + 1, -1);
          mOrderedMediaMap.put(model, sparseArray);
        }
        if (mMediaBoardCallback != null) {
          mMediaBoardCallback.onOrderChanged(mOrderedMediaMap);
        }
      }
    });

    collapse();
  }

  /**
   * 删除某个item
   */
  private void deleteItem(int position) {
    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    if (null == settings || null == mMediaBoardAdapter || position < 0) {
      return;
    }
    mMediaBoardAdapter.removeMissionItem(position);
    updateClipCount(mMediaBoardAdapter.getItemCount());
  }

  @Override
  public void addMediaItem(MediaModel model, boolean replace) {
    if (mMediaBoardAdapter == null || null == model) {
      return;
    }
    boolean missionExist = isMissionExist(model);
    if (missionExist) {
      removeMediaItem(model);
    }
    if (!missionExist || replace) {
      mMediaBoardAdapter.addMissionItem(model);
      mRecyclerView.postDelayed(this::scrollToEnd, 100);
      updateClipCount(mMediaBoardAdapter.getItemCount());
    }
  }

  @Override
  public void addMediaItem(List<MediaModel> modelList, int choosePos) {
    if (mMediaBoardAdapter == null) {
      return;
    }
    for (MediaModel model : modelList) {
      boolean missionExist = isMissionExist(model);
      if (missionExist) {
        removeMediaItem(model);
      }
    }

    mMediaBoardAdapter.addMissionItem(modelList);
    mRecyclerView.postDelayed(new Runnable() {
      @Override public void run() {
        if (BaseMediaBoardView.DEF_NEGATIVE_ONE == choosePos) {
          scrollToEnd();
        } else {
          scrollToPos(choosePos);
        }
      }
    }, 100);

    updateClipCount(mMediaBoardAdapter.getItemCount());
  }

  @Override
  public void removeMediaItem(MediaModel model) {
    if (mMediaBoardAdapter != null) {
      int adapterMediaPosition = mMediaBoardAdapter.getMediaPosition(model);
      if (adapterMediaPosition >= 0) {
        //remove it if the media exist in selected(logic)
        mMediaBoardAdapter.removeMissionItem(adapterMediaPosition);
        updateClipCount(mMediaBoardAdapter.getItemCount());
      }
    }
  }

  @Override
  public void updateOnlyMission(MediaModel model) {
    if (mMediaBoardAdapter != null) {
      mMediaBoardAdapter.clearMissionItem();
    }
    addMediaItem(model, false);
  }

  @Override
  public boolean isMissionExist(MediaModel model) {
    return getMediaBoardIndex(model) >= 0;
  }

  @Override
  public int getMediaBoardIndex(MediaModel model) {
    if (mMediaBoardAdapter != null) {
      return mMediaBoardAdapter.getMediaPosition(model);
    }
    return -1;
  }

  private void scrollToEnd() {
    if (mMediaBoardAdapter == null) {
      return;
    }
    LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
    if (layoutManager == null) {
      return;
    }
    try {
      int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
      if (lastVisibleItemPosition == mMediaBoardAdapter.getItemCount() - 1) {
        mRecyclerView.smoothScrollToPosition(lastVisibleItemPosition);
      } else {
        smoothScroller.setTargetPosition(mMediaBoardAdapter.getItemCount() - 1);
        layoutManager.startSmoothScroll(smoothScroller);
      }
    } catch (Exception ignore) {
    } finally {
      //do nothing
    }
  }

  private void scrollToPos(int pos) {
    if (mMediaBoardAdapter == null) {
      return;
    }
    LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
    if (layoutManager == null) {
      return;
    }
    try {
      smoothScroller.setTargetPosition(pos);
      layoutManager.startSmoothScroll(smoothScroller);
    } catch (Exception ignore) {
    } finally {
      //do nothing
    }
  }

  @Override
  public ArrayList<MediaModel> getMediaMissionList() {
    if (mMediaBoardAdapter != null) {
      return mMediaBoardAdapter.getMissionList();
    }
    return null;
  }

  @Override
  public int getSelectedMediaCount() {
    if (mMediaBoardAdapter != null) {
      return mMediaBoardAdapter.getItemCount();
    }
    return 0;
  }
}
