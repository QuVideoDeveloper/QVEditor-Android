package com.quvideo.application.editor.effect.collage;

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
import com.quvideo.application.editor.edit.EditFilterTemplate;
import com.quvideo.application.superedit.ZXingManager;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.QEXytUtil;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.effect.EffectOverlayInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPOverlayInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

public class CollageOverlayDialog extends BaseMenuView {

  private int groupId = 0;
  private int effectIndex = 0;

  private CustomSeekbarPop mCustomSeekbarPop;
  private int curOverlayLevel = 100;

  private SimpleTemplateAdapter adapter;
  private RecyclerView clipRecyclerView;

  public CollageOverlayDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.CollageOverlay;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_collage_filter;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    mCustomSeekbarPop.setVisibility(VISIBLE);
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
        new ArrayList<>(Arrays.asList(AssetConstants.getXytListByType(AssetConstants.XytType.Overlay)));
    adapter.updateList(filterTemplates);
    clipRecyclerView.setAdapter(adapter);
    adapter.setOnItemClickListener(this::applyTemplate);

    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("100")
        .progress(0)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 100))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            curOverlayLevel = progress;
            EffectOverlayInfo overlayInfo = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex)).mEffectOverlayInfo;
            if (overlayInfo != null) {
              overlayInfo.level = curOverlayLevel;
            } else {
              overlayInfo = new EffectOverlayInfo("", curOverlayLevel);
            }
            EffectOPOverlayInfo effectOPOverlayInfo = new EffectOPOverlayInfo(groupId, effectIndex, overlayInfo);
            mWorkSpace.handleOperation(effectOPOverlayInfo);
          }
        }));

    EffectOverlayInfo overlayInfo = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex)).mEffectOverlayInfo;
    boolean isfind = false;
    if (overlayInfo != null && !TextUtils.isEmpty(overlayInfo.overlayPath)) {
      XytInfo xytInfo = XytManager.getXytInfo(overlayInfo.overlayPath);
      int select = 0;
      for (SimpleTemplate editFilterTemplate : filterTemplates) {
        if (editFilterTemplate.getTemplateId() == xytInfo.getTtidLong()) {
          adapter.changeFocus(select);
          curOverlayLevel = overlayInfo.level;
          isfind = true;
          break;
        }
        select++;
      }
    }
    if (!isfind) {
      curOverlayLevel = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex)).alpha;
    }
    mCustomSeekbarPop.setProgress(curOverlayLevel);
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
            if (!ttid.contains("0x4B000000000F")) {
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
            Arrays.asList(AssetConstants.getXytListByType(AssetConstants.XytType.Filter)));
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
    EffectOverlayInfo overlayInfo;
    if (template.getTemplateId() <= 0) {
      // 混合程度
      overlayInfo = new EffectOverlayInfo("", curOverlayLevel);
      mCustomSeekbarPop.setProgress(curOverlayLevel);
    } else {
      XytInfo info = XytManager.getXytInfo(template.getTemplateId());
      overlayInfo = new EffectOverlayInfo(info.getFilePath(), curOverlayLevel);
    }
    EffectOPOverlayInfo effectOPOverlayInfo = new EffectOPOverlayInfo(groupId, effectIndex, overlayInfo);
    mWorkSpace.handleOperation(effectOPOverlayInfo);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_collage_overlay);
  }
}
