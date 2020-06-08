package com.quvideo.application.gallery.magicindicator;

import androidx.viewpager.widget.ViewPager;

/**
 * 简化和ViewPager绑定
 */

public class ViewPagerHelper {

  public static void bindWithCallback(final MagicIndicator magicIndicator, ViewPager viewPager,
      final ViewPager.OnPageChangeListener changeListener) {
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        if (changeListener != null) {
          changeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
      }

      @Override public void onPageSelected(int position) {
        magicIndicator.onPageSelected(position);
        if (changeListener != null) {
          changeListener.onPageSelected(position);
        }
      }

      @Override public void onPageScrollStateChanged(int state) {
        magicIndicator.onPageScrollStateChanged(state);
        if (changeListener != null) {
          changeListener.onPageScrollStateChanged(state);
        }
      }
    });
  }
}
