package com.quvideo.application.gallery.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.MediaConfig;
import com.quvideo.application.gallery.comparator.GroupComparator;
import com.quvideo.application.gallery.comparator.MediaItemComparator;
import com.quvideo.application.gallery.constant.StorageInfo;
import com.quvideo.application.gallery.enums.BROWSE_TYPE;
import com.quvideo.application.gallery.enums.GROUP_MEDIA_TYPE;
import com.quvideo.application.gallery.enums.MediaType;
import com.quvideo.application.gallery.interfaces.DataChangeListener;
import com.quvideo.application.gallery.model.ExtMediaItem;
import com.quvideo.application.gallery.model.MediaGroupItem;
import com.quvideo.application.gallery.utils.MediaFileSupported;
import com.quvideo.application.gallery.utils.MediaFolderNameMapUtils;
import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

public class MediaManager {

  public final static String SYSTEM_GALLERY_CACHE = "SystemGallery";
  public static final String MEDIA_ITEM_FLAG = "flag";
  public static final String MEDIA_ITEM_FROM_TYPE = "from_type";
  public static final String MEDIA_ITEM_MISC = "misc";
  private final static String TAG = MediaManager.class.getSimpleName();

  private GROUP_MEDIA_TYPE mGroupType = GROUP_MEDIA_TYPE.GROUP_MEDIA_TYPE_FOLDER;
  private static final long ONE_DAY_IN_MILLSECONDS = (3600L * 24L * 1000L);

  private MediaGroupItem recentGroupItem;

  public static int TOTAL_COUNT_PHOTO = 0;
  public static int TOTAL_COUNT_VIDEO = 0;

  public static final int NOTIFY_EVENT_ITEM_LOAD = 0;

  public static final int NOTIFY_STATUS_RUNNING = 0;
  public static final int NOTIFY_STATUS_DONE = 1;
  public static final int NOTIFY_STATUS_ASK = 2;

  private DataChangeListener mListener;

  private BROWSE_TYPE mBrowseType = BROWSE_TYPE.PHOTO_AND_VIDEO;
  private static final int MAX_LAYER_DEEP = 2;
  private int mMaxLayerCount = MAX_LAYER_DEEP;

  private Map<Long, MediaGroupItem> mMediaGroupMap =
      Collections.synchronizedMap(new HashMap<>());
  private Long[] mMediaGroupKeys = null;
  private String mMainStorageXYMediaCameraPath = null;
  private String mExtStorageXYMediaCameraPath = null;
  private boolean mbGotXYMediaCameraPath = false;

