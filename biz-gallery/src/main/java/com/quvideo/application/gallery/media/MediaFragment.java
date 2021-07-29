package com.quvideo.application.gallery.media;

import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.gallery.R;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.GalleryStatus;
import com.quvideo.application.gallery.adapterhelper.BaseQuickAdapter;
import com.quvideo.application.gallery.adapterhelper.listener.OnItemClickListener;
import com.quvideo.application.gallery.controller.IMedia;
import com.quvideo.application.gallery.controller.MediaSController;
import com.quvideo.application.gallery.media.adapter.MediaAdapter;
import com.quvideo.application.gallery.media.adapter.PinnedHeaderEntity;
import com.quvideo.application.gallery.media.decoration.PinnedHeaderItemDecoration;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaGroupItem;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.utils.pop.Pop;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Media(Video & Photo) fragment
 */
public class MediaFragment extends Fragment implements IMedia {

  private static final String BUNDLE_KEY_MEDIA_TYPE = "gallery_bundle_key_media_type";

  private LinearLayout mEmptyLayout;
  private RecyclerView mMediaRcView;
  private MediaAdapter mMediaAdapter;

  private MediaGroupItem mCurrentMediaGroup;
  private MediaSController mMediaController;

  private @GalleryDef.SourceType int mSourceType;
  private MediaFragmentInterCallback mFragmentInterCallback;

  private Map<MediaModel, SparseIntArray> mOrderedMediaMap = new LinkedHashMap<>();

  public MediaFragment() {
    // Required empty public constructor
  }

  public static MediaFragment newInstance(@GalleryDef.SourceType int sourceType) {
    Bundle args = new Bundle();
    args.putInt(BUNDLE_KEY_MEDIA_TYPE, sourceType);
    MediaFragment fragment = new MediaFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.gallery_media_fragment_layout, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (getArguments() != null) {
      mSourceType = getArguments().getInt(BUNDLE_KEY_MEDIA_TYPE);
    }

    mMediaController = new MediaSController(this, false);

    mEmptyLayout = view.findViewById(R.id.empty_layout);
    mMediaRcView = view.findViewById(R.id.recycler_view);

    mMediaAdapter = new MediaAdapter(new ArrayList<>());

    mMediaRcView.setLayoutManager(
        new GridLayoutManager(getContext(), GallerySettings.SHOWN_COLUMNS,
            GridLayoutManager.VERTICAL, false));

    mMediaAdapter.setOnItemChildClickListener((adapter, itemView, position) -> {
      Pop.showQuietly(itemView);
      if (mFragmentInterCallback == null) {
        return;
      }
      if (itemView.getId() == R.id.preview_icon) {
        if (mSourceType == GalleryDef.TYPE_VIDEO) {
          PinnedHeaderEntity<MediaModel> item = mMediaAdapter.getItem(position);
          if (item != null && item.getData() != null) {
            mFragmentInterCallback.onVideoPreview(item.getData(), itemView);
          }
        } else {
          int finalPos = position;
          List<PinnedHeaderEntity<MediaModel>> entityList = mMediaAdapter.getData();
          //filter header item
          for (int i = 0; i < position; i++) {
            PinnedHeaderEntity<MediaModel> entity = entityList.get(i);
            if (entity.getData() == null) {
              finalPos--;
            }
          }

          mFragmentInterCallback.onPhotoPreview(finalPos, itemView);
        }
      }
    });

    mMediaRcView.addOnItemTouchListener(new OnItemClickListener() {
      @Override public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int i) {
        switch (mMediaAdapter.getItemViewType(i)) {
          case MediaAdapter.TYPE_DATA:
            PinnedHeaderEntity<MediaModel> entity = mMediaAdapter.getData().get(i);
            if (mFragmentInterCallback != null) {
              mFragmentInterCallback.onMediaSelected(entity.getData());
            }

            break;
          case MediaAdapter.TYPE_HEADER:
          case MediaAdapter.TYPE_FOOTER:
            //do nothing
            break;
        }
      }
    });

    mMediaRcView.addItemDecoration(
        new PinnedHeaderItemDecoration.Builder(MediaAdapter.TYPE_HEADER).enableDivider(false)
            .create());

    mMediaRcView.setAdapter(mMediaAdapter);

