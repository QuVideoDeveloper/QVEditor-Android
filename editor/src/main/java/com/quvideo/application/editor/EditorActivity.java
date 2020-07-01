package com.quvideo.application.editor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quvideo.application.EditorConst;
import com.quvideo.application.camera.CameraActivity;
import com.quvideo.application.camera.recorder.RecorderClipInfo;
import com.quvideo.application.editor.base.BaseMenuLayer;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.edit.EditEditDialog;
import com.quvideo.application.editor.effect.EditEffectDialog;
import com.quvideo.application.editor.effect.fake.FakeView;
import com.quvideo.application.editor.sound.EditSoundMainDialog;
import com.quvideo.application.editor.theme.EditThemeDialog;
import com.quvideo.application.export.ExportChooseDialog;
import com.quvideo.application.export.ExportDialog;
import com.quvideo.application.player.PlayerControllerView;
import com.quvideo.application.utils.FileUtils;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.QEEngineClient;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.clip.ClipAddItem;
import com.quvideo.mobile.engine.model.clip.FilterInfo;
import com.quvideo.mobile.engine.model.effect.EffectAddItem;
import com.quvideo.mobile.engine.model.export.ExportParams;
import com.quvideo.mobile.engine.player.EditorPlayerView;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.QEStoryBoardResult;
import com.quvideo.mobile.engine.project.QEWorkSpaceListener;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPAdd;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPRatio;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAdd;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPDel;
import java.util.ArrayList;
import java.util.List;
import xiaoying.utils.LogUtils;

/**
 * Created by santa on 2020-04-29.
 */
public class EditorActivity extends AppCompatActivity implements ItemOnClickListener {

  private RecyclerView mRecyclerView;
  private List<EditOperate> mEditOperates;

  private View rlTitle;
  private ImageView btnBack;
  private Button btnExport;

  private PlayerControllerView mPlayerControllerView;

  private EditorPlayerView editorPlayerView;

  private IQEWorkSpace mWorkSpace;

  private MenuContainer mMenuLayout;

  private FakeView mFakeView;

