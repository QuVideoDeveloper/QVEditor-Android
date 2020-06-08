package com.quvideo.application.gallery.interfaces;

/**
 * Created by liuzhonghu on 2017/8/1.
 *
 * @Description
 */

public interface DataChangeListener {
  boolean onNotify(int what, int lParam, int wParam, int nStatus, Object obj, DataChangeListener cb);
}
