package com.quvideo.application.utils.pop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @desc Pop Animation
 * @since 2019/5/13
 */
public class Pop {

  public static void show(View view) {
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(Arrays.<Animator>asList(
        AnimatorUtils.getScaleAnimation(view, 1.1f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1.2f, 70, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 0.85f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator())));
    animatorSet.start();
  }

  public static void showSameAnim(View view, int count, int totalTime) {
    if (count < 1) {
      return;
    }
    AnimatorSet animatorSet = new AnimatorSet();
    int precentTime = totalTime / 2;
    List<Animator> list = new ArrayList<>();
    for (int index = 0; index < count; index++) {
      list.add(AnimatorUtils.getScaleAnimation(view, 1.1f, precentTime, new AccelerateDecelerateInterpolator()));
      list.add(AnimatorUtils.getScaleAnimation(view, 1f, precentTime, new AccelerateDecelerateInterpolator()));
    }
    animatorSet.playSequentially(list);
    animatorSet.start();
  }

  public static void showSoftly(View view) {
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(Arrays.<Animator>asList(
        AnimatorUtils.getScaleAnimation(view, 1.1f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1.15f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator())));
    animatorSet.start();
  }

  public static void showDeepSoftly(View view) {
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(Arrays.<Animator>asList(
        AnimatorUtils.getScaleAnimation(view, 0.85f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 0.95f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 0.92f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator())));
    animatorSet.start();
  }

  public static void none2Show(View view) {
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(Arrays.<Animator>asList(
        AnimatorUtils.getScaleAnimation(view, 0f, 1f, 200, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1.1f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1.2f, 70, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 0.85f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator())));
    animatorSet.start();
  }

  public static void show(View view, final PopCallback callback) {
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(Arrays.<Animator>asList(
        AnimatorUtils.getScaleAnimation(view, 1.1f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1.2f, 70, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 0.85f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator())));
    animatorSet.addListener(new AnimatorListenerAdapter() {

      @Override public void onAnimationEnd(Animator animation) {
        if (callback != null) {
          callback.onFinish();
        }
      }
    });
    animatorSet.start();
  }

  public static void showQuietly(View view) {
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(Arrays.<Animator>asList(
        AnimatorUtils.getScaleAnimation(view, 0.92f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 0.88f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator())));
    animatorSet.start();
  }

  public static void raiseUp(View view, float from, float to, final PopCallback callback) {
    AnimatorSet animatorSet = AnimatorUtils.getTranslationYAnimation(view, from, to, 300,
        new AccelerateDecelerateInterpolator());
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        if (callback != null) {
          callback.onFinish();
        }
      }
    });
    animatorSet.start();
  }

  public static void fallDown(View view, float from, float to, final PopCallback callback) {
    AnimatorSet animatorSet = AnimatorUtils.getTranslationYAnimation(view, from, to, 300,
        new AccelerateDecelerateInterpolator());
    animatorSet.addListener(new AnimatorListenerAdapter() {

      @Override public void onAnimationEnd(Animator animation) {
        if (callback != null) {
          callback.onFinish();
        }
      }
    });
    animatorSet.start();
  }

  public static AnimatorSet getPopAnimation(View view) {
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(Arrays.<Animator>asList(
        AnimatorUtils.getScaleAnimation(view, 1.1f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1.2f, 70, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 0.85f, 50, new AccelerateDecelerateInterpolator()),
        AnimatorUtils.getScaleAnimation(view, 1f, 50, new AccelerateDecelerateInterpolator())));
    return animatorSet;
  }

  public static void alphaHide(View view, final PopCallback callback) {
    AnimatorSet animatorSet =
        AnimatorUtils.getAlphaAnimation(view, 1f, 0.f, 200, new AccelerateDecelerateInterpolator());
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {
        if (callback != null) {
          callback.onFinish();
        }
      }
    });
    animatorSet.start();
  }

  public static void alphaShow(View view) {
    AnimatorSet animatorSet =
        AnimatorUtils.getAlphaAnimation(view, 0f, 1.f, 200, new AccelerateDecelerateInterpolator());
    animatorSet.start();
  }
}
