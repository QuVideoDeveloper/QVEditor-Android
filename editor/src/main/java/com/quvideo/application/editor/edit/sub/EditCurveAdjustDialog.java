package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.View;
import android.widget.RadioGroup;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.widget.curve.CurveData;
import com.quvideo.application.widget.curve.SpecialCurveLineView;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.ColorCurveInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPColorCurve;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import xiaoying.utils.QPoint;

public class EditCurveAdjustDialog extends BaseMenuView {

  private RadioGroup mRGColor;
  private SpecialCurveLineView curveLineView;

  private int clipIndex = 0;

  public EditCurveAdjustDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex) {
    super(context, workSpace);
    this.clipIndex = clipIndex;

    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipCurveAdjust;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_curve_color;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mRGColor = view.findViewById(R.id.rg_color);
    curveLineView = view.findViewById(R.id.curveLineView);
    curveLineView.initRange(new VeRange(0, 255), new VeRange(0, 255));

    mRGColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_color_rgb) {
          // 切换到rgb线
          curveLineView.switchSpLineType(0, true);
        } else if (checkedId == R.id.rb_color_r) {
          curveLineView.switchSpLineType(1, true);
          // 切换到r线
        } else if (checkedId == R.id.rb_color_g) {
          // 切换到g线
          curveLineView.switchSpLineType(2, true);
        } else {
          // 切换到b线
          curveLineView.switchSpLineType(3, true);
        }
      }
    });
    curveLineView.setOnCtrPointsUpdateCallBack(new SpecialCurveLineView.OnCtrPointsUpdateCallBack() {
      @Override public void onUpdate(@NotNull ArrayList<QPoint> points, int selectIndex) {
        changeCurveColor();
      }
    });

    Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
      @Override public boolean queueIdle() {
        initData();
        return false;
      }
    });
  }

  @Override protected void releaseAll() {
  }

  private void initData() {
    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    ColorCurveInfo colorCurveInfo = clipData.getColorCurveInfo();
    ColorCurveInfo.ColorCurveItem colorCurveItem = colorCurveInfo.mColorCurveItems.get(0);
    // rgb
    CurveData rgbCurveData = new CurveData();
    rgbCurveData.curveType = 0;
    rgbCurveData.lineColor = getResources().getColor(R.color.white);
    rgbCurveData.knotsList = new LinkedList<>();
    ArrayList<QPoint> rgbbefore = new ArrayList<>();
    for (QPoint point : colorCurveItem.rgb) {
      rgbbefore.add(new QPoint(point.x, point.y));
    }
    rgbCurveData.knotsList = curveLineView.getRealPoints(rgbbefore);
    // r
    CurveData rCurveData = new CurveData();
    rCurveData.curveType = 0;
    rCurveData.lineColor = getResources().getColor(R.color.color_fe3d42);
    rCurveData.knotsList = new LinkedList<>();
    ArrayList<QPoint> rbefore = new ArrayList<>();
    for (QPoint point : colorCurveItem.red) {
      rbefore.add(new QPoint(point.x, point.y));
    }
    rCurveData.knotsList = curveLineView.getRealPoints(rbefore);
    // g
    CurveData gCurveData = new CurveData();
    gCurveData.curveType = 0;
    gCurveData.lineColor = getResources().getColor(R.color.color_00b300);
    gCurveData.knotsList = new LinkedList<>();
    ArrayList<QPoint> gbefore = new ArrayList<>();
    for (QPoint point : colorCurveItem.green) {
      gbefore.add(new QPoint(point.x, point.y));
    }
    gCurveData.knotsList = curveLineView.getRealPoints(gbefore);
    // b
    CurveData bCurveData = new CurveData();
    bCurveData.curveType = 0;
    bCurveData.lineColor = getResources().getColor(R.color.color_3493f2);
    bCurveData.knotsList = new LinkedList<>();
    ArrayList<QPoint> bbefore = new ArrayList<>();
    for (QPoint point : colorCurveItem.blue) {
      bbefore.add(new QPoint(point.x, point.y));
    }
    bCurveData.knotsList = curveLineView.getRealPoints(bbefore);
    // 初始化数据
    List<CurveData> sourceList = new ArrayList<>();
    sourceList.add(rgbCurveData);
    sourceList.add(rCurveData);
    sourceList.add(gCurveData);
    sourceList.add(bCurveData);
    curveLineView.setCurveDate(0, sourceList);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  private void changeCurveColor() {
    ColorCurveInfo colorCurveInfo = new ColorCurveInfo();
    colorCurveInfo.mColorCurveItems = new ArrayList<>();
    ColorCurveInfo.ColorCurveItem item = new ColorCurveInfo.ColorCurveItem();
    item.ts = 0;
    item.rgb = curveLineView.getFixArrayPoints(0);
    item.red = curveLineView.getFixArrayPoints(1);
    item.green = curveLineView.getFixArrayPoints(2);
    item.blue = curveLineView.getFixArrayPoints(3);
    colorCurveInfo.mColorCurveItems.add(item);
    ClipOPColorCurve clipOPColorCurve = new ClipOPColorCurve(clipIndex, colorCurveInfo);
    mWorkSpace.handleOperation(clipOPColorCurve);
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_adjust_curve);
  }
}
