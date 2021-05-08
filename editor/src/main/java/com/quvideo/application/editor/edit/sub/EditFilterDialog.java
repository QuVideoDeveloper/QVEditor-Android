package com.quvideo.application.editor.edit.sub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import com.quvideo.application.superedit.SuperEditManager;
import com.quvideo.application.superedit.ZXingManager;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.QEXytUtil;
import com.quvideo.mobile.engine.entity.XmlType;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.FilterInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

public class EditFilterDialog extends BaseMenuView {

  private int clipIndex = 0;

  private CustomSeekbarPop mCustomSeekbarPop;
  private int curFilterLevel = 100;

  private SimpleTemplateAdapter adapter;
  private RecyclerView clipRecyclerView;
  private ImageView ivAddXml;
  private ImageView ivSaveXml;

  public EditFilterDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipFilter;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_filter;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    ivSaveXml = view.findViewById(R.id.ivSaveXml);
    ivAddXml = view.findViewById(R.id.ivAddXml);
    mCustomSeekbarPop.setVisibility(INVISIBLE);
    ivAddXml.setVisibility(SuperEditManager.isHadSuperEdit() ? VISIBLE : GONE);
    ivSaveXml.setVisibility(GONE);
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
        new ArrayList<>(Arrays.asList(AssetConstants.getXytListByType(AssetConstants.XytType.Filter)));
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
            curFilterLevel = progress;
            ClipData oldClipData = mWorkSpace.getClipAPI().getClipByIndex(clipIndex);
            if (oldClipData != null) {
              FilterInfo filterInfo = oldClipData.getFilterInfo();
              if (filterInfo != null) {
                filterInfo.filterLevel = curFilterLevel;
                ClipOPFilter clipOPFilter = new ClipOPFilter(clipIndex, filterInfo);
                mWorkSpace.handleOperation(clipOPFilter);
              }
            }
          }
        }));
    ivAddXml.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        SuperEditManager.gotoAddXml(getContext(), mMenuContainer, mWorkSpace, XmlType.TYPE_FILTER, clipIndex);
        dismissMenu();
      }
    });
    ivSaveXml.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        SuperEditManager.saveFilter2Xml(getContext(), mWorkSpace, clipIndex, 0);
      }
    });

    ClipData clipData = mWorkSpace.getClipAPI().getClipByIndex(clipIndex);
    if (clipData.getFilterInfo() != null) {
      FilterInfo filterInfo = clipData.getFilterInfo();
      XytInfo xytInfo = XytManager.getXytInfo(filterInfo.filterPath);
      int select = 0;
      for (SimpleTemplate editFilterTemplate : filterTemplates) {
        if (editFilterTemplate.getTemplateId() == xytInfo.getTtidLong()) {
          adapter.changeFocus(select);
          mCustomSeekbarPop.setVisibility(select == 0 ? INVISIBLE : VISIBLE);
          ivSaveXml.setVisibility(select == 0 || !SuperEditManager.isHadSuperEdit() ? GONE : VISIBLE);
          curFilterLevel = filterInfo.filterLevel;
          break;
        }
        select++;
      }
    }
    mCustomSeekbarPop.setProgress(curFilterLevel);
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
            if (!(ttid.contains("0x04")
                && !ttid.contains("0x04000000005")
                && !ttid.contains("0x4B000000000F")
                && !ttid.contains("0x04006"))) {
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
    FilterInfo filterInfo;
    if (template.getTemplateId() <= 0) {
      // 无滤镜
      filterInfo = null;
      curFilterLevel = 100;
      mCustomSeekbarPop.setProgress(curFilterLevel);
      mCustomSeekbarPop.setVisibility(INVISIBLE);
      ivSaveXml.setVisibility(INVISIBLE);
    } else {
      XytInfo info = XytManager.getXytInfo(template.getTemplateId());
      filterInfo = new FilterInfo(info.getFilePath());
      filterInfo.filterLevel = curFilterLevel;
      mCustomSeekbarPop.setVisibility(VISIBLE);
      ivSaveXml.setVisibility(!SuperEditManager.isHadSuperEdit() ? GONE : VISIBLE);
    }
    ClipOPFilter clipOPFilter = new ClipOPFilter(clipIndex, filterInfo);
    mWorkSpace.handleOperation(clipOPFilter);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_filter);
  }
}
