package com.quvideo.application.gallery.preview.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import com.quvideo.application.editor.R;

public class AnimUtil {

  public static void topViewAnim(View view, boolean bShow) {
    if (null == view) {
      return;
    }
    if (bShow) {
      if (view.getVisibility() == View.VISIBLE) {
        return;
      }
      // show view.
      TranslateAnimation showAnim =
          new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
              0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
      showAnim.setStartOffset(40);
      showAnim.setDuration(300);
      view.startAnimation(showAnim);
      view.setVisibility(View.VISIBLE);
    } else {
      if (view.getVisibility() != View.VISIBLE) {
        return;
      }
      // hide view.
      TranslateAnimation hideAnim =
          new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
              0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
      hideAnim.setStartOffset(40);
      hideAnim.setDuration(300);
      view.setVisibility(View.INVISIBLE);
    }
  }

  public static void bottomViewAnim(View view, boolean bShow) {
    if (null == view) {
      return;
    }
    if (bShow) {
      if (view.getVisibility() == View.VISIBLE) {
        return;
      }
      // show view.
      Animation mBottomShowAnim =
          AnimationUtils.loadAnimation(view.getContext(), R.anim.gallery_cropper_slide_out_down_self);
      mBottomShowAnim.setStartOffset(40);
      view.startAnimation(mBottomShowAnim);
      view.setVisibility(View.VISIBLE);
    } else {
      if (view.getVisibility() != View.VISIBLE) {
        return;
      }
      // hide view.
      Animation mBottomHideAnim =
          AnimationUtils.loadAnimation(view.getContext(), R.anim.gallery_cropper_slide_in_down_self);
      mBottomHideAnim.setStartOffset(40);
      view.startAnimation(mBottomHideAnim);
      view.setVisibility(View.INVISIBLE);
    }
  }
}

