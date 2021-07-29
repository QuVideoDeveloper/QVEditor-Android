package com.quvideo.application.editor.effect;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.AssetConstants;
import com.quvideo.application.DPUtils;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.template.EditFilterTemplate;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.utils.RandomUtil;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.effect.EffectAddItem;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAdd;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPStaticPic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EffectAddDialog extends BaseMenuView {

  private int groupId;

  private int length;

  private boolean isNeedStatic = false;

  private ArrayList<Integer> addPos = new ArrayList<>();

  public EffectAddDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int groupId) {
    super(context, workSpace);
    this.groupId = groupId;
    isNeedStatic =
        groupId == QEGroupConst.GROUP_ID_STICKER || groupId == QEGroupConst.GROUP_ID_SUBTITLE;
    length = workSpace.getEffectAPI().getEffectList(groupId).size();
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectAdd;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_add;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    RecyclerView clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
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

    EffectAddAdapter adapter =
        new EffectAddAdapter(context, this);
    List<SimpleTemplate> templateList = getDataList();
    adapter.updateList(templateList);
    clipRecyclerView.setAdapter(adapter);

    adapter.setOnItemClickListener(this::applyTemplate);
  }

  @Override protected void releaseAll() {
    if (isNeedStatic) {
      for (Integer position : addPos) {
        EffectOPStaticPic effectOPStaticPic = new EffectOPStaticPic(groupId, position, false);
        mWorkSpace.handleOperation(effectOPStaticPic);
      }
    }
  }

  private void applyTemplate(SimpleTemplate template) {
    if (template.getTemplateId() <= 0) {
      // 无滤镜
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
          R.string.mn_edit_tips_error_template, Toast.LENGTH_LONG);
      return;
    }
    XytInfo info = XytManager.getXytInfo(template.getTemplateId());
    if (info == null) {
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
          R.string.mn_edit_tips_error_template, Toast.LENGTH_LONG);
      return;
    }
    EffectAddItem effectAddItem = new EffectAddItem();
    effectAddItem.mEffectPath = info.filePath;
    effectAddItem.destRange
        = new VeRange(mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime(), 0);
    VeMSize streamSize = mWorkSpace.getStoryboardAPI().getStreamSize();
    EffectPosInfo effectPosInfo = new EffectPosInfo();
    effectPosInfo.center.x = streamSize.width * RandomUtil.randInt(1000, 9000) / 10000f;
    effectPosInfo.center.y = streamSize.height * RandomUtil.randInt(1000, 9000) / 10000f;
    effectAddItem.mEffectPosInfo = effectPosInfo;
    if (groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
      effectAddItem.subtitleTexts = Collections.singletonList(
          EditorApp.Companion.getInstance().app.getString(R.string.mn_edit_tips_input_text));
    }
    EffectOPAdd effectOPAdd = new EffectOPAdd(groupId, length, effectAddItem);
    mWorkSpace.handleOperation(effectOPAdd);
    addPos.add(length);
    if (isNeedStatic) {
      EffectOPStaticPic effectOPStaticPic = new EffectOPStaticPic(groupId, length, true);
      mWorkSpace.handleOperation(effectOPStaticPic);
    }
    length++;
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  private List<SimpleTemplate> getDataList() {
    EditFilterTemplate[] templates = null;
    if (groupId == QEGroupConst.GROUP_ID_MOSAIC) {
      templates = AssetConstants.TEST_MOSIC_TID;
    } else if (groupId == QEGroupConst.GROUP_ID_STICKER) {
      templates = AssetConstants.getXytListByType(AssetConstants.XytType.Sticker);
    } else if (groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
      templates = AssetConstants.getXytListByType(AssetConstants.XytType.Subtitle);
    } else if (groupId == QEGroupConst.GROUP_ID_STICKER_FX) {
      templates = AssetConstants.getXytListByType(AssetConstants.XytType.Fx);
    }
    if (templates != null) {
      return new ArrayList<>(Arrays.asList(templates));
    }
    return new ArrayList<>();
  }

  @Override
  protected String getBottomTitle() {
    if (groupId == QEGroupConst.GROUP_ID_MOSAIC) {
      return getContext().getString(R.string.mn_edit_title_mosaic);
    } else if (groupId == QEGroupConst.GROUP_ID_STICKER) {
      return getContext().getString(R.string.mn_edit_title_sticker);
    } else if (groupId == QEGroupConst.GROUP_ID_SUBTITLE) {
      return getContext().getString(R.string.mn_edit_title_subtitle);
    } else if (groupId == QEGroupConst.GROUP_ID_STICKER_FX) {
      return getContext().getString(R.string.mn_edit_title_fx);
    }
    return getContext().getString(R.string.mn_edit_title_edit);
  }
}
