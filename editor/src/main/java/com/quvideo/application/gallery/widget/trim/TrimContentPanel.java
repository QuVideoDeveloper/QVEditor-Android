package com.quvideo.application.gallery.widget.trim;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;
import com.quvideo.application.editor.R;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.preview.adapter.TrimGalleryImageAdapter;
import com.quvideo.application.gallery.preview.utils.TimeUtil;
import com.quvideo.application.gallery.utils.GSizeUtil;
import com.quvideo.application.utils.ToastUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Create by zhengjunfei on 2019/9/16
 */
public class TrimContentPanel {
  public static final int DEFAULT_PERFRAMEINTERVAL = 500;
  public static final int DEFAULT_SCALE_INTERVAL = 500;
  private ViewGroup mPanelLayout;

  private TextView mLeftTrimTimeTextView;
  private TextView mRightTrimTimeTextView;
  private VeAdvanceTrimGallery mTrimGallery;
  private OnSeekListener mOnSeekListener;
  private OnTrimListener mOnTrimListener;

  private volatile boolean mTrimGalleryFirstLayout, bVideoThumbNerverDecode = true;

  private int mPreChildDuration = DEFAULT_PERFRAMEINTERVAL;
  private int mTrimChildIndex;
  private boolean isPanelLoaded;
  public int minTrimInterval = 500;
  private List<Bitmap> mBitmapList;
  private MediaModel mMediaModel;

  /**
   * 不能操作的宽度
   */
  private int notAvailableWidth = 0;
  /**
   * 最长截取长度  在gallery可拖动时使用
   * 标识timeLine是否支持拖动
   */
  private int mLimitDuration = 0;

  public TrimContentPanel(ViewGroup rootlayout, int position) {
    mPanelLayout = rootlayout;
    mTrimChildIndex = position;
    isPanelLoaded = false;
  }

  public void setOnTrimListener(OnTrimListener mOnTrimListener) {
    this.mOnTrimListener = mOnTrimListener;
  }

  public void loadPanel() {
    initUI();
    mTrimGallery.setClipIndex(mTrimChildIndex);
    mTrimGallery.setMbDragSatus(0);
    ////初始化设定为focus左侧
    mTrimGallery.setLeftDraging(true);
    Context ctx = mPanelLayout.getContext();
    Resources resources = mTrimGallery.getResources();
    int mItemWidth = (int) resources.getDimension(R.dimen.d_52dp);
    int baseCount = getItemBaseCount(ctx, mItemWidth);
    if (null != mMediaModel) {
      mPreChildDuration = getPerChildDuration((int) mMediaModel.getDuration(), baseCount, 0);
    }

    updateTrimTimeView();
    getKeyFrameBitmapList(ctx, baseCount, mItemWidth, mItemWidth);
    VeAdvanceTrimGallery.MIN_TRIM_INTERVAL = minTrimInterval;
    isPanelLoaded = true;
  }

  public void initUI() {
    if (mPanelLayout != null) {
      mTrimGallery = mPanelLayout.findViewById(R.id.video_trim_tool);
      mTrimGallery.setVisibility(View.VISIBLE);
      enableTrimGalleryLayout(true);
      mTrimGalleryFirstLayout = true;
      mLeftTrimTimeTextView = mPanelLayout.findViewById(R.id.video_trim_left_time);
      mRightTrimTimeTextView = mPanelLayout.findViewById(R.id.video_trim_right_time);
    }
  }

