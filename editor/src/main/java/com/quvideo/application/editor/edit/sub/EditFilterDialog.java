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
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
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

  private CustomSeekbarPop mCustomSeekbarPop;
  private int curFilterLevel = 100;

  public EditFilterDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    showMenu(container, l);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipFilter;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_filter;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);
    mCustomSeekbarPop.setVisibility(INVISIBLE);
    RecyclerView clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    clipRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
          @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int dp2 = DPUtils.dpToPixel(getContext(), 2);
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
          outRect.left = DPUtils.dpToPixel(getContext(), 16);
        } else {
          outRect.left = dp2;
        }
        outRect.right = dp2;
      }
    });

    SimpleTemplateAdapter adapter =
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

    ClipData clipData = mWorkSpace.getClipAPI().getClipByIndex(clipIndex);
    if (clipData.getFilterInfo() != null && !TextUtils.isEmpty(clipData.getFilterInfo().filterPath)) {
      XytInfo xytInfo = XytManager.getXytInfo(clipData.getFilterInfo().filterPath);
      int select = 0;
      for (SimpleTemplate editFilterTemplate : filterTemplates) {
        if (editFilterTemplate.getTemplateId() == xytInfo.getTtidLong()) {
          adapter.changeFocus(select);
          mCustomSeekbarPop.setVisibility(select == 0 ? INVISIBLE : VISIBLE);
          curFilterLevel = clipData.getFilterInfo().filterLevel;
          break;
        }
        select++;
      }
    }
    mCustomSeekbarPop.setProgress(curFilterLevel);
  }

  @Override protected void releaseAll() {
  }

  private void applyTemplate(SimpleTemplate template) {
    FilterInfo filterInfo;
    if (template.getTemplateId() <= 0) {
      // 无滤镜
      filterInfo = null;
      curFilterLevel = 100;
      mCustomSeekbarPop.setProgress(curFilterLevel);
      mCustomSeekbarPop.setVisibility(INVISIBLE);
    } else {
      XytInfo info = XytManager.getXytInfo(template.getTemplateId());
      filterInfo = new FilterInfo(info.getFilePath());
      filterInfo.filterLevel = curFilterLevel;
      mCustomSeekbarPop.setVisibility(VISIBLE);
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
