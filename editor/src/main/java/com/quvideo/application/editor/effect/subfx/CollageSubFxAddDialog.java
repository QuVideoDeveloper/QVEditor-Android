package com.quvideo.application.editor.effect.subfx;

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
import com.quvideo.application.editor.edit.EditFilterTemplate;
import com.quvideo.application.editor.effect.EffectAddAdapter;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.effect.EffectSubFx;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubFxAdd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollageSubFxAddDialog extends BaseMenuView {

  private int groupId = 0;
  private int effectIndex = 0;

  private int length;

  public CollageSubFxAddDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    List<EffectSubFx> subFxes = ((AnimEffect) workSpace.getEffectAPI().getEffect(groupId, effectIndex)).mEffectSubFxList;
    length = subFxes == null ? 0 : subFxes.size();
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.CollageFxAdd;
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
    AnimEffect baseEffect = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex));
    EffectOPSubFxAdd effectOPSubFxAdd =
        new EffectOPSubFxAdd(groupId, effectIndex, info.filePath, new VeRange(0, baseEffect.destRange.getTimeLength()));
    mWorkSpace.handleOperation(effectOPSubFxAdd);
    length++;
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  private List<SimpleTemplate> getDataList() {
    EditFilterTemplate[] templates = AssetConstants.getXytListByType(AssetConstants.XytType.SubFx);
    return new ArrayList<>(Arrays.asList(templates));
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_fx);
  }
}
