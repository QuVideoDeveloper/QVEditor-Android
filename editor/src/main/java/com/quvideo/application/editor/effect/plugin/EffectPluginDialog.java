package com.quvideo.application.editor.effect.plugin;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.DPUtils;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.effect.SubPluginAttriItem;
import com.quvideo.mobile.engine.model.effect.SubPluginInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubPluginAdd;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubPluginDel;
import java.util.ArrayList;
import java.util.List;

public class EffectPluginDialog extends BaseMenuView {

  private int groupId;
  private int effectIndex;

  private RecyclerView mRecyclerView;
  private EffectPluginAdapter mPluginAdapter;

  private EffectPluginAttriAdapter mEffectPluginAttriAdapter;

  public EffectPluginDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    mWorkSpace.getPlayerAPI().getPlayerControl().pause();
    mWorkSpace.addObserver(mBaseObserver);
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectPlugin;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    View rootView = view.findViewById(R.id.root_layout);
    rootView.setOnClickListener(v -> {
      // 只是为了拦击点击事件
    });
    mPluginAdapter = new EffectPluginAdapter(getActivity(), mOnEffectClickListener);
    // effect
    mRecyclerView = view.findViewById(R.id.clip_recyclerview);
    mRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mRecyclerView.setAdapter(mPluginAdapter);
    mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
          @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int dp2 = DPUtils.dpToPixel(getContext(), 4);
        outRect.left = dp2;
        outRect.right = dp2;
      }
    });
    AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    mPluginAdapter.updateList(baseEffect.mEffectSubPluginList);
    mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition());
    // 操作view
    RecyclerView editRecyclerView = view.findViewById(R.id.operate_recyclerview);
    editRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mEffectPluginAttriAdapter = new EffectPluginAttriAdapter(getOperateList(null),
        mIPluginEditClickListener);
    editRecyclerView.setAdapter(mEffectPluginAttriAdapter);
    int selectIndex = mPluginAdapter.getSelectIndex();
    mEffectPluginAttriAdapter.updateList(getOperateList(selectIndex < 0 ? null : baseEffect.mEffectSubPluginList.get(selectIndex)));
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof EffectOPSubPluginAdd
          || operate instanceof EffectOPSubPluginDel) {
        // 刷新数据
        if (mWorkSpace.getPlayerAPI() != null
            && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
          mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        }
        AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        mPluginAdapter.updateList(baseEffect.mEffectSubPluginList);
        mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition());
        if (mEffectPluginAttriAdapter != null) {
          int selectIndex = mPluginAdapter.getSelectIndex();
          mEffectPluginAttriAdapter.updateList(getOperateList(selectIndex < 0 ? null : baseEffect.mEffectSubPluginList.get(selectIndex)));
        }
      }
    }
  };

  private EffectPluginAttriAdapter.IPluginEditClickListener mIPluginEditClickListener
      = new EffectPluginAttriAdapter.IPluginEditClickListener() {
    @Override public void onClick(View view, EffectPluginAttriAdapter.PluginEditItem operate) {
      AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
      if (operate.getResId() == R.drawable.edit_icon_delete_nor) {
        // 删除
        int selectIndex = mPluginAdapter.getSelectIndex();
        if (selectIndex >= 0 && selectIndex < baseEffect.mEffectSubPluginList.size()) {
          EffectOPSubPluginDel effectOPSubPluginDel = new EffectOPSubPluginDel(groupId, effectIndex,
              baseEffect.mEffectSubPluginList.get(selectIndex).getSubType());
          mWorkSpace.handleOperation(effectOPSubPluginDel);
        } else {
          ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
              R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        }
      } else {
        int selectIndex = mPluginAdapter.getSelectIndex();
        if (selectIndex >= 0 && selectIndex < baseEffect.mEffectSubPluginList.size()) {
          new EffectEditPluginDialog(getContext(), mMenuContainer, mWorkSpace, groupId, effectIndex,
              baseEffect.mEffectSubPluginList.get(selectIndex).getSubType(), operate.getName());
        } else {
          ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
              R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        }
      }
    }
  };

  @Override protected void releaseAll() {
    if (mWorkSpace != null) {
      mWorkSpace.removeObserver(mBaseObserver);
    }
  }

  private EffectPluginAdapter.OnPluginClickListener mOnEffectClickListener =
      new EffectPluginAdapter.OnPluginClickListener() {
        @Override public void onClick(int index, SubPluginInfo item) {
          if (item == null || index < 0) {
            new EffectAddPluginDialog(getContext(), mMenuContainer, mWorkSpace, groupId, effectIndex);
          } else {
            BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
            mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition());
            if (mEffectPluginAttriAdapter != null) {
              mEffectPluginAttriAdapter.updateList(getOperateList(item));
            }
          }
        }
      };

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_tools_plugin_title);
  }

  private List<EffectPluginAttriAdapter.PluginEditItem> getOperateList(SubPluginInfo subPluginInfo) {
    List<EffectPluginAttriAdapter.PluginEditItem> list = new ArrayList<>();
    if (subPluginInfo == null) {
      return list;
    }
    for (SubPluginAttriItem item : subPluginInfo.attributeList) {
      list.add(new EffectPluginAttriAdapter.PluginEditItem(R.drawable.editor_icon_collage_tool_framework,
          item.name));
    }
    list.add(new EffectPluginAttriAdapter.PluginEditItem(R.drawable.edit_icon_delete_nor,
        getContext().getString(R.string.mn_edit_title_delete)));
    return list;
  }
}
