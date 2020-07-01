package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.AssetConstants;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.base.SimpleTemplateAdapter;
import com.quvideo.application.editor.edit.EditFilterTemplate;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.CrossInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPTrans;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditTransDialog extends BaseMenuView {

  private int clipIndex = 0;

  public EditTransDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, l);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipTrans;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_trans;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    RecyclerView clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

    SimpleTemplateAdapter adapter =
        new SimpleTemplateAdapter(getActivity(), this);
    List<SimpleTemplate> filterTemplates =
        new ArrayList<>(Arrays.asList(AssetConstants.TEST_EDIT_TRANS_TID));
    adapter.updateList(filterTemplates);
    clipRecyclerView.setAdapter(adapter);
    adapter.setOnItemClickListener(this::applyTemplate);

    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    if (clipData.getCrossInfo() != null && !TextUtils.isEmpty(clipData.getCrossInfo().crossPath)) {
      XytInfo xytInfo = XytManager.getXytInfo(clipData.getCrossInfo().crossPath);
      int select = 0;
      for (EditFilterTemplate editFilterTemplate : AssetConstants.TEST_EDIT_TRANS_TID) {
        if (editFilterTemplate.getTemplateId() == xytInfo.getTtidLong()) {
          adapter.changeFocus(select);
          break;
        }
        select++;
      }
    }
  }

  @Override protected void releaseAll() {
  }

  private void applyTemplate(SimpleTemplate template) {
    CrossInfo crossInfo;
    if (template.getTemplateId() <= 0) {
      // 无转场
      crossInfo = null;
    } else {
      XytInfo info = XytManager.getXytInfo(template.getTemplateId());
      crossInfo = new CrossInfo(info.getFilePath(), 2000, 0);
    }
    ClipOPTrans clipOPTrans = new ClipOPTrans(clipIndex, crossInfo);
    mWorkSpace.handleOperation(clipOPTrans);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_transitions);
  }
}
