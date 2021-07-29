package com.quvideo.application.gallery.magicindicator;

/**
 * 抽象的ViewPager导航器
 */
public interface IPagerNavigator {

  ///////////////////////// ViewPager的3个回调
  void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

  void onPageSelected(int position);

  void onPageScrollStateChanged(int state);
  /////////////////////////

  /**
   * 当IPagerNavigator被添加到MagicIndicator时调用
   */
  void onAttachToMagicIndicator();

  /**
   * 当IPagerNavigator从MagicIndicator上移除时调用
   */
  void onDetachFromMagicIndicator();

  /**
   * ViewPager内容改变时需要先调用此方法，自定义的IPagerNavigator应当遵守此约定
   */
  void notifyDataSetChanged();

  /**
   * 获取Viewpager indicator
   * @return indicator
   */
  IPagerIndicator getPagerIndicator();
}
