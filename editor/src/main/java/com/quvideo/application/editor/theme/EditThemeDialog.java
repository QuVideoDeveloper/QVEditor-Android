package com.quvideo.application.editor.theme;

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
import com.quvideo.application.dialog.LoadingDialog;
import com.quvideo.application.download.DownloadDialog;
import com.quvideo.application.editor.EditorActivity;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.base.SimpleTemplateAdapter;
import com.quvideo.application.editor.edit.EditFilterTemplate;
import com.quvideo.application.superedit.ZXingManager;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.QEXytUtil;
import com.quvideo.mobile.engine.model.ThemeSubtitleEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.theme.ThemeOPApply;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

public class EditThemeDialog extends BaseMenuView {

  private SimpleTemplateAdapter adapter;
  private RecyclerView clipRecyclerView;
  private View vThemeEdit;

  private LoadingDialog mLoadingDialog = new LoadingDialog();

  public EditThemeDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      ItemOnClickListener l) {
    super(context, workSpace);
    showMenu(container, l);
  }

  @Override public MenuType getMenuType() {
    return MenuType.Theme;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_add;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    vThemeEdit = view.findViewById(R.id.vThemeEdit);
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
    List<SimpleTemplate> themeTemplates =
        new ArrayList<>(
            Arrays.asList(AssetConstants.getXytListByType(AssetConstants.XytType.Theme)));
    adapter.updateList(themeTemplates);
    clipRecyclerView.setAdapter(adapter);
    adapter.setOnItemClickListener(this::applyTemplate);

    long themeId = mWorkSpace.getStoryboardAPI().getThemeId();
    if (themeId != 0) {
      int select = 0;
      for (SimpleTemplate editFilterTemplate : themeTemplates) {
        if (editFilterTemplate.getTemplateId() == themeId) {
          adapter.changeFocus(select);
          break;
        }
        select++;
      }
    }
    vThemeEdit.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        // TODO 处理主题字幕功能
        new ThemeSubtitleDialog(getContext(), mMenuContainer, mWorkSpace);
      }
    });
    changeThemeSubtitleView();
    mWorkSpace.addObserver(mBaseObserver);
  }

  private void changeThemeSubtitleView() {
    List<ThemeSubtitleEffect> titleList = mWorkSpace.getStoryboardAPI().getThemeTitleInfoList();
    if (titleList != null && titleList.size() > 0) {
      vThemeEdit.setVisibility(VISIBLE);
    } else {
      vThemeEdit.setVisibility(GONE);
    }
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof ThemeOPApply) {
        changeThemeSubtitleView();
        mLoadingDialog.dismissLoading();
      }
    }
  };

  @Override protected void releaseAll() {
    mWorkSpace.removeObserver(mBaseObserver);
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
            if (!ttid.contains("0x01000000000")) {
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
            Arrays.asList(AssetConstants.getXytListByType(AssetConstants.XytType.Theme)));
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
    mLoadingDialog.showDownloading(getActivity());
    XytInfo xytInfo = XytManager.getXytInfo(template.getTemplateId());
    String themePath = xytInfo != null ? xytInfo.getFilePath() : null;
    ThemeOPApply themeOPApply = new ThemeOPApply(themePath);
    mWorkSpace.handleOperation(themeOPApply);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_theme);
  }
}
