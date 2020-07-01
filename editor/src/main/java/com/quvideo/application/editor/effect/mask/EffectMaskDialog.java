package com.quvideo.application.editor.effect.mask;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;
import android.widget.SeekBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.control.EditSeekBarController;
import com.quvideo.application.editor.effect.fake.FakePosInfo;
import com.quvideo.application.editor.effect.fake.FakePosUtils;
import com.quvideo.application.editor.effect.fake.IFakeViewApi;
import com.quvideo.application.editor.effect.fake.IFakeViewListener;
import com.quvideo.application.editor.effect.fake.draw.MaskLinearDraw;
import com.quvideo.application.editor.effect.fake.draw.MaskMirrorDraw;
import com.quvideo.application.editor.effect.fake.draw.MaskRadialDraw;
import com.quvideo.application.editor.effect.fake.draw.MaskRectDraw;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.effect.EffectMaskInfo;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPMaskInfo;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class EffectMaskDialog extends BaseEffectMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private EditSeekBarController seekBarController;
  private View seekView;

  public EffectMaskDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    seekBarController = new EditSeekBarController();
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectMask;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_mask;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    seekView = view.findViewById(R.id.seekbar);
    RecyclerView clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

    seekBarController.bindView(view.findViewById(R.id.seekbar));
    seekBarController.setSeekBarTextColor(Color.parseColor("#80FFFFFF"));
    seekBarController.setSeekBarStartText("0");
    seekBarController.setSeekBarEndText("10000");
    seekBarController.setMaxProgress(10000);

    seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarController.setProgressText(progress + "");
        AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        if (baseEffect.mEffectMaskInfo != null) {
          EffectMaskInfo effectMaskInfo = baseEffect.mEffectMaskInfo;
          effectMaskInfo.softness = progress;
          EffectOPMaskInfo effectOPMaskInfo = new EffectOPMaskInfo(groupId, effectIndex, effectMaskInfo);
          mWorkSpace.handleOperation(effectOPMaskInfo);
        }
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });

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
      seekBarController.setProgressText("" + baseEffect.mEffectMaskInfo.softness);
      seekBarController.setSeekBarProgress(baseEffect.mEffectMaskInfo.softness);
    }
    initFakeView();
    adapter.setOnItemClickListener(this::changeMaskType);
  }

  private void initFakeView() {
    AnimEffect animEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
    EffectPosInfo effectPosInfo = animEffect.mEffectPosInfo;
    if (animEffect.mEffectMaskInfo != null) {
      changeFakeView(animEffect.mEffectMaskInfo.maskType, effectPosInfo, animEffect.mEffectMaskInfo);
    } else {
      mFakeApi.setTarget(null, null);
      seekView.setVisibility(View.INVISIBLE);
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
  }

  private void changeFakeView(EffectMaskInfo.MaskType maskType, EffectPosInfo effectPosInfo, EffectMaskInfo effectMaskInfo) {
    if (maskType == EffectMaskInfo.MaskType.MASK_LINEAR) {
      mFakeApi.setTarget(new MaskLinearDraw(), effectPosInfo, effectMaskInfo);
      seekView.setVisibility(View.VISIBLE);
      seekBarController.setProgressText("" + effectMaskInfo.softness);
      seekBarController.setSeekBarProgress(effectMaskInfo.softness);
    } else if (maskType == EffectMaskInfo.MaskType.MASK_MIRROR) {
      mFakeApi.setTarget(new MaskMirrorDraw(), effectPosInfo, effectMaskInfo);
      seekView.setVisibility(View.VISIBLE);
      seekBarController.setProgressText("" + effectMaskInfo.softness);
      seekBarController.setSeekBarProgress(effectMaskInfo.softness);
    } else if (maskType == EffectMaskInfo.MaskType.MASK_RADIAL) {
      mFakeApi.setTarget(new MaskRadialDraw(), effectPosInfo, effectMaskInfo);
      seekView.setVisibility(View.VISIBLE);
      seekBarController.setProgressText("" + effectMaskInfo.softness);
      seekBarController.setSeekBarProgress(effectMaskInfo.softness);
    } else if (maskType == EffectMaskInfo.MaskType.MASK_RECTANGLE) {
      mFakeApi.setTarget(new MaskRectDraw(), effectPosInfo, effectMaskInfo);
      seekView.setVisibility(View.VISIBLE);
      seekBarController.setProgressText("" + effectMaskInfo.softness);
      seekBarController.setSeekBarProgress(effectMaskInfo.softness);
    } else {
      mFakeApi.setTarget(null, null);
      seekView.setVisibility(View.INVISIBLE);
    }
  }

  @Override protected void releaseAll() {
  }

  private void changeMaskType(EffectMaskAdapter.MaskItem maskItem) {
    EffectMaskInfo effectMaskInfo = null;
    if (maskItem.maskType != EffectMaskInfo.MaskType.MASK_NONE) {
      AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
      if (baseEffect.mEffectMaskInfo != null) {
        effectMaskInfo = baseEffect.mEffectMaskInfo;
      } else {
        effectMaskInfo = new EffectMaskInfo();
        effectMaskInfo.centerX = baseEffect.mEffectPosInfo.centerPosX;
        effectMaskInfo.centerY = baseEffect.mEffectPosInfo.centerPosY;
        effectMaskInfo.radiusX = baseEffect.mEffectPosInfo.width / 2;
        effectMaskInfo.radiusY = baseEffect.mEffectPosInfo.height / 2;
        effectMaskInfo.rotation = baseEffect.mEffectPosInfo.degree;
      }
      effectMaskInfo.reverse = maskItem.reverse;
      effectMaskInfo.maskType = maskItem.maskType;
      changeFakeView(maskItem.maskType, baseEffect.mEffectPosInfo, effectMaskInfo);
    } else {
      mFakeApi.setTarget(null, null);
      seekView.setVisibility(View.INVISIBLE);
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
