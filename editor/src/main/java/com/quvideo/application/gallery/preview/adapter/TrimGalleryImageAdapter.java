package com.quvideo.application.gallery.preview.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.quvideo.application.gallery.utils.GSizeUtil;
import com.quvideo.application.gallery.widget.trim.VeGallery;
import java.util.List;

/**
 * Create by zhengjunfei on 2019/9/17
 */
public class TrimGalleryImageAdapter extends BaseAdapter {
  private Context mContext;
  private int mItemWidth;
  private int mItemHeight;
  private int mTrimGalleryChildCount = 0;
  private List<Bitmap> mBitmapList;

  public TrimGalleryImageAdapter(Context c, int width, int height) {
    mContext = c;
    mItemWidth = width;
    mItemHeight = height;
  }

  public void setData(List<Bitmap> bitmapList) {
    this.mBitmapList = bitmapList;
  }

  @Override public int getCount() {
    return null == mBitmapList ? 0 : mBitmapList.size();
  }

  @Override public Object getItem(int position) {
    return position;
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    if (null == parent) {
      return null;
    }
    VeGallery gallery = (VeGallery) parent;
    int iStartindex = gallery.getFirstVisiblePosition();

    View view = gallery.getChildAt(position - iStartindex);
    if (view == null) {
      view = new ImageView(mContext);
    }
    updateTrimGalleryImage((ImageView) view, position);
    ((ImageView) view).setScaleType(ImageView.ScaleType.CENTER_CROP);
    view.setLayoutParams(new VeGallery.LayoutParams(mItemWidth, mItemHeight));
    view.setLongClickable(false);
    return view;
  }

  private int updateTrimGalleryImage(ImageView iv, int position) {
    if (iv == null) {
      return -1;
    }
    String strBitmapSeted = "true";
    Bitmap bmpOld = null;
    Bitmap bmp;
    if (iv.getDrawable() instanceof BitmapDrawable) {
      BitmapDrawable bmpDrawable = (BitmapDrawable) iv.getDrawable();
      if (bmpDrawable != null) {
        Bitmap bmpiv = bmpDrawable.getBitmap();
        if (bmpiv != null && !bmpiv.isRecycled()) {
          bmpOld = bmpiv;
          bmpOld.eraseColor(0);
        }
      }
    }

    Bitmap bmpTmpThumb = mBitmapList.get(position);
    int iGalleryCellWidth = GSizeUtil.getFitPxFromDp(iv.getContext(), 37.4f);
    int iGalleryCellHeight = bmpTmpThumb.getHeight() * iGalleryCellWidth / bmpTmpThumb.getWidth();

    iv.setTag(strBitmapSeted);

    if (bmpOld != null) {
      bmp = bmpOld;
    } else {
      bmp = Bitmap.createBitmap(iGalleryCellWidth, iGalleryCellHeight, Bitmap.Config.ARGB_8888);
      if (bmp == null) {
        return -1;
      }
    }

    Canvas cv = new Canvas(bmp);
    cv.save();
    if (bmpTmpThumb != null && !bmpTmpThumb.isRecycled()) {
      cv.drawBitmap(bmpTmpThumb, null, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()),
          new Paint());
    }

    cv.restore();
    if (bmpOld != null) {
      iv.invalidate();
    } else {
      iv.setImageBitmap(bmp);
      iv.invalidate();
    }
    return 0;
  }
}
