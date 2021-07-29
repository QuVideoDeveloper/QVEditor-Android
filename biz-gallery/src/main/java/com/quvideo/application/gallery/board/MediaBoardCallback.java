package com.quvideo.application.gallery.board;

import android.util.SparseIntArray;
import com.quvideo.application.gallery.model.MediaModel;
import java.util.ArrayList;
import java.util.Map;

public interface MediaBoardCallback {
  void onMediaSelectDone(ArrayList<MediaModel> missionModelList);

  void onOrderChanged(Map<MediaModel, SparseIntArray> orderedIntArrayMap);
}
