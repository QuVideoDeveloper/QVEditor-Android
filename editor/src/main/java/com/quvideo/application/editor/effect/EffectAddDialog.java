package com.quvideo.application.editor.effect;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.AssetConstants;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.edit.EditFilterTemplate;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.utils.RandomUtil;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.constant.XYSdkConstants;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.clip.FilterInfo;
import com.quvideo.mobile.engine.model.effect.EffectAddItem;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAdd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EffectAddDialog extends BaseMenuView {

  private int groupId;

  public EffectAddDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId, ItemOnClickListener l) {
    super(context, workSpace);
    this.groupId = groupId;
    showMenu(container, l);
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_add;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    RecyclerView clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

    EffectAddAdapter adapter =
        new EffectAddAdapter(context, this);
    List<SimpleTemplate> templateList = getDataList();
    adapter.updateList(templateList);
    clipRecyclerView.setAdapter(adapter);

    adapter.setOnItemClickListener(this::applyTemplate);
  }

  @Override protected void releaseAll() {
  }

  private void applyTemplate(SimpleTemplate template) {
    FilterInfo filterInfo;
    if (template.getTemplateId() <= 0) {
      // 无滤镜
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(), R.string.mn_edit_tips_error_template, Toast.LENGTH_LONG);
      return;
    }
    XytInfo info = XytManager.getXytInfo(template.getTemplateId());
    if (info == null) {
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(), R.string.mn_edit_tips_error_template, Toast.LENGTH_LONG);
      return;
    }
    EffectAddItem effectAddItem = new EffectAddItem();
    effectAddItem.mEffectPath = info.filePath;
    effectAddItem.destRange
        = new VeRange(mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime(), 0);
    EffectPosInfo effectPosInfo = new EffectPosInfo();
    effectPosInfo.centerPosX = RandomUtil.randInt(1000, 9000);
    effectPosInfo.centerPosY = RandomUtil.randInt(1000, 9000);
    effectAddItem.mEffectPosInfo = effectPosInfo;
    EffectOPAdd effectOPAdd = new EffectOPAdd(groupId, 0, effectAddItem);
    mWorkSpace.handleOperation(effectOPAdd);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  private List<SimpleTemplate> getDataList() {
    EditFilterTemplate[] templates = null;
    if (groupId == XYSdkConstants.GROUP_ID_MOSAIC) {
      templates = AssetConstants.TEST_MOSIC_TID;
    } else if (groupId == XYSdkConstants.GROUP_ID_STICKER) {
      templates = AssetConstants.TEST_STICKER_TID;
    } else if (groupId == XYSdkConstants.GROUP_ID_SUBTITLE) {
      templates = AssetConstants.TEST_SUBTITLE_TID;
    } else if (groupId == XYSdkConstants.GROUP_ID_STICKER_FX) {
      templates = AssetConstants.TEST_FX_TID;
    }
    if (templates != null) {
      return new ArrayList<>(Arrays.asList(templates));
    }
    return new ArrayList<>();
  }

  @Override
  protected String getBottomTitle() {
    if (groupId == XYSdkConstants.GROUP_ID_MOSAIC) {
      return getContext().getString(R.string.mn_edit_title_mosaic);
    } else if (groupId == XYSdkConstants.GROUP_ID_STICKER) {
      return getContext().getString(R.string.mn_edit_title_sticker);
    } else if (groupId == XYSdkConstants.GROUP_ID_SUBTITLE) {
      return getContext().getString(R.string.mn_edit_title_subtitle);
    } else if (groupId == XYSdkConstants.GROUP_ID_STICKER_FX) {
      return getContext().getString(R.string.mn_edit_title_fx);
    }
    return getContext().getString(R.string.mn_edit_title_edit);
  }
}
