package com.quvideo.application.gallery.utils;

import android.content.Context;
import android.content.res.Resources;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.quvideo.application.editor.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GalleryUtil {

  private static final int DEFAULT_DOUBLE_CLICK_DURATION = 500;
  private static long lastClickTime = 0;

  public static void loadCover(Context context, ImageView imageView, String url,
      long frameTimeMicros) {
    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    Glide.with(context)
        .setDefaultRequestOptions(
            new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .frame(frameTimeMicros)
                .centerCrop())
        .load(url)
        .into(imageView);
  }

  public static void loadCover(Context context, ImageView imageView, int placeHolder,
      String url) {
    Glide.with(context)
        .setDefaultRequestOptions(new RequestOptions().placeholder(placeHolder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .centerCrop())
        .load(url)
        .into(imageView);
  }

  public static void loadCoverFitCenter(Context context, ImageView imageView, int placeHolder,
      String url) {
    Glide.with(context)
        .setDefaultRequestOptions(new RequestOptions().placeholder(placeHolder)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .fitCenter())
        .load(url)
        .into(imageView);
  }

  public static void loadImage(int width, int height, int placeHolder, String url,
      ImageView imageView) {
    RequestOptions requestOptions =
        new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(placeHolder)
            .override(width, height);

    Glide.with(imageView.getContext()).load(url).apply(requestOptions).into(imageView);
  }

  public static String getUnCutTextViewContent(String txt) {
    if (null == txt) {
      return "";
    }
    return txt + "\u00A0";
  }

  public static String getFormatDuration(long duration) {
    String durationStr = "";
    if (duration < 0) {
      duration = 0;
    }
    duration = (duration + 500) / 1000;
    try {
      if (duration >= 3600) {
        durationStr =
            String.format(Locale.US, "%02d:%02d:%02d", duration / 3600, (duration % 3600) / 60,
                duration % 60);
      } else {
        durationStr =
            String.format(Locale.US, "%2d:%02d", (duration % 3600) / 60, duration % 60);
      }
    } catch (Exception ignore) {
    }
    return durationStr.trim();
  }

  public static String getFormatSecondDuration(long duration){
    String durationStr;
    if(duration < 0){
      duration = 0;
    }
    StringBuilder stringBuilder = new StringBuilder();
    java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.0");
    durationStr = myformat.format(duration / 1000f);
    stringBuilder.append(durationStr);
    stringBuilder.append("s");
    return stringBuilder.toString();
  }


  public static String getDate(Context context, String pTime) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Resources resources = context.getResources();
    String strDay = "";

    Calendar calendar = Calendar.getInstance();
    int todayYear = calendar.get(Calendar.YEAR);
    int todayMonth = calendar.get(Calendar.MONTH);
    int todayDay = calendar.get(Calendar.DAY_OF_MONTH);

    calendar.add(Calendar.DAY_OF_MONTH, -1);
    int yesterdayYear = calendar.get(Calendar.YEAR);
    int yesterdayMonth = calendar.get(Calendar.MONTH);
    int yesterdayDay = calendar.get(Calendar.DAY_OF_MONTH);

    try {
      calendar.setTime(format.parse(pTime));
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return strDay;
    }

    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    //1970-1-10 1970-1-9
    if (year == todayYear && month == todayMonth && day == todayDay) {
      strDay = resources.getString(R.string.mn_gallery_date_today);
    } else if (year == yesterdayYear && month == yesterdayMonth && day == yesterdayDay) {
      strDay = resources.getString(R.string.mn_gallery_date_yesterday);
    } else {
      month++;
      String preMonth = month < 10 ? "0" : "";
      String preDay = day < 10 ? "0" : "";

      strDay = year + "." + preMonth + month + "." + preDay + day;
    }

    return strDay;
  }

  public static String getCommonIndex(int index) {
    String preIndex = index < 10 ? "0" : "";
    return preIndex + index;
  }

  public static boolean isFastDoubleClick() {
    return isFastDoubleClick(DEFAULT_DOUBLE_CLICK_DURATION);
  }

  /**
   * 避免快速点击
   */
  public static boolean isFastDoubleClick(int duration) {
    long time = System.currentTimeMillis();
    if (Math.abs(time - lastClickTime) < duration) {
      return true;
    }
    lastClickTime = time;
    return false;
  }
}
