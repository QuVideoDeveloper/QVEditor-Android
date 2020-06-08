package com.quvideo.application.gallery.model;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.quvideo.application.gallery.enums.BROWSE_TYPE;
import com.quvideo.application.gallery.enums.MediaType;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuzhonghu on 2017/8/1.
 *
 * @Description 文件夹数据模型
 */

public class MediaGroupItem implements Serializable {

  private static final long serialVersionUID = -1526030110757968541L;
  public final static int ITEM_MEDIA_TYPE_NONE = 0;
  public final static int ITEM_MEDIA_TYPE_PHOTO = 1;
  public final static int ITEM_MEDIA_TYPE_VIDEO = 2;

  public long lGroupTimestamp;
  public long lGroupExtInfo;
  public String strGroupDisplayName;
  public List<ExtMediaItem> mediaItemList;

  public Map<String, ExtMediaItem> mediaItemMap = new HashMap<>();
  public long lFlag;
  public String strParentPath;
  public long lNewItemCount = 0;

  public int countForSns = 0;
  public String coverPhotoUrl;
  public String albumId;
  public MediaType mediaType;
  boolean isVirtualFile = false;
  BROWSE_TYPE browseType = null;

  public boolean isEmptyGroup() {
    return mediaItemList == null || mediaItemList.isEmpty();
  }

  public void remove(ExtMediaItem item) {
    if (item != null && item.path != null) {
      mediaItemList.remove(item);
      mediaItemMap.remove(item.path);
    }
  }

  public void add(ExtMediaItem item) {
    if (!mediaItemMap.containsKey(item.path)) {
      mediaItemMap.put(item.path, item);
      mediaItemList.add(item);
    }
    if (item.lFlag != 0) {
      lNewItemCount++;
    }
  }

  public void add(ExtMediaItem item, int filterType) {
    boolean canAdd = false;
    if (filterType == ITEM_MEDIA_TYPE_PHOTO && MediaFileUtils.isImageFileType(item.path)) {
      canAdd = true;
    } else if (filterType == ITEM_MEDIA_TYPE_VIDEO && MediaFileUtils.isVideoFileType(item.path)) {
      canAdd = true;
    } else if (filterType == ITEM_MEDIA_TYPE_NONE) {
      canAdd = true;
    }
    if (canAdd && !mediaItemMap.containsKey(item.path)) {
      mediaItemMap.put(item.path, item);
      mediaItemList.add(item);
      if (item.lFlag != 0) {
        lNewItemCount++;
      }
    }
  }

  public void remove(int nIndex) {
    if (mediaItemList == null) {
      return;
    }
    if (nIndex < 0 || nIndex >= mediaItemList.size()) {
      return;
    }

    ExtMediaItem item = mediaItemList.remove(nIndex);
    if (item != null) {
      mediaItemMap.remove(item.path);
      if (item.lFlag != 0) {
        lNewItemCount--;
      }
    }
  }

  public long getlGroupTimestamp() {
    return lGroupTimestamp;
  }

  public void setlGroupTimestamp(long lGroupTimestamp) {
    this.lGroupTimestamp = lGroupTimestamp;
  }

  public long getlGroupExtInfo() {
    return lGroupExtInfo;
  }

  public void setlGroupExtInfo(long lGroupExtInfo) {
    this.lGroupExtInfo = lGroupExtInfo;
  }

  public String getStrGroupDisplayName() {
    return strGroupDisplayName;
  }

  public void setStrGroupDisplayName(String strGroupDisplayName) {
    this.strGroupDisplayName = strGroupDisplayName;
  }

  public List<ExtMediaItem> getMediaItemList() {
    return mediaItemList;
  }

  public void setMediaItemList(ArrayList<ExtMediaItem> mediaItemList) {
    this.mediaItemList = mediaItemList;
  }

  public Map<String, ExtMediaItem> getMediaItemMap() {
    return mediaItemMap;
  }

  public void setMediaItemMap(Map<String, ExtMediaItem> mediaItemMap) {
    this.mediaItemMap = mediaItemMap;
  }

  public long getlFlag() {
    return lFlag;
  }

  public void setlFlag(long lFlag) {
    this.lFlag = lFlag;
  }

  public String getStrParentPath() {
    return strParentPath;
  }

  public void setStrParentPath(String strParentPath) {
    this.strParentPath = strParentPath;
  }

  public long getlNewItemCount() {
    return lNewItemCount;
  }

  public void setlNewItemCount(long lNewItemCount) {
    this.lNewItemCount = lNewItemCount;
  }

  public int getCountForSns() {
    return countForSns;
  }

  public void setCountForSns(int countForSns) {
    this.countForSns = countForSns;
  }

  public String getCoverPhotoUrl() {
    return coverPhotoUrl;
  }

  public void setCoverPhotoUrl(String coverPhotoUrl) {
    this.coverPhotoUrl = coverPhotoUrl;
  }

  public String getAlbumId() {
    return albumId;
  }

  public void setAlbumId(String albumId) {
    this.albumId = albumId;
  }

  public MediaType getMediaType() {
    return mediaType;
  }

  public void setMediaType(MediaType mediaType) {
    this.mediaType = mediaType;
  }

  public boolean isVirtualFile() {
    return isVirtualFile;
  }

  public void setVirtualFile(boolean virtualFile) {
    isVirtualFile = virtualFile;
  }

  public BROWSE_TYPE getBrowseType() {
    return browseType;
  }

  public void setBrowseType(BROWSE_TYPE browseType) {
    this.browseType = browseType;
  }

  @NonNull @Override public String toString() {
    return new Gson().toJson(this);
  }
}