  volatile boolean reverse = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit);
    initView();
    // 临时demo
    QEEngineClient.createNewProject(new QEWorkSpaceListener() {
      @Override public void onSuccess(IQEWorkSpace qeWorkSpace) {
        List<RecorderClipInfo> recorderClipInfos = getRecordClipList();
        ArrayList<String> albumChoose =
            getIntent().getStringArrayListExtra(EditorConst.INTENT_EXT_KEY_ALBUM);
        if (recorderClipInfos != null && recorderClipInfos.size() > 0) {
          // 它来自Camera
          mWorkSpace = qeWorkSpace;
          mWorkSpace.getPlayerAPI().bindPlayerView(editorPlayerView, 0);
          List<ClipAddItem> list = new ArrayList<>();
          for (RecorderClipInfo clipInfo : recorderClipInfos) {
            int length = clipInfo.getRecorderPos()[1] - clipInfo.getRecorderPos()[0];
            ClipAddItem item = new ClipAddItem();
            item.clipFilePath = clipInfo.getFilePath();
            item.trimRange = new VeRange(clipInfo.getRecorderPos()[0], length);
            if (clipInfo.getEffectItem() != null) {
              item.filterInfo = new FilterInfo(clipInfo.getEffectItem().getEffectFilePath());
            }
            list.add(item);
          }
          ClipOPAdd clipOPAdd = new ClipOPAdd(0, list);
          mWorkSpace.handleOperation(clipOPAdd);
        } else if (albumChoose != null && albumChoose.size() > 0) {
          // 它来自相册
          mWorkSpace = qeWorkSpace;
          mWorkSpace.getPlayerAPI().bindPlayerView(editorPlayerView, 0);
          List<ClipAddItem> list = new ArrayList<>();
          for (String path : albumChoose) {
            ClipAddItem item = new ClipAddItem();
            item.clipFilePath = path;
            list.add(item);
          }
          ClipOPAdd clipOPAdd = new ClipOPAdd(0, list);
          mWorkSpace.handleOperation(clipOPAdd);
        } else {
          ToastUtils.show(EditorActivity.this, "No Video or Pic selected", Toast.LENGTH_LONG);
          EditorActivity.this.finish();
          return;
        }
        mWorkSpace.addObserver(mProjectObserver);
        if (mPlayerControllerView != null) {
          mPlayerControllerView.setPlayerAPI(mWorkSpace.getPlayerAPI());
        }
      }

      @Override public void onError(QEStoryBoardResult error) {
      }
    });
    btnBack.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        handleBack();
      }
    });
    btnExport.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //boolean[] expDialogShowCfgs = new boolean[] { true, bShow720P, bShow1080PItem, false, false };
        if (mWorkSpace != null
            && mWorkSpace.getPlayerAPI() != null
            && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
          mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        }
        ExportChooseDialog dialog = new ExportChooseDialog(EditorActivity.this);
        dialog.setOnDialogItemListener(new ExportChooseDialog.OnDialogItemListener() {
          @Override public void onItemClick(int expType) {
            Bitmap bitmap = mWorkSpace.getProjectThumbnail();
            String thumbnail = "/sdcard/ExportTest/test_thumbnail.jpg";
            FileUtils.saveBitmap(thumbnail, bitmap, 100);
            ExportDialog exportDialog = new ExportDialog();
            ExportParams exportParams = new ExportParams();
            exportParams.outputPath = "/sdcard/ExportTest/test.mp4";
            exportParams.expType = expType;
            exportDialog.showExporting(EditorActivity.this, thumbnail, exportParams, mWorkSpace);
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
    rlTitle = findViewById(R.id.title_layout);
    btnBack = findViewById(R.id.btn_back);
    btnExport = findViewById(R.id.btn_export);
    mPlayerControllerView = findViewById(R.id.edit_enter_play_controller);
    editorPlayerView = findViewById(R.id.editor_play_view);
    mMenuLayout = findViewById(R.id.menu_container);
    mFakeView = findViewById(R.id.editor_fake_layer);

    mMenuLayout.setOnMenuListener(new MenuContainer.OnMenuListener() {
      @Override public void onMenuChange(BaseMenuLayer.MenuType menuType) {
        if (menuType == BaseMenuLayer.MenuType.EffectAdd
            || menuType == BaseMenuLayer.MenuType.EffectSubtitleInput) {
          rlTitle.setVisibility(View.INVISIBLE);
        } else {
          rlTitle.setVisibility(View.VISIBLE);
        }
      }
    });

    mEditOperates = new ArrayList<EditOperate>() {{
      add(new EditOperate(R.drawable.edit_icon_edit_nor, getString(R.string.mn_edit_title_edit)));
      add(new EditOperate(R.drawable.edit_icon_sticker_nor,
          getString(R.string.mn_edit_title_sticker)));
      add(new EditOperate(R.drawable.edit_icon_effect_nor, getString(R.string.mn_edit_title_fx)));
      add(new EditOperate(R.drawable.edit_icon_midpic_nor,
          getString(R.string.mn_edit_title_collages)));
      add(new EditOperate(R.drawable.edit_icon_watermark_nor,
          getString(R.string.mn_edit_title_watermark)));
      add(new EditOperate(R.drawable.edit_icon_mosaic_nor,
          getString(R.string.mn_edit_title_mosaic)));
      add(new EditOperate(R.drawable.edit_icon_text_nor,
          getString(R.string.mn_edit_title_subtitle)));
      add(new EditOperate(R.drawable.edit_icon_theme_nor, getString(R.string.mn_edit_title_theme)));
      add(new EditOperate(R.drawable.edit_icon_music_nor, getString(R.string.mn_edit_title_music)));
      add(new EditOperate(R.drawable.edit_icon_pic_nor, getString(R.string.mn_edit_title_ratio)));
    }};

    mRecyclerView = findViewById(R.id.edit_enter_recyclerview);
    mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
    mRecyclerView.setAdapter(new EditEnterAdapter(mEditOperates, this));
  }

  @Override
  public void onClick(View view, EditOperate operate) {
    if (operate.getResId() == R.drawable.edit_icon_edit_nor) {
      new EditEditDialog(this, mMenuLayout, mWorkSpace, this);
    } else if (operate.getResId() == R.drawable.edit_icon_sticker_nor) {
      new EditEffectDialog(this, mMenuLayout, mWorkSpace, QEGroupConst.GROUP_ID_STICKER,
          mFakeView);
    } else if (operate.getResId() == R.drawable.edit_icon_effect_nor) {
      new EditEffectDialog(this, mMenuLayout, mWorkSpace, QEGroupConst.GROUP_ID_STICKER_FX, null);
    } else if (operate.getResId() == R.drawable.edit_icon_midpic_nor) {
      new EditEffectDialog(this, mMenuLayout, mWorkSpace, QEGroupConst.GROUP_ID_COLLAGES, mFakeView);
    } else if (operate.getResId() == R.drawable.edit_icon_watermark_nor) {// 水印
      if (mWorkSpace.getEffectAPI().getEffect(QEGroupConst.GROUP_ID_WATERMARK, 0)
          != null) {
        EffectOPDel effectOPDel = new EffectOPDel(QEGroupConst.GROUP_ID_WATERMARK, 0);
        mWorkSpace.handleOperation(effectOPDel);
      } else {
        EffectAddItem effectAddItem = new EffectAddItem();
        effectAddItem.mEffectPath = "assets_android://quvideo/watermark/water_mark_logo.png";
        EffectOPAdd effectOPAdd =
            new EffectOPAdd(QEGroupConst.GROUP_ID_WATERMARK, 0, effectAddItem);
        mWorkSpace.handleOperation(effectOPAdd);
      }
    } else if (operate.getResId() == R.drawable.edit_icon_mosaic_nor) {
      new EditEffectDialog(this, mMenuLayout, mWorkSpace, QEGroupConst.GROUP_ID_MOSAIC,
          mFakeView);
    } else if (operate.getResId() == R.drawable.edit_icon_text_nor) {
      new EditEffectDialog(this, mMenuLayout, mWorkSpace, QEGroupConst.GROUP_ID_SUBTITLE,
          mFakeView);
    } else if (operate.getResId() == R.drawable.edit_icon_theme_nor) {
      new EditThemeDialog(this, mMenuLayout, mWorkSpace, this);
    } else if (operate.getResId() == R.drawable.edit_icon_music_nor) {
      new EditSoundMainDialog(this, mMenuLayout, mWorkSpace, this);
    } else if (operate.getResId() == R.drawable.cam_icon_ratio_1_1
        || operate.getResId() == R.drawable.cam_icon_ratio_4_3
        || operate.getResId() == R.drawable.edit_icon_pic_nor) {
      doClipRatioChange();
    }
  }

  @Override public void onBackPressed() {
    if (mMenuLayout != null && mMenuLayout.handleBackPress()) {
      return;
    }
    handleBack();
  }

  private void handleBack() {
    new MaterialDialog.Builder(this)
        .negativeText(R.string.mn_app_cancel)
        .positiveText(R.string.mn_app_confirm)
        .negativeColor(ContextCompat.getColor(this, R.color.color_585858))
        .positiveColor(ContextCompat.getColor(this, R.color.color_585858))
        .canceledOnTouchOutside(false)
        .content(R.string.mn_edit_exit_confirm_content)
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            finish();
          }
        })
        .build()
        .show();
  }

  private BaseObserver mProjectObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof ClipOPRatio) {

      }
    }
  };

  private static final int[][] RATIO_ARRAY = new int[][] {
      new int[] { 3, 4 },
      new int[] { 1, 1 },
      new int[] { -1, 1 },
  };

  private void doClipRatioChange() {
    VeMSize veMSize = mWorkSpace.getStoryboardAPI().getStreamSize();
    float curRatio = veMSize.width * 1.0f / veMSize.height;
    int nextRatioIndex = 0;
    for (int i = 0; i < RATIO_ARRAY.length; i++) {
      int[] ratio = RATIO_ARRAY[i];
      if (Math.abs(curRatio - ratio[0] * 1.0f / ratio[1]) < 0.01f) {
        nextRatioIndex = (i + 1) % RATIO_ARRAY.length;
        break;
      }
    }

    LogUtils.d("ClipOP", "curRatio = " + curRatio);
    VeMSize oriSize = mWorkSpace.getClipAPI().getClipList().get(0).getSourceSize();
    VeMSize ratioSize;
    if (RATIO_ARRAY[nextRatioIndex][0] < 0) {
      // 原始比例
      ratioSize = new VeMSize(oriSize.width, oriSize.height);
    } else {
      ratioSize = new VeMSize(RATIO_ARRAY[nextRatioIndex][0], RATIO_ARRAY[nextRatioIndex][1]);
    }
    ClipOPRatio clipOPRatio = new ClipOPRatio(ratioSize, RATIO_ARRAY[nextRatioIndex][0] < 0);
    mWorkSpace.handleOperation(clipOPRatio);
    int resId;
    if (nextRatioIndex == 0) {
      resId = R.drawable.cam_icon_ratio_4_3;
    } else if (nextRatioIndex == 1) {
      resId = R.drawable.cam_icon_ratio_1_1;
    } else {
      resId = R.drawable.edit_icon_pic_nor;
    }
    ((EditEnterAdapter) mRecyclerView.getAdapter()).setItemDrawable(mEditOperates.size() - 1,
        resId);
  }

  private List<RecorderClipInfo> getRecordClipList() {
    try {
      String paramStr = getIntent().getStringExtra(CameraActivity.INTENT_EXT_KEY_CAMERA);
      return new Gson().fromJson(paramStr, new TypeToken<List<RecorderClipInfo>>() {
      }.getType());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    // TODO 释放掉播放器和workspace的绑定
    if (mWorkSpace != null) {
      mWorkSpace.destory();
      mWorkSpace = null;
    }
    if (editorPlayerView != null) {
      editorPlayerView = null;
    }
  }
}
