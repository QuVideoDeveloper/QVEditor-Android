package com.quvideo.application.gallery;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.quvideo.application.gallery.provider.IGalleryProvider;

public class GalleryClient {
  private static GalleryClient instance;
  private GallerySettings mGallerySettings;
  private IGalleryProvider mGalleryProvider;
  private String mCurrFolderDisplayName;

  private GalleryClient() {
    mGallerySettings = new GallerySettings.Builder().build();
    mGalleryProvider = new IGalleryProvider();
  }

  public static GalleryClient getInstance() {
    if (instance == null) {
      instance = new GalleryClient();
    }
    return instance;
  }

  public void initSetting(@NonNull GallerySettings settings) {
    this.mGallerySettings = settings;
  }

  public void initProvider(@Nullable IGalleryProvider provider) {
    if (provider == null) {
      provider = new IGalleryProvider();
    }
    this.mGalleryProvider = provider;
  }

  public GallerySettings getGallerySettings() {
    return mGallerySettings;
  }

  public IGalleryProvider getGalleryProvider() {
    return mGalleryProvider;
  }

  public String getCurrFolderDisplayName() {
    return mCurrFolderDisplayName;
  }

  public void setCurrFolderDisplayName(String currFolderDisplayName) {
    mCurrFolderDisplayName = currFolderDisplayName;
  }

  public void performLaunchGallery(Activity activity) {
    mGallerySettings.setOnlySupportFragment(false);
    Intent intent = new Intent(activity, GalleryActivity.class);
    activity.startActivity(intent);
  }

  public Fragment addGalleryFragment(FragmentActivity activity, @IdRes int containerViewId) {
    mGallerySettings.setOnlySupportFragment(true);
    GalleryFragment fragment = GalleryFragment.newInstance();
    activity.getSupportFragmentManager()
        .beginTransaction()
        .add(containerViewId, fragment)
        .commitAllowingStateLoss();
    return fragment;
  }

  public boolean isChinaArea() {
    String countryCode = mGallerySettings.getCountryCode();
    return TextUtils.equals(countryCode, "CN");
  }

  public void destory() {
    this.mGalleryProvider = null;
  }
}
