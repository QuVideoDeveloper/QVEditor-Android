package com.quvideo.application.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.board.BaseMediaBoardView;
import com.quvideo.application.gallery.board.MediaBoardCallback;
import com.quvideo.application.gallery.controller.GalleryFileController;
import com.quvideo.application.gallery.controller.IGalleryFile;
import com.quvideo.application.gallery.controller.IMedia;
import com.quvideo.application.gallery.controller.MediaSController;
import com.quvideo.application.gallery.magicindicator.CommonNavigator;
import com.quvideo.application.gallery.magicindicator.CommonNavigatorAdapter;
import com.quvideo.application.gallery.magicindicator.IPagerIndicator;
import com.quvideo.application.gallery.magicindicator.IPagerTitleView;
import com.quvideo.application.gallery.magicindicator.LinePagerIndicator;
import com.quvideo.application.gallery.magicindicator.MagicIndicator;
import com.quvideo.application.gallery.magicindicator.SimplePagerTitleView;
import com.quvideo.application.gallery.magicindicator.ViewPagerHelper;
import com.quvideo.application.gallery.media.MediaFragment;
import com.quvideo.application.gallery.media.adapter.FolderListAdapter;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaGroupItem;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.preview.PhotoActivity;
import com.quvideo.application.gallery.preview.VideoTrimActivity;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.gallery.utils.GSizeUtil;
import com.quvideo.application.gallery.utils.GalleryToast;
import com.quvideo.application.gallery.widget.FolderListPopwindow;
import com.quvideo.application.gallery.widget.GalleryTitleView;
import com.quvideo.application.utils.pop.Pop;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment implements IMedia, IGalleryFile {

  private static final int REQUEST_CODE_PHOTO_PREVIEW = 8001;
  private static final int REQUEST_CODE_VIDEO_PREVIEW = 8002;

  private GalleryTitleView mTitleView;
  private BaseMediaBoardView mMediaBoardView;
  private ViewPager mViewPager;
  private MediaPagerAdapter mPagerAdapter;

  private List<MediaFragment> mFragmentList;
  private FolderListPopwindow mFolderPopWindow;
  private MediaSController mMediaSController;
  private GalleryFileController mFileController;

  private Map<String, Map<MediaModel, SparseIntArray>> mVideoFragmentOrderedMap =
      new HashMap<>();

  private Map<String, Map<MediaModel, SparseIntArray>> mPhotoFragmentOrderedMap =
      new HashMap<>();

  public GalleryFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment GalleryFragment.
   */
  public static GalleryFragment newInstance() {
    GalleryFragment fragment = new GalleryFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.gallery_main_fragment_layout, container, false);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mTitleView = view.findViewById(R.id.title_view);
    mMediaBoardView = view.findViewById(R.id.board_view);
    mViewPager = view.findViewById(R.id.view_pager);

    mMediaBoardView.setVisibility(View.VISIBLE);

    mMediaSController = new MediaSController(this, true);
    mFileController = new GalleryFileController(this);

    initTitleLayout();
    initViewPager();
    addListener();
  }

  private void addListener() {
    mTitleView.setTitleViewCallback(new GalleryTitleView.TitleViewCallback() {
      @Override public void onBack() {
        GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
        if (getActivity() != null) {
          getActivity().finish();
        }
      }

      @Override public void onFolderEntrance(View anchor) {
        if (anchor == null) {
          return;
        }
        Log.i("zjf FolderListData", " GalleryFragment onFolderEntrance : mFolderPopWindow init");
        List<MediaGroupItem> groupList = mMediaSController.getMediaGroupList(getActivity());
        Log.i("zjf FolderListData",
            " GalleryFragment onFolderEntrance : mFolderPopWindow init, groupList = "
                + (null == groupList ? "null" : groupList.size()));
        int direction = anchor.getLayoutDirection();
        if (mFolderPopWindow == null) {
          mFolderPopWindow =
              new FolderListPopwindow(getActivity(), FolderListAdapter.TYPE_SINGLE, direction,
                  mFolderSelectListener);
        }
        mFolderPopWindow.initData(groupList);
        if (View.LAYOUT_DIRECTION_RTL == direction) {
          mFolderPopWindow.showAsDropDown(anchor,
              -anchor.getWidth() / 2 - GSizeUtil.getFitPxFromDp(getContext(), 39), 0,
              Gravity.BOTTOM | Gravity.START);
        } else {
          mFolderPopWindow.showAsDropDown(anchor);
        }
      }
    });
    mMediaBoardView.setMediaBoardCallback(new MediaBoardCallback() {
      @Override public void onMediaSelectDone(ArrayList<MediaModel> missionModelList) {
        if (missionModelList == null || missionModelList.isEmpty()) {
          return;
        }
        GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
        int minSelectCount = settings.getMinSelectCount();
        if (missionModelList.size() < minSelectCount) {
          GalleryToast.show(getActivity(),
              getString(R.string.mn_gallery_template_selected_count_deficient_description));
          return;
        }

        //done , do next
        mFileController.processFileCompress(missionModelList);
      }

      @Override public void onOrderChanged(Map<MediaModel, SparseIntArray> orderedIntArrayMap) {
        for (MediaFragment fragment : mFragmentList) {
          fragment.updateMediaOrder(orderedIntArrayMap);
        }
      }
    });
  }

  private void initTitleLayout() {
    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    final int showMode = settings.getShowMode();

    CommonNavigator commonNavigator = new CommonNavigator(getContext());
    commonNavigator.setScrollPivotX(0.25f);
    commonNavigator.setAdapter(new CommonNavigatorAdapter() {
      @Override public int getCount() {
        return showMode == GalleryDef.MODE_BOTH ? 2 : 1;
      }

      @Override public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
        if (showMode == GalleryDef.MODE_BOTH) {
          if (index == 0) {
            simplePagerTitleView.setText(R.string.mn_gallery_select_video);
          } else if (index == 1) {
            simplePagerTitleView.setText(R.string.mn_gallery_select_photo);
          }
        } else {
          if (showMode == GalleryDef.MODE_VIDEO) {
            simplePagerTitleView.setText(R.string.mn_gallery_select_video);
          } else {
            simplePagerTitleView.setText(R.string.mn_gallery_select_photo);
          }
        }

        simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
        simplePagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        simplePagerTitleView.setTextAppearance(getContext(), R.style.tab_text_style);
        if (showMode == GalleryDef.MODE_BOTH) {
          simplePagerTitleView.setOnClickListener(v -> {
            Pop.showQuietly(v);
            mViewPager.setCurrentItem(index);
          });
        }
        return simplePagerTitleView;
      }

      @Override public IPagerIndicator getIndicator(Context context) {
        if (showMode != GalleryDef.MODE_BOTH) {
          return null;
        }
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
        indicator.setRoundRadius(GSizeUtil.dp2Pixel(context, 1.5f));
        indicator.setLineWidth(GSizeUtil.dp2Pixel(context, 12f));
        indicator.setYOffset(GSizeUtil.dp2Pixel(context, 5f));
        indicator.setColors(Color.parseColor("#ffffff"));
        return indicator;
      }
    });

    MagicIndicator magicIndicator = mTitleView.getMagicIndicator();
    magicIndicator.setNavigator(commonNavigator);
    ViewPagerHelper.bindWithCallback(magicIndicator, mViewPager,
        new ViewPager.SimpleOnPageChangeListener() {
          @Override public void onPageSelected(int position) {
          }
        });
  }

  private void initViewPager() {
    GallerySettings settings = GalleryClient.getInstance().getGallerySettings();
    final int showMode = settings.getShowMode();
    mFragmentList = new ArrayList<>();
    if (showMode == GalleryDef.MODE_VIDEO) {
      mFragmentList.add(MediaFragment.newInstance(GalleryDef.TYPE_VIDEO));
    } else if (showMode == GalleryDef.MODE_PHOTO) {
      mFragmentList.add(MediaFragment.newInstance(GalleryDef.TYPE_PHOTO));
    } else {
      mFragmentList.add(MediaFragment.newInstance(GalleryDef.TYPE_VIDEO));
      mFragmentList.add(MediaFragment.newInstance(GalleryDef.TYPE_PHOTO));
    }

    for (MediaFragment fragment : mFragmentList) {
      fragment.setFragmentInterCallback(mFragmentInterCallback);
    }

    if (mPagerAdapter == null) {
      mPagerAdapter = new MediaPagerAdapter(getChildFragmentManager(), mFragmentList);
    }
    mViewPager.setAdapter(mPagerAdapter);

    //set default fragment visible
    boolean photoTabFocus = settings.isPhotoTabFocus();
    if (photoTabFocus && mFragmentList.size() > 1) {
      mViewPager.setCurrentItem(1);
      mFragmentList.get(1).setUserVisibleHint(true);
    } else {
      mViewPager.setCurrentItem(0);
      mFragmentList.get(0).setUserVisibleHint(true);
    }
  }

  @Override public int getMediaOrder(MediaModel model) {
    if (mMediaBoardView != null) {
      return mMediaBoardView.getMediaBoardIndex(model);
    }
    return -1;
  }

  @Override public void onMediaGroupReady(MediaGroupItem groupItem) {
    if (mFragmentList == null || mFragmentList.isEmpty() || null == mMediaSController) {
      return;
    }
    for (MediaFragment fragment : mFragmentList) {
      MediaGroupItem displayMediaGroup = fragment.getDisplayMediaGroup();
      if (displayMediaGroup != null && TextUtils.equals(
          displayMediaGroup.getStrGroupDisplayName(), groupItem.getStrGroupDisplayName())) {
        fragment.loadMediaGroupInfo(groupItem);
      }
    }
    if (mFolderPopWindow != null && mFolderPopWindow.isShowing()) {
      mFolderPopWindow.initData(mMediaSController.getMediaGroupList(getContext()));
    }
  }

  @Override public void onMediaGroupListReady(List<MediaGroupItem> groupItemList) {
    if (mFragmentList == null
        || mFragmentList.isEmpty()
        || null == mMediaSController
        || null == mFolderPopWindow
        || !mFolderPopWindow.isShowing()) {
      return;
    }
    mFolderPopWindow.initData(mMediaSController.getMediaGroupList(getContext()));
  }

  private FolderListPopwindow.OnFolderSelectListener mFolderSelectListener =
      new FolderListPopwindow.OnFolderSelectListener() {
        @Override public void onSingleFolderSelected(MediaGroupItem groupItem) {
          if (groupItem != null) {
            GalleryClient.getInstance().setCurrFolderDisplayName(groupItem.strGroupDisplayName);
            String displayName = groupItem.getStrGroupDisplayName();
          }
          for (MediaFragment fragment : mFragmentList) {
            fragment.loadMediaGroupInfo(groupItem);
          }
        }

        @Override public void onSnsLogin(MediaGroupItem groupItem) {
          if (groupItem == null) {
            return;
          }
        }
      };
  private MediaFragment.MediaFragmentInterCallback mFragmentInterCallback =
      new MediaFragment.MediaFragmentInterCallback() {
        @Override public int getMediaOrder(MediaModel model) {
          return GalleryFragment.this.getMediaOrder(model);
        }

        @Override public void onMediaSelected(MediaModel model) {
          if (model == null || getActivity() == null) {
            return;
          }
          if (!checkFileEditAble(model.getFilePath())) {
            return;
          }

          GallerySettings gallerySettings = GalleryClient.getInstance().getGallerySettings();
          long videoMinDuration = gallerySettings.getVideoMinDuration();
          long videoMaxDuration = gallerySettings.getVideoMaxDuration();
          int minSelectCount = gallerySettings.getMinSelectCount();
          int maxSelectCount = gallerySettings.getMaxSelectCount();

          int sourceType = model.getSourceType();
          if (sourceType == GalleryDef.TYPE_VIDEO) {
            //check video duration
            long duration = model.getDuration();
            if (videoMinDuration > 0 && duration < videoMinDuration) {
              GalleryToast.show(getActivity(), getString(
                  R.string.mn_gallery_template_selected_duration_deficient_description));
              return;
            }
            if (videoMaxDuration > 0 && duration > videoMaxDuration) {
              GalleryToast.show(getActivity(), getString(
                  R.string.mn_gallery_ve_limit_max_duration_text));
              return;
            }
          }

          if (minSelectCount == 1 && maxSelectCount == 1) {
            //only one
            mMediaBoardView.updateOnlyMission(model);
            return;
          }

          boolean missionExist = mMediaBoardView.isMissionExist(model);
          if (!missionExist) {
            if (maxSelectCount > GallerySettings.NO_LIMIT_UP_FLAG) {
              if (mMediaBoardView.getSelectedMediaCount() == maxSelectCount) {
                GalleryToast.show(getActivity(),
                    getString(R.string.mn_gallery_template_enough_tip_text));
                return;
              }
            }
          }
          mMediaBoardView.addMediaItem(model, false);
        }

        @Override public void onPhotoPreview(int position, View view) {
          List<MediaModel> photoList = GalleryStatus.getInstance().getPhotoList();
          if (photoList == null || photoList.isEmpty()) {
            return;
          }

          PhotoActivity.launchPhoto(getActivity(), position, getRemainCount(), view,
              REQUEST_CODE_PHOTO_PREVIEW);
        }

        @Override public void onVideoPreview(MediaModel mediaModel, View view) {
          if (null == mediaModel || getActivity() == null || !checkFileEditAble(
              mediaModel.getFilePath())) {
            return;
          }

          GallerySettings gallerySettings = GalleryClient.getInstance().getGallerySettings();
          long videoMinDuration = gallerySettings.getVideoMinDuration();
          long videoMaxDuration = gallerySettings.getVideoMaxDuration();
          //check video duration
          long duration = mediaModel.getDuration();
          if (videoMinDuration > 0 && duration < videoMinDuration) {
            GalleryToast.show(getActivity(), getString(
                R.string.mn_gallery_template_selected_duration_deficient_description));
            return;
          }
          if (videoMaxDuration > 0 && duration > videoMaxDuration) {
            GalleryToast.show(getActivity(), getString(
                R.string.mn_gallery_ve_limit_max_duration_text));
            return;
          }

          if (mFileController.isNetMedia(mediaModel.getFilePath())) {
            List<MediaModel> modelList = new ArrayList<>();
            modelList.add(mediaModel);
            ArrayList<MediaModel> needDownloadModelList =
                mFileController.checkDownloadMedia(modelList);
            if (needDownloadModelList != null && !needDownloadModelList.isEmpty()) {
              mFileController.startDownloadNetMedia(needDownloadModelList);
              return;
            }
          }
          VideoTrimActivity.launchVideoTrim(getActivity(), REQUEST_CODE_VIDEO_PREVIEW, view,
              mediaModel);
        }

        @Override public void updateOrderMap(int sourceType, String key,
            Map<MediaModel, SparseIntArray> map) {
          if (sourceType == GalleryDef.TYPE_VIDEO) {
            mVideoFragmentOrderedMap.put(key, map);
          } else {
            mPhotoFragmentOrderedMap.put(key, map);
          }
        }

        @Override
        public Map<MediaModel, SparseIntArray> getOrderedMap(int sourceType, String key) {
          Map<MediaModel, SparseIntArray> fragmentOrderedMap;
          if (sourceType == GalleryDef.TYPE_VIDEO) {
            fragmentOrderedMap = mVideoFragmentOrderedMap.get(key);
          } else {
            fragmentOrderedMap = mPhotoFragmentOrderedMap.get(key);
          }
          return fragmentOrderedMap;
        }
      };

  private boolean checkFileEditAble(String filePath) {
    if (TextUtils.isEmpty(filePath)) {
      return false;
    }
    if (mFileController.isNetMedia(filePath)) {
      return true;
    }
    IGalleryProvider galleryProvider = GalleryClient.getInstance().getGalleryProvider();
    if (null == galleryProvider) {
      return false;
    }
    boolean fileEditAble = galleryProvider.checkFileEditAble(filePath);
    if (!fileEditAble) {
      if (getActivity() != null) {
        GalleryToast.show(getActivity(),
            getString(R.string.mn_gallery_template_file_type_no_support));
      }
      return false;
    }
    return true;
  }

  @Override public void onFileDone(ArrayList<MediaModel> modelArrayList) {
    GallerySettings gallerySettings = GalleryClient.getInstance().getGallerySettings();
    IGalleryProvider galleryProvider = GalleryClient.getInstance().getGalleryProvider();
    if (null == galleryProvider) {
      if (getActivity() != null) {
        getActivity().finish();
      }
      return;
    }
    galleryProvider.onGalleryFileDone(modelArrayList);
    onGalleryDoneEvent(modelArrayList);

    boolean onlySupportFragment = gallerySettings.isOnlySupportFragment();
    if (!onlySupportFragment) {
      if (getActivity() != null) {
        getActivity().finish();
      }
    }
  }

  private void onGalleryDoneEvent(ArrayList<MediaModel> modelArrayList) {
    if (modelArrayList != null && !modelArrayList.isEmpty()) {
      int videoCount = 0;
      for (MediaModel model : modelArrayList) {
        if (model.getSourceType() == GalleryDef.TYPE_VIDEO) {
          videoCount++;
        }
      }
    }
  }

  public class MediaPagerAdapter extends FragmentPagerAdapter {
    private List<MediaFragment> fragmentList;

    MediaPagerAdapter(FragmentManager fm, List<MediaFragment> list) {
      super(fm);
      this.fragmentList = list;
    }

    @Override public Fragment getItem(int position) {
      return fragmentList.get(position);
    }

    @Override public int getCount() {
      return fragmentList.size();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == REQUEST_CODE_PHOTO_PREVIEW) {
      if (resultCode == Activity.RESULT_OK && data != null) {
        ArrayList<Integer> list =
            data.getIntegerArrayListExtra(PhotoActivity.INTENT_PHOTO_LIST_KEY);
        List<MediaModel> missionModelList = new ArrayList<>();
        List<MediaModel> photoList = GalleryStatus.getInstance().getPhotoList();
        if (photoList != null && !photoList.isEmpty()) {
          for (Integer index : list) {
            if (index >= 0 && index < photoList.size()) {
              missionModelList.add(photoList.get(index));
            }
          }
        }
        if (mMediaBoardView != null) {
          int remainCount = getRemainCount();
          if (remainCount <= 0) {
            GalleryToast.show(getActivity(),
                getString(R.string.mn_gallery_template_enough_tip_text));
            return;
          }
          if (missionModelList.size() > remainCount) {
            List<MediaModel> finalModelList = missionModelList.subList(0, remainCount);
            mMediaBoardView.addMediaItem(finalModelList, BaseMediaBoardView.DEF_NEGATIVE_ONE);
          } else {
            mMediaBoardView.addMediaItem(missionModelList, BaseMediaBoardView.DEF_NEGATIVE_ONE);
          }
        }
      }
    } else if (requestCode == REQUEST_CODE_VIDEO_PREVIEW) {
      if (resultCode == Activity.RESULT_OK && null != data) {
        MediaModel mediaModel = data.getParcelableExtra(VideoTrimActivity.EXTRAC_MEDIA_MODEL);
        if (null != mediaModel && null != mMediaBoardView) {
          int remainCount = getRemainCount();
          if (remainCount <= 0) {
            GalleryToast.show(getActivity(),
                getString(R.string.mn_gallery_template_enough_tip_text));
            return;
          }

          mMediaBoardView.addMediaItem(mediaModel, true);
        }
      }
    }
  }

  /**
   * 基于数量上限，还能添加数量
   *
   * @return 剩余添加数量
   */
  private int getRemainCount() {
    GallerySettings gallerySettings = GalleryClient.getInstance().getGallerySettings();
    int maxSelectCount = gallerySettings.getMaxSelectCount();
    boolean isCountLimit = maxSelectCount > GallerySettings.NO_LIMIT_UP_FLAG;
    if (isCountLimit && mMediaBoardView != null) {
      int selectedMediaCount = mMediaBoardView.getSelectedMediaCount();
      return maxSelectCount - selectedMediaCount;
    }
    return Integer.MAX_VALUE / 2;
  }

  /**
   * for external ad
   *
   * @return ad view container
   */
  public ViewGroup getAdContainer() {
    if (getView() != null) {
      return getView().findViewById(R.id.gallery_ad);
    }
    return null;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    GalleryStatus.getInstance().reset();
    if (mFolderPopWindow != null && mFolderPopWindow.isShowing()) {
      mFolderPopWindow.dismiss();
      mFolderPopWindow = null;
    }
    if (mMediaSController != null) {
      mMediaSController.detachView();
    }
    if (mFileController != null) {
      mFileController.detachView();
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    GalleryClient.getInstance().setCurrFolderDisplayName("");
  }
}
