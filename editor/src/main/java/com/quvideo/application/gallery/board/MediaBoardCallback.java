package com.quvideo.application.gallery.board;

import android.util.SparseIntArray;
import com.quvideo.application.gallery.model.MediaModel;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @since 5/22/2019
 */
public interface MediaBoardCallback {
  void onMediaSelectDone(ArrayList<MediaModel> missionModelList);

  void onOrderChanged(Map<MediaModel, SparseIntArray> orderedIntArrayMap);
}
