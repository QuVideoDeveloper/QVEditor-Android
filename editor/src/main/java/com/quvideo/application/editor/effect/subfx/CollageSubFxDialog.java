package com.quvideo.application.editor.effect.subfx;

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
import com.quvideo.application.editor.base.IEffectEditClickListener;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.EffectBarItem;
import com.quvideo.application.editor.effect.EffectOperateAdapter;
import com.quvideo.application.superedit.ZXingManager;
import com.quvideo.application.utils.ToastUtils;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import com.quvideo.mobile.engine.QEXytUtil;
import com.quvideo.mobile.engine.constant.QEGroupConst;
import com.quvideo.mobile.engine.entity.VeRange;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.effect.EffectSubFx;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubFxAdd;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubFxDel;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubFxDisable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class CollageSubFxDialog extends BaseMenuView {

  private int groupId = 0;
  private int effectIndex = 0;

  private RecyclerView mRecyclerView;
  private CollageSubFxAdapter mSubFxAdapter;

  private EffectOperateAdapter mEffectOperateAdapter;

  public CollageSubFxDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    workSpace.getPlayerAPI().getPlayerControl().pause();
    mWorkSpace.addObserver(mBaseObserver);
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.CollageFx;
  }

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof EffectOPSubFxAdd
          || operate instanceof EffectOPSubFxDisable
          || operate instanceof EffectOPSubFxDel) {
        // 刷新数据
        if (mWorkSpace.getPlayerAPI() != null
            && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
          mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        }
        AnimEffect baseEffect = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex));
        List<EffectSubFx> dataList = baseEffect.mEffectSubFxList;
        mSubFxAdapter.updateList(dataList);
        int selectIndex = mSubFxAdapter.getSelectIndex();
        if (selectIndex >= 0) {
          try {
            EffectSubFx item = dataList.get(selectIndex);
            if (item != null) {
              if (mWorkSpace.getPlayerAPI() != null
                  && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
                mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition() + item.getDestRange().getPosition());
              }
            }
          } catch (Throwable ignore) {
          }
        }
        if (mEffectOperateAdapter != null) {
          mEffectOperateAdapter.updateList(getOperateList());
        }
      }
    }
  };

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    View rootView = view.findViewById(R.id.root_layout);
    rootView.setOnClickListener(v -> {
      // 只是为了拦击点击事件
    });
    mSubFxAdapter =
        new CollageSubFxAdapter(mWorkSpace, getActivity(), groupId, mOnEffectClickListener);
    // effect
    mRecyclerView = view.findViewById(R.id.clip_recyclerview);
    mRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mRecyclerView.setAdapter(mSubFxAdapter);
    mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
          @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int dp2 = DPUtils.dpToPixel(getContext(), 4);
        outRect.left = dp2;
        outRect.right = dp2;
      }
    });

    AnimEffect baseEffect = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex));
    List<EffectSubFx> dataList = baseEffect.mEffectSubFxList;
    mSubFxAdapter.updateList(dataList);
    int selectIndex = mSubFxAdapter.getSelectIndex();
    if (selectIndex >= 0) {
      try {
        EffectSubFx item = dataList.get(selectIndex);
        if (item != null) {
          if (mWorkSpace.getPlayerAPI() != null
              && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
            mWorkSpace.getPlayerAPI().getPlayerControl().seek(baseEffect.destRange.getPosition() + item.getDestRange().getPosition());
          }
        }
      } catch (Throwable ignore) {
      }
    }
    // 操作view
    RecyclerView editRecyclerView = view.findViewById(R.id.operate_recyclerview);
    editRecyclerView.setLayoutManager(
        new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    mEffectOperateAdapter = new EffectOperateAdapter(getOperateList(), mItemOnClickListener);
    editRecyclerView.setAdapter(mEffectOperateAdapter);
  }

  @Override protected void releaseAll() {
    if (mWorkSpace != null) {
      mWorkSpace.removeObserver(mBaseObserver);
    }
  }

  private IEffectEditClickListener mItemOnClickListener = new IEffectEditClickListener() {
    @Override public void onClick(View view, EffectBarItem operate) {
      if (operate != null && operate.getAction() == EffectBarItem.ACTION_QRCODE) {
        ZXingManager.go2CaptureActivity(getActivity(), EditorActivity.INTENT_REQUEST_QRCODE);
        return;
      }
      int index = mSubFxAdapter.getSelectIndex();
      AnimEffect baseEffect = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex));
      if (index < 0 || baseEffect == null) {
        ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
            R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        return;
      }
      List<EffectSubFx> effectSubFxes = baseEffect.mEffectSubFxList;
      if (effectSubFxes == null || effectSubFxes.size() < 1 || index > effectSubFxes.size() - 1) {
        ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
            R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        return;
      }
      EffectSubFx item = effectSubFxes.get(index);
      if (item == null) {
        ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
            R.string.mn_edit_tips_cannot_operate, Toast.LENGTH_LONG);
        return;
      }
      switch (operate.getAction()) {
        case EffectBarItem.ACTION_TRIM:
          new CollageSubFxTrimDialog(getContext(), mMenuContainer, mWorkSpace, groupId, effectIndex, item.getSubType());
          break;
        case EffectBarItem.ACTION_DEL:
          EffectOPSubFxDel effectOPSubFxDel = new EffectOPSubFxDel(groupId, effectIndex, item.getSubType());
          mWorkSpace.handleOperation(effectOPSubFxDel);
          break;
        case EffectBarItem.ACTION_COLLAGE_SUBEFFECT_DISABLE:
          EffectOPSubFxDisable effectOPSubFxDisable = new EffectOPSubFxDisable(groupId, effectIndex, item.getSubType(), !item.disable);
          mWorkSpace.handleOperation(effectOPSubFxDisable);
          break;
        default:
          ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
              getContext().getString(R.string.mn_edit_tips_no_define),
              Toast.LENGTH_LONG);
          break;
      }
    }
  };

  @Override public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (resultCode == Activity.RESULT_OK && requestCode == EditorActivity.INTENT_REQUEST_QRCODE && data != null) {
      String result = data.getStringExtra(ZXingManager.ZXING_RESULT_QRMSG);
      if (!TextUtils.isEmpty(result)) {
        try {
          JSONObject jsonObject = new JSONObject(result);
          String ttid = jsonObject.optString("ttid");
          String url = jsonObject.optString("url");
          if (groupId != QEGroupConst.GROUP_ID_STICKER
              && groupId != QEGroupConst.GROUP_ID_STICKER_FX
              && groupId != QEGroupConst.GROUP_ID_SUBTITLE) {
            ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
                getContext().getString(R.string.mn_edit_tips_no_support),
                Toast.LENGTH_LONG);
            return false;
          }
          if (!TextUtils.isEmpty(ttid) && !TextUtils.isEmpty(url)) {
            if (!ttid.contains("0x06")) {
              // 需要特效，但不是特效素材
              // 无滤镜
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
    if (info == null || !QEXytUtil.isSupportSubFx(info.filePath)) {
      ToastUtils.show(EditorApp.Companion.getInstance().getApp(),
          R.string.mn_edit_tips_error_template, Toast.LENGTH_LONG);
      return;
    }
    AnimEffect baseEffect = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex));
    EffectOPSubFxAdd effectOPSubFxAdd =
        new EffectOPSubFxAdd(groupId, effectIndex, info.filePath, new VeRange(0, baseEffect.destRange.getTimeLength()));
    mWorkSpace.handleOperation(effectOPSubFxAdd);
  }

  private CollageSubFxAdapter.OnSubFxClickListener mOnEffectClickListener =
      new CollageSubFxAdapter.OnSubFxClickListener() {
        @Override public void onClick(int index, EffectSubFx item) {
          if (item == null || index < 0) {
            // TODO 添加
            new CollageSubFxAddDialog(getContext(), mMenuContainer, mWorkSpace, groupId, effectIndex);
          } else {
            AnimEffect baseEffect = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex));
            int selectIndex = mSubFxAdapter.getSelectIndex();
            if (selectIndex >= 0) {
              try {
                if (mWorkSpace.getPlayerAPI() != null
                    && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
                  mWorkSpace.getPlayerAPI().getPlayerControl().pause();
                  mWorkSpace.getPlayerAPI()
                      .getPlayerControl()
                      .seek(baseEffect.destRange.getPosition() + item.getDestRange().getPosition());
                }
              } catch (Throwable ignore) {
              }
            }
            if (mEffectOperateAdapter != null) {
              mEffectOperateAdapter.updateList(getOperateList());
            }
          }
        }
      };

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_title_fx);
  }

  private List<EffectBarItem> getOperateList() {
    List<EffectBarItem> list = new ArrayList<>();
    int selectIndex = mSubFxAdapter.getSelectIndex();
    boolean isOpEnabled = selectIndex >= 0;

    AnimEffect baseEffect = ((AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex));
    List<EffectSubFx> dataList = baseEffect.mEffectSubFxList;
    mSubFxAdapter.updateList(dataList);
    boolean disable = false;
    if (selectIndex >= 0) {
      try {
        EffectSubFx item = dataList.get(selectIndex);
        if (item != null) {
          disable = item.disable;
        }
      } catch (Throwable ignore) {
      }
    }
    if (ZXingManager.isHadSuperZXing()) {
      list.add(new EffectBarItem(EffectBarItem.ACTION_QRCODE, R.drawable.editor_tool_qrcode_scan,
          getContext().getString(R.string.mn_edit_qrcode_scan), true));
    }
    list.add(
        new EffectBarItem(EffectBarItem.ACTION_TRIM, R.drawable.edit_icon_trim_n,
            getContext().getString(R.string.mn_edit_title_trim), isOpEnabled));
    if (disable) {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_COLLAGE_SUBEFFECT_DISABLE, R.drawable.edit_icon_subeffect_on,
              getContext().getString(R.string.mn_edit_subeffect_off), isOpEnabled));
    } else {
      list.add(
          new EffectBarItem(EffectBarItem.ACTION_COLLAGE_SUBEFFECT_DISABLE, R.drawable.edit_icon_subeffect_on,
              getContext().getString(R.string.mn_edit_subeffect_on), isOpEnabled));
    }

    list.add(new EffectBarItem(EffectBarItem.ACTION_DEL, R.drawable.edit_icon_delete_nor,
        getContext().getString(R.string.mn_edit_title_delete), isOpEnabled));
    return list;
  }
}
