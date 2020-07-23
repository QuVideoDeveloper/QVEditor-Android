package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.AssetConstants;
import com.quvideo.application.DPUtils;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.base.SimpleTemplateAdapter;
import com.quvideo.application.template.SimpleTemplate;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.FxFilterInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPFxFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditFxFilterDialog extends BaseMenuView {

  private int clipIndex = 0;

  public EditFxFilterDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, l);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipFxFilter;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_fx_filter;
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

    SimpleTemplateAdapter adapter =
        new SimpleTemplateAdapter(getActivity(), this);
    List<SimpleTemplate> filterTemplates =
        new ArrayList<>(Arrays.asList(AssetConstants.getXytListByType(AssetConstants.XytType.FxFilter)));
    adapter.updateList(filterTemplates);
    clipRecyclerView.setAdapter(adapter);
    adapter.setOnItemClickListener(this::applyTemplate);

    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    if (clipData.getFxFilterInfo() != null && !TextUtils.isEmpty(clipData.getFxFilterInfo().filterPath)) {
      XytInfo xytInfo = XytManager.getXytInfo(clipData.getFxFilterInfo().filterPath);
      int select = 0;
      for (SimpleTemplate editFilterTemplate : filterTemplates) {
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
    FxFilterInfo fxFilterInfo;
    if (template.getTemplateId() <= 0) {
      // 无滤镜
      fxFilterInfo = null;
    } else {
      XytInfo info = XytManager.getXytInfo(template.getTemplateId());
      fxFilterInfo = new FxFilterInfo(info.getFilePath());
    }
    ClipOPFxFilter clipOPFxFilter = new ClipOPFxFilter(clipIndex, fxFilterInfo);
    mWorkSpace.handleOperation(clipOPFxFilter);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_fx_filter);
  }
}
