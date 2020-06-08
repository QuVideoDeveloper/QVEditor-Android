package com.quvideo.application.gallery.comparator;

import android.os.Environment;
import com.quvideo.application.gallery.MediaConfig;
import com.quvideo.application.gallery.enums.GROUP_MEDIA_TYPE;
import com.quvideo.application.gallery.model.MediaGroupItem;
import java.io.File;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by liuzhonghu on 2017/8/1.
 *
 * @Description
 */

public class GroupComparator implements Comparator<Long> {
  final Map<Long, MediaGroupItem> mMediaGroupMap;
  final String mStrCameraPath;        //-->DCIM/Camera
  final String mStrXYVideoPath;        //-->XiaoYing/Videos
  final String mStrXYVideoCreation;    //-->DCIM/XiaoYing
  final GROUP_MEDIA_TYPE mGroupType;

  public GroupComparator(Map<Long, MediaGroupItem> mediaGroupMap, GROUP_MEDIA_TYPE nGroupType,
      String strXYCameraPath) {
    mMediaGroupMap = mediaGroupMap;
    mGroupType = nGroupType;
    if (strXYCameraPath == null) {
      mStrXYVideoPath = null;
    } else if (strXYCameraPath.endsWith("/")) {
      mStrXYVideoPath = strXYCameraPath;
    } else {
      mStrXYVideoPath = strXYCameraPath + "/";
    }
    mStrCameraPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
            + File.separator
            + "Camera/";
    mStrXYVideoCreation = MediaConfig.APP_DEFAULT_EXPORT_PATH;
  }

  @Override public int compare(Long obj1, Long obj2) {
    int nRet = 0;
    if (mGroupType == GROUP_MEDIA_TYPE.GROUP_MEDIA_TYPE_FOLDER) {
      MediaGroupItem item1 = mMediaGroupMap.get(obj1);
      MediaGroupItem item2 = mMediaGroupMap.get(obj2);
      if (item1 != null && item2 != null) {
        if ((mStrXYVideoPath != null) && mStrXYVideoPath.compareTo(item1.strParentPath) == 0) {
          nRet = -1;
        } else if (mStrXYVideoPath != null && mStrXYVideoPath.compareTo(item2.strParentPath) == 0) {
          nRet = 1;
        } else if ((mStrXYVideoCreation != null)
            && mStrXYVideoCreation.compareTo(item1.strParentPath) == 0) {
          nRet = -1;
        } else if (mStrXYVideoCreation != null
            && mStrXYVideoCreation.compareTo(item2.strParentPath) == 0) {
          nRet = 1;
        } else if (mStrCameraPath.compareTo(item1.strParentPath) == 0) {
          nRet = -1;
        } else if (mStrCameraPath.compareTo(item2.strParentPath) == 0) {
          nRet = 1;
        } else {
          nRet = item1.strGroupDisplayName.compareToIgnoreCase(item2.strGroupDisplayName);
        }

        if (item1.isVirtualFile()) {
          nRet = -1;
        } else if (item2.isVirtualFile()) {
          nRet = 1;
        }
      }
    } else {
      // 降序排序
      nRet = obj2.compareTo(obj1);
    }
    return nRet;
  }
}
