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
import com.quvideo.mobile.engine.model.clip.FilterInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditFilterDialog extends BaseMenuView {

  private int clipIndex = 0;

  public EditFilterDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, l);
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_filter;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    RecyclerView clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

    SimpleTemplateAdapter adapter =
        new SimpleTemplateAdapter(getActivity(), this);
    List<SimpleTemplate> filterTemplates =
        new ArrayList<>(Arrays.asList(AssetConstants.TEST_EDIT_FILTER_TID));
    adapter.updateList(filterTemplates);
    clipRecyclerView.setAdapter(adapter);
    adapter.setOnItemClickListener(this::applyTemplate);

    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    if (clipData.getFilterInfo() != null && !TextUtils.isEmpty(clipData.getFilterInfo().filterPath)) {
      XytInfo xytInfo = XytManager.getXytInfo(clipData.getFilterInfo().filterPath);
      int select = 0;
      for (EditFilterTemplate editFilterTemplate : AssetConstants.TEST_EDIT_FILTER_TID) {
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
    FilterInfo filterInfo;
    if (template.getTemplateId() <= 0) {
      // 无滤镜
      filterInfo = null;
    } else {
      XytInfo info = XytManager.getXytInfo(template.getTemplateId());
      filterInfo = new FilterInfo(info.getFilePath());
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
