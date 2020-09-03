package com.quvideo.application;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.quvideo.application.camera.CameraActivity;
import com.quvideo.application.draft.DraftActivity;
import com.quvideo.application.editor.EditorActivity;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.permission.PermissionHelper;
import com.quvideo.application.permission.PermissionProxyActivity;
import com.quvideo.application.slide.SlideTemplateDialog;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.mobile.engine.error.SDKErrCode;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  public static final String INTENT_EXT_KEY_ALBUM = "intentAlbumChoose";
  public static final String INTENT_EXT_KEY_SLIDE_THEMEID = "intentSlideThemeId";
  private AlertDialog alertDialog;
  private AlertDialog mDialog;

  private SlideTemplateDialog dialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    requestPermission();
  }

  public void gotoCamera(View view) {
    startActivity(new Intent(this, CameraActivity.class));
  }

  public void gotoTemplate(View view) {
    List<SimpleTemplate> slideTemplates = new ArrayList<>();
    for (SimpleTemplate item : AssetConstants.TEST_SLIDE_THEME_TID) {
      slideTemplates.add(item);
    }
    dialog = new SlideTemplateDialog();
    dialog.setTemplates(slideTemplates);
    dialog.show(getSupportFragmentManager(), "simpleTemplate");
  }

  public void gotoDraft(View view) {
    Intent intent = new Intent(this, DraftActivity.class);
    startActivity(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (dialog != null) {
      dialog.onActivityResult(requestCode, resultCode, data);
    }
  }

  public void gotoEdit(View view) {
    //update settings
    GallerySettings settings = new GallerySettings.Builder()
        .minSelectCount(1)
        .maxSelectCount(-1)
        .showMode(GalleryDef.MODE_BOTH)
        .build();

    GalleryClient.getInstance().initSetting(settings);
    //enter gallery
    GalleryClient.getInstance().performLaunchGallery(this);

    GalleryClient.getInstance().initProvider(new IGalleryProvider() {
      @Override
      public boolean checkFileEditAble(String filePath) {
        int res = MediaFileUtils.checkFileEditAble(filePath);
        return res == SDKErrCode.RESULT_OK;
      }

      @Override
      public void onGalleryFileDone(ArrayList<MediaModel> mediaList) {
        super.onGalleryFileDone(mediaList);
        ArrayList<String> albumChoose = new ArrayList<>();
        if (mediaList != null && mediaList.size() > 0) {
          for (MediaModel item : mediaList) {
            albumChoose.add(item.getFilePath());
          }
        }
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra(INTENT_EXT_KEY_ALBUM, albumChoose);
        startActivity(intent);
      }
    });
  }

  private void requestPermission() {
    if (!PermissionHelper.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
      if (PermissionHelper.targetSdkVersionBelowAndroidM(this)) {
        //targetSdkVersion以下版本，暂时失效。
        return;
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        String[] deniedPermissions = PermissionHelper.getDeniedPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE);
        PermissionHelper.startPermissionProxyActivity(this, deniedPermissions,
            PermissionProxyActivity.MODE_RATIONALE, new PermissionProxyActivity.PermissionListener() {
              @Override public void onPermissionGrant() {
                Toast.makeText(MainActivity.this, "权限申请成功", Toast.LENGTH_SHORT).show();
              }

              @Override public void onPermissionDenied(List<String> deniedList) {
                finish();
              }

              @Override public void onNeverAskAgain() {

              }

              @Override public void onAlwaysDenied() {

              }

              @Override public void onPermissionRationaleResult() {

              }
            });
      }
    }
  }
}


