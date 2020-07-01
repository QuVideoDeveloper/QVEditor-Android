package com.quvideo.application.slide;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.quvideo.application.EditorConst;
import com.quvideo.application.editor.R;
import com.quvideo.application.export.ExportChooseDialog;
import com.quvideo.application.export.ExportDialog;
import com.quvideo.application.gallery.GalleryClient;
import com.quvideo.application.gallery.GallerySettings;
import com.quvideo.application.gallery.model.GalleryDef;
import com.quvideo.application.gallery.model.MediaModel;
import com.quvideo.application.gallery.provider.IGalleryProvider;
import com.quvideo.application.player.PlayerControllerView;
import com.quvideo.application.utils.FileUtils;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.QEEngineClient;
import com.quvideo.mobile.engine.error.SDKErrCode;
import com.quvideo.mobile.engine.model.export.ExportParams;
import com.quvideo.mobile.engine.player.EditorPlayerView;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.slide.ISlideWorkSpace;
import com.quvideo.mobile.engine.slide.QESlideShowResult;
import com.quvideo.mobile.engine.slide.QESlideWorkSpaceListener;
import com.quvideo.mobile.engine.slide.SlideInfo;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.slide.SlideOPMove;
import com.quvideo.mobile.engine.work.operate.slide.SlideOPReplace;
import java.util.ArrayList;

public class SlideShowActivity extends AppCompatActivity {

  private RecyclerView mRecyclerView;
  private Button btnExport;
  private ImageView btnBack;

  private PlayerControllerView mPlayerControllerView;

  private EditorPlayerView editorPlayerView;

  private ISlideWorkSpace mSlideWorkSpace;

  private SlideAdapter adapter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_slide_show);
    initView();
    ArrayList<String> albumChoose = getIntent().getStringArrayListExtra(EditorConst.INTENT_EXT_KEY_ALBUM);
    long themeId = getIntent().getLongExtra(EditorConst.INTENT_EXT_KEY_SLIDE_THEMEID, 0L);
    if (themeId == 0) {
      ToastUtils.show(this, R.string.mn_edit_tips_template_theme_error, Toast.LENGTH_LONG);
      finish();
      return;
    }
    QEEngineClient.createNewSlideProject(themeId, albumChoose, new QESlideWorkSpaceListener() {
      @Override public void onSuccess(ISlideWorkSpace workSpace) {
        mSlideWorkSpace = workSpace;
        mSlideWorkSpace.getPlayerAPI().bindPlayerView(editorPlayerView, 0);
        if (mPlayerControllerView != null) {
          mPlayerControllerView.setPlayerAPI(mSlideWorkSpace.getPlayerAPI());
        }
        initData();
        mSlideWorkSpace.addObserver(new BaseObserver() {
          @Override public void onChange(BaseOperate operate) {
            initData();
          }
        });
      }

      @Override public void onError(QESlideShowResult error) {
      }
    });
    btnBack.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    btnExport.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //boolean[] expDialogShowCfgs = new boolean[] { true, bShow720P, bShow1080PItem, false, false };
        ExportChooseDialog dialog = new ExportChooseDialog(SlideShowActivity.this);
        dialog.setOnDialogItemListener(new ExportChooseDialog.OnDialogItemListener() {
          @Override public void onItemClick(int expType) {
            Bitmap bitmap = mSlideWorkSpace.getProjectThumbnail();
            String thumbnail = "/sdcard/ExportTest/slide_test_thumbnail.jpg";
            FileUtils.saveBitmap(thumbnail, bitmap, 100);
            ExportDialog exportDialog = new ExportDialog();
            ExportParams exportParams = new ExportParams();
            exportParams.outputPath = "/sdcard/ExportTest/slideTest.mp4";
            exportParams.expType = expType;
            exportDialog.showExporting(SlideShowActivity.this, thumbnail, exportParams, mSlideWorkSpace);
          }
        });
        try {
          dialog.show();
        } catch (Exception ignore) {
        }
      }
    });
  }

  private void initView() {
    btnBack = findViewById(R.id.btn_back);
    btnExport = findViewById(R.id.btn_export);
    mPlayerControllerView = findViewById(R.id.edit_enter_play_controller);
    editorPlayerView = findViewById(R.id.editor_play_view);
    mRecyclerView = findViewById(R.id.edit_enter_recyclerview);

    mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    adapter = new SlideAdapter(this, new SlideAdapter.OnSlideClickListener() {
      @Override public void onClick(SlideInfo item) {
        if (mPlayerControllerView != null) {
          mPlayerControllerView.seekPlayer(item.previewPos);
        }
      }

      @Override public void onReplaceClick(SlideInfo item) {
        //moveSlideNode(item.index, item.index + 1);
        replaceSlideNode(item.index);
      }
    });
    mRecyclerView.setAdapter(adapter);
    ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    mRecyclerView.setHasFixedSize(true);
  }

  private void initData() {
    if (mSlideWorkSpace != null) {
      ArrayList<SlideInfo> slideInfos = mSlideWorkSpace.getSlideInfoList();
      if (adapter != null) {
        adapter.updateList(slideInfos);
      }
    }
  }

  /**
   * 交换顺序
   */
  public void moveSlideNode(int fromPos, int toPos) {
    SlideOPMove slideOPMove = new SlideOPMove(fromPos, toPos);
    mSlideWorkSpace.handleOperation(slideOPMove);
  }

  /**
   * 替换数据源
   */
  public void replaceSlideNode(int position) {
    //update settings
    GallerySettings settings = new GallerySettings.Builder()
        .minSelectCount(1)
        .maxSelectCount(1)
        .showMode(GalleryDef.MODE_PHOTO)
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
        if (mediaList != null && mediaList.size() > 0 && mSlideWorkSpace != null) {
          SlideOPReplace slideOPReplace = new SlideOPReplace(position, mediaList.get(0).getFilePath());
          mSlideWorkSpace.handleOperation(slideOPReplace);
        }
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    // TODO 释放掉播放器和workspace的绑定
    if (mSlideWorkSpace != null) {
      mSlideWorkSpace.destory();
      mSlideWorkSpace = null;
    }
    if (editorPlayerView != null) {
      editorPlayerView = null;
    }
  }
}