  public void initGallery(Context context, int itemShowWidth, int itemShowHeight) {
    TrimGalleryImageAdapter mAdapter =
        new TrimGalleryImageAdapter(mTrimGallery.getContext(), itemShowWidth, itemShowHeight);
    mAdapter.setData(mBitmapList);

    mTrimGalleryFirstLayout = true;

    Resources res = context.getResources();
    Drawable trimLeftSlideNormal = res.getDrawable(R.drawable.gallery_media_trim_left_icon);
    Drawable trimRightSlideNormal = res.getDrawable(R.drawable.gallery_media_trim_right_icon);
    Drawable trimCurNeedle = res.getDrawable(R.drawable.gallery_media_timeline_currtime_icon);

    Drawable trimMask = res.getDrawable(R.color.transparent);
    Drawable trimCntDis = res.getDrawable(R.color.transparent);
    int intrinsicWidth = trimLeftSlideNormal.getIntrinsicWidth();

    mTrimGallery.setGravity(Gravity.CENTER_VERTICAL);
    mTrimGallery.setSpacing(0);
    mTrimGallery.setMbDragSatus(0);
    mTrimGallery.setLeftDraging(true);
    mTrimGallery.setClipDuration((int) mMediaModel.getDuration());
    mTrimGallery.setPerChildDuration(mPreChildDuration);
    mTrimGallery.setmDrawableLeftTrimBarDis(trimLeftSlideNormal);
    mTrimGallery.setmDrawableRightTrimBarDis(trimRightSlideNormal);
    mTrimGallery.setmDrawableTrimContentDis(trimCntDis);
    mTrimGallery.setLeftTrimBarDrawable(trimLeftSlideNormal, trimLeftSlideNormal);
    mTrimGallery.setRightTrimBarDrawable(trimRightSlideNormal, trimRightSlideNormal);
    mTrimGallery.setChildWidth(itemShowWidth);
    mTrimGallery.setmDrawableTrimContent(trimMask);
    mTrimGallery.setDrawableCurTimeNeedle(trimCurNeedle);
    mTrimGallery.setCenterAlign(false);
    mTrimGallery.setParentViewOffset(intrinsicWidth / 2);
    mTrimGallery.isAllowedIdlySpaceOnEnds(false);
    mTrimGallery.setAdapter(mAdapter);
    if (isScrollEnable()) {
      mTrimGallery.setLimitMoveOffset(trimLeftSlideNormal.getIntrinsicWidth(),
          -trimLeftSlideNormal.getIntrinsicWidth());
      mTrimGallery.setSelectionInfoOnLayout(0, trimLeftSlideNormal.getIntrinsicWidth());
      //考虑复用以前的属性
      mTrimGallery.setMinLeftPos(trimLeftSlideNormal.getIntrinsicWidth());
      mTrimGallery.setMaxRightPos(
          GSizeUtil.getsScreenWidth(context) - trimLeftSlideNormal.getIntrinsicWidth());
    } else {
      mTrimGallery.setLimitMoveOffset(30, -20);
    }
    if (null != mMediaModel && null != mMediaModel.getRangeInFile()) {
      mTrimGallery.setTrimLeftValue(mMediaModel.getRangeInFile().getLeftValue());
      mTrimGallery.setTrimRightValue(mMediaModel.getRangeInFile().getRightValue());
    }

    mTrimGallery.setOnLayoutListener(mTrimGalleryLayoutListener);
    mTrimGallery.setOnGalleryOperationListener(mTrimGalleryOperationlistener);
    mTrimGallery.setOnTrimGalleryListener(mOnTrimGalleryListener);
    mTrimGallery.enableTouchEvent(false);
  }

  /**
   * Trim 模式下更新时间
   */
  private void updateTrimTimeView() {
    if (mMediaModel == null || null == mMediaModel.getRangeInFile()) {
      return;
    }

    int leftValue = mMediaModel.getRangeInFile().getLeftValue();
    int rightValue = mMediaModel.getRangeInFile().getRightValue();

    String formatLeftMessage = TimeUtil.getFloatFormatDuration(leftValue);
    String formatRightMessage = TimeUtil.getFloatFormatDuration(rightValue);

    mTrimGallery.setLeftMessage(formatLeftMessage);
    mTrimGallery.setRightMessage(formatRightMessage);
    mRightTrimTimeTextView.setText(TimeUtil.getFloatFormatDuration(rightValue - leftValue));

    mLeftTrimTimeTextView.setVisibility(View.GONE);
    mRightTrimTimeTextView.setVisibility(View.VISIBLE);
  }

