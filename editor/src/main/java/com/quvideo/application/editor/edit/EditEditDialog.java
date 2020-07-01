package com.quvideo.application.editor.edit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.EditOperate;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.edit.sub.EditAdjustDialog;
import com.quvideo.application.editor.edit.sub.EditFilterDialog;
import com.quvideo.application.editor.edit.sub.EditFxFilterDialog;
import com.quvideo.application.editor.edit.sub.EditMagicSoundDialog;
import com.quvideo.application.editor.edit.sub.EditSpeedDialog;
import com.quvideo.application.editor.edit.sub.EditSplitDialog;
import com.quvideo.application.editor.edit.sub.EditTransDialog;
import com.quvideo.application.editor.edit.sub.EditTrimDialog;
import com.quvideo.application.editor.edit.sub.EditVolumeDialog;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.error.SDKErrCode;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.ClipAddItem;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPAdd;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPCopy;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPDel;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPMirror;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPReverse;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPRotate;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPSplit;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.ArrayList;
import java.util.List;

public class EditEditDialog extends BaseMenuView {

  private EditClipAdapter clipAdapter;

  public EditEditDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      ItemOnClickListener l) {
    super(context, workSpace);
    showMenu(container, l);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipEdit;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_edit;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    // clip
    RecyclerView clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    clipAdapter = new EditClipAdapter(mWorkSpace);
    clipRecyclerView.setAdapter(clipAdapter);
    initData();
    // operate
    List<EditOperate> list = new ArrayList<EditOperate>() {{
      add(new EditOperate(R.drawable.edit_icon_trim_n, context.getString(R.string.mn_edit_title_trim)));
      add(new EditOperate(R.drawable.edit_icon_split_nor, context.getString(R.string.mn_edit_title_split)));
      add(new EditOperate(R.drawable.edit_icon_duplicate, context.getString(R.string.mn_edit_duplicate_title)));
      add(new EditOperate(R.drawable.edit_icon_delete_nor, context.getString(R.string.mn_edit_title_delete)));
      add(new EditOperate(R.drawable.edit_icon_muteoff_n, context.getString(R.string.mn_edit_title_volume)));
      add(new EditOperate(R.drawable.edit_icon_filter_nor, context.getString(R.string.mn_edit_title_filter)));
      add(new EditOperate(R.drawable.edit_icon_effect_nor, context.getString(R.string.mn_edit_title_fx_filter)));
      add(new EditOperate(R.drawable.edit_icon_change_nor, context.getString(R.string.mn_edit_title_transitions)));
      add(new EditOperate(R.drawable.edit_icon_changevoice_nor, context.getString(R.string.mn_edit_title_change_voice)));
      add(new EditOperate(R.drawable.edit_icon_speed_nor, context.getString(R.string.mn_edit_title_speed)));
      add(new EditOperate(R.drawable.edit_icon_adjust_nor, context.getString(R.string.mn_edit_title_adjust)));
      add(new EditOperate(R.drawable.edit_icon_mirror_nor, context.getString(R.string.mn_edit_title_mirror)));
      add(new EditOperate(R.drawable.edit_icon_reserve_nor, context.getString(R.string.mn_edit_title_reserve)));
      add(new EditOperate(R.drawable.edit_icon_rotate_nor, context.getString(R.string.mn_edit_title_rotate)));
    }};
    RecyclerView editRecyclerView = view.findViewById(R.id.operate_recyclerview);
    editRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    editRecyclerView.setAdapter(new EditOperateAdapter(list, this));
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
    for (int index = 0; index < count; index++) {
      int endTime = clipList.get(index).getDestRange().getLimitValue();
      if (endTime > curTime) {
        return index;
      }
    }
    return count - 1;
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof ClipOPAdd
          || operate instanceof ClipOPDel
          || operate instanceof ClipOPCopy
          || operate instanceof ClipOPSplit) {
        // 添加 / 删除 clip完成监听
        AndroidSchedulers.mainThread().scheduleDirect(() -> clipAdapter.updateClipList());
      }
    }
  };

  @Override protected void releaseAll() {
    mWorkSpace.removeObserver(mBaseObserver);
    mWorkSpace.getPlayerAPI().unregisterListener(mPlayerListener);
  }

  private void initData() {
    clipAdapter.updateClipList();
    clipAdapter.setOnAddClipListener(() -> {
      int selClip = clipAdapter.getSelClipIndex();
      gotoEdit(selClip + 1);
    });

    mWorkSpace.addObserver(mBaseObserver);
    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);
  }

  @Override public void onClick(View view, EditOperate operate) {
    int selIndex = clipAdapter.getSelClipIndex();
    if (operate.getResId() == R.drawable.edit_icon_trim_n) {
      new EditTrimDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, this);
    } else if (operate.getResId() == R.drawable.edit_icon_split_nor) {
      new EditSplitDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, this);
    } else if (operate.getResId() == R.drawable.edit_icon_duplicate) {
      doClipDuplicate(selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_delete_nor) {
      doClipDel(selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_mirror_nor) {
      doClipMirror(selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_reserve_nor) {
      doClipReserve(selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_rotate_nor) {
      doClipRotate(selIndex);
    } else if (operate.getResId() == R.drawable.edit_icon_changevoice_nor) {
      new EditMagicSoundDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, this);
    } else if (operate.getResId() == R.drawable.edit_icon_muteoff_n) {
      new EditVolumeDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, this);
    } else if (operate.getResId() == R.drawable.edit_icon_speed_nor) {
      new EditSpeedDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, this);
    } else if (operate.getResId() == R.drawable.edit_icon_filter_nor) {
      new EditFilterDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, this);
    } else if (operate.getResId() == R.drawable.edit_icon_effect_nor) {
      new EditFxFilterDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, this);
    } else if (operate.getResId() == R.drawable.edit_icon_change_nor) {
      if (selIndex >= clipAdapter.getItemCount() - 2) {
        ToastUtils.show(getContext(), R.string.mn_edit_tips_no_allow_set_last_trans, Toast.LENGTH_LONG);
        return;
      }
      new EditTransDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, this);
    } else if (operate.getResId() == R.drawable.edit_icon_adjust_nor) {
      new EditAdjustDialog(getContext(), mMenuContainer, mWorkSpace, selIndex, this);
    }
  }

  private void doClipDuplicate(int selClipIndex) {
    if (mWorkSpace.getClipAPI().getClipList().size() > 1
        && selClipIndex >= 0
        && selClipIndex < mWorkSpace.getClipAPI().getClipList().size()) {
      ClipOPCopy clipOPCopy = new ClipOPCopy(selClipIndex);
      mWorkSpace.handleOperation(clipOPCopy);
    }
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

  private void doClipMirror(int selClipIndex) {
    int mirrorRandom = (int) (Math.random() * 10f) % 4;
    ClipData.Mirror mirror = ClipData.Mirror.CLIP_FLIP_NONE;
    if (mirrorRandom == 1) {
      mirror = ClipData.Mirror.CLIP_FLIP_X;
    } else if (mirrorRandom == 2) {
      mirror = ClipData.Mirror.CLIP_FLIP_Y;
    } else if (mirrorRandom == 3) {
      mirror = ClipData.Mirror.CLIP_FLIP_XY;
    }
    ClipOPMirror clipOPMirror = new ClipOPMirror(selClipIndex, mirror);
    mWorkSpace.handleOperation(clipOPMirror);
  }

  private void doClipRotate(int selClipIndex) {
    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(selClipIndex);
    int rotate = clipData.getRotateAngle();
    ClipOPRotate clipOPRotate = new ClipOPRotate(selClipIndex, rotate + 90);
    mWorkSpace.handleOperation(clipOPRotate);
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
