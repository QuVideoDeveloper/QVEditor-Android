package com.quvideo.application.editor.edit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.DPUtils;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.EditOperate;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.edit.sub.EditAdjustDialog;
import com.quvideo.application.editor.edit.sub.EditClipBGDialog;
import com.quvideo.application.editor.edit.sub.EditClipCropDialog;
import com.quvideo.application.editor.edit.sub.EditClipKeyFrameDialog;
import com.quvideo.application.editor.edit.sub.EditClipPosInfoDialog;
import com.quvideo.application.editor.edit.sub.EditCurveAdjustDialog;
import com.quvideo.application.editor.edit.sub.EditFilterDialog;
import com.quvideo.application.editor.edit.sub.EditFxFilterDialog;
import com.quvideo.application.editor.edit.sub.EditMagicSoundDialog;
import com.quvideo.application.editor.edit.sub.EditPicDurationDialog;
import com.quvideo.application.editor.edit.sub.EditSpeedDialog;
import com.quvideo.application.editor.edit.sub.EditSplitDialog;
import com.quvideo.application.editor.edit.sub.EditTransDialog;
import com.quvideo.application.editor.edit.sub.EditTrimDialog;
import com.quvideo.application.editor.edit.sub.EditVolumeDialog;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.superedit.SuperEditManager;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.application.widget.sort.CusSortRecycler;
import com.quvideo.application.widget.sort.ItemDragHelperCallback;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.error.SDKErrCode;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.ClipAddItem;
import com.quvideo.mobile.engine.model.clip.ClipReplaceItem;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPAdd;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPCopy;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPDel;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPMove;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPPicAnim;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPReplace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPReverse;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPSplit;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.ArrayList;
import java.util.List;

public class EditEditDialog extends BaseMenuView {

  private EditClipAdapter clipAdapter;

  private AppCompatImageView mCropImageView;

  private EditOperateAdapter mOperateAdapter;

