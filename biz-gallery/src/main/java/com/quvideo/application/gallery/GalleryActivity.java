package com.quvideo.application.gallery;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.quvideo.application.utils.NotchUtil;

public class GalleryActivity extends AppCompatActivity {

  Fragment mGalleryFragment;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (NotchUtil.isNotchDevice(getApplicationContext())) {
      setTheme(R.style.Theme_NoSplash);
    }
    setContentView(R.layout.gallery_main_activity);

    //add gallery content fragment
    mGalleryFragment = GalleryFragment.newInstance();
    this.getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.container, mGalleryFragment)
        .commitAllowingStateLoss();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (null != mGalleryFragment) {
      mGalleryFragment.onActivityResult(requestCode, resultCode, data);
    }
  }
}
