package com.quvideo.application.editor.sound;

import android.content.Context;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.ItemOnClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.EditEffectDialog;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.project.IQEWorkSpace;

public class EditSoundMainDialog extends BaseMenuView {

  public EditSoundMainDialog(Context context, MenuContainer container, IQEWorkSpace workSpace, ItemOnClickListener l) {
    super(context, workSpace);
    showMenu(container, l);
  }

  @Override public MenuType getMenuType() {
    return MenuType.Audio;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_sound_main;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    View btnAddMusic = view.findViewById(R.id.btnMusic);
    View btnAddSoundEffect = view.findViewById(R.id.btnSoundEffect);
    View btnDub = view.findViewById(R.id.btnDub);

    btnAddMusic.setOnClickListener(v -> {
      new EditEffectDialog(getContext(), mMenuContainer, mWorkSpace, QEGroupConst.GROUP_ID_BGMUSIC, null);
    });
    btnAddSoundEffect.setOnClickListener(v -> {
      new EditEffectDialog(getContext(), mMenuContainer, mWorkSpace, QEGroupConst.GROUP_ID_DUBBING, null);
    });
    btnDub.setOnClickListener(v -> {
      new EditEffectDialog(getContext(), mMenuContainer, mWorkSpace, QEGroupConst.GROUP_ID_RECORD, null);
    });
  }

  @Override protected void releaseAll() {
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_dubbing);
  }
}