  private int getPerChildDuration(int clipDuration, int baseItemCount, int limitDuration) {
    int preChildDuration = 0;
    if (baseItemCount > 0) {
      if (limitDuration > 0
          && limitDuration < clipDuration
          && limitDuration / baseItemCount > 0) {
        preChildDuration = limitDuration / baseItemCount;
      } else {
        preChildDuration = clipDuration / baseItemCount;
      }
    } else {
      if (clipDuration >= DEFAULT_SCALE_INTERVAL) {
        preChildDuration = DEFAULT_SCALE_INTERVAL;
      } else {
        preChildDuration = clipDuration;
      }
    }

    return preChildDuration;
  }

  private int getAvailableWidth(Context ctx) {
    return GSizeUtil.getsScreenWidth(ctx) - notAvailableWidth;
  }

  private int getItemBaseCount(Context ctx, int itemShowWidth) {
    int width = getAvailableWidth(ctx);
    int baseCount = width / itemShowWidth;
    int modWidth = width % itemShowWidth;
    final int twoDragBarWidth = GSizeUtil.getFitPxFromDp(ctx, 40f);
    if (modWidth < twoDragBarWidth) {
      baseCount--;
    }
    return baseCount;
  }

  private void enableTrimGalleryLayout(boolean enable) {
    mTrimGallery.enableLayout(enable);
    mTrimGallery.blockLayoutRequests(!enable);
  }

  public void setMinTrimInterval(int minTrimInterval) {
    this.minTrimInterval = minTrimInterval;
  }

  public void setVideoPath(MediaModel mediaModel) {
    this.mMediaModel = mediaModel;
  }

  public void setNotAvailableWidth(int notAvailableWidth) {
    this.notAvailableWidth = notAvailableWidth;
  }

  public void setOnSeekListener(OnSeekListener mOnSeekListener) {
    this.mOnSeekListener = mOnSeekListener;
  }

  /**
   * false 变成有trimbar的状态，关闭播放needle状态
   * true 播放状态
   */
  public void setPlaying(boolean bPlaying) {
    if (mTrimGallery != null) {
      mTrimGallery.setPlaying(bPlaying);
    }
  }

  /**
   * 获取播放状态
   */
  public boolean isPlaying() {
    return null != mTrimGallery && mTrimGallery.isPlaying();
  }

  /**
   * 更新时间线当前指针位置
   */
  public void updateCurrentPlayCursor(int time) {
    setCurPlayPos(time);
  }

  public void setCurPlayPos(int time) {
    if (mTrimGallery != null) {
      mTrimGallery.setCurPlayPos(time);
    }
  }

  public MediaModel getMediaModel() {
    return mMediaModel;
  }

  /**
   * @return timeLine是否可拖动
   */
  public boolean isScrollEnable() {
    return mLimitDuration > 0;
  }

