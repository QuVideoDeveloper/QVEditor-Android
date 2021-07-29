package com.quvideo.application.editor.effect;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import java.util.ArrayList;
import java.util.List;

public class ElementDialog extends BaseEffectMenuView {

  private RecyclerView mRecyclerView;

  private ElementAdapter mEffectOperateAdapter;

  public ElementDialog(Context context, MenuContainer container, IQEWorkSpace workSpace, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectEdit;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_add;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    // 操作view
    RecyclerView editRecyclerView = view.findViewById(R.id.clip_recyclerview);
    editRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mEffectOperateAdapter = new ElementAdapter(getOperateList(), mItemOnClickListener);
    editRecyclerView.setAdapter(mEffectOperateAdapter);
  }

  @Override protected void releaseAll() {
  }

  private ElementAdapter.IDataClickListener mItemOnClickListener = new ElementAdapter.IDataClickListener() {
    @Override public void onClick(View view, DataItem operate) {
      if (operate.action == Type.Sticker) {
        // 贴纸
        new EditEffectDialog(getContext(), mMenuContainer, mWorkSpace, QEGroupConst.GROUP_ID_STICKER, mFakeApi);
        dismissMenu();
      } else if (operate.action == Type.Collage) {
        // 画中画
        new EditEffectDialog(getContext(), mMenuContainer, mWorkSpace, QEGroupConst.GROUP_ID_COLLAGES, mFakeApi);
        dismissMenu();
      } else if (operate.action == Type.Subtitle) {
        // 字幕
        new EditEffectDialog(getContext(), mMenuContainer, mWorkSpace, QEGroupConst.GROUP_ID_SUBTITLE, mFakeApi);
        dismissMenu();
      } else if (operate.action == Type.Mosaic) {
        // 画马赛克
        new EditEffectDialog(getContext(), mMenuContainer, mWorkSpace, QEGroupConst.GROUP_ID_MOSAIC, mFakeApi);
        dismissMenu();
      }
    }
  };

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_effect);
  }

  private List<DataItem> getOperateList() {
    List<DataItem> list = new ArrayList<>();
    list.add(new DataItem(Type.Sticker, R.drawable.edit_icon_sticker_nor,
        getContext().getString(R.string.mn_edit_title_sticker)));
    list.add(new DataItem(Type.Collage, R.drawable.edit_icon_midpic_nor,
        getContext().getString(R.string.mn_edit_title_collages)));
    list.add(new DataItem(Type.Subtitle, R.drawable.edit_icon_text_nor,
        getContext().getString(R.string.mn_edit_title_subtitle)));
    list.add(new DataItem(Type.Mosaic, R.drawable.edit_icon_mosaic_nor,
        getContext().getString(R.string.mn_edit_title_mosaic)));
    return list;
  }

  enum Type {
    Sticker, Collage, Subtitle, Mosaic
  }

  public static class DataItem {

    private Type action;
    private int resId;
    private String title;

    public DataItem(Type action, int resId, String title) {
      this.action = action;
      this.resId = resId;
      this.title = title;
    }

    public Type getAction() {
      return action;
    }

    public int getResId() {
      return resId;
    }

    public String getTitle() {
      return title;
    }
  }
}
