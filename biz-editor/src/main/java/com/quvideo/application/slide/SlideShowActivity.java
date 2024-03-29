package com.quvideo.application.slide;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.quvideo.application.EditorConst;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.FakePosUtils;
import com.quvideo.application.editor.fake.FakeView;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.PosDraw;
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
import com.quvideo.application.widget.sort.CusSortRecycler;
import com.quvideo.application.widget.sort.ItemDragHelperCallback;
import com.quvideo.mobile.engine.QEEngineClient;
import com.quvideo.mobile.engine.error.SDKErrCode;
import com.quvideo.mobile.engine.model.export.ExportParams;
import com.quvideo.mobile.engine.player.EditorPlayerView;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.slide.ISlideWorkSpace;
import com.quvideo.mobile.engine.slide.QESlideShowResult;
import com.quvideo.mobile.engine.slide.QESlideWorkSpaceListener;
import com.quvideo.mobile.engine.slide.SlideInfo;
import com.quvideo.mobile.engine.slide.SlidePosInfo;
import com.quvideo.mobile.engine.utils.MediaFileUtils;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.slide.SlideOPMove;
import com.quvideo.mobile.engine.work.operate.slide.SlideOPPosition;
import com.quvideo.mobile.engine.work.operate.slide.SlideOPReplace;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SlideShowActivity extends AppCompatActivity {

  private CusSortRecycler mRecyclerView;
  private Button btnExport;
  private ImageView btnBack;

  private PlayerControllerView mPlayerControllerView;

  private EditorPlayerView editorPlayerView;

  private ISlideWorkSpace mSlideWorkSpace;

  private SlideAdapter adapter;

  private FakeView mFakeView;

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
        mSlideWorkSpace.getPlayerAPI().registerListener(mPlayerListener);
        if (mFakeView != null) {
          mFakeView.setStreamSize(mSlideWorkSpace.getSlideStreamSize());
        }
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

          @Override public void onConfirmExport(ExportParams exportParams) {
            String thumbnail = FileUtils.getFileParentPath(exportParams.outputPath)
                + FileUtils.getFileName(exportParams.outputPath) + "_thumbnail.jpg";
            Observable.create((ObservableOnSubscribe<Boolean>) emitter -> emitter.onNext(true))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                  @Override public void onSubscribe(Disposable d) {
                  }

                  @Override public void onNext(Boolean result) {
                    Bitmap bitmap = mSlideWorkSpace.getProjectThumbnail();
                    if (Build.VERSION.SDK_INT < 29 || Environment.isExternalStorageLegacy()) {
                      FileUtils.saveBitmap(thumbnail, bitmap, 80);
                    } else {
                      ContentValues contentValues = new ContentValues();
                      contentValues.put(MediaStore.Downloads.DATE_TAKEN, 0);
                      contentValues.put(MediaStore.Downloads.DISPLAY_NAME, FileUtils.getFileNameWithExt(thumbnail));
                      contentValues.put(MediaStore.Downloads.TITLE, FileUtils.getFileNameWithExt(thumbnail));
                      contentValues.put(MediaStore.Downloads.RELATIVE_PATH, "Download" + File.separator + "ExportTest");
                      Uri path = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                      FileUtils.saveBitmap(path.toString(), bitmap, 100);
                    }
                    if (bitmap != null) {
                      bitmap.recycle();
                    }
                  }

                  @Override public void onError(Throwable e) {
                  }

                  @Override public void onComplete() {
                  }
                });
            ExportDialog exportDialog = new ExportDialog();
            exportDialog.showExporting(SlideShowActivity.this, thumbnail, exportParams,
                mSlideWorkSpace);
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
    mFakeView = findViewById(R.id.editor_fake_layer);

    mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    adapter = new SlideAdapter(this, new SlideAdapter.OnSlideClickListener() {
      @Override public void onClick(SlideInfo item) {
        if (mSlideWorkSpace == null || !mSlideWorkSpace.getPlayerAPI().isPlayerReady()) {
          return;
        }
        if (mPlayerControllerView != null) {
          mPlayerControllerView.seekPlayer(item.previewPos);
        }
        if (mFakeView != null && item.mSlidePosInfo != null) {
          // 设置选中情况
          mFakeView.setSlideClipTarget(new PosDraw(), item.mSlidePosInfo, mSlideWorkSpace.getSlideStreamSize());
          mFakeView.setFakeViewListener(new IFakeViewListener() {

            @Override public void onEffectMoving(float pointX, float pointY) {
              FakePosInfo curFakePos = mFakeView.getFakePosInfo();
              SlidePosInfo slidePosInfo = new SlidePosInfo();
              FakePosUtils.INSTANCE.updateSlidePosByFakePos(curFakePos, slidePosInfo);

              SlideOPPosition slideOPPosition = new SlideOPPosition(item.index, slidePosInfo);
              mSlideWorkSpace.handleOperation(slideOPPosition);
            }

            @Override public void onEffectMoveStart() {
            }

            @Override public void onEffectMoveEnd(boolean moved) {
            }

            @Override public void checkEffectTouchHit(PointF pointF) {
            }
          });
        }
      }

      @Override public void onReplaceClick(SlideInfo item) {
        if (mSlideWorkSpace == null || !mSlideWorkSpace.getPlayerAPI().isPlayerReady()) {
          return;
        }
        replaceSlideNode(item.index);
        if (mFakeView != null) {
          mFakeView.setTarget(null, null);
        }
      }
    });
    mRecyclerView.setAdapter(adapter);
    ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setSceneListener(new CusSortRecycler.SelectSceneListener() {

      @Override public void onOrderStart() {
        mSlideWorkSpace.getPlayerAPI().getPlayerControl().pause();
      }

      @Override public void onOrderChanged(int from, int to) {
        if (from != to) {
          if (from >= adapter.getItemCount() - 1
              || to >= adapter.getItemCount() - 1) {
            return;
          }
          moveSlideNode(from, to);
        }
      }
    });
    ItemDragHelperCallback callback = new ItemDragHelperCallback() {
      @Override public boolean isLongPressDragEnabled() {
        return true;
      }
    };
    callback.setOnItemMoveListener(mRecyclerView);
    ItemTouchHelper helper = new ItemTouchHelper(callback);
    helper.attachToRecyclerView(mRecyclerView);
  }

  private void initData() {
    if (mSlideWorkSpace != null) {
      List<SlideInfo> slideInfos = mSlideWorkSpace.getSlideInfoList();
      if (adapter != null) {
        adapter.updateList(slideInfos);
      }
    }
  }

  private QEPlayerListener mPlayerListener = new QEPlayerListener() {
    @Override public void onPlayerCallback(PlayerStatus playerStatus, int progress) {
      if (playerStatus == PlayerStatus.STATUS_STOP
          || playerStatus == PlayerStatus.STATUS_SEEKING
          || playerStatus == PlayerStatus.STATUS_PLAYING) {
        if (mFakeView != null) {
          mFakeView.setTarget(null, null);
        }
      }
    }

    @Override public void onPlayerRefresh() {
    }

    @Override public void onSizeChanged(Rect resultRect) {
    }
  };

  /**
   * 交换顺序
   */
  public void moveSlideNode(int fromPos, int toPos) {
    SlideOPMove slideOPMove = new SlideOPMove(fromPos, toPos);
    mSlideWorkSpace.handleOperation(slideOPMove);
    if (mFakeView != null) {
      mFakeView.setTarget(null, null);
    }
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
          SlideOPReplace slideOPReplace =
              new SlideOPReplace(position, mediaList.get(0).getFilePath());
          mSlideWorkSpace.handleOperation(slideOPReplace);
        }
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    // TODO 释放掉播放器和workspace的绑定
    if (mSlideWorkSpace != null) {
      mSlideWorkSpace.destory(true);
      mSlideWorkSpace = null;
    }
    if (editorPlayerView != null) {
      editorPlayerView = null;
    }
    if (mFakeView != null) {
      mFakeView.setTarget(null, null);
      mFakeView.setFakeViewListener(null);
      mFakeView = null;
    }
  }
}