  private void getKeyFrameBitmapList(Context ctx, int baseCount, int itemWidth, int itemHeight) {
    Observable.just(true)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .map(new Function<Boolean, List<Bitmap>>() {
          @Override public List<Bitmap> apply(Boolean aBoolean) {
            return getKeyFrameBitmap(baseCount);
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Bitmap>>() {
          @Override public void onSubscribe(Disposable d) {
          }

          @Override public void onNext(List<Bitmap> bitmaps) {
            mBitmapList = bitmaps;
            initGallery(ctx, itemWidth, itemHeight);
          }

          @Override public void onError(Throwable e) {

          }

          @Override public void onComplete() {

          }
        });
  }

  private List<Bitmap> getKeyFrameBitmap(int length) {
    List<Bitmap> bitmapList = new ArrayList<>();
    if (null == mMediaModel) {
      return bitmapList;
    }
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    String videoPath = mMediaModel.getFilePath();
    try {
      if (videoPath.startsWith("http://")
          || videoPath.startsWith("https://")
          || videoPath.startsWith("widevine://")) {
        retriever.setDataSource(videoPath, new Hashtable<String, String>());
      } else {
        retriever.setDataSource(videoPath);
      }
      long timeS = mMediaModel.getDuration() / length;
      for (int i = 0; i < length; i++) {
        Bitmap bitmap = retriever.getFrameAtTime(i * timeS * 1000,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC); //retriever.getFrameAtTime(-1);
        bitmapList.add(bitmap);
      }
    } catch (IllegalArgumentException ex) {
      // Assume this is a corrupt video file
      ex.printStackTrace();
    } catch (RuntimeException ex) {
      // Assume this is a corrupt video file.
      ex.printStackTrace();
    } finally {
      try {
        retriever.release();
      } catch (RuntimeException ex) {
        // Ignore failures while cleaning up.
        ex.printStackTrace();
      }
    }

    return bitmapList;
  }

  private VeGallery.OnLayoutListener mTrimGalleryLayoutListener =
      new VeGallery.OnLayoutListener() {
        @Override public void onLayout(View view) {
          if (view == null || mBitmapList == null) {
            return;
          }

          VeGallery gallery = (VeGallery) view;
          int firstIndex = gallery.getFirstVisiblePosition();
          int lastIndex = gallery.getLastVisiblePosition();
          if (isScrollEnable()) {
            //timeLine支持拖动 则从0开始到最后都加载thumb
            //mTrimManager.getmThumbManagerList()
            //    .setCurIdentifierBound(0,
            //        mTrimManager.getMiIdentifierStep() * mTrimGallery.getCount());
          } else {
            //mTrimManager.getmThumbManagerList()
            //    .setCurIdentifierBound(firstIndex * mTrimManager.getMiIdentifierStep(),
            //        lastIndex * mTrimManager.getMiIdentifierStep());
          }
          if (!mTrimGalleryFirstLayout) {
            enableTrimGalleryLayout(false);
            return;
          }

          mTrimGalleryFirstLayout = false;
          for (int i = firstIndex; i <= lastIndex; i++) {
            View child = gallery.getChildAt(i - firstIndex);
            if (child != null) {
              float fromXDelta = -child.getLeft();
              float toXDelta = 0;
              float fromYDelta = 0;
              float toYDelta = 0;

              TranslateAnimation transAni =
                  new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
              transAni.setDuration(500);
              child.startAnimation(transAni);
              if (i == firstIndex) {
                transAni.setAnimationListener(mTrimGalleryAttachAnimationlistener);
              }
            }
          }
        }
      };

  private final VeGallery.OnGalleryOperationListener mTrimGalleryOperationlistener =
      new VeGallery.OnGalleryOperationListener() {

        @Override public void onChildReLocation(View child, int fromX, int toX, int type) {

        }

        @Override public void onDown() {
        }

        @Override public void onEmptyAreaClick() {
        }

        @Override public void onMoveStart(View view) {
        }

        @Override public void onMoveStoped(View view) {
          if (mTrimGallery != null) {
            //当滑动停下来时，leftValue、rightValue都需要进行更新；因为firstIndex变了
            //因为滑动不改变左右bar实际距离，这里不进行设置value检测最小值
            int leftValue = mTrimGallery.getTrimValueByPosition(mTrimGallery.getmTrimLeftPos(),
                mTrimGallery.getCount());
            int rightValue = mTrimGallery.getTrimValueByPosition(mTrimGallery.getmTrimRightPos(),
                mTrimGallery.getCount());
            mTrimGallery.setTrimLeftValueWithoutLimitDetect(leftValue);
            mTrimGallery.setTrimRightValueWithoutLimitDetect(rightValue);
            mMediaModel.getRangeInFile().setLeftValue(leftValue);
            mMediaModel.getRangeInFile().setRightValue(rightValue);
          }
        }

        @Override public void onMoving(View view, int movedistance) {
        }

        @Override public void onUp() {
        }
      };

  private final VeAdvanceTrimGallery.OnTrimGalleryListener mOnTrimGalleryListener =
      new VeAdvanceTrimGallery.OnTrimGalleryListener() {

        @Override public boolean onDispatchKeyDown(int keyCode, KeyEvent event) {
          return false;
        }

        @Override public boolean onDispatchKeyUp(int keyCode, KeyEvent event) {
          return false;
        }

        @Override public void onTrimAnimationEnd(boolean isShowAnimation) {
        }

        @Override public void onTrimEnd(int clipIndex, boolean isLeftTrim, int trimPosition) {
          if (null == mMediaModel || null == mMediaModel.getRangeInFile()) {
            return;
          }
          if (isLeftTrim) {
            mMediaModel.getRangeInFile().setLeftValue(trimPosition);
          } else {
            mMediaModel.getRangeInFile().setRightValue(trimPosition);
          }

          if (isLeftTrim) {
            mTrimGallery.setTrimLeftValue(trimPosition);
          } else {
            mTrimGallery.setTrimRightValue(trimPosition);
          }
          updateTrimTimeView();

          if (mOnTrimListener != null) {
            mOnTrimListener.onTrimEnd(isLeftTrim, trimPosition);
          }
        }

        @Override
        public void onTrimPosChanged(int clipIndex, boolean isLeftTrim, int trimPosition) {
          if (mOnTrimListener != null) {
            mOnTrimListener.onTrimPosChange(trimPosition);
          }
          if (null == mMediaModel || null == mMediaModel.getRangeInFile()) {
            return;
          }
          if (isLeftTrim) {
            mMediaModel.getRangeInFile().setLeftValue(trimPosition);
          } else {
            mMediaModel.getRangeInFile().setRightValue(trimPosition);
          }

          updateTrimTimeView();
          setCurPlayPos(trimPosition);
        }

        @Override public void onTrimStart(int clipIndex, boolean isLeftTrim, int trimPosition) {
          if (mOnTrimListener != null) {
            mOnTrimListener.onTrimStart(isLeftTrim);
          }
        }

        @Override public boolean onAttainLimit() {
          if (isPanelLoaded) {
            ToastUtils.show(mPanelLayout.getContext(), R.string.mn_gallery_mini_mum_txt,
                Toast.LENGTH_SHORT);
          }
          return false;
        }

        @Override public void onSeekStart(int seekTime) {
          if (mOnSeekListener != null) {
            mOnSeekListener.onSeekStart();
          }
        }

        @Override public void onSeekPosChange(int progress) {
          if (mOnSeekListener != null) {
            mOnSeekListener.onSeekPosChange(progress);
          }
          updateCurrentPlayCursor(progress);
        }

        @Override public void onSeekEnd(int trimPosition) {
          if (mOnSeekListener != null) {
            mOnSeekListener.onSeekEnd(trimPosition);
          }
        }
      };

  private Animation.AnimationListener mTrimGalleryAttachAnimationlistener =
      new Animation.AnimationListener() {

        @Override public void onAnimationEnd(Animation animation) {
          if (mTrimGallery != null) {
            mTrimGallery.isShowTrimInfo(true, true);
            mTrimGallery.enableTouchEvent(true);

            enableTrimGalleryLayout(false);
          }
        }

        @Override public void onAnimationRepeat(Animation animation) {
        }

        @Override public void onAnimationStart(Animation animation) {
        }
      };

  /**
   * Range条的拖动
   */
  public interface OnTrimListener {
    /**
     * 按下trim bar的回调
     *
     * @param isLeft 按的是左边还是右边
     */
    void onTrimStart(boolean isLeft);

    /**
     * 按下trim bar 拖动过程中的进度回调(ms)
     *
     * @param position ms
     */
    void onTrimPosChange(int position);

    /**
     * 抬手之后的回调
     */
    void onTrimEnd(boolean isLeftTrim, int position);
  }

  /**
   * seek timeline的进度
   */
  public interface OnSeekListener {

    void onSeekStart();

    void onSeekPosChange(int progress);

    void onSeekEnd(int destTime);
  }
}
