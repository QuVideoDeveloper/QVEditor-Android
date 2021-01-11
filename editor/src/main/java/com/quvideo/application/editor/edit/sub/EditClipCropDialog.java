package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import androidx.appcompat.widget.AppCompatImageView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.ClipCropDraw;
import com.quvideo.application.utils.DeviceSizeUtil;
import com.quvideo.mobile.engine.QEThumbnailTools;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPCrop;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EditClipCropDialog extends BaseMenuView {

  private View btnZoomIn;
  private View btnZoomOut;

  private int clipIndex;

  private VeMSize clipSourceSize;

  private AppCompatImageView mCropImageView;

  private boolean isChanged = false;

  private Bitmap mBitmap;

  public EditClipCropDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int clipIndex, AppCompatImageView cropImageView, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    this.mCropImageView = cropImageView;
    clipSourceSize = mWorkSpace.getClipAPI().getClipByIndex(clipIndex).getSourceSize();
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipCrop;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_clip_crop;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    btnZoomIn = view.findViewById(R.id.btnZoomIn);
    btnZoomOut = view.findViewById(R.id.btnZoomOut);
    btnZoomIn.setEnabled(false);
    btnZoomOut.setEnabled(false);

    btnZoomIn.setOnClickListener(mOnClickListener);
    btnZoomOut.setOnClickListener(mOnClickListener);
    mCropImageView.setVisibility(VISIBLE);
    ClipData clipData = mWorkSpace.getClipAPI().getClipByIndex(clipIndex);
    Observable.just(true)
        .subscribeOn(Schedulers.newThread())
        .observeOn(Schedulers.newThread())
        .map(aBoolean -> {
          if (clipData.isVideo()) {
            int offset = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime() - clipData.destRange.getPosition();
            offset = Math.min(Math.max(offset, 0), clipData.destRange.getTimeLength());
            return QEThumbnailTools.getVideoThumbnail(clipData.getClipFilePath(),
                (int) DeviceSizeUtil.dpToPixel((float) clipData.sourceSize.width),
                (int) DeviceSizeUtil.dpToPixel((float) clipData.sourceSize.height),
                offset);
          } else {
            return QEThumbnailTools.getPicFileThumbnail(clipData.getClipFilePath(),
                (int) DeviceSizeUtil.dpToPixel((float) clipData.sourceSize.width),
                (int) DeviceSizeUtil.dpToPixel((float) clipData.sourceSize.height),
                0);
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new io.reactivex.Observer<Bitmap>() {
          @Override public void onSubscribe(Disposable d) {
          }

          @Override public void onNext(Bitmap bitmap) {
            mBitmap = bitmap;
            if (mBitmap != null) {
              mCropImageView.setImageBitmap(mBitmap);
              initFakeView();
              btnZoomIn.setEnabled(true);
              btnZoomOut.setEnabled(true);
            } else {
              dismissMenu();
            }
          }

          @Override public void onError(Throwable e) {
            dismissMenu();
          }

          @Override public void onComplete() {
          }
        });
  }

  private void initFakeView() {
    if (mFakeApi == null || clipSourceSize == null) {
      return;
    }
    mFakeApi.setStreamSize(clipSourceSize);
    Rect cropRect = mWorkSpace.getClipAPI().getClipByIndex(clipIndex).cropRect;
    if (cropRect == null) {
      cropRect = new Rect(0, 0, clipSourceSize.width, clipSourceSize.height);
    }
    mFakeApi.setClipTarget(new ClipCropDraw(), cropRect);
    mFakeApi.setFakeViewListener(new IFakeViewListener() {

      @Override public void onEffectMoving(float pointX, float pointY) {
        isChanged = true;
      }

      @Override public void onEffectMoveStart() {
      }

      @Override public void onEffectMoveEnd(boolean moved) {
      }

      @Override public void checkEffectTouchHit(PointF pointF) {
      }
    });
  }

  private OnClickListener mOnClickListener = new OnClickListener() {
    @Override public void onClick(View v) {
      if (v.equals(btnZoomIn)) {
        // TODO 放大Target区域
        isChanged = true;
        FakePosInfo fakePosInfo = mFakeApi.getFakePosInfo();
        fakePosInfo.setWidth(fakePosInfo.getWidth() * 1.1f);
        fakePosInfo.setHeight(fakePosInfo.getHeight() * 1.1f);
        Rect cropRect = new Rect((int) (fakePosInfo.getCenterX() - fakePosInfo.getWidth() / 2),
            (int) (fakePosInfo.getCenterY() - fakePosInfo.getHeight() / 2),
            (int) (fakePosInfo.getCenterX() + fakePosInfo.getWidth() / 2),
            (int) (fakePosInfo.getCenterY() + fakePosInfo.getHeight() / 2));
        mFakeApi.setClipTarget(new ClipCropDraw(), cropRect);
      } else if (v.equals(btnZoomOut)) {
        // TODO 缩小Target区域
        isChanged = true;
        FakePosInfo fakePosInfo = mFakeApi.getFakePosInfo();
        fakePosInfo.setWidth(fakePosInfo.getWidth() * 0.9f);
        fakePosInfo.setHeight(fakePosInfo.getHeight() * 0.9f);
        Rect cropRect = new Rect((int) (fakePosInfo.getCenterX() - fakePosInfo.getWidth() / 2),
            (int) (fakePosInfo.getCenterY() - fakePosInfo.getHeight() / 2),
            (int) (fakePosInfo.getCenterX() + fakePosInfo.getWidth() / 2),
            (int) (fakePosInfo.getCenterY() + fakePosInfo.getHeight() / 2));
        mFakeApi.setClipTarget(new ClipCropDraw(), cropRect);
      }
    }
  };

  @Override protected void releaseAll() {
    mCropImageView.setImageBitmap(null);
    mCropImageView.setVisibility(GONE);
    if (mBitmap != null && !mBitmap.isRecycled()) {
      mBitmap.recycle();
      mBitmap = null;
    }
  }

  @Override public void onClick(View v) {
    // TODO 确认了，处理裁剪
    if (isChanged) {
      FakePosInfo fakePosInfo = mFakeApi.getFakePosInfo();
      Rect cropRect = new Rect((int) (fakePosInfo.getCenterX() - fakePosInfo.getWidth() / 2),
          (int) (fakePosInfo.getCenterY() - fakePosInfo.getHeight() / 2),
          (int) (fakePosInfo.getCenterX() + fakePosInfo.getWidth() / 2),
          (int) (fakePosInfo.getCenterY() + fakePosInfo.getHeight() / 2));
      cropRect = getIntersectRect(cropRect, new Rect(0, 0, clipSourceSize.width, clipSourceSize.height));
      cropRect = getRelativeRect(cropRect, clipSourceSize);
      ClipOPCrop clipOPCrop = new ClipOPCrop(clipIndex, cropRect);
      mWorkSpace.handleOperation(clipOPCrop);
    }
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_crop);
  }

  private static Rect getIntersectRect(Rect first, Rect second) {
    Rect result = new Rect(Math.max(first.left, second.left),
        Math.max(first.top, second.top),
        Math.min(first.right, second.right),
        Math.min(first.bottom, second.bottom));
    if (result.left >= result.right || result.top >= result.bottom) {
      return null;
    }
    return result;
  }

  /**
   * streamSize转万分比尺寸
   */
  private static Rect getRelativeRect(Rect rtAbsolute, VeMSize frameSize) {
    if (rtAbsolute == null || frameSize == null || frameSize.width <= 0 || frameSize.height <= 0) {
      return null;
    }

    Rect rtTextBounds = new Rect();
    rtTextBounds.left = getScaleValue(rtAbsolute.left, frameSize.width);
    rtTextBounds.top = getScaleValue(rtAbsolute.top, frameSize.height);
    rtTextBounds.right = getScaleValue(rtAbsolute.right, frameSize.width);
    rtTextBounds.bottom = getScaleValue(rtAbsolute.bottom, frameSize.height);
    return rtTextBounds;
  }

  private static int getScaleValue(float lAbsoluteValue, int lFullSize) {
    if (lFullSize == 0) {
      return 0;
    }
    float fTemp = lAbsoluteValue * 10000f / lFullSize;
    return Math.round(fTemp);
  }
}