  public EditEditDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      ItemOnClickListener l, AppCompatImageView cropImageView, IFakeViewApi iFakeViewApi) {
    super(context, workSpace);
    this.mCropImageView = cropImageView;
    showMenu(container, l, iFakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipEdit;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_edit;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    // clip
    CusSortRecycler clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    clipAdapter = new EditClipAdapter(mWorkSpace);
    clipRecyclerView.setAdapter(clipAdapter);
    clipRecyclerView.setSceneListener(new CusSortRecycler.SelectSceneListener() {

      @Override public void onOrderStart() {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      }

      @Override public void onOrderChanged(int from, int to) {
        if (from != to) {
          if (from >= clipAdapter.getItemCount() - 1
              || to >= clipAdapter.getItemCount() - 1) {
            return;
          }
          ClipOPMove clipOPMove = new ClipOPMove(from, to);
          mWorkSpace.handleOperation(clipOPMove);
        }
      }
    });
    clipRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
          @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int offset = DPUtils.dpToPixel(getContext(), 4);
        outRect.left = offset;
        outRect.right = offset;
      }
    });
    ItemDragHelperCallback callback = new ItemDragHelperCallback() {
      @Override public boolean isLongPressDragEnabled() {
        return true;
      }
    };
    callback.setOnItemMoveListener(clipRecyclerView);
    ItemTouchHelper helper = new ItemTouchHelper(callback);
    helper.attachToRecyclerView(clipRecyclerView);

    initData();
    RecyclerView editRecyclerView = view.findViewById(R.id.operate_recyclerview);
    editRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mOperateAdapter = new EditOperateAdapter(this);
    editRecyclerView.setAdapter(mOperateAdapter);
    refreshMenu();
  }

  private void refreshMenu() {
    // operate
    List<EditOperate> list = new ArrayList<EditOperate>() {{
      ClipData clipData = mWorkSpace.getClipAPI().getClipByIndex(clipAdapter.getSelClipIndex());
      if (clipData == null || clipData.isVideo()) {
        add(new EditOperate(R.drawable.edit_icon_trim_n,
            getContext().getString(R.string.mn_edit_title_trim)));
      } else {
        add(new EditOperate(R.drawable.editorx_icon_pic_trim_n,
            getContext().getString(R.string.mn_edit_clip_img_duration)));
        if (clipData.isPicAnimOn()) {
          add(new EditOperate(R.drawable.editorx_icon_pic_anim_on,
              getContext().getString(R.string.mn_edit_clip_img_anim)));
        } else {
          add(new EditOperate(R.drawable.editorx_icon_pic_anim_off,
              getContext().getString(R.string.mn_edit_clip_img_anim)));
        }
      }
      add(new EditOperate(R.drawable.edit_icon_split_nor,
          getContext().getString(R.string.mn_edit_title_split)));
      add(new EditOperate(R.drawable.edit_icon_duplicate,
          getContext().getString(R.string.mn_edit_duplicate_title)));
      add(new EditOperate(R.drawable.edit_icon_location_nor,
          getContext().getString(R.string.mn_edit_effect_position)));
      add(new EditOperate(R.drawable.edit_icon_delete_nor,
          getContext().getString(R.string.mn_edit_title_delete)));
      add(new EditOperate(R.drawable.edit_icon_edit_nor,
          getContext().getString(R.string.mn_edit_bgm_edit)));
      add(new EditOperate(R.drawable.edit_icon_muteoff_n,
          getContext().getString(R.string.mn_edit_title_volume)));
      add(new EditOperate(R.drawable.edit_icon_filter_nor,
          getContext().getString(R.string.mn_edit_title_filter)));
      add(new EditOperate(R.drawable.edit_icon_effect_nor,
          getContext().getString(R.string.mn_edit_title_fx_filter)));
      add(new EditOperate(R.drawable.edit_icon_change_nor,
          getContext().getString(R.string.mn_edit_title_transitions)));
      add(new EditOperate(R.drawable.edit_icon_changevoice_nor,
          getContext().getString(R.string.mn_edit_title_change_voice)));
      add(new EditOperate(R.drawable.edit_icon_speed_nor,
          getContext().getString(R.string.mn_edit_title_speed)));
      add(new EditOperate(R.drawable.edit_icon_adjust_nor,
          getContext().getString(R.string.mn_edit_title_adjust)));
      add(new EditOperate(R.drawable.editor_tool_adjust_curve,
          getContext().getString(R.string.mn_edit_title_adjust_curve)));
      add(new EditOperate(R.drawable.editor_tool_background_icon,
          getContext().getString(R.string.mn_edit_title_background)));
      add(new EditOperate(R.drawable.edit_icon_reserve_nor,
          getContext().getString(R.string.mn_edit_title_reserve)));
      if (EditorApp.Companion.getInstance().getEditorConfig().isCropEditValid()) {
        add(new EditOperate(R.drawable.edit_icon_crop_n,
            getContext().getString(R.string.mn_edit_title_crop)));
      }
      add(new EditOperate(R.drawable.editor_tool_keyframeanimator_icon,
          getContext().getString(R.string.mn_edit_keyframe_animator_title)));
    }};
    SuperEditManager.addClipOPFunc(getContext(), list);
    if (mOperateAdapter != null) {
      mOperateAdapter.updateDatas(list);
    }
  }

  private QEPlayerListener mPlayerListener = new QEPlayerListener() {
    @Override public void onPlayerCallback(PlayerStatus playerStatus, int progress) {
      if (playerStatus == PlayerStatus.STATUS_PAUSE
          || playerStatus == PlayerStatus.STATUS_PLAYING
          || playerStatus == PlayerStatus.STATUS_SEEKING) {
        int selectIndex = getClipIndexByTime(progress);
        if (clipAdapter.getSelClipIndex() != selectIndex) {
          clipAdapter.changeSelect(selectIndex);
        }
      }
    }

    @Override public void onPlayerRefresh() {
      if (mWorkSpace != null
          && mWorkSpace.getPlayerAPI() != null
          && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
        int currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
        int selectIndex = getClipIndexByTime(currentTime);
        if (clipAdapter.getSelClipIndex() != selectIndex) {
          clipAdapter.changeSelect(selectIndex);
        }
      }
    }

    @Override public void onSizeChanged(Rect resultRect) {

    }
  };

  public int getClipIndexByTime(int curTime) {
    List<ClipData> clipList = mWorkSpace.getClipAPI().getClipList();
    int count = clipList.size();
    if (count <= 0) {
      return 0;
    }
    VeRange preRange = null;
    VeRange nextRange = null;
    VeRange curRange = null;
    int startTime = 0;
    for (int index = 0; index < count; index++) {
      curRange = clipList.get(index).getDestRange();
      if (preRange == null) {
        startTime = 0;
      } else {
        startTime = (curRange.getPosition() + preRange.getLimitValue()) / 2;
      }
      if (index < count - 1) {
        nextRange = clipList.get(index + 1).getDestRange();
      }
      int endTime = clipList.get(index).getDestRange().getLimitValue();
      if (nextRange != null) {
        endTime = (endTime + nextRange.getPosition()) / 2 - 1;
      }
      if (startTime <= curTime && endTime >= curTime) {
        return index;
      }
      preRange = curRange;
    }
    return count - 1;
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof ClipOPAdd
          || operate instanceof ClipOPDel
          || operate instanceof ClipOPCopy
          || operate instanceof ClipOPReplace
          || operate instanceof ClipOPMove
          || operate instanceof ClipOPSplit) {
        // 添加 / 删除 clip完成监听
        AndroidSchedulers.mainThread().scheduleDirect(() -> clipAdapter.updateClipList());
        refreshMenu();
      } else if (operate instanceof ClipOPPicAnim) {
        refreshMenu();
      }
    }
  };

  @Override protected void releaseAll() {
    mWorkSpace.removeObserver(mBaseObserver);
    mWorkSpace.getPlayerAPI().unregisterListener(mPlayerListener);
    if (clipAdapter != null) {
      clipAdapter.release();
    }
  }

  private void initData() {
    clipAdapter.updateClipList();
    clipAdapter.setOnClipClickListener(new EditClipAdapter.OnClipClickListener() {
      @Override public void onClipAdd() {
        int selClip = clipAdapter.getSelClipIndex();
        gotoEdit(selClip + 1);
      }

      @Override public void onFocusChange() {
        refreshMenu();
      }
    });

    mWorkSpace.addObserver(mBaseObserver);
    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);
  }

  @Override public void onClick(View view, EditOperate operate) {
    int selIndex = clipAdapter.getSelClipIndex();
    if (SuperEditManager.clickClipOPFunc(getActivity(), operate, selIndex, mMenuContainer, mWorkSpace, mFakeApi)) {
      return;
    }
    if (operate.getResId() == R.drawable.edit_icon_trim_n) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditTrimDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.editorx_icon_pic_trim_n) {
      // 图片时长
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditPicDurationDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.editorx_icon_pic_anim_on) {
      // 图片动画关闭
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      doClipPicAnim(selIndex, false);
    } else if (operate.getResId() == R.drawable.editorx_icon_pic_anim_off) {
      // 图片动画打开
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      doClipPicAnim(selIndex, true);
    } else if (operate.getResId() == R.drawable.edit_icon_split_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditSplitDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_duplicate) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      doClipDuplicate(selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_location_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditClipPosInfoDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, mFakeApi);
    } else if (operate.getResId() == R.drawable.edit_icon_edit_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      doClipReplace(selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_delete_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      doClipDel(selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_reserve_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      doClipReserve(selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_changevoice_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditMagicSoundDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_muteoff_n) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditVolumeDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_speed_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditSpeedDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_filter_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditFilterDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_effect_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditFxFilterDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_change_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      if (selIndex >= clipAdapter.getItemCount() - 2) {
        ToastUtils.show(getContext(), R.string.mn_edit_tips_no_allow_set_last_trans,
            Toast.LENGTH_LONG);
        return;
      }
      new EditTransDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_adjust_nor) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditAdjustDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.editor_tool_adjust_curve) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditCurveAdjustDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.editor_tool_background_icon) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditClipBGDialog(getContext(), mMenuContainer, mWorkSpace, selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_crop_n) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditClipCropDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, mCropImageView,
          mFakeApi);
    } else if (operate.getResId() == R.drawable.editor_tool_keyframeanimator_icon) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      new EditClipKeyFrameDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, mFakeApi);
    }
  }

  private void doClipDuplicate(int selClipIndex) {
    if (mWorkSpace.getClipAPI().getClipList().size() > 0
        && selClipIndex >= 0
        && selClipIndex < mWorkSpace.getClipAPI().getClipList().size()) {
      ClipOPCopy clipOPCopy = new ClipOPCopy(selClipIndex);
      mWorkSpace.handleOperation(clipOPCopy);
    }
  }

  private void doClipReplace(int selClipIndex) {
    GallerySettings settings = new GallerySettings.Builder()
        .minSelectCount(1)
        .maxSelectCount(1)
        .showMode(GalleryDef.MODE_BOTH)
        .build();
    GalleryClient.getInstance().initSetting(settings);
    //enter gallery
    GalleryClient.getInstance().performLaunchGallery(getActivity());
    GalleryClient.getInstance().initProvider(new IGalleryProvider() {
      @Override
      public boolean checkFileEditAble(String filePath) {
        int res = MediaFileUtils.checkFileEditAble(filePath);
        return res == SDKErrCode.RESULT_OK;
      }

      @Override
      public void onGalleryFileDone(ArrayList<MediaModel> mediaList) {
        super.onGalleryFileDone(mediaList);
        if (mediaList != null && mediaList.size() > 0 && mWorkSpace != null) {
          ClipReplaceItem clipReplaceItem = new ClipReplaceItem();
          clipReplaceItem.clipFilePath = mediaList.get(0).getFilePath();
          ClipOPReplace clipOPReplace = new ClipOPReplace(selClipIndex, clipReplaceItem);
          mWorkSpace.handleOperation(clipOPReplace);
        }
      }
    });
  }

  private void doClipDel(int selClipIndex) {
    if (mWorkSpace.getClipAPI().getClipList().size() > 1) {
      ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(selClipIndex);
      ClipOPDel clipOPDel = new ClipOPDel(clipData.getUniqueId());
      mWorkSpace.handleOperation(clipOPDel);
    } else if (selClipIndex >= mWorkSpace.getClipAPI().getClipList().size()) {
      ToastUtils.show(getContext(), R.string.mn_edit_tips_error_params, Toast.LENGTH_LONG);
    } else {
      ToastUtils.show(getContext(), R.string.mn_edit_tips_no_allow_del_last, Toast.LENGTH_LONG);
    }
  }

  private void doClipPicAnim(int selClipIndex, boolean animOn) {
    mWorkSpace.handleOperation(new ClipOPPicAnim(selClipIndex, animOn));
  }

  private void doClipReserve(int selClipIndex) {
    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(selClipIndex);
    boolean toReverse = !clipData.isReversed();
    mWorkSpace.handleOperation(new ClipOPReverse(selClipIndex, toReverse));
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_edit);
  }

  private void gotoEdit(int selClipIndex) {
    //update settings
    GallerySettings settings = new GallerySettings.Builder()
        .minSelectCount(1)
        .maxSelectCount(-1)
        .showMode(GalleryDef.MODE_BOTH)
        .build();

    GalleryClient.getInstance().initSetting(settings);
    //enter gallery
    GalleryClient.getInstance().performLaunchGallery((Activity) getContext());

    GalleryClient.getInstance().initProvider(new IGalleryProvider() {
      @Override
      public boolean checkFileEditAble(String filePath) {
        int res = MediaFileUtils.checkFileEditAble(filePath);
        return res == SDKErrCode.RESULT_OK;
      }

      @Override
      public void onGalleryFileDone(ArrayList<MediaModel> mediaList) {
        super.onGalleryFileDone(mediaList);
        ArrayList<String> albumChoose = new ArrayList<>();
        if (mediaList != null && mediaList.size() > 0) {
          for (MediaModel item : mediaList) {
            albumChoose.add(item.getFilePath());
          }
        }
        List<ClipAddItem> list = new ArrayList<>();
        for (String path : albumChoose) {
          ClipAddItem item = new ClipAddItem();
          item.clipFilePath = path;
          list.add(item);
        }
        ClipOPAdd clipOPAdd = new ClipOPAdd(selClipIndex, list);
        mWorkSpace.handleOperation(clipOPAdd);
      }
    });
  }
}
