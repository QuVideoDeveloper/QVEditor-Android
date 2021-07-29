package com.quvideo.application.editor.edit.sub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.AssetConstants;
import com.quvideo.application.DPUtils;
import com.quvideo.application.EditorApp;
import com.quvideo.application.download.DownloadDialog;
import com.quvideo.application.editor.EditorActivity;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.base.SimpleTemplateAdapter;
import com.quvideo.application.template.EditFilterTemplate;
import com.quvideo.application.superedit.ZXingManager;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.QEXytUtil;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.CrossInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPTrans;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

public class EditTransDialog extends BaseMenuView {

  private int clipIndex = 0;

  private CustomSeekbarPop mCustomSeekbarPop;
  private int curTransDuration = 0;
  private String curTransPath = null;

  private SimpleTemplateAdapter adapter;
  private RecyclerView clipRecyclerView;

  public EditTransDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipTrans;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_trans;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    mCustomSeekbarPop.setVisibility(INVISIBLE);
    clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    clipRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
          @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
          outRect.left = DPUtils.dpToPixel(getContext(), 16);
        } else {
          outRect.left = DPUtils.dpToPixel(getContext(), 8);
        }
      }
    });

    adapter =
        new SimpleTemplateAdapter(getActivity(), this);
    List<SimpleTemplate> filterTemplates =
        new ArrayList<>(Arrays.asList(AssetConstants.getXytListByType(AssetConstants.XytType.Transition)));
    adapter.updateList(filterTemplates);
    clipRecyclerView.setAdapter(adapter);
    adapter.setOnItemClickListener(this::applyTemplate);

    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("6000")
        .progress(0)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 6000))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            curTransDuration = progress;
          }
        }));

    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    if (clipData.getCrossInfo() != null && !TextUtils.isEmpty(clipData.getCrossInfo().crossPath)) {
      XytInfo xytInfo = XytManager.getXytInfo(clipData.getCrossInfo().crossPath);
      int select = 0;
      for (SimpleTemplate editFilterTemplate : filterTemplates) {
        if (editFilterTemplate.getTemplateId() == xytInfo.getTtidLong()) {
          adapter.changeFocus(select);
          if (isTransDurationEditable(clipData.getCrossInfo().crossPath)) {
            mCustomSeekbarPop.setVisibility(select == 0 ? INVISIBLE : VISIBLE);
          }
          curTransDuration = clipData.getCrossInfo().duration;
          curTransPath = clipData.getCrossInfo().crossPath;
          break;
        }
        select++;
      }
    }
    mCustomSeekbarPop.setProgress(curTransDuration);
  }

  @Override protected void releaseAll() {
  }

  @Override public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (resultCode == Activity.RESULT_OK && requestCode == EditorActivity.INTENT_REQUEST_QRCODE && data != null) {
      String result = data.getStringExtra(ZXingManager.ZXING_RESULT_QRMSG);
      if (!TextUtils.isEmpty(result)) {
        try {
          JSONObject jsonObject = new JSONObject(result);
          String ttid = jsonObject.optString("ttid");
          String url = jsonObject.optString("url");
          if (!TextUtils.isEmpty(ttid) && !TextUtils.isEmpty(url)) {
            if (!ttid.contains("0x03")) {
              // 需要贴纸，但不是贴纸素材
              // 无滤镜
              ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                  R.string.mn_edit_tips_template_qrcode_error, Toast.LENGTH_LONG);
              return true;
            }
            DownloadDialog downloadDialog = new DownloadDialog(new DownloadDialog.OnTemplateDownloadOver() {
              @Override public void onDownloadOver(String templateCode) {
                handleAddEffect(templateCode);
              }
            });
            downloadDialog.showDownloading(getActivity(), ttid, url);
            return true;
          }
        } catch (Exception ignore) {
        }
      }
    }
    return false;
  }

  private void handleAddEffect(String templateCode) {
    long templateId = QEXytUtil.ttidHexStrToLong(templateCode);
    List<SimpleTemplate> themeTemplates =
        new ArrayList<>(
            Arrays.asList(AssetConstants.getXytListByType(AssetConstants.XytType.Transition)));
    adapter.updateList(themeTemplates);
    int select = 0;
    if (templateId != 0) {
      for (SimpleTemplate editFilterTemplate : themeTemplates) {
        if (editFilterTemplate.getTemplateId() == templateId) {
          break;
        }
        select++;
      }
    }
    adapter.changeFocus(select);
    applyTemplate(new EditFilterTemplate(templateId));
  }

  private void applyTemplate(SimpleTemplate template) {
    if (template.getTemplateId() <= 0) {
      // 无转场
      curTransPath = null;
      curTransDuration = 0;
      mCustomSeekbarPop.setProgress(curTransDuration);
      mCustomSeekbarPop.setVisibility(INVISIBLE);
    } else {
      XytInfo info = XytManager.getXytInfo(template.getTemplateId());
      curTransDuration = getTranDftDuration(info.getFilePath());
      mCustomSeekbarPop.setProgress(curTransDuration);
      curTransPath = info.getFilePath();
      if (isTransDurationEditable(info.getFilePath())) {
        if (curTransDuration == 0) {
          curTransDuration = 1500;
        }
        mCustomSeekbarPop.setVisibility(VISIBLE);
        mCustomSeekbarPop.setProgress(curTransDuration);
      } else {
        mCustomSeekbarPop.setVisibility(INVISIBLE);
      }
    }
  }

  @Override public void onClick(View v) {
    CrossInfo crossInfo = null;
    if (!TextUtils.isEmpty(curTransPath)) {
      crossInfo = new CrossInfo(curTransPath, curTransDuration, 0);
    }
    ClipOPTrans clipOPTrans = new ClipOPTrans(clipIndex, crossInfo);
    mWorkSpace.handleOperation(clipOPTrans);
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_transitions);
  }

  private static boolean isTransDurationEditable(String transPath) {
    return QEXytUtil.getTranEditable(transPath);
  }

  private static int getTranDftDuration(String transPath) {
    return QEXytUtil.getTranDuration(transPath);
  }
}
