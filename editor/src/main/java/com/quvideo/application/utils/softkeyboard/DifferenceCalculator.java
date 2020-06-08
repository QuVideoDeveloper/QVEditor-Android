package com.quvideo.application.utils.softkeyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import com.quvideo.application.utils.SystemUtils;

public class DifferenceCalculator extends BaseInputPanelRuler {
  private DifferenceCalculator() {
  }

  private static class Holder {
    private final static DifferenceCalculator instance = new DifferenceCalculator();
  }

  public static DifferenceCalculator getInstance() {
    return Holder.instance;
  }

  private BaseInputPanelRuler ruler;

  private BaseInputPanelRuler getRuler() {
    if (null != ruler) return ruler;

    if (SystemUtils.isOppo()) {
      ruler = new OppoInputPanelRuler();
    } else if (SystemUtils.isSanxing()) {
      ruler = new SanxingInputPanelRuler();
    } else if (SystemUtils.isHuawei()) {
      ruler = new HuaweiInputPanelRuler();
    } else if (SystemUtils.isXiaomi()) {
      ruler = new XiaomiInputPanelRuler();
    } else {
      ruler = new BaseInputPanelRuler();
    }
    return ruler;
  }

  @Override
  public void onSpeakShow(Activity activity) {
    getRuler().onSpeakShow(activity);
  }

  @Override
  public void onSpeakHide(Activity activity) {
    getRuler().onSpeakHide(activity);
  }

  @Override
  public int getDifference(Context ctx, Rect r) {
    return getRuler().getDifference(ctx, r);
  }
}
