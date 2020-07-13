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
import com.quvideo.mobile.engine.model.clip.CrossInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPTrans;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditTransDialog extends BaseMenuView {

  private int clipIndex = 0;

  private CustomSeekbarPop mCustomSeekbarPop;
  private int curTransDuration = 0;

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
        new ArrayList<>(Arrays.asList(AssetConstants.getXytListByType(AssetConstants.XytType.Transition)));
    adapter.updateList(filterTemplates);
    clipRecyclerView.setAdapter(adapter);
    adapter.setOnItemClickListener(this::applyTemplate);

    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("6000")
        .progress(0)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 6000))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
            curTransDuration = progress;
            ClipData oldClipData = mWorkSpace.getClipAPI().getClipByIndex(clipIndex);
            if (oldClipData != null) {
              CrossInfo crossInfo = oldClipData.getCrossInfo();
              if (crossInfo != null) {
                crossInfo.duration = curTransDuration;
                ClipOPTrans clipOPTrans = new ClipOPTrans(clipIndex, crossInfo);
                mWorkSpace.handleOperation(clipOPTrans);
              }
            }
          }
        }));

    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    if (clipData.getCrossInfo() != null && !TextUtils.isEmpty(clipData.getCrossInfo().crossPath)) {
      XytInfo xytInfo = XytManager.getXytInfo(clipData.getCrossInfo().crossPath);
      int select = 0;
      for (SimpleTemplate editFilterTemplate : filterTemplates) {
        if (editFilterTemplate.getTemplateId() == xytInfo.getTtidLong()) {
          adapter.changeFocus(select);
          if (isTransDurationEditable(clipData.getCrossInfo().crossPath)) {
            mCustomSeekbarPop.setVisibility(select == 0 ? INVISIBLE : VISIBLE);
          }
          curTransDuration = clipData.getCrossInfo().duration;
          break;
        }
        select++;
      }
    }
    mCustomSeekbarPop.setProgress(curTransDuration);
  }

  @Override protected void releaseAll() {
  }

  private void applyTemplate(SimpleTemplate template) {
    CrossInfo crossInfo;
    if (template.getTemplateId() <= 0) {
      // 无转场
      crossInfo = null;
      curTransDuration = 0;
      mCustomSeekbarPop.setProgress(curTransDuration);
      mCustomSeekbarPop.setVisibility(INVISIBLE);
    } else {
      XytInfo info = XytManager.getXytInfo(template.getTemplateId());
      curTransDuration = getTranDftDuration(info.getFilePath());
      crossInfo = new CrossInfo(info.getFilePath(), curTransDuration, 0);
      if (isTransDurationEditable(info.getFilePath())) {
        if (curTransDuration == 0) {
          curTransDuration = 1500;
          crossInfo.duration = curTransDuration;
        }
        mCustomSeekbarPop.setVisibility(VISIBLE);
        mCustomSeekbarPop.setProgress(curTransDuration);
      } else {
        mCustomSeekbarPop.setVisibility(INVISIBLE);
      }
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

  private static boolean isTransDurationEditable(String transPath) {
    return XytManager.getTranEditable(transPath);
  }

  private static int getTranDftDuration(String transPath) {
    return XytManager.getTranDuration(transPath);
  }
}