    if (mCurrentMediaGroup != null) {
      loadMediaGroupInfo(mCurrentMediaGroup);
    } else {
      mMediaController.getLocalMedia(getContext(), mSourceType,
          getUserVisibleHint() ? 0 : GalleryDef.DEFAULT_TIME_DELAY);
    }
  }

  public void setFragmentInterCallback(MediaFragmentInterCallback callback) {
    mFragmentInterCallback = callback;
  }

  public void loadMediaGroupInfo(MediaGroupItem groupItem) {
    if (getContext() == null) {
      return;
    }

    this.mCurrentMediaGroup = groupItem;
    if (groupItem == null) {
      this.mMediaController.getLocalMedia(getContext(), mSourceType,
          getUserVisibleHint() ? 0 : GalleryDef.DEFAULT_TIME_DELAY);
    } else {
      this.mMediaController.updateMediaGroupData(getContext(), mSourceType, groupItem);
    }
  }

  @Override public int getMediaOrder(MediaModel model) {
    if (mFragmentInterCallback != null) {
      return mFragmentInterCallback.getMediaOrder(model);
    }
    return -1;
  }

  public MediaGroupItem getDisplayMediaGroup() {
    return mCurrentMediaGroup;
  }

  @Override public void onMediaGroupReady(MediaGroupItem groupItem) {
    this.mCurrentMediaGroup = groupItem;
  }

  @Override public void onMediaListReady(List<PinnedHeaderEntity<MediaModel>> entityList) {
    if (entityList == null || entityList.isEmpty()) {
      mMediaAdapter.setNewData(new ArrayList<>());
      mEmptyLayout.setVisibility(View.VISIBLE);
    } else {
      mMediaAdapter.setNewData(entityList);
      mEmptyLayout.setVisibility(View.GONE);
    }

    if (mSourceType == GalleryDef.TYPE_PHOTO) {
      if (entityList != null) {
        List<MediaModel> photoList = new ArrayList<>();
        for (PinnedHeaderEntity<MediaModel> entity : entityList) {
          MediaModel model = entity.getData();
          if (model != null) {
            photoList.add(model);
          }
        }
        GalleryStatus.getInstance().setPhotoList(photoList);
      }
    }

    //update OrderedMediaMap
    if (mFragmentInterCallback != null && mCurrentMediaGroup != null) {
      Map<MediaModel, SparseIntArray> orderedMap =
          mFragmentInterCallback.getOrderedMap(mSourceType,
              mCurrentMediaGroup.strGroupDisplayName);
      if (orderedMap == null) {
        orderedMap = new LinkedHashMap<>();
      }
      this.mOrderedMediaMap = orderedMap;
    }
  }

  public void updateMediaOrder(Map<MediaModel, SparseIntArray> orderedMediaMap) {
    if (mMediaAdapter == null) {
      return;
    }
    if (orderedMediaMap == null) {
      return;
    }
    if (orderedMediaMap.isEmpty() && mOrderedMediaMap.isEmpty()) {
      return;
    }
    Map<MediaModel, SparseIntArray> newOrderedMap = new LinkedHashMap<>(orderedMediaMap);
    /**
     * compare array{@link newOrderedMap} with last array{@link mOrderedMediaMap}
     * before update
     */
    //1 过滤掉不是当前资源类型的model,并更新位置
    Iterator<MediaModel> keyIterator = newOrderedMap.keySet().iterator();
    while (keyIterator.hasNext()) {
      MediaModel model = keyIterator.next();
      if (model.getSourceType() != mSourceType) {
        keyIterator.remove();
        continue;
      }
      int adapterMediaPosition = mMediaAdapter.getMediaPosition(model);
      if (adapterMediaPosition < 0) {
        keyIterator.remove();
        continue;
      }
      //update adapter position
      SparseIntArray sparseArray = newOrderedMap.get(model);
      if (sparseArray != null && sparseArray.size() > 0) {
        int key = sparseArray.keyAt(0);
        sparseArray.put(key, adapterMediaPosition);
      }
    }
    //对比数据变化，并指定刷新
    if (newOrderedMap.isEmpty()) {
      mMediaAdapter.clearOrder(mOrderedMediaMap);
    } else if (mOrderedMediaMap.isEmpty()) {
      mMediaAdapter.updateOrder(newOrderedMap);
    } else {
      Map<MediaModel, SparseIntArray> compareOrderMap =
          compareOrderMap(mOrderedMediaMap, newOrderedMap);
      mMediaAdapter.updateOrder(compareOrderMap);
    }

    this.mOrderedMediaMap.clear();
    this.mOrderedMediaMap.putAll(newOrderedMap);
    if (mFragmentInterCallback != null && mCurrentMediaGroup != null) {
      mFragmentInterCallback.updateOrderMap(mSourceType, mCurrentMediaGroup.strGroupDisplayName,
          mOrderedMediaMap);
    }
  }

  private Map<MediaModel, SparseIntArray> compareOrderMap(Map<MediaModel, SparseIntArray> preMap,
      Map<MediaModel, SparseIntArray> newMap) {
    Map<MediaModel, SparseIntArray> finalMap = new LinkedHashMap<>();
    for (MediaModel model : preMap.keySet()) {
      SparseIntArray preSparseIntArray = preMap.get(model);
      SparseIntArray newSparseIntArray = newMap.get(model);
      if (preSparseIntArray == null) {
        continue;
      }
      if (newSparseIntArray == null) {
        //not exist in new map
        int adapterPosition = preSparseIntArray.valueAt(0);
        SparseIntArray array = new SparseIntArray();
        array.put(0, adapterPosition);
        finalMap.put(model, array);
        continue;
      }
      //order check(exist in both map)
      //int preOrder = preSparseIntArray.keyAt(0);
      //int newOrder = newSparseIntArray.keyAt(0);
      //if (preOrder != newOrder) {
      //  finalMap.put(model, newSparseIntArray);
      //}
    }
    finalMap.putAll(newMap);
    return finalMap;
  }

  @Override public int getSourceType() {
    return mSourceType;
  }

  public interface MediaFragmentInterCallback {
    int getMediaOrder(MediaModel model);

    void onMediaSelected(MediaModel model);

    void onPhotoPreview(int position, View view);

    void onVideoPreview(MediaModel mediaModel, View view);

    void updateOrderMap(int sourceType, String key, Map<MediaModel, SparseIntArray> map);

    Map<MediaModel, SparseIntArray> getOrderedMap(int sourceType, String key);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (mMediaController != null) {
      mMediaController.detachView();
    }
  }
}


