package com.quvideo.application.editor.sound;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.AssetConstants;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.mobile.engine.constant.XYSdkConstants;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.clip.FilterInfo;
import com.quvideo.mobile.engine.model.effect.EffectAddItem;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAdd;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPAudioReplace;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EffectAddMusicDialog extends BaseMenuView {

  private int groupId;

  public EffectAddMusicDialog(Context context, MenuContainer container,
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

    AudioTemplateAdapter adapter =
        new AudioTemplateAdapter(context, this);
    List<AudioTemplate> templateList = getDataList();
    adapter.updateList(templateList);
    clipRecyclerView.setAdapter(adapter);

    adapter.setOnItemClickListener(this::applyTemplate);
  }

  @Override protected void releaseAll() {
  }

  private void applyTemplate(AudioTemplate template) {
    FilterInfo filterInfo;
    EffectAddItem effectAddItem = new EffectAddItem();
    effectAddItem.mEffectPath = template.getAudioPath();
    effectAddItem.destRange = new VeRange(mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime(), 0);
    if (groupId == XYSdkConstants.GROUP_ID_DUBBING) {
      EffectOPAdd effectOPAdd = new EffectOPAdd(groupId, 0, effectAddItem);
      mWorkSpace.handleOperation(effectOPAdd);
    } else {
      effectAddItem.destRange = new VeRange(0, -1);
      if (mWorkSpace.getEffectAPI().getEffect(groupId, 0) != null) {
        EffectOPAudioReplace effectOPAudioReplace = new EffectOPAudioReplace(groupId, 0, template.getAudioPath(), null);
        mWorkSpace.handleOperation(effectOPAudioReplace);
      } else {
        EffectOPAdd effectOPAdd = new EffectOPAdd(groupId, 0, effectAddItem);
        mWorkSpace.handleOperation(effectOPAdd);
      }
    }
    dismissMenu();
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  private List<AudioTemplate> getDataList() {
    AudioTemplate[] templates = null;
    if (groupId == XYSdkConstants.GROUP_ID_DUBBING) {
      templates = AssetConstants.TEST_DUB_TID;
    } else if (groupId == XYSdkConstants.GROUP_ID_BGMUSIC) {
      templates = AssetConstants.TEST_MUSIC_TID;
    }
    if (templates != null) {
      return new ArrayList<>(Arrays.asList(templates));
    }
    return new ArrayList<>();
  }

  @Override
  protected String getBottomTitle() {
    if (groupId == XYSdkConstants.GROUP_ID_BGMUSIC) {
      return getContext().getString(R.string.mn_edit_title_bgm);
    } else if (groupId == XYSdkConstants.GROUP_ID_DUBBING) {
      return getContext().getString(R.string.mn_edit_title_dubbing);
    }
    return getContext().getString(R.string.mn_edit_title_edit);
  }
}