package com.quvideo.application.editor.theme;

import android.content.Context;
import android.graphics.Rect;
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
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.theme.ThemeOPApply;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditThemeDialog extends BaseMenuView {

  private int clipIndex = 0;

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
  }

  @Override protected void releaseAll() {
  }

  private void applyTemplate(SimpleTemplate template) {
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
