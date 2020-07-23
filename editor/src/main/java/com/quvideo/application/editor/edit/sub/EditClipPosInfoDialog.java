package com.quvideo.application.editor.edit.sub;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.FakePosUtils;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.ClipPosDraw;
import com.quvideo.mobile.engine.entity.VeMSize;
import com.quvideo.mobile.engine.model.ClipData;
import com.quvideo.mobile.engine.model.clip.ClipPosInfo;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPMirror;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPPosInfo;
import com.quvideo.mobile.engine.work.operate.clip.ClipOPRotate;

public class EditClipPosInfoDialog extends BaseMenuView {

  private View btnMirror;
  private View btnFlip;
  private View btnRotation;
  private View btnFitIn;
  private View btnFitOut;

  private int clipIndex;

  private VeMSize clipSourceSize;

  public EditClipPosInfoDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int clipIndex, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.clipIndex = clipIndex;
    clipSourceSize = mWorkSpace.getClipAPI().getClipByIndex(clipIndex).getSourceSize();
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.ClipPosInfo;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_clip_posinfo;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    btnMirror = view.findViewById(R.id.btnMirror);
    btnFlip = view.findViewById(R.id.btnFlip);
    btnRotation = view.findViewById(R.id.btnRotation);
    btnFitIn = view.findViewById(R.id.btnFitIn);
    btnFitOut = view.findViewById(R.id.btnFitOut);

    btnMirror.setOnClickListener(mOnClickListener);
    btnFlip.setOnClickListener(mOnClickListener);
    btnRotation.setOnClickListener(mOnClickListener);
    btnFitIn.setOnClickListener(mOnClickListener);
    btnFitOut.setOnClickListener(mOnClickListener);
    initFakeView();
  }

  private void initFakeView() {
    mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
    ClipPosInfo clipPosInfo = mWorkSpace.getClipAPI().getClipByIndex(clipIndex).getClipPosInfo();
    mFakeApi.setClipTarget(new ClipPosDraw(), clipPosInfo, mWorkSpace.getStoryboardAPI().getStreamSize());
    mFakeApi.setFakeViewListener(new IFakeViewListener() {

      @Override public void onEffectMoving() {
        FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
        ClipPosInfo targetClipPosInfo = new ClipPosInfo();
        FakePosUtils.INSTANCE.updateClipPos2FakePos(curFakePos, targetClipPosInfo,
            mWorkSpace.getStoryboardAPI().getStreamSize(), clipSourceSize);
        ClipOPPosInfo clipOPPosInfo = new ClipOPPosInfo(clipIndex, targetClipPosInfo);
        mWorkSpace.handleOperation(clipOPPosInfo);
      }

      @Override public void onEffectMoveStart() {
      }

      @Override public void onEffectMoveEnd(boolean moved) {
      }

      @Override public void checkEffectTouchHit(PointF pointF) {
      }
    });
  }

  private OnClickListener mOnClickListener = new OnClickListener() {
    @Override public void onClick(View v) {
      if (v.equals(btnMirror)) {
        ClipData.Mirror mirror = mWorkSpace.getClipAPI().getClipByIndex(clipIndex).getMirror();
        if (mirror == ClipData.Mirror.CLIP_FLIP_NONE) {
          mirror = ClipData.Mirror.CLIP_FLIP_X;
        } else if (mirror == ClipData.Mirror.CLIP_FLIP_X) {
          mirror = ClipData.Mirror.CLIP_FLIP_NONE;
        } else if (mirror == ClipData.Mirror.CLIP_FLIP_Y) {
          mirror = ClipData.Mirror.CLIP_FLIP_XY;
        } else if (mirror == ClipData.Mirror.CLIP_FLIP_XY) {
          mirror = ClipData.Mirror.CLIP_FLIP_Y;
        }
        ClipOPMirror clipOPMirror = new ClipOPMirror(clipIndex, mirror);
        mWorkSpace.handleOperation(clipOPMirror);
      } else if (v.equals(btnFlip)) {
        ClipData.Mirror mirror = mWorkSpace.getClipAPI().getClipByIndex(clipIndex).getMirror();
        if (mirror == ClipData.Mirror.CLIP_FLIP_NONE) {
          mirror = ClipData.Mirror.CLIP_FLIP_Y;
        } else if (mirror == ClipData.Mirror.CLIP_FLIP_X) {
          mirror = ClipData.Mirror.CLIP_FLIP_XY;
        } else if (mirror == ClipData.Mirror.CLIP_FLIP_Y) {
          mirror = ClipData.Mirror.CLIP_FLIP_NONE;
        } else if (mirror == ClipData.Mirror.CLIP_FLIP_XY) {
          mirror = ClipData.Mirror.CLIP_FLIP_X;
        }
        ClipOPMirror clipOPMirror = new ClipOPMirror(clipIndex, mirror);
        mWorkSpace.handleOperation(clipOPMirror);
      } else if (v.equals(btnRotation)) {
        ClipData clipData = mWorkSpace.getClipAPI().getClipList().get(clipIndex);
        int rotate = clipData.getRotateAngle();
        ClipOPRotate clipOPRotate = new ClipOPRotate(clipIndex, rotate + 90);
        mWorkSpace.handleOperation(clipOPRotate);
      } else if (v.equals(btnFitIn)) {
        ClipOPPosInfo clipOPPosInfo = new ClipOPPosInfo(clipIndex, false);
        mWorkSpace.handleOperation(clipOPPosInfo);
      } else if (v.equals(btnFitOut)) {
        ClipOPPosInfo clipOPPosInfo = new ClipOPPosInfo(clipIndex, true);
        mWorkSpace.handleOperation(clipOPPosInfo);
      }
    }
  };

  @Override protected void releaseAll() {
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_effect_position);
  }
}
