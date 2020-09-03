package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.keyframe.KeyFrameTimeline;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.FakePosUtils;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.ClipPosDraw;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.ClipKeyFrameInfo;
import com.quvideo.mobile.engine.model.clip.ClipPosInfo;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPKeyFrame;
import java.util.ArrayList;

public class EditClipKeyFrameDialog extends BaseMenuView {

  private View btnDel;

  private KeyFrameTimeline mKeyFrameTimeline;

  private VeMSize clipSourceSize;

  private int clipIndex;

  private int curPlayerTime = 0;
  private int startTime = 0;
  private int maxTime = 0;

  public EditClipKeyFrameDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int clipIndex, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    clipSourceSize = mWorkSpace.getClipAPI().getClipByIndex(clipIndex).getSourceSize();
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipKeyFrame;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_clip_keyframe;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    btnDel = view.findViewById(R.id.btn_delete);
    mKeyFrameTimeline = view.findViewById(R.id.v_keyframe_timeline);
    initFakeView();

    curPlayerTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
    ClipData clipData = mWorkSpace.getClipAPI().getClipByIndex(clipIndex);
    startTime = clipData.destRange.getPosition();
    maxTime = clipData.destRange.getLimitValue();
    mKeyFrameTimeline.setMaxOffsetTime(maxTime - startTime, new KeyFrameTimeline.OnKeyFrameListener() {
      @Override public void onKeyFrameClick(int focusTs) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime + focusTs);
        curPlayerTime = startTime + focusTs;
        mKeyFrameTimeline.setCurOffsetTime(focusTs);
        updateBtnEnable(focusTs);
      }

      @Override public void onOtherClick(int offsetTime) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime + offsetTime);
        curPlayerTime = startTime + offsetTime;
        mKeyFrameTimeline.setCurOffsetTime(offsetTime);
        updateBtnEnable(-1);
      }
    });

    updateKeyFrameTimeline();

    mWorkSpace.addObserver(mBaseObserver);
    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);

    btnDel.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        // 删除关键帧
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        btnDel.setEnabled(false);
        btnDel.setAlpha(0.1f);
        if (mKeyFrameTimeline.getFocusTs() >= 0) {
          ClipData clipItem = mWorkSpace.getClipAPI().getClipByIndex(clipIndex);
          ArrayList<ClipKeyFrameInfo> baseKeyFrames = clipItem.getClipKeyFrameList();
          int focusTs = mKeyFrameTimeline.getFocusTs();
          if (baseKeyFrames != null) {
            for (ClipKeyFrameInfo baseKeyFrame : baseKeyFrames) {
              if (baseKeyFrame.relativeTime == focusTs) {
                baseKeyFrames.remove(baseKeyFrame);
                break;
              }
            }
            ClipOPKeyFrame clipOPKeyFrame = new ClipOPKeyFrame(clipIndex, baseKeyFrames);
            mWorkSpace.handleOperation(clipOPKeyFrame);
          }
        }
      }
    });
  }

  private void initFakeView() {
    mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
    ClipPosInfo clipPosInfo = mWorkSpace.getClipAPI().getClipByIndex(clipIndex).getClipPosInfo();
    mFakeApi.setClipTarget(new ClipPosDraw(), clipPosInfo, mWorkSpace.getStoryboardAPI().getStreamSize());
    mFakeApi.setFakeViewListener(new IFakeViewListener() {

      @Override public void onEffectMoving() {
        FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
        ClipPosInfo targetClipPosInfo = new ClipPosInfo();
        FakePosUtils.INSTANCE.updateClipPos2FakePos(curFakePos, targetClipPosInfo,
            mWorkSpace.getStoryboardAPI().getStreamSize(), clipSourceSize);

        ClipData clipData = mWorkSpace.getClipAPI().getClipByIndex(clipIndex);
        int focusTs = mKeyFrameTimeline.getFocusTs();
        ArrayList<ClipKeyFrameInfo> clipKeyFrameInfos = clipData.getClipKeyFrameList();
        boolean update = false;
        if (focusTs >= 0) {
          if (clipKeyFrameInfos != null) {
            for (ClipKeyFrameInfo item : clipKeyFrameInfos) {
              if (item.relativeTime == focusTs) {
                update = true;
                item.centerX = (int) targetClipPosInfo.centerPosX;
                item.centerY = (int) targetClipPosInfo.centerPosY;
                item.heightRatio = targetClipPosInfo.heightScale;
                item.widthRatio = targetClipPosInfo.widthScale;
                item.rotation = targetClipPosInfo.degree;
                break;
              }
            }
          }
        }
        if (!update) {
          ClipKeyFrameInfo addKeyFrame = new ClipKeyFrameInfo((int) targetClipPosInfo.centerPosX, (int) targetClipPosInfo.centerPosY,
              targetClipPosInfo.heightScale, targetClipPosInfo.widthScale, targetClipPosInfo.degree,
              curPlayerTime - startTime);
          clipKeyFrameInfos.add(addKeyFrame);
        }
        ClipOPKeyFrame clipOPKeyFrame = new ClipOPKeyFrame(clipIndex, clipKeyFrameInfos);
        mWorkSpace.handleOperation(clipOPKeyFrame);
      }

      @Override public void onEffectMoveStart() {
      }

      @Override public void onEffectMoveEnd(boolean moved) {
      }

      @Override public void checkEffectTouchHit(PointF pointF) {
      }
    });
  }

  private void updateKeyFrameTimeline() {
    ClipData clipData = mWorkSpace.getClipAPI().getClipByIndex(clipIndex);
    mKeyFrameTimeline.setKeyFrameData(ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.color_3493f2),
        clipData.getClipKeyFrameList());
    mKeyFrameTimeline.setCurOffsetTime(curPlayerTime - startTime);
    updateBtnEnable(mKeyFrameTimeline.getFocusTs());
  }

  private void updateBtnEnable(int focusTs) {
    btnDel.setEnabled(focusTs >= 0);
    btnDel.setAlpha(focusTs >= 0 ? 1f : 0.1f);
  }

  private QEPlayerListener mPlayerListener = new QEPlayerListener() {
    @Override public void onPlayerCallback(PlayerStatus playerStatus, int progress) {
      curPlayerTime = progress;
      if (playerStatus == PlayerStatus.STATUS_PAUSE
          || playerStatus == PlayerStatus.STATUS_PLAYING
          || playerStatus == PlayerStatus.STATUS_STOP
          || playerStatus == PlayerStatus.STATUS_SEEKING) {
        mKeyFrameTimeline.setCurOffsetTime(progress - startTime);
        updateBtnEnable(mKeyFrameTimeline.getFocusTs());
      }
    }

    @Override public void onPlayerRefresh() {
      if (mWorkSpace != null
          && mWorkSpace.getPlayerAPI() != null
          && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
        curPlayerTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
        mKeyFrameTimeline.setCurOffsetTime(curPlayerTime - startTime);
        updateBtnEnable(mKeyFrameTimeline.getFocusTs());
      }
    }

    @Override public void onSizeChanged(Rect resultRect) {
    }
  };

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof ClipOPKeyFrame) {
        // 刷新数据
        updateKeyFrameTimeline();
      }
    }
  };

  @Override protected void releaseAll() {
    mWorkSpace.removeObserver(mBaseObserver);
    mWorkSpace.getPlayerAPI().unregisterListener(mPlayerListener);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_keyframe_animator_title);
  }
}
