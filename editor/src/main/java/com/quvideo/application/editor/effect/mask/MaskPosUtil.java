package com.quvideo.application.editor.effect.mask;

import android.graphics.PointF;
import android.graphics.RectF;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.model.effect.EffectMaskInfo;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.utils.RectTransUtils;

class MaskPosUtil {

  /**
   * 将万分比坐标转换为在stream中的坐标
   */
  static FakeMaskPosData conver2FakeMaskData(int groupId, VeMSize streamSize, EffectMaskInfo effectMaskInfo, EffectPosInfo effectPosInfo) {
    if (effectMaskInfo == null || effectPosInfo == null) {
      return null;
    }
    FakeMaskPosData fakeMaskPosData = new FakeMaskPosData();
    if (effectMaskInfo.maskType == EffectMaskInfo.MaskType.MASK_NONE) {
      return null;
    }
    boolean isSubtitle = groupId == QEGroupConst.GROUP_ID_SUBTITLE;
    RectF limitRectF = effectPosInfo.getRectArea();
    float effectDegree = effectPosInfo.degree.z;
    if (isSubtitle) {
      effectDegree = 0;
      limitRectF = new RectF(0, 0, streamSize.width, streamSize.height);
    }
    float relCenterX = effectMaskInfo.centerX * limitRectF.width() / 10000f + limitRectF.left;
    float relCenterY = effectMaskInfo.centerY * limitRectF.height() / 10000f + limitRectF.top;

    PointF centerOldPointF = RectTransUtils.calcNewPoint(new PointF(relCenterX, relCenterY),
        new PointF(limitRectF.centerX(), limitRectF.centerY()), effectDegree);
    //propertyFirst 对应--> engine centerX 属性
    fakeMaskPosData.centerX = centerOldPointF.x;
    fakeMaskPosData.centerY = centerOldPointF.y;
    // X轴半径
    fakeMaskPosData.radiusY = limitRectF.height() * effectMaskInfo.radiusY / 10000f;
    // Y轴半径
    fakeMaskPosData.radiusX = limitRectF.width() * effectMaskInfo.radiusX / 10000f;
    // 角度
    // 这个角度是以x轴为0度，逆时针增大
    fakeMaskPosData.rotation = effectDegree + effectMaskInfo.rotation;
    return fakeMaskPosData;
  }

  /**
   * 将stream中的坐标转换为万分比坐标
   */
  static void fill2EffectMaskInfo(int groupId, VeMSize streamSize, FakeMaskPosData fakeMaskPosData,
      EffectPosInfo effectPosInfo, EffectMaskInfo effectMaskInfo) {
    if (effectMaskInfo == null || fakeMaskPosData == null || effectPosInfo == null) {
      return;
    }
    boolean isSubtitle = groupId == QEGroupConst.GROUP_ID_SUBTITLE;
    float effectDegree = effectPosInfo.degree.z;
    RectF limitRectF = effectPosInfo.getRectArea();
    if (isSubtitle) {
      effectDegree = 0;
      limitRectF = new RectF(0, 0, streamSize.width, streamSize.height);
    }
    // 计算中心点位置（先以画中画中心点旋转一下)
    PointF centerOldPointF = RectTransUtils.calcNewPoint(new PointF(fakeMaskPosData.centerX, fakeMaskPosData.centerY),
        new PointF(limitRectF.centerX(), limitRectF.centerY()), -effectDegree);
    float offsetX = centerOldPointF.x - limitRectF.centerX();
    float realX = offsetX + limitRectF.width() / 2f;
    effectMaskInfo.centerX = (int) (realX / limitRectF.width() * 10000f);
    float offsetY = centerOldPointF.y - limitRectF.centerY();
    float realY = offsetY + limitRectF.height() / 2f;
    effectMaskInfo.centerY = (int) (realY / limitRectF.height() * 10000f);
    effectMaskInfo.radiusY = (int) (fakeMaskPosData.radiusY * 10000f / limitRectF.height());
    effectMaskInfo.radiusX = (int) (fakeMaskPosData.radiusX * 10000f / limitRectF.width());
    effectMaskInfo.rotation = (int) (fakeMaskPosData.rotation - effectDegree);
  }
}
