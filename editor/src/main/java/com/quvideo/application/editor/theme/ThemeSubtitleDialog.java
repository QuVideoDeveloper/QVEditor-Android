package com.quvideo.application.editor.theme;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.mobile.engine.model.ThemeSubtitleEffect;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.theme.ThemeOPSubtitleText;
import java.util.List;

public class ThemeSubtitleDialog extends BaseMenuView {

  //字幕列表
  private RecyclerView mRvText;
  // 修改文本
  private ImageView mIvInput;

  private ThemeSubtitleAdapter mEffectSubtitleAdapter;

  public ThemeSubtitleDialog(Context context, MenuContainer container, IQEWorkSpace workSpace) {
    super(context, workSpace);
    mWorkSpace.addObserver(mBaseObserver);
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ThemeSubtitle;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_theme_subtitle_edit;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    mRvText = view.findViewById(R.id.subtitle_recyclerview);
    mIvInput = view.findViewById(R.id.btn_edit);

    mIvInput.setOnClickListener(mOnClickListener);

    List<ThemeSubtitleEffect> titleList = mWorkSpace.getStoryboardAPI().getThemeTitleInfoList();
    mRvText.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mEffectSubtitleAdapter = new ThemeSubtitleAdapter(titleList,
        new ThemeSubtitleAdapter.OnSubtitleClickListener() {
          @Override public void onClick(int index) {
            List<ThemeSubtitleEffect> titleList = mWorkSpace.getStoryboardAPI().getThemeTitleInfoList();
            ThemeSubtitleEffect item = titleList.get(index);
            mWorkSpace.getPlayerAPI().getPlayerControl().seek(item.destRange.getPosition());
          }
        });
    mRvText.setAdapter(mEffectSubtitleAdapter);
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof ThemeOPSubtitleText) {
        List<ThemeSubtitleEffect> titleList = mWorkSpace.getStoryboardAPI().getThemeTitleInfoList();
        mEffectSubtitleAdapter.updateDataList(titleList);
      }
    }
  };

  private OnClickListener mOnClickListener = new OnClickListener() {
    @Override public void onClick(View v) {
      if (v.equals(mIvInput)) {
        List<ThemeSubtitleEffect> titleList = mWorkSpace.getStoryboardAPI().getThemeTitleInfoList();
        new EditThemeInputDialog(getContext(), mMenuContainer, mWorkSpace, titleList.get(mEffectSubtitleAdapter.getSelectIndex()));
      }
    }
  };

  @Override protected void releaseAll() {
    if (mWorkSpace != null) {
      mWorkSpace.removeObserver(mBaseObserver);
    }
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_subtitle_input);
  }
}
