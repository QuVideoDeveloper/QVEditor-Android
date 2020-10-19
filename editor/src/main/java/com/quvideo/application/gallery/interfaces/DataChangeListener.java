package com.quvideo.application.gallery.interfaces;

/**
 *
 * @Description
 */

public interface DataChangeListener {
  boolean onNotify(int what, int lParam, int wParam, int nStatus, Object obj, DataChangeListener cb);
}
