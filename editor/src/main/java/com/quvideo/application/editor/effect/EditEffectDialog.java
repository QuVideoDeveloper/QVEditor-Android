package com.quvideo.application.editor.effect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.IEffectEditClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.chroma.EffectChromaDialog;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.FakePosUtils;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.PosDraw;
import com.quvideo.application.editor.effect.mask.EffectMaskDialog;
import com.quvideo.application.editor.sound.EditDubDialog;
import com.quvideo.application.editor.sound.EffectAddMusicDialog;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.utils.RandomUtil;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.error.SDKErrCode;
import com.quvideo.mobile.engine.model.AudioEffect;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.FloatEffect;
import com.quvideo.mobile.engine.model.SubtitleEffect;
import com.quvideo.mobile.engine.model.effect.EffectAddItem;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAdd;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAudioReplace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPCopy;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPDel;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPLock;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPPosInfo;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPStaticPic;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class EditEffectDialog extends BaseEffectMenuView {

  private static final String[] AUDIO_RECORD_PERMISSIONS = {
      Manifest.permission.RECORD_AUDIO,
  };

  private int groupId;

  private RecyclerView mRecyclerView;
  private EditEffectAdapter mEffectAdapter;

  private EffectOperateAdapter mEffectOperateAdapter;

  private int currentTime = 0;

  public EditEffectDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.groupId = groupId;
    workSpace.getPlayerAPI().getPlayerControl().pause();
    currentTime = workSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
    mWorkSpace.addObserver(mBaseObserver);
    showMenu(container, mItemOnClickListener, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectEdit;
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof EffectOPAdd
          || operate instanceof EffectOPDel
          || operate instanceof EffectOPCopy
          || operate instanceof EffectOPAudioReplace) {
        // 刷新数据
        if (mWorkSpace.getPlayerAPI() != null
            && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
          mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        }
        List<BaseEffect> dataList = mWorkSpace.getEffectAPI().getEffectList(groupId);
        mEffectAdapter.updateList(dataList);
        int selectIndex = mEffectAdapter.getSelectIndex();
        if (selectIndex >= 0) {
          BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, selectIndex);
          if (baseEffect != null) {
            if (mWorkSpace.getPlayerAPI() != null
                && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
              mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition());
            }

            updateFakeView(selectIndex, baseEffect);
          }
        } else {
          if (mFakeApi != null) {
            mFakeApi.setTarget(null, null);
          }
        }
        if (groupId == QEGroupConst.GROUP_ID_BGMUSIC && mEffectOperateAdapter != null) {
          mEffectOperateAdapter.updateList(getOperateList());
        }
      }
    }
  };

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    View rootView = view.findViewById(R.id.root_layout);
    rootView.setOnClickListener(v -> {
      // 只是为了拦击点击事件
    });
    mEffectAdapter = new EditEffectAdapter(getActivity(), groupId, mOnEffectlickListener);
    // effect
    mRecyclerView = view.findViewById(R.id.clip_recyclerview);
    mRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mRecyclerView.setAdapter(mEffectAdapter);
    List<BaseEffect> dataList = mWorkSpace.getEffectAPI().getEffectList(groupId);
    mEffectAdapter.updateList(dataList);
    int selectIndex = mEffectAdapter.getSelectIndex();
    if (selectIndex >= 0) {
      BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, selectIndex);
      if (baseEffect != null) {
        mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition());

        updateFakeView(selectIndex, baseEffect);
      }
    }
    // 操作view
    RecyclerView editRecyclerView = view.findViewById(R.id.operate_recyclerview);
    editRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mEffectOperateAdapter = new EffectOperateAdapter(getOperateList(), mItemOnClickListener);
    editRecyclerView.setAdapter(mEffectOperateAdapter);
  }

  @Override protected void releaseAll() {
    if (mWorkSpace != null) {
      mWorkSpace.removeObserver(mBaseObserver);
    }
  }

  private IEffectEditClickListener mItemOnClickListener = new IEffectEditClickListener() {
    @Override public void onClick(View view, EffectBarItem operate) {
      int index = mEffectAdapter.getSelectIndex();
      if (index < 0) {
        ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
            R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        return;
      }
      BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, index);
      if (baseEffect == null) {
        ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
            R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        return;
      }
      switch (operate.getAction()) {
        case EffectBarItem.ACTION_EDIT:
          if (groupId == QEGroupConst.GROUP_ID_BGMUSIC) {
            new EffectAddMusicDialog(getContext(), mMenuContainer, mWorkSpace, groupId);
          } else {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                getContext().getString(R.string.mn_edit_tips_no_support),
                Toast.LENGTH_LONG);
          }
          break;
        case EffectBarItem.ACTION_TRIM:
          new EditEffectTrimDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_DUPLICATE:
          EffectOPCopy effectOPCopy = new EffectOPCopy(groupId, index);
          mWorkSpace.handleOperation(effectOPCopy);
          break;
        case EffectBarItem.ACTION_INPUT:
          if (baseEffect instanceof SubtitleEffect) {
            new EditEffectInputDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          } else {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                getContext().getString(R.string.mn_edit_tips_no_support),
                Toast.LENGTH_LONG);
          }
          break;
        case EffectBarItem.ACTION_VOLUME:
          if (baseEffect.isHadAudio) {
            new EditEffectVolumeDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index
            );
          } else {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
          }
          break;
        case EffectBarItem.ACTION_ALPHA:
          new EditEffectAlphaDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index);
          break;
        case EffectBarItem.ACTION_MAGIC:
          if (baseEffect instanceof AudioEffect) {
            new EditEffectToneDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index
            );
          } else {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                getContext().getString(R.string.mn_edit_tips_no_support),
                Toast.LENGTH_LONG);
          }
          break;
        case EffectBarItem.ACTION_MIRROR:
          if (baseEffect instanceof FloatEffect) {
            EffectPosInfo effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
            if ((int) (Math.random() * 10f) % 2 == 0) {
              effectPosInfo.isHorFlip = !effectPosInfo.isHorFlip;
            } else {
              effectPosInfo.isVerFlip = !effectPosInfo.isVerFlip;
            }
            EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, index, effectPosInfo);
            mWorkSpace.handleOperation(effectOPPosInfo);
          } else {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                getContext().getString(R.string.mn_edit_tips_no_support),
                Toast.LENGTH_LONG);
          }
          break;
        case EffectBarItem.ACTION_MASK:
          new EffectMaskDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index, mFakeApi);
          break;
        case EffectBarItem.ACTION_CHROMA:
          new EffectChromaDialog(getContext(), mMenuContainer, mWorkSpace, groupId, index, mFakeApi);
          break;
        case EffectBarItem.ACTION_DEL:
          EffectOPDel effectOPDel = new EffectOPDel(groupId, index);
          mWorkSpace.handleOperation(effectOPDel);
          break;
        default:
          ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
              getContext().getString(R.string.mn_edit_tips_no_define),
              Toast.LENGTH_LONG);
          break;
      }
    }
  };

  private EditEffectAdapter.OnEffectlickListener mOnEffectlickListener =
      new EditEffectAdapter.OnEffectlickListener() {
        @Override public void onClick(int index, BaseEffect item) {
          if (item == null || index < 0) {
            // TODO 添加
            if (QEGroupConst.GROUP_ID_COLLAGES == groupId) {
              currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
              go2choosePhoto();
            } else if (QEGroupConst.GROUP_ID_BGMUSIC == groupId
                || QEGroupConst.GROUP_ID_DUBBING == groupId) {
              new EffectAddMusicDialog(getContext(), mMenuContainer, mWorkSpace, groupId);
            } else if (QEGroupConst.GROUP_ID_RECORD == groupId) {
              if (hasPermissionsGranted(getActivity())) {
                new EditDubDialog(getContext(), mMenuContainer, mWorkSpace);
              } else {
                ActivityCompat.requestPermissions(getActivity(), AUDIO_RECORD_PERMISSIONS, 1);
              }
            } else {
              new EffectAddDialog(getContext(), mMenuContainer, mWorkSpace, groupId);
            }
          } else {
            BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, index);
            if (baseEffect != null) {
              mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition());
              currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
              updateFakeView(index, baseEffect);
            }
          }
        }
      };

  private void updateFakeView(final int index, BaseEffect baseEffect) {
    if (mFakeApi == null) {
      return;
    }
    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_MOSAIC
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_WATERMARK
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
      EffectPosInfo effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
      mFakeApi.setTarget(new PosDraw(), effectPosInfo);
      mFakeApi.setFakeViewListener(new IFakeViewListener() {

        @Override public void onEffectMoving() {
          FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
          BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, index);
          EffectPosInfo targetPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
          FakePosUtils.INSTANCE.updateEffectPosByFakePos(curFakePos, targetPosInfo);
          EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, index, targetPosInfo);
          effectOPPosInfo.setFastRefresh(true);
          mWorkSpace.handleOperation(effectOPPosInfo);
        }

        @Override public void onEffectMoveStart() {
          EffectOPLock effectOPLock = new EffectOPLock(groupId, index, true);
          mWorkSpace.handleOperation(effectOPLock);
          EffectOPStaticPic
              effectOPStaticPic = new EffectOPStaticPic(groupId, index, true);
          mWorkSpace.handleOperation(effectOPStaticPic);
        }

        @Override public void onEffectMoveEnd(boolean moved) {
          EffectOPLock effectOPLock = new EffectOPLock(groupId, index, false);
          mWorkSpace.handleOperation(effectOPLock);
          EffectOPStaticPic
              effectOPStaticPic = new EffectOPStaticPic(groupId, index, false);
          mWorkSpace.handleOperation(effectOPStaticPic);
          FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
          BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, index);
          EffectPosInfo targetPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
          FakePosUtils.INSTANCE.updateEffectPosByFakePos(curFakePos, targetPosInfo);
          EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, index, targetPosInfo);
          effectOPPosInfo.setFastRefresh(false);
          mWorkSpace.handleOperation(effectOPPosInfo);
        }

        @Override public void checkEffectTouchHit(@NotNull PointF pointF) {
          List<BaseEffect> list = mWorkSpace.getEffectAPI().getEffectList(groupId);
          if (list == null || list.isEmpty()) {
            return;
          }

          for (int i = 0; i < list.size(); i++) {
            BaseEffect effect = list.get(i);
            EffectPosInfo effectPosInfo = ((FloatEffect) effect).mEffectPosInfo;
            RectF targetRect = effectPosInfo.getRectArea();
            if (mEffectAdapter.getSelectIndex() != i && targetRect != null
                && targetRect.contains(pointF.x, pointF.y)) {
              updateFakeView(i, effect);
              mEffectAdapter.setSelectIndex(i);
              return;
            }
          }
        }
      });
    } else {
      mFakeApi.setTarget(null, null);
    }
  }

  private boolean hasPermissionsGranted(Activity activity) {
    for (String permission : AUDIO_RECORD_PERMISSIONS) {
      if (ActivityCompat.checkSelfPermission(activity, permission)
          != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private void go2choosePhoto() {
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
          EffectAddItem effectAddItem = new EffectAddItem();
          effectAddItem.mEffectPath = mediaList.get(0).getFilePath();
          effectAddItem.destRange = new VeRange(currentTime, 0);
          VeMSize streamSize = mWorkSpace.getStoryboardAPI().getStreamSize();
          EffectPosInfo effectPosInfo = new EffectPosInfo();
          effectPosInfo.centerPosX = streamSize.width * RandomUtil.randInt(1000, 9000) / 10000f;
          effectPosInfo.centerPosY = streamSize.height * RandomUtil.randInt(1000, 9000) / 10000f;
          effectAddItem.mEffectPosInfo = effectPosInfo;
          EffectOPAdd effectOPAdd = new EffectOPAdd(groupId, 0, effectAddItem);
          mWorkSpace.handleOperation(effectOPAdd);
        }
      }
    });
  }

  @Override
  protected String getBottomTitle() {
    if (groupId == QEGroupConst.GROUP_ID_MOSAIC) {
      return getContext().getString(R.string.mn_edit_title_mosaic);
    } else if (groupId == QEGroupConst.GROUP_ID_STICKER) {
      return getContext().getString(R.string.mn_edit_title_sticker);
    } else if (groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
      return getContext().getString(R.string.mn_edit_title_subtitle);
    } else if (groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      return getContext().getString(R.string.mn_edit_title_collages);
    } else if (groupId == QEGroupConst.GROUP_ID_STICKER_FX) {
      return getContext().getString(R.string.mn_edit_title_fx);
    } else if (groupId == QEGroupConst.GROUP_ID_BGMUSIC) {
      return getContext().getString(R.string.mn_edit_title_bgm);
    } else if (groupId == QEGroupConst.GROUP_ID_DUBBING) {
      return getContext().getString(R.string.mn_edit_title_dubbing);
    }
    return getContext().getString(R.string.mn_edit_title_edit);
  }

  private List<EffectBarItem> getOperateList() {
    List<EffectBarItem> list = new ArrayList<>();
    if (groupId == QEGroupConst.GROUP_ID_BGMUSIC) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_EDIT, R.drawable.edit_icon_edit_nor,
              getContext().getString(R.string.mn_edit_bgm_edit)));
    }
    if (groupId != QEGroupConst.GROUP_ID_BGMUSIC) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_TRIM, R.drawable.edit_icon_trim_n,
              getContext().getString(R.string.mn_edit_title_trim)));
    }
    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_DUPLICATE, R.drawable.edit_icon_duplicate,
          getContext().getString(R.string.mn_edit_duplicate_title)));
    }
    if (groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_INPUT, R.drawable.edit_icon_key_nor,
          getContext().getString(R.string.mn_edit_subtitle_input)));
    }
    if (groupId == QEGroupConst.GROUP_ID_BGMUSIC
        || groupId == QEGroupConst.GROUP_ID_DUBBING
        || groupId == QEGroupConst.GROUP_ID_RECORD
        || groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_STICKER_FX
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_VOLUME, R.drawable.edit_icon_muteoff_n,
          getContext().getString(R.string.mn_edit_title_volume)));
    }
    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_WATERMARK
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_ALPHA, R.drawable.edit_icon_alpha_nor,
          getContext().getString(R.string.mn_edit_alpha_change)));
    }
    if (groupId == QEGroupConst.GROUP_ID_BGMUSIC
        || groupId == QEGroupConst.GROUP_ID_DUBBING
        || groupId == QEGroupConst.GROUP_ID_RECORD) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_MAGIC, R.drawable.edit_icon_changevoice_nor,
          getContext().getString(R.string.mn_edit_title_change_voice)));
    }
    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_MIRROR, R.drawable.edit_icon_mirror_nor,
          getContext().getString(R.string.mn_edit_title_mirror)));
    }
    if (groupId == QEGroupConst.GROUP_ID_STICKER
        || groupId == QEGroupConst.GROUP_ID_SUBTITLE
        || groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_MASK, R.drawable.editor_icon_collage_tool_mask,
          getContext().getString(R.string.mn_edit_title_mask)));
    }
    if (groupId == QEGroupConst.GROUP_ID_COLLAGES) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_CHROMA, R.drawable.editor_icon_collage_tool_chroma,
          getContext().getString(R.string.mn_edit_title_chroma)));
    }
    list.add(new EffectBarItem(EffectBarItem.ACTION_DEL, R.drawable.edit_icon_delete_nor,
        getContext().getString(R.string.mn_edit_title_delete)));
    return list;
  }
}
