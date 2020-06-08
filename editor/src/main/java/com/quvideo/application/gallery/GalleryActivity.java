package com.quvideo.application.gallery;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.quvideo.application.editor.R;
import com.quvideo.application.utils.NotchUtil;

/**
 * @author Elijah <a href="https://github.com/liuzhonghu">Contact me.</a>
 * @desc Common Gallery activity
 * @since 8/29/2019
 */
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
