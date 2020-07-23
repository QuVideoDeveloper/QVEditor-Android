package com.quvideo.application.editor.effect;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.FakePosUtils;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.PosDraw;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.FloatEffect;
import com.quvideo.mobile.engine.model.SubtitleEffect;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPLock;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPPosInfo;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPStaticPic;
import org.jetbrains.annotations.NotNull;

public class EffectRotateAxleDialog extends BaseEffectMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private Button btnZAxle;
  private Button btnXAxle;
  private Button btnYAxle;
  private Button btnAnchor;

  // 0-Z轴 1-X轴 2-Y轴 3-anchor
  private int curAxle = 0;

  private PosDraw mPosDraw = new PosDraw();

  public EffectRotateAxleDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectAxle;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_rotate_axle;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    btnZAxle = view.findViewById(R.id.btn_axle_z);
    btnXAxle = view.findViewById(R.id.btn_axle_x);
    btnYAxle = view.findViewById(R.id.btn_axle_y);
    btnAnchor = view.findViewById(R.id.btn_axle_anchor);
    btnZAxle.setBackgroundResource(R.drawable.edit_item_bg_selected);

    btnZAxle.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        curAxle = 0;
        btnZAxle.setBackgroundResource(R.drawable.edit_item_bg_selected);
        btnXAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnYAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnAnchor.setBackgroundResource(R.drawable.edit_item_bg_normal);

        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectPosInfo effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        mPosDraw.setNormalFake(1, effectPosInfo.degree.z);
        mFakeApi.setTarget(mPosDraw, effectPosInfo);
      }
    });
    btnXAxle.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        curAxle = 1;
        btnZAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnXAxle.setBackgroundResource(R.drawable.edit_item_bg_selected);
        btnYAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnAnchor.setBackgroundResource(R.drawable.edit_item_bg_normal);
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectPosInfo effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        mPosDraw.setNormalFake(2, effectPosInfo.degree.z);
        mFakeApi.setTarget(mPosDraw, effectPosInfo, effectPosInfo.degree.x);
      }
    });
    btnYAxle.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        curAxle = 2;
        btnZAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnXAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnYAxle.setBackgroundResource(R.drawable.edit_item_bg_selected);
        btnAnchor.setBackgroundResource(R.drawable.edit_item_bg_normal);
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectPosInfo effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        mPosDraw.setNormalFake(2, effectPosInfo.degree.z);
        mFakeApi.setTarget(mPosDraw, effectPosInfo, effectPosInfo.degree.y);
      }
    });
    btnAnchor.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        if (baseEffect instanceof SubtitleEffect) {
          if (((SubtitleEffect) baseEffect).getTextBubbleInfo().isDftTemplate) {
            ToastUtils.show(getContext(), R.string.mn_edit_tips_no_support, Toast.LENGTH_SHORT);
            return;
          }
        }
        curAxle = 3;
        btnZAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnXAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnYAxle.setBackgroundResource(R.drawable.edit_item_bg_normal);
        btnAnchor.setBackgroundResource(R.drawable.edit_item_bg_selected);
        EffectPosInfo effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        mPosDraw.setNormalFake(3, effectPosInfo.degree.z);
        PointF oldCenter = new PointF(effectPosInfo.center.x, effectPosInfo.center.y);
        mFakeApi.setOldAnchor(oldCenter);
        mFakeApi.setTarget(mPosDraw, effectPosInfo);
      }
    });
    initFakeView();
  }

  private void initFakeView() {
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
    EffectPosInfo effectPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
    mPosDraw.setNormalFake(1, 0);
    mFakeApi.setTarget(mPosDraw, effectPosInfo);
    mFakeApi.setFakeViewListener(new IFakeViewListener() {

      @Override public void onEffectMoving() {
        FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectPosInfo targetPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        FakePosUtils.INSTANCE.updateEffectPosByFakePos(curFakePos, targetPosInfo, curAxle, mFakeApi.getOldAnchor());
        EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, effectIndex, targetPosInfo);
        effectOPPosInfo.setFastRefresh(true);
        mWorkSpace.handleOperation(effectOPPosInfo);
      }

      @Override public void onEffectMoveStart() {
        EffectOPLock effectOPLock = new EffectOPLock(groupId, effectIndex, true);
        mWorkSpace.handleOperation(effectOPLock);
        EffectOPStaticPic
            effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, true);
        mWorkSpace.handleOperation(effectOPStaticPic);
      }

      @Override public void onEffectMoveEnd(boolean moved) {
        EffectOPLock effectOPLock = new EffectOPLock(groupId, effectIndex, false);
        mWorkSpace.handleOperation(effectOPLock);
        EffectOPStaticPic
            effectOPStaticPic = new EffectOPStaticPic(groupId, effectIndex, false);
        mWorkSpace.handleOperation(effectOPStaticPic);
        FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        EffectPosInfo targetPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
        FakePosUtils.INSTANCE.updateEffectPosByFakePos(curFakePos, targetPosInfo, curAxle, mFakeApi.getOldAnchor());
        EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, effectIndex, targetPosInfo);
        effectOPPosInfo.setFastRefresh(false);
        mWorkSpace.handleOperation(effectOPPosInfo);
      }

      @Override public void checkEffectTouchHit(@NotNull PointF pointF) {
      }
    });
  }

  @Override protected void releaseAll() {
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_rotate);
  }
}
