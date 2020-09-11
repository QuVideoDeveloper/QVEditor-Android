package com.quvideo.application.superedit;

import android.content.Context;
import android.widget.Toast;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.BaseOperate;

public class SuperEditManager {

  public static boolean isHadSuperEdit() {
    return false;
  }

  /**
   * 进入曲线调速设置
   */
  public static void gotoEditCurveSpeedDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex) {
    ToastUtils.show(context, R.string.mn_edit_tips_no_support, Toast.LENGTH_LONG);
  }

  /**
   */
  public static BaseOperate createSizeChangeOperate(IQEWorkSpace workSpace, VeMSize newStreamSize, VeMSize oldStreamSize) {
    return null;
  }
}
