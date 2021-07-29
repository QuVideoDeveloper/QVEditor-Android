package com.quvideo.application.editor.effect.operate;

import com.quvideo.mobile.engine.entity.Ve3DDataF;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.work.IEngine;
import com.quvideo.mobile.engine.work.ModifyData;
import com.quvideo.mobile.engine.work.PlayerRefreshListener;
import com.quvideo.mobile.engine.work.operate.effect.BaseEffectOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPKeyFrameUpdateOffsetAll;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPPosInfo;

/**
 * @author wuzhongyou
 * @date 2020/9/7.
 */
public class EffectOPCustomKeyOffset extends BaseEffectOperate {

  private Ve3DDataF posOffsetValue;
  private Ve3DDataF rotateOffsetValue;
  private Ve3DDataF scaleOffsetValue;
  private Ve3DDataF anchorOffsetValue;

  private EffectPosInfo effectPosInfo;

  private boolean fastRefresh;

  /**
   *
   */
  public EffectOPCustomKeyOffset(int groupId, int effectIndex,
      Ve3DDataF posOffsetValue, Ve3DDataF rotateOffsetValue, Ve3DDataF scaleOffsetValue, Ve3DDataF anchorOffsetValue,
      boolean fastRefresh, EffectPosInfo effectPosInfo) {
    super(groupId, effectIndex);
    this.posOffsetValue = posOffsetValue;
    this.rotateOffsetValue = rotateOffsetValue;
    this.scaleOffsetValue = scaleOffsetValue;
    this.anchorOffsetValue = anchorOffsetValue;
    this.fastRefresh = fastRefresh;
    this.effectPosInfo = effectPosInfo;
  }

  @Override protected boolean operateRun(IEngine engine) {
    EffectOPKeyFrameUpdateOffsetAll effectOPKeyFrameUpdateOffsetAll = new EffectOPKeyFrameUpdateOffsetAll(groupId, effectIndex,
        posOffsetValue, rotateOffsetValue, scaleOffsetValue, anchorOffsetValue);
    boolean result = effectOPKeyFrameUpdateOffsetAll.operate(engine);
    if (!result) {
      return false;
    }
    EffectOPPosInfo effectOPPosInfo = new EffectOPPosInfo(groupId, effectIndex, effectPosInfo);
    effectOPPosInfo.setFastRefresh(false);
    return effectOPPosInfo.operate(engine);
  }

  @Override public String getTaskEqualKey() {
    if (fastRefresh) {
      return "EffectOPCustomKeyOffset_" + groupId + "_" + effectIndex;
    }
    return "";
  }

  @Override protected PlayerRefreshListener.RefreshEvent getPlayRefreshEvent(IEngine engine) {
    PlayerRefreshListener.RefreshEvent event = new PlayerRefreshListener.RefreshEvent();
    event.refreshType = PlayerRefreshListener.OperaRefreshType.TYPE_REFRESH_DISPLAY;
    return event;
  }

  @Override protected ModifyData getModifyData() {
    ModifyData modifyData = new ModifyData();
    modifyData.mEffectModifyData = new ModifyData.EffectModifyData(ModifyData.ModifyType.MODIFY_TYPE_UPDATE, groupId, effectIndex);
    return modifyData;
  }
}
