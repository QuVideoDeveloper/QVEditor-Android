package com.quvideo.application.superedit;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.quvideo.application.editor.EditOperate;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.EffectBarItem;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.entity.XmlType;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.BaseOperate;
import java.util.ArrayList;
import java.util.List;

public class SuperEditManager {

  public static ArrayList<String> mSuperXytList = new ArrayList<>();
  public static ArrayList<String> mSuperZipList = new ArrayList<>();

  public static boolean isHadSuperEdit() {
    return false;
  }

  public static void initAIComponent(Context context) {
  }

  public static void addAdvancedFunc(Context context, List<EditOperate> operateList) {
  }

  public static boolean clickAdvancedFunc(Context context, EditOperate operate, MenuContainer container,
      IQEWorkSpace workSpace, IFakeViewApi fakeViewApi) {
    return false;
  }


  public static void addClipOPFunc(Context context, List<EditOperate> operateList) {
  }

  public static boolean clickClipOPFunc(Activity context, EditOperate operate, int clipIndex, MenuContainer container,
      IQEWorkSpace workSpace, IFakeViewApi fakeViewApi) {
    return false;
  }

  public static void addEffectOPFunc(Context context, List<EffectBarItem> list, int groupId, boolean isOpEnabled) {
  }

  public static boolean clickEffectOPFunc(Activity context, EffectBarItem operate, int groupId, int effectIndex,
      MenuContainer container, IQEWorkSpace workSpace, IFakeViewApi fakeViewApi) {
    return false;
  }

  public static boolean isSuperRefreshOP(BaseOperate operate) {
    return false;
  }

  public static BaseOperate createSizeChangeOperate(IQEWorkSpace workSpace, VeMSize newStreamSize, VeMSize oldStreamSize) {
    return null;
  }

  public static void saveFilter2Xml(Context context, IQEWorkSpace workSpace, int clipIndex, int index) {
    ToastUtils.show(context, R.string.mn_edit_tips_no_support, Toast.LENGTH_LONG);
  }

  public static void saveAdjust2Xml(Context context, IQEWorkSpace workSpace, int clipIndex) {
    ToastUtils.show(context, R.string.mn_edit_tips_no_support, Toast.LENGTH_LONG);
  }

  public static void saveCurveColor2Xml(Context context, IQEWorkSpace workSpace, int clipIndex) {
    ToastUtils.show(context, R.string.mn_edit_tips_no_support, Toast.LENGTH_LONG);
  }

  public static void gotoAddXml(Context context, MenuContainer container, IQEWorkSpace workSpace, XmlType xmlType, int clipIndex) {
    ToastUtils.show(context, R.string.mn_edit_tips_no_support, Toast.LENGTH_LONG);
  }

}
