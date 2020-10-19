package com.quvideo.application.superedit;

import android.app.Activity;
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

  public static void gotoEditCurveSpeedDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex) {
    ToastUtils.show(context, R.string.mn_edit_tips_no_support, Toast.LENGTH_LONG);
  }

  public static void gotoEditAudioSpeedDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex) {
    ToastUtils.show(context, R.string.mn_edit_tips_no_support, Toast.LENGTH_LONG);
  }

  /**
   * 进入音频特征点解析
   */
  public static void gotoEditAudioDotDialog(Activity activity, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex) {
    ToastUtils.show(activity, R.string.mn_edit_tips_no_support, Toast.LENGTH_LONG);
  }

  /**
   *
   */
  public static BaseOperate createSizeChangeOperate(IQEWorkSpace workSpace, VeMSize newStreamSize, VeMSize oldStreamSize) {
    return null;
  }

  /**
   * 进入画中画拼贴功能
   */
  public static void gotoEditCollageMotionDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, int effectIndex) {
    ToastUtils.show(context, R.string.mn_edit_tips_no_support, Toast.LENGTH_LONG);
  }
}
