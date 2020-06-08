package com.quvideo.application.utils.pop;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @desc Pop Animation  util
 * @since 2019/5/13
 */
public class AnimatorUtils {

  public static AnimatorSet getScaleAnimation(View view, float scale, int duration,
      TimeInterpolator timeInterpolator) {
    AnimatorSet animatorSet = new AnimatorSet();
    ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, scale);
    ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, scale);
    animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
    animatorSet.setDuration(duration);
    animatorSet.setInterpolator(timeInterpolator);
    return animatorSet;
  }

  public static AnimatorSet getScaleAnimation(View view, float scaleFrom, float scaleTo,
      int duration, TimeInterpolator timeInterpolator) {
    AnimatorSet animatorSet = new AnimatorSet();
    ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, scaleFrom, scaleTo);
    ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, scaleFrom, scaleTo);
    animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
    animatorSet.setDuration(duration);
    animatorSet.setInterpolator(timeInterpolator);
    return animatorSet;
  }

  public static AnimatorSet getScaleXAnimation(View view, float scaleFrom, float scaleTo,
      int duration, TimeInterpolator timeInterpolator) {
    AnimatorSet animatorSet = new AnimatorSet();
    ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, scaleFrom, scaleTo);
    animatorSet.playTogether(scaleXAnimator);
    animatorSet.setDuration(duration);
    animatorSet.setInterpolator(timeInterpolator);
    return animatorSet;
  }

  public static Animator getScaleAnimation(Object target, String scaleTag, float scaleFrom,
      float scaleTo, int duration, TimeInterpolator timeInterpolator) {
    ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(target, scaleTag, scaleFrom, scaleTo);
    scaleAnimator.setDuration(duration);
    scaleAnimator.setInterpolator(timeInterpolator);
    return scaleAnimator;
  }

  public static AnimatorSet getTranslationXAnimation(View view, float from, float to, int duration,
      TimeInterpolator timeInterpolator) {
    AnimatorSet animatorSet = new AnimatorSet();
    ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, from, to);
    animatorSet.playTogether(animator);
    animatorSet.setDuration(duration);
    animatorSet.setInterpolator(timeInterpolator);
    return animatorSet;
  }

  public static AnimatorSet getTranslationYAnimation(View view, float from, float to, int duration,
      TimeInterpolator timeInterpolator) {
    AnimatorSet animatorSet = new AnimatorSet();
    ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, from, to);
    animatorSet.playTogether(animator);
    animatorSet.setDuration(duration);
    animatorSet.setInterpolator(timeInterpolator);
    return animatorSet;
  }

  public static AnimatorSet getAlphaAnimation(View view, float from, float to, int duration,
      TimeInterpolator timeInterpolator) {
    AnimatorSet animatorSet = new AnimatorSet();
    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, from, to);
    animatorSet.playTogether(alphaAnimator);
    animatorSet.setDuration(duration);
    animatorSet.setInterpolator(timeInterpolator);
    return animatorSet;
  }
}
