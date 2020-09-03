package com.quvideo.application.editor.effect.mask;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.FakePosUtils;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.MaskLinearDraw;
import com.quvideo.application.editor.fake.draw.MaskMirrorDraw;
import com.quvideo.application.editor.fake.draw.MaskRadialDraw;
import com.quvideo.application.editor.fake.draw.MaskRectDraw;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.effect.EffectMaskInfo;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMaskInfo;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class EffectMaskDialog extends BaseEffectMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private CustomSeekbarPop mCustomSeekbarPop;

  private int currentTime = 0;
  private int startTime = 0;
  private int maxTime = 0;

  public EffectMaskDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectMask;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_mask;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    RecyclerView clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("10000")
        .progress(0)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 10000))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
            focusStartTime(currentTime);
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
            if (baseEffect.mEffectMaskInfo != null) {
              EffectMaskInfo effectMaskInfo = baseEffect.mEffectMaskInfo;
              effectMaskInfo.softness = progress;
              EffectOPMaskInfo effectOPMaskInfo = new EffectOPMaskInfo(groupId, effectIndex, effectMaskInfo);
              mWorkSpace.handleOperation(effectOPMaskInfo);
            }
          }
        }));

    ArrayList<EffectMaskAdapter.MaskItem> maskItems = new ArrayList<>();
    maskItems.add(new EffectMaskAdapter.MaskItem(EffectMaskInfo.MaskType.MASK_NONE, R.drawable.editor_icon_collage_mask_none_n,
        R.string.mn_edit_none));
    maskItems.add(new EffectMaskAdapter.MaskItem(EffectMaskInfo.MaskType.MASK_LINEAR, R.drawable.editor_icon_collage_mask_linear_n,
        R.string.mn_edit_mask_linear));
    maskItems.add(new EffectMaskAdapter.MaskItem(EffectMaskInfo.MaskType.MASK_MIRROR, R.drawable.editor_icon_collage_mask_mirror_n,
        R.string.mn_edit_mask_mirror));
    maskItems.add(new EffectMaskAdapter.MaskItem(EffectMaskInfo.MaskType.MASK_RADIAL, R.drawable.editor_icon_collage_mask_radial_n,
        R.string.mn_edit_mask_radial));
    maskItems.add(new EffectMaskAdapter.MaskItem(EffectMaskInfo.MaskType.MASK_RECTANGLE, R.drawable.editor_icon_collage_mask_rect_n,
        R.string.mn_edit_mask_rectangle));

    EffectMaskAdapter adapter = new EffectMaskAdapter(context, this, maskItems);
    clipRecyclerView.setAdapter(adapter);

    AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    if (baseEffect.mEffectMaskInfo != null) {
      adapter.setSelectType(baseEffect.mEffectMaskInfo.maskType, baseEffect.mEffectMaskInfo.reverse);
      mCustomSeekbarPop.setProgress(baseEffect.mEffectMaskInfo.softness);
    }
    initFakeView();
    adapter.setOnItemClickListener(this::changeMaskType);
  }

  private QEPlayerListener mPlayerListener = new QEPlayerListener() {
    @Override public void onPlayerCallback(PlayerStatus playerStatus, int progress) {
      currentTime = progress;
      if (playerStatus == PlayerStatus.STATUS_PAUSE
          || playerStatus == PlayerStatus.STATUS_PLAYING
          || playerStatus == PlayerStatus.STATUS_STOP
          || playerStatus == PlayerStatus.STATUS_SEEKING) {
        updateFakeFocus(progress);
      }
    }

    @Override public void onPlayerRefresh() {
      if (mWorkSpace != null
          && mWorkSpace.getPlayerAPI() != null
          && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
        currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
        updateFakeFocus(currentTime);
      }
    }

    @Override public void onSizeChanged(Rect resultRect) {
    }
  };

  private void updateFakeFocus(int curTime) {
    if (curTime < startTime || curTime > maxTime) {
      mFakeApi.setTarget(null, null);
    }
  }

  private void focusStartTime(int curTime) {
    if (curTime < startTime || curTime > maxTime) {
      mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime);
    }
  }

  private void initFakeView() {
    AnimEffect animEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
    startTime = animEffect.destRange.getPosition();
    if (animEffect.destRange.getTimeLength() > 0) {
      maxTime = animEffect.destRange.getLimitValue();
    } else {
      maxTime = mWorkSpace.getStoryboardAPI().getDuration();
    }
    currentTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);
    EffectPosInfo effectPosInfo = animEffect.mEffectPosInfo;
    if (animEffect.mEffectMaskInfo != null) {
      changeFakeView(animEffect.mEffectMaskInfo.maskType, effectPosInfo, animEffect.mEffectMaskInfo);
    } else {
      mFakeApi.setTarget(null, null);
      mCustomSeekbarPop.setVisibility(View.INVISIBLE);
    }
    mFakeApi.setFakeViewListener(new IFakeViewListener() {

      @Override public void onEffectMoving() {
        FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
        AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectMaskInfo targetMaskInfo = baseEffect.mEffectMaskInfo;
        FakePosUtils.INSTANCE.updateMaskPos2FakePos(curFakePos, targetMaskInfo);
        EffectOPMaskInfo effectOPMaskInfo = new EffectOPMaskInfo(groupId, effectIndex, targetMaskInfo);
        mWorkSpace.handleOperation(effectOPMaskInfo);
      }

      @Override public void onEffectMoveStart() {
      }

      @Override public void onEffectMoveEnd(boolean moved) {
        FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
        AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectMaskInfo targetMaskInfo = baseEffect.mEffectMaskInfo;
        FakePosUtils.INSTANCE.updateMaskPos2FakePos(curFakePos, targetMaskInfo);
        EffectOPMaskInfo effectOPMaskInfo = new EffectOPMaskInfo(groupId, effectIndex, targetMaskInfo);
        mWorkSpace.handleOperation(effectOPMaskInfo);
      }

      @Override public void checkEffectTouchHit(@NotNull PointF pointF) {
      }
    });
    updateFakeFocus(currentTime);
  }

  private void changeFakeView(EffectMaskInfo.MaskType maskType, EffectPosInfo effectPosInfo, EffectMaskInfo effectMaskInfo) {
    if (maskType == EffectMaskInfo.MaskType.MASK_LINEAR) {
      mFakeApi.setTarget(new MaskLinearDraw(), effectPosInfo, effectMaskInfo);
      mCustomSeekbarPop.setVisibility(View.VISIBLE);
      mCustomSeekbarPop.setProgress(effectMaskInfo.softness);
    } else if (maskType == EffectMaskInfo.MaskType.MASK_MIRROR) {
      mFakeApi.setTarget(new MaskMirrorDraw(), effectPosInfo, effectMaskInfo);
      mCustomSeekbarPop.setVisibility(View.VISIBLE);
      mCustomSeekbarPop.setProgress(effectMaskInfo.softness);
    } else if (maskType == EffectMaskInfo.MaskType.MASK_RADIAL) {
      mFakeApi.setTarget(new MaskRadialDraw(), effectPosInfo, effectMaskInfo);
      mCustomSeekbarPop.setVisibility(View.VISIBLE);
      mCustomSeekbarPop.setProgress(effectMaskInfo.softness);
    } else if (maskType == EffectMaskInfo.MaskType.MASK_RECTANGLE) {
      mFakeApi.setTarget(new MaskRectDraw(), effectPosInfo, effectMaskInfo);
      mCustomSeekbarPop.setVisibility(View.VISIBLE);
      mCustomSeekbarPop.setProgress(effectMaskInfo.softness);
    } else {
      mFakeApi.setTarget(null, null);
      mCustomSeekbarPop.setVisibility(View.INVISIBLE);
    }
    focusStartTime(currentTime);
  }

  @Override protected void releaseAll() {
    if (mWorkSpace != null) {
      mWorkSpace.getPlayerAPI().unregisterListener(mPlayerListener);
    }
  }

  private void changeMaskType(EffectMaskAdapter.MaskItem maskItem) {
    EffectMaskInfo effectMaskInfo = null;
    if (maskItem.maskType != EffectMaskInfo.MaskType.MASK_NONE) {
      AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
      if (baseEffect.mEffectMaskInfo != null) {
        effectMaskInfo = baseEffect.mEffectMaskInfo;
      } else {
        effectMaskInfo = new EffectMaskInfo();
        effectMaskInfo.centerX = baseEffect.mEffectPosInfo.center.x;
        effectMaskInfo.centerY = baseEffect.mEffectPosInfo.center.y;
        effectMaskInfo.radiusX = baseEffect.mEffectPosInfo.size.x / 2;
        effectMaskInfo.radiusY = baseEffect.mEffectPosInfo.size.y / 2;
        effectMaskInfo.rotation = baseEffect.mEffectPosInfo.degree.z;
      }
      effectMaskInfo.reverse = maskItem.reverse;
      effectMaskInfo.maskType = maskItem.maskType;
      changeFakeView(maskItem.maskType, baseEffect.mEffectPosInfo, effectMaskInfo);
    } else {
      mFakeApi.setTarget(null, null);
      mCustomSeekbarPop.setVisibility(View.INVISIBLE);
    }
    EffectOPMaskInfo effectOPMaskInfo = new EffectOPMaskInfo(groupId, effectIndex, effectMaskInfo);
    mWorkSpace.handleOperation(effectOPMaskInfo);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_mask);
  }
}
