package com.quvideo.application.widget.curve;

import android.graphics.Point;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author wuzhongyou
 * @date 2020/8/17.
 */
public class CurveData {
  public int lineColor;
  /** 曲线类型,0-颜色曲线(样条曲线) 1-曲线变速(正弦曲线) */
  public int curveType;
  public LinkedList<PointF> knotsList = new LinkedList();

  public CurveData() {
  }

  public CurveData(int lineColor, LinkedList<PointF> knotsList) {
    this.lineColor = lineColor;
    this.knotsList = knotsList;
  }

}
