package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.AssetConstants;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.base.SimpleTemplateAdapter;
import com.quvideo.application.editor.control.EditSeekBarController;
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

  private EditSeekBarController seekBarController;
  private View seekView;
  private int curTransDuration = 0;

  public EditTransDialog(Context context, MenuContainer container,
      IQEWorkSpace workSpace, int clipIndex, ItemOnClickListener l) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    seekBarController = new EditSeekBarController();
    showMenu(container, l);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipTrans;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_trans;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    seekView = view.findViewById(R.id.seekbar);
    seekView.setVisibility(INVISIBLE);
    RecyclerView clipRecyclerView = view.findViewById(R.id.clip_recyclerview);
    clipRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

    SimpleTemplateAdapter adapter =
        new SimpleTemplateAdapter(getActivity(), this);
    List<SimpleTemplate> filterTemplates =
        new ArrayList<>(Arrays.asList(AssetConstants.getXytListByType(AssetConstants.XytType.Transition)));
    adapter.updateList(filterTemplates);
    clipRecyclerView.setAdapter(adapter);
    adapter.setOnItemClickListener(this::applyTemplate);

    seekBarController.bindView(view.findViewById(R.id.seekbar));
    seekBarController.setSeekBarTextColor(Color.parseColor("#80FFFFFF"));
    seekBarController.setSeekBarStartText("0");
    seekBarController.setSeekBarEndText("6000");
    seekBarController.setMaxProgress(6000);

    seekBarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarController.setProgressText(progress + "");
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

      @Override public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });

    ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
    if (clipData.getCrossInfo() != null && !TextUtils.isEmpty(clipData.getCrossInfo().crossPath)) {
      XytInfo xytInfo = XytManager.getXytInfo(clipData.getCrossInfo().crossPath);
      int select = 0;
      for (SimpleTemplate editFilterTemplate : filterTemplates) {
        if (editFilterTemplate.getTemplateId() == xytInfo.getTtidLong()) {
          adapter.changeFocus(select);
          if (isTransDurationEditable(clipData.getCrossInfo().crossPath)) {
            seekView.setVisibility(select == 0 ? INVISIBLE : VISIBLE);
          }
          curTransDuration = clipData.getCrossInfo().duration;
          break;
        }
        select++;
      }
    }
    seekBarController.setSeekBarProgress(curTransDuration);
    seekBarController.setProgressText(curTransDuration + "");
  }

  @Override protected void releaseAll() {
  }

  private void applyTemplate(SimpleTemplate template) {
    CrossInfo crossInfo;
    if (template.getTemplateId() <= 0) {
      // 无转场
      crossInfo = null;
      curTransDuration = 0;
      seekBarController.setSeekBarProgress(curTransDuration);
      seekBarController.setProgressText(curTransDuration + "");
      seekView.setVisibility(INVISIBLE);
    } else {
      XytInfo info = XytManager.getXytInfo(template.getTemplateId());
      curTransDuration = getTranDftDuration(info.getFilePath());
      crossInfo = new CrossInfo(info.getFilePath(), curTransDuration, 0);
      if (isTransDurationEditable(info.getFilePath())) {
        if (curTransDuration == 0) {
          curTransDuration = 1500;
          crossInfo.duration = curTransDuration;
        }
        seekView.setVisibility(VISIBLE);
        seekBarController.setSeekBarProgress(curTransDuration);
        seekBarController.setProgressText(curTransDuration + "");
      } else {
        seekView.setVisibility(INVISIBLE);
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
