package com.quvideo.application.widget.softkeyboard;

import android.content.Context;
import android.graphics.Rect;
import com.quvideo.application.utils.NotchUtil;

public class HuaweiInputPanelRuler extends BaseInputPanelRuler {

  @Override
  public int getDifference(Context ctx, Rect r) {
    int differ = super.getDifference(ctx, r);

    if (NotchUtil.isNotchDevice(ctx) && Math.abs(differ) == NotchUtil.getNotchSizeAtHuawei(ctx)[1]) {
      return 0;
    } else {
      return differ;
    }
  }
}
