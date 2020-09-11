package com.quvideo.application.editor.effect.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quvideo.application.DPUtils;
import com.quvideo.application.EditorApp;
import com.quvideo.application.download.DownloadDialog;
import com.quvideo.application.editor.EditorActivity;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.superedit.ZXingManager;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.QEXytUtil;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.effect.SubPluginAttriItem;
import com.quvideo.mobile.engine.model.effect.SubPluginInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubPluginAdd;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubPluginDel;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubPluginDisable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

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
    mPluginAdapter.updateList(baseEffect.mEffectSubPluginList, -1);
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

  @Override public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (resultCode == Activity.RESULT_OK && requestCode == EditorActivity.INTENT_REQUEST_QRCODE && data != null) {
      String result = data.getStringExtra(ZXingManager.ZXING_RESULT_QRMSG);
      if (!TextUtils.isEmpty(result)) {
        try {
          JSONObject jsonObject = new JSONObject(result);
          String ttid = jsonObject.optString("ttid");
          String url = jsonObject.optString("url");
          if (!TextUtils.isEmpty(ttid) && !TextUtils.isEmpty(url)) {
            if (!ttid.contains("0x04006")) {
              // 需要效果插件，但不是效果插件
              ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                  R.string.mn_edit_tips_template_qrcode_error, Toast.LENGTH_LONG);
              return true;
            }
            DownloadDialog downloadDialog = new DownloadDialog(new DownloadDialog.OnTemplateDownloadOver() {
              @Override public void onDownloadOver(String templateCode) {
                handleAddEffect(templateCode);
              }
            });
            downloadDialog.showDownloading(getActivity(), ttid, url);
            return true;
          }
        } catch (Exception ignore) {
        }
      }
    }
    return false;
  }

  private void handleAddEffect(String templateCode) {
    long templateId = QEXytUtil.ttidHexStrToLong(templateCode);
    if (templateId <= 0) {
      // 无滤镜
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
          R.string.mn_edit_tips_error_template, Toast.LENGTH_LONG);
      return;
    }
    XytInfo info = XytManager.getXytInfo(templateId);
    if (info == null) {
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
          R.string.mn_edit_tips_error_template, Toast.LENGTH_LONG);
      return;
    }
    EffectOPSubPluginAdd effectOPSubPluginAdd = new EffectOPSubPluginAdd(groupId, effectIndex, info.filePath);
    mWorkSpace.handleOperation(effectOPSubPluginAdd);
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof EffectOPSubPluginAdd
          || operate instanceof EffectOPSubPluginDisable
          || operate instanceof EffectOPSubPluginDel) {
        // 刷新数据
        if (mWorkSpace.getPlayerAPI() != null
            && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
          mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        }
        AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        mPluginAdapter.updateList(baseEffect.mEffectSubPluginList,
            operate instanceof EffectOPSubPluginAdd ? baseEffect.mEffectSubPluginList.size() - 1 : -1);
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
      } else if (operate.getResId() == R.drawable.edit_icon_subeffect_on) {
        // 关闭
        int selectIndex = mPluginAdapter.getSelectIndex();
        if (selectIndex >= 0 && selectIndex < baseEffect.mEffectSubPluginList.size()) {
          SubPluginInfo subPluginInfo = baseEffect.mEffectSubPluginList.get(selectIndex);
          EffectOPSubPluginDisable effectOPSubPluginDisable = new EffectOPSubPluginDisable(groupId, effectIndex,
              subPluginInfo.getSubType(), !subPluginInfo.disable);
          mWorkSpace.handleOperation(effectOPSubPluginDisable);
        } else {
          ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
              R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        }
      } else if (operate.getResId() == R.drawable.editor_tool_qrcode_scan) {
        // 二维码扫描
        ZXingManager.go2CaptureActivity(getActivity(), EditorActivity.INTENT_REQUEST_QRCODE);
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
      if (ZXingManager.isHadSuperZXing()) {
        list.add(new EffectPluginAttriAdapter.PluginEditItem(R.drawable.editor_tool_qrcode_scan,
            getContext().getString(R.string.mn_edit_qrcode_scan)));
      }
      return list;
    }
    AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    // 删除
    int selectIndex = mPluginAdapter.getSelectIndex();
    boolean disalbe = false;
    if (selectIndex >= 0 && selectIndex < baseEffect.mEffectSubPluginList.size()) {
      disalbe = baseEffect.mEffectSubPluginList.get(selectIndex).disable;
    }
    for (SubPluginAttriItem item : subPluginInfo.attributeList) {
      list.add(new EffectPluginAttriAdapter.PluginEditItem(R.drawable.editor_icon_collage_tool_framework,
          item.name));
    }
    if (disalbe) {
      list.add(new EffectPluginAttriAdapter.PluginEditItem(R.drawable.edit_icon_subeffect_on,
          getContext().getString(R.string.mn_edit_subeffect_off)));
    } else {
      list.add(new EffectPluginAttriAdapter.PluginEditItem(R.drawable.edit_icon_subeffect_on,
          getContext().getString(R.string.mn_edit_subeffect_on)));
    }
    list.add(new EffectPluginAttriAdapter.PluginEditItem(R.drawable.edit_icon_delete_nor,
        getContext().getString(R.string.mn_edit_title_delete)));

    if (ZXingManager.isHadSuperZXing()) {
      list.add(new EffectPluginAttriAdapter.PluginEditItem(R.drawable.editor_tool_qrcode_scan,
          getContext().getString(R.string.mn_edit_qrcode_scan)));
    }
    return list;
  }
}