  public MediaManager() {
    try {
      String strMainStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
      if (!TextUtils.isEmpty(strMainStorage)) {
        String path[] = strMainStorage.split("/");
        mMaxLayerCount = path.length + MAX_LAYER_DEEP;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setGroupType(GROUP_MEDIA_TYPE nGroupType) {
    mGroupType = nGroupType;
  }

  public void setListener(DataChangeListener listener) {
    mListener = listener;
  }

  public synchronized boolean init(Context ctx, BROWSE_TYPE browseType) {
    long lStart = System.currentTimeMillis();
    mBrowseType = browseType;

    //load from cache and notify UI
    //			loadCache(ctx, SYSTEM_GALLERY_CACHE, mMediaGroupMap);
    if (mListener != null) {
      mListener.onNotify(NOTIFY_EVENT_ITEM_LOAD, 0, 0, NOTIFY_STATUS_DONE, null, null);
    }
    if (mBrowseType == BROWSE_TYPE.AUDIO) {
      setGroupType(GROUP_MEDIA_TYPE.GROUP_MEDIA_TYPE_TITLE);
    }
    boolean bResult = queryMediaStore(ctx, mMediaGroupMap, mBrowseType);
    return bResult;
  }

  public synchronized boolean init(Context ctx, MediaGroupItem groupItemFrom) {
    return init(ctx, groupItemFrom, -1);
  }

  private synchronized boolean init(Context ctx, MediaGroupItem groupItemFrom, int filterType) {
    if (groupItemFrom == null || groupItemFrom.mediaItemList == null) {
      return false;
    }
    ArrayList<ExtMediaItem> mediaItems = new ArrayList<>(groupItemFrom.mediaItemList);

    MediaGroupItem groupItem;
    for (ExtMediaItem item : mediaItems) {
      if (TextUtils.isEmpty(item.path)) {
        continue;
      }
      //because qq music add bad jpg file into media store, so we skip these files. cfchen@20130629
      if (item.path.contains("/qqmusic/")) {
        continue;
      }

      /*if (!FileUtils.isFileExisted(item.path)) {
        continue;
      }*/

      groupItem = searchGroupItem(ctx, mMediaGroupMap, item);
      if (groupItem == null) {
        continue;
      }

      item.lGroupKey = groupItem.lGroupTimestamp;
      if (filterType < 0) {
        groupItem.add(item);
      } else {
        groupItem.add(item, filterType);
      }
    }
    sortItemInGroup(mMediaGroupMap);
    return true;
  }

  private static String formatMimeTypeQuery(String[] mimeTypes) {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (String mime : mimeTypes) {
      if (sb.length() > 1) {
        sb.append(" OR ");
      }

      sb.append(MediaStore.Images.Media.MIME_TYPE).append(" = '").append(mime).append("'");
    }
    sb.append(")");
    return sb.toString();
  }

  private static String getQuerySelect(BROWSE_TYPE nBrowType) {
    String strWhere = null;
    switch (nBrowType) {
      case PHOTO:
        strWhere = formatMimeTypeQuery(MediaFileSupported.getSupportPhotosMimeType());
        break;
      case VIDEO:
        strWhere = formatMimeTypeQuery(MediaFileSupported.getSupportVideosMimeType());
        break;
      case AUDIO:
        strWhere = formatMimeTypeQuery(MediaFileSupported.getSupportMusicsMimeType());
        break;
      default:
        break;
    }
    return strWhere;
  }

  private static MediaType getMediaType(Uri uri) {
    if (uri.equals(MediaStore.Video.Media.EXTERNAL_CONTENT_URI)) {
      return MediaType.MEDIA_TYPE_VIDEO;
    } else if (uri.equals(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)) {
      return MediaType.MEDIA_TYPE_IMAGE;
    }
    return MediaType.MEDIA_TYPE_UNKNOWN;
  }

  private static Cursor getMediaStoreCursor(Context ctx, Uri uri) {
    if (ctx == null || uri == null) {
      return null;
    }
    String[] projection = null;
    String sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " desc";
    String strWhere = null;
    if (uri.equals(MediaStore.Video.Media.EXTERNAL_CONTENT_URI)) {
      projection = new String[] {
          MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA,
          MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media.DURATION,
          //				MediaStore.Video.Media.RESOLUTION,
      };
      strWhere = getQuerySelect(BROWSE_TYPE.VIDEO);
    } else if (uri.equals(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)) {
      projection = new String[] {
          MediaStore.Images.Media._ID, MediaStore.Images.Media.TITLE,
          MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED
      };

      strWhere = getQuerySelect(BROWSE_TYPE.PHOTO);
    } else if (uri.equals(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)) {
      projection = new String[] {
          MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
          MediaStore.Audio.Media.DATE_MODIFIED, MediaStore.Audio.Media.DURATION,
          MediaStore.Audio.Media.ARTIST,
      };
      strWhere = getQuerySelect(BROWSE_TYPE.AUDIO);
    }

    if (!TextUtils.isEmpty(strWhere)) {
      strWhere = "(" + strWhere + ")";
    }

    Cursor cursor = null;
    try {
      cursor = ctx.getContentResolver().query(uri, projection, strWhere, null, sortOrder);
    } catch (Exception ignore) {

    }
    return cursor;
  }

  private static ExtMediaItem getMediaItem(Cursor cursor, Uri baseUri, MediaType mediaType) {
    if (cursor == null) {
      return null;
    }
    ExtMediaItem item = new ExtMediaItem();
    item.mediaType = mediaType;
    item.mediaId = cursor.getInt(0);
    item.title = item.displayTitle = cursor.getString(1);
    item.path = Uri.withAppendedPath(baseUri, "" + item.mediaId).toString();
    item.date = cursor.getLong(3);
    if (String.valueOf(item.date).length() <= 10) {
      //to mill seconds
      item.date *= 1000L;
    }

    //video or music?
    int nDurationIdx = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
    if (nDurationIdx >= 0) {
      item.duration = cursor.getLong(nDurationIdx);
    }

    int nArtistIdx = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
    if (nArtistIdx >= 0) {
      item.artist = cursor.getString(nArtistIdx);
    }

    int nFlagIdx = cursor.getColumnIndex(MEDIA_ITEM_FLAG);
    if (nFlagIdx >= 0) {
      item.lFlag = cursor.getInt(nFlagIdx);
    }

    int nFromtypeIdx = cursor.getColumnIndex(MEDIA_ITEM_FROM_TYPE);
    if (nFromtypeIdx >= 0) {
      item.nFromtype = cursor.getInt(nFromtypeIdx);
    }

    int nMiscIdx = cursor.getColumnIndex(MEDIA_ITEM_MISC);
    if (nMiscIdx >= 0) {
      item.strMisc = cursor.getString(nMiscIdx);
    }

    return item;
  }

  private boolean sortItemInGroup(Map<Long, MediaGroupItem> map) {
    if (map.size() <= 1) {
      return true;
    }
    Entry<Long, MediaGroupItem> entry;
    MediaGroupItem groupItem;
    for (Entry<Long, MediaGroupItem> longMediaGroupItemEntry : map.entrySet()) {
      entry = longMediaGroupItemEntry;
      groupItem = entry.getValue();
      if (groupItem != null
          && groupItem.mediaItemList != null
          && groupItem.mediaItemList.size() > 1) {
        Collections.sort(groupItem.mediaItemList,
            new MediaItemComparator(MediaItemComparator.SORT_TYPE_DATE_DESC));
      }
      if (mListener != null) {
        mListener.onNotify(NOTIFY_EVENT_ITEM_LOAD, 0, 0, NOTIFY_STATUS_RUNNING, null, null);
      }
    }

    if (mListener != null) {
      mListener.onNotify(NOTIFY_EVENT_ITEM_LOAD, 0, 0, NOTIFY_STATUS_DONE, null, null);
    }
    return true;
  }

  private boolean queryMediaStore(Context ctx, Map<Long, MediaGroupItem> map,
      BROWSE_TYPE nBrowseType) {
    initXiaoYingDataAndCameraPath();
    recentGroupItem = new MediaGroupItem();
    recentGroupItem.setVirtualFile(true);
    recentGroupItem.strParentPath = "";
    if (nBrowseType != null) {
      switch (nBrowseType) {
        case PHOTO_AND_VIDEO:
          queryMediaStore(ctx, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, map);
          queryMediaStore(ctx, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, map);
          break;
        case PHOTO:
          queryMediaStore(ctx, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, map);

          recentGroupItem.setBrowseType(BROWSE_TYPE.PHOTO);
          recentGroupItem.strGroupDisplayName =
              ctx.getString(R.string.mn_gallery_recent_image_folder);
          recentGroupItem.mediaItemList = queryRecentMediaFile(ctx, BROWSE_TYPE.PHOTO);
          break;
        case VIDEO:
          queryMediaStore(ctx, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, map);

          recentGroupItem.setBrowseType(BROWSE_TYPE.VIDEO);
          recentGroupItem.strGroupDisplayName =
              ctx.getString(R.string.mn_gallery_recent_video_folder);
          recentGroupItem.mediaItemList = queryRecentMediaFile(ctx, BROWSE_TYPE.VIDEO);
          break;
        case AUDIO:
          queryMediaStore(ctx, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, map);
          break;
        default:
          break;
      }
    }

    sortItemInGroup(map);
    return true;
  }

  private String getMediaPath(String strFullName) {
    if (strFullName == null) {
      return "";
    }

    int nLayerAdd = strFullName.contains("/Android/data/") ? 1 : 0;
    ArrayList<String> pathList = new ArrayList<>();
    File file = new File(strFullName);
    while (file != null) {
      pathList.add(0, file.getName());
      file = file.getParentFile();
    }

    int nLayer = Math.min(mMaxLayerCount + nLayerAdd, pathList.size() - 1);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < nLayer; i++) {
      sb.append(pathList.get(i)).append("/");
    }

    return sb.toString();
  }

  public BROWSE_TYPE getBrowseType() {
    return mBrowseType;
  }

  public MediaGroupItem getRecentGroupItem() {
    return recentGroupItem;
  }

  private String getLastFolderName(String strFullName) {
    if (strFullName == null) {
      return "";
    }
    int nLastPath = strFullName.lastIndexOf("/");
    if (nLastPath == -1) {
      return "";
    }
    String strPath = strFullName.substring(0, nLastPath);
    int nLastPath2 = strPath.lastIndexOf("/");
    if (nLastPath2 == -1) {
      return strPath;
    }
    return strPath.substring(nLastPath2 + 1);
  }

  private MediaGroupItem searchGroupItem(Context context, Map<Long, MediaGroupItem> map,
      ExtMediaItem item) {
    if (map == null || item == null) {
      return null;
    }
    MediaGroupItem groupItem = null;
    long lTimestamp;
    if (mGroupType == GROUP_MEDIA_TYPE.GROUP_MEDIA_TYPE_TITLE) {
      groupItem = map.get(1L);
      if (groupItem == null) {
        groupItem = new MediaGroupItem();
        groupItem.lGroupTimestamp = 1L;
        groupItem.mediaItemList = new ArrayList<>();
        groupItem.strGroupDisplayName = "";
        map.put(groupItem.lGroupTimestamp, groupItem);
      }
    } else if (mGroupType == GROUP_MEDIA_TYPE.GROUP_MEDIA_TYPE_DATE) {
      lTimestamp = (item.date + TimeZone.getDefault().getRawOffset()) / ONE_DAY_IN_MILLSECONDS
          * ONE_DAY_IN_MILLSECONDS + 1;
      groupItem = map.get(lTimestamp);

      if (groupItem == null) {
        groupItem = new MediaGroupItem();
        groupItem.lGroupTimestamp = lTimestamp;
        groupItem.mediaItemList = new ArrayList<>();
        groupItem.strGroupDisplayName = new Date(lTimestamp).toString();
        map.put(groupItem.lGroupTimestamp, groupItem);
      }
    } else {
      String strMediaPath = getMediaPath(item.path);

      //merge All clips in XiaoYing/Videos
      if (strMediaPath.equals(mMainStorageXYMediaCameraPath) || strMediaPath.equals(
          mExtStorageXYMediaCameraPath)) {
        strMediaPath = mMainStorageXYMediaCameraPath;
      }

      if (map.size() > 0) {
        Entry<Long, MediaGroupItem> entry = null;
        for (Entry<Long, MediaGroupItem> longMediaGroupItemEntry : map.entrySet()) {
          entry = longMediaGroupItemEntry;
          MediaGroupItem groupItemTmp = entry.getValue();
          if (strMediaPath.compareToIgnoreCase(groupItemTmp.strParentPath) == 0) {
            groupItem = groupItemTmp;
            break;
          }
        }
      }

      if (groupItem == null) {
        groupItem = new MediaGroupItem();
        groupItem.mediaItemList = new ArrayList<>();
        groupItem.strParentPath = strMediaPath;
        String name = getFolderMapName(context, groupItem.strParentPath);
        groupItem.strGroupDisplayName =
            TextUtils.isEmpty(name) ? getLastFolderName(groupItem.strParentPath) : name;
        groupItem.lGroupTimestamp = map.size() + 1;
        map.put(groupItem.lGroupTimestamp, groupItem);
      }
    }
    return groupItem;
  }

  private String getFolderMapName(Context context, String path) {
    if (context == null) {
      return null;
    }
    Map<String, Integer> nameMap = MediaFolderNameMapUtils.getNameMap();
    Integer nameRes = null;
    if (nameMap != null) {
      nameRes = nameMap.get(path);
    }

    String name = null;
    try {
      if (nameRes != null) {
        name = context.getString(nameRes);
      }
    } catch (Exception e) {
    }

    return name;
  }

  private void initXiaoYingDataAndCameraPath() {
    if (mbGotXYMediaCameraPath) {
      return;
    }
    String strMainStorage = StorageInfo.getMainStorage();
    //String strExtStorage = StorageInfo.getExtStorage();
    //		String strXYCameraPath = CommonConfigure.APP_DATA_PATH_RELATIVE + CommonConfigure.APP_CAMERA_DATA_PATH_RELATIVE;
    String strXYCameraPath = MediaConfig.CAMERA_VIDEO_RELATIVE_PATH;

    mMainStorageXYMediaCameraPath =
        (new File(strMainStorage + File.separator + strXYCameraPath)).getAbsolutePath();
    if (!TextUtils.isEmpty(strMainStorage)) {
      mExtStorageXYMediaCameraPath =
          (new File(strMainStorage + File.separator + strXYCameraPath)).getAbsolutePath();
    }

    mbGotXYMediaCameraPath = true;
  }

  private void queryMediaStore(Context ctx, Uri uri, Map<Long, MediaGroupItem> map) {
    initXiaoYingDataAndCameraPath();

    Cursor cursor = getMediaStoreCursor(ctx, uri);
    ExtMediaItem item;

    MediaGroupItem groupItem;

    if (cursor != null) {
      while (cursor.moveToNext()) {
        item = getMediaItem(cursor, uri, getMediaType(uri));

        //because qq music add bad jpg file into media store, so we skip these files. cfchen@20130629
        if (!isValidItem(item, mBrowseType)) {
          continue;
        }

        groupItem = searchGroupItem(ctx, map, item);
        if (groupItem == null) {
          continue;
        }

        item.lGroupKey = groupItem.lGroupTimestamp;
        groupItem.add(item);
      }
      cursor.close();
    }
  }

  public synchronized void unInit() {
    if (mMediaGroupMap != null) {
      mMediaGroupMap.clear();
    }
  }

  public synchronized int getGroupCount() {
    return mMediaGroupMap == null ? 0 : mMediaGroupMap.size();
  }

  public synchronized int getSubGroupCount(int nGroupIndex) {
    if (mMediaGroupMap == null) {
      return 0;
    }
    MediaGroupItem item = getGroupItem(nGroupIndex);
    if (item == null) {
      return 0;
    }
    return item.mediaItemList == null ? 0 : item.mediaItemList.size();
  }

  private void makeGroupSortOrder() {
    if (mMediaGroupKeys == null || mMediaGroupKeys.length != mMediaGroupMap.size()) {
      //not change for group key
      Set<Long> set = mMediaGroupMap.keySet();
      Long[] lGroupArray = set.toArray(new Long[set.size()]);
      List<Long> listGroup = Arrays.asList(lGroupArray);

      //添加近期文件
      //if (GROUP_MEDIA_TYPE.GROUP_MEDIA_TYPE_FOLDER == mGroupType
      //    && recentGroupItem != null
      //    && recentGroupItem.mediaItemList != null
      //    && recentGroupItem.mediaItemList.size() > 0
      //    && !mMediaGroupMap.containsValue(recentGroupItem)) {
      //  long mapSize = mMediaGroupMap.size();
      //  mMediaGroupMap.put(mapSize + 1, recentGroupItem);
      //}

      Collections.sort(listGroup,
          new GroupComparator(mMediaGroupMap, mGroupType, mMainStorageXYMediaCameraPath));
      mMediaGroupKeys = listGroup.toArray(new Long[listGroup.size()]);
    }
  }

  public synchronized List<MediaGroupItem> getGroupItemList() {
    List<MediaGroupItem> list = new ArrayList<>();
    if (mMediaGroupMap != null && mMediaGroupMap.size() > 0) {
      makeGroupSortOrder();
      list.addAll(mMediaGroupMap.values());
    }

    return list;
  }

  public synchronized MediaGroupItem getGroupItem(int nGroupIdx) {
    if (mMediaGroupMap == null || nGroupIdx < 0 || nGroupIdx >= mMediaGroupMap.size()) {
      return null;
    }
    makeGroupSortOrder();

    return mMediaGroupMap.get(mMediaGroupKeys[nGroupIdx]);
  }

  private ArrayList<ExtMediaItem> queryRecentMediaFile(Context context, BROWSE_TYPE browseType) {
    Uri uri = getRecentUriByType(browseType);
    if (browseType == null || uri == null) {
      return null;
    }
    ArrayList<ExtMediaItem> recentFileList = new ArrayList<>();
    if (context != null) {
      ContentResolver cr = context.getContentResolver();
      String where = getQuerySelect(browseType);
      String[] projections = getRecentProjection(browseType);
      String sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " desc";
      Cursor cursor = null;
      try {
        cursor = cr.query(uri, projections, where, null, sortOrder);
        while (cursor != null && cursor.moveToNext()) {
          try {
            ExtMediaItem item = getMediaItem(cursor, uri,
                browseType == BROWSE_TYPE.VIDEO ? MediaType.MEDIA_TYPE_VIDEO
                    : MediaType.MEDIA_TYPE_IMAGE);
            if (isValidItem(item, browseType)) {
              recentFileList.add(item);
            }
          } catch (IllegalStateException ex) {
            //catch for
            // [Couldn't read row 5262, col 0 from CursorWindow.
            // Make sure the Cursor is initialized correctly before accessing data from it.]
            break;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (cursor != null) {
          cursor.close();
        }
      }
    }
    int count = recentFileList.size();
    if (BROWSE_TYPE.PHOTO == browseType) {
      TOTAL_COUNT_PHOTO = count;
    } else if (BROWSE_TYPE.VIDEO == browseType) {
      TOTAL_COUNT_VIDEO = count;
    }
    return recentFileList;
  }

  private boolean isValidItem(ExtMediaItem item, BROWSE_TYPE browseType) {
    boolean isInValid = (item == null) || TextUtils.isEmpty(item.path) || isGifInvalid(item.path)
        || (browseType != BROWSE_TYPE.AUDIO && item.path.contains("/qqmusic/"));

    return !isInValid;
  }

  private String[] getRecentProjection(BROWSE_TYPE browseType) {
    if (browseType == null) {
      return null;
    }
    String[] projections = null;

    switch (browseType) {
      case VIDEO: {
        projections = new String[] {
            MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.DURATION,
        };
        break;
      }
      case PHOTO: {
        projections = new String[] {
            MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_MODIFIED,
        };
        break;
      }
      default:
        break;
    }
    return projections;
  }

  private static Map<BROWSE_TYPE, MediaContentObserverHolder> CONTENT_OBSERVER_MAP =
      new HashMap<>();

  public void registerDataListener(Context context,
      MediaDataChangedListener mediaDataChangedListener) {
    if (context == null) {
      return;
    }
    for (BROWSE_TYPE browseType : new BROWSE_TYPE[] { BROWSE_TYPE.PHOTO, BROWSE_TYPE.VIDEO }) {
      Uri uri = getRecentUriByType(browseType);
      MediaContentObserverHolder holder = getContentObserverHolder(context, browseType);
      holder.mediaDataChangedListener = mediaDataChangedListener;
      context.getContentResolver().registerContentObserver(uri, false, holder.contentObserver);
    }
  }

  public void unregisterDataListener(Context context) {
    if (context == null) {
      return;
    }
    for (BROWSE_TYPE browseType : new BROWSE_TYPE[] { BROWSE_TYPE.PHOTO, BROWSE_TYPE.VIDEO }) {
      MediaContentObserverHolder holder = getContentObserverHolder(context, browseType);
      context.getContentResolver().unregisterContentObserver(holder.contentObserver);
      holder.mediaDataChangedListener = null;
    }
  }

  private MediaContentObserverHolder getContentObserverHolder(Context context,
      BROWSE_TYPE browseType) {
    MediaContentObserverHolder holder = CONTENT_OBSERVER_MAP.get(browseType);
    if (holder == null) {
      holder = new MediaContentObserverHolder(context);
      CONTENT_OBSERVER_MAP.put(browseType, holder);
    }
    return holder;
  }

  private static Uri getRecentUriByType(BROWSE_TYPE browseType) {
    if (browseType == null) {
      return null;
    }
    Uri uri = null;
    switch (browseType) {
      case PHOTO:
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        break;
      case VIDEO:
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        break;
      default:
        break;
    }
    return uri;
  }

  public interface MediaDataChangedListener {
    void onMediaDataChanged();
  }

  private class MediaContentObserverHolder {
    ContentObserver contentObserver;
    MediaDataChangedListener mediaDataChangedListener;

    public MediaContentObserverHolder(final Context context) {
      this.contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override public void onChange(boolean selfChange) {
          super.onChange(selfChange);
          if (recentGroupItem != null) {
            BROWSE_TYPE browseType = recentGroupItem.getBrowseType();
            recentGroupItem.mediaItemList =
                queryRecentMediaFile(context.getApplicationContext(), browseType);
            if (mediaDataChangedListener != null) {
              mediaDataChangedListener.onMediaDataChanged();
            }
          }
        }
      };
    }
  }

  private boolean isGifInvalid(String path) {
    return path.toLowerCase().contains(".gif") && !MediaConfig.GIF_AVAILABLE;
  }
}
