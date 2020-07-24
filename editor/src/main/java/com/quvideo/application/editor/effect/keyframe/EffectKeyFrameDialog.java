package com.quvideo.application.editor.effect.keyframe;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseEffectMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.fake.FakePosInfo;
import com.quvideo.application.editor.fake.IFakeViewApi;
import com.quvideo.application.editor.fake.IFakeViewListener;
import com.quvideo.application.editor.fake.draw.KeyFramePosDraw;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.entity.Ve3DDataF;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.BaseEffect;
import com.quvideo.mobile.engine.model.FloatEffect;
import com.quvideo.mobile.engine.model.effect.EffectPosInfo;
import com.quvideo.mobile.engine.model.effect.keyframe.BaseKeyFrame;
import com.quvideo.mobile.engine.model.effect.keyframe.KeyAlphaInfo;
import com.quvideo.mobile.engine.model.effect.keyframe.KeyBezierCurve;
import com.quvideo.mobile.engine.model.effect.keyframe.KeyPosInfo;
import com.quvideo.mobile.engine.model.effect.keyframe.KeyRotationInfo;
import com.quvideo.mobile.engine.model.effect.keyframe.KeyScaleInfo;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPKeyFrame;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPKeyFrameInsert;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPKeyFrameRemove;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPKeyFrameUpdate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPKeyFrameUpdateOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import xiaoying.utils.QPoint;

public class EffectKeyFrameDialog extends BaseEffectMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private TextView btnKeyPosition;
  private TextView btnKeyRotate;
  private TextView btnKeyScale;
  private TextView btnKeyAlpha;
  private View btnCurve;
  private View btnDel;

  private KeyFrameTimeline mKeyFrameTimeline;
  private CustomSeekbarPop mCustomSeekbarPop;

  private KeyFramePosDraw mKeyFramePosDraw = new KeyFramePosDraw();

  private BaseKeyFrame.KeyFrameType curKeyFrameType = BaseKeyFrame.KeyFrameType.Position;

  private int curPlayerTime = 0;
  private int startTime = 0;
  private int maxTime = 0;

  public EffectKeyFrameDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex, IFakeViewApi fakeViewApi) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    showMenu(container, null, fakeViewApi);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectKeyFrame;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_keyframe;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    btnKeyPosition = view.findViewById(R.id.btn_keyframe_location);
    btnKeyRotate = view.findViewById(R.id.btn_keyframe_rotate);
    btnKeyScale = view.findViewById(R.id.btn_keyframe_zoom);
    btnKeyAlpha = view.findViewById(R.id.btn_keyframe_alpha);
    btnCurve = view.findViewById(R.id.btn_curve);
    btnDel = view.findViewById(R.id.btn_delete);
    mKeyFrameTimeline = view.findViewById(R.id.v_keyframe_timeline);
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);

    btnKeyPosition.setBackgroundResource(R.drawable.edit_item_bg_selected);

    btnKeyPosition.setOnClickListener(mOnClickListener);
    btnKeyRotate.setOnClickListener(mOnClickListener);
    btnKeyScale.setOnClickListener(mOnClickListener);
    btnKeyAlpha.setOnClickListener(mOnClickListener);
    btnCurve.setOnClickListener(mOnClickListener);
    btnDel.setOnClickListener(mOnClickListener);
    initFakeView();

    curPlayerTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    startTime = baseEffect.destRange.getPosition();
    if (baseEffect.destRange.getTimeLength() > 0) {
      maxTime = baseEffect.destRange.getLimitValue();
    } else {
      maxTime = mWorkSpace.getPlayerAPI().getPlayerControl().getPlayerDuration();
    }

    mKeyFrameTimeline.setMaxOffsetTime(maxTime - startTime, new KeyFrameTimeline.OnKeyFrameListener() {
      @Override public void onKeyFrameClick(int focusTs) {
        mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime + focusTs);
        curPlayerTime = startTime + focusTs;
        mKeyFrameTimeline.setCurOffsetTime(focusTs);
        updateBtnEnable(focusTs);
        updatePosAndAlpha(curPlayerTime);
      }
    });
    updateKeyFrameTimeline();

    mWorkSpace.addObserver(mBaseObserver);
    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);

    mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
        .start("0")
        .end("100")
        .progress(100)
        .seekRange(new CustomSeekbarPop.SeekRange(0, 100))
        .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
          @Override public void onSeekStart(boolean isFirst, int progress) {
            mWorkSpace.getPlayerAPI().getPlayerControl().pause();
          }

          @Override public void onSeekOver(boolean isFirst, int progress) {
            BaseEffect effect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
            List<KeyAlphaInfo> baseKeyFrames = ((AnimEffect) effect).mEffectKeyFrameInfo.alphaList;
            int focusTs = mKeyFrameTimeline.getFocusTs();
            boolean update = false;
            if (focusTs >= 0) {
              if (baseKeyFrames != null) {
                for (KeyAlphaInfo baseKeyFrame : baseKeyFrames) {
                  if (baseKeyFrame.relativeTime == focusTs) {
                    update = true;
                    baseKeyFrame.alpha = progress;
                    EffectOPKeyFrameUpdate effectOPKeyFrameUpdate = new EffectOPKeyFrameUpdate(groupId, effectIndex,
                        BaseKeyFrame.KeyFrameType.Alpha, baseKeyFrames);
                    mWorkSpace.handleOperation(effectOPKeyFrameUpdate);
                    //EffectOPKeyFrame effectOPKeyFrame =
                    //    new EffectOPKeyFrame(groupId, effectIndex, ((AnimEffect) effect).mEffectKeyFrameInfo);
                    //mWorkSpace.handleOperation(effectOPKeyFrame);
                    break;
                  }
                }
              }
            }
            if (!update) {
              if (baseKeyFrames == null) {
                baseKeyFrames = new ArrayList<>();
              }
              KeyAlphaInfo item = new KeyAlphaInfo(curPlayerTime - startTime, progress);
              baseKeyFrames.add(item);
              Collections.sort(baseKeyFrames);
              //EffectOPKeyFrame effectOPKeyFrame =
              //    new EffectOPKeyFrame(groupId, effectIndex, ((AnimEffect) effect).mEffectKeyFrameInfo);
              //mWorkSpace.handleOperation(effectOPKeyFrame);
              EffectOPKeyFrameUpdate effectOPKeyFrameUpdate = new EffectOPKeyFrameUpdate(groupId, effectIndex,
                  BaseKeyFrame.KeyFrameType.Alpha, baseKeyFrames);
              mWorkSpace.handleOperation(effectOPKeyFrameUpdate);
            }
          }

          @Override public void onSeekChange(boolean isFirst, int progress) {
          }
        }));
  }

  private void initFakeView() {
    mFakeApi.setStreamSize(mWorkSpace.getStoryboardAPI().getStreamSize());
    mKeyFramePosDraw.setNormalFake(0);
    updatePosAndAlpha(curPlayerTime);
    mFakeApi.setFakeViewListener(new IFakeViewListener() {

      @Override public void onEffectMoving() {
        FakePosInfo curFakePos = mFakeApi.getFakePosInfo();
        BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        int focusTs = mKeyFrameTimeline.getFocusTs();
        BaseKeyFrame baseKeyFrame = null;
        boolean update = false;
        if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Position) {
          List<KeyPosInfo> baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.positionList;
          if (focusTs >= 0) {
            if (baseKeyFrames != null) {
              for (KeyPosInfo item : baseKeyFrames) {
                if (item.relativeTime == focusTs) {
                  update = true;
                  item.center = new Ve3DDataF(curFakePos.getCenterX(), curFakePos.getCenterY(), 0);
                  baseKeyFrame = item;
                  break;
                }
              }
            }
          }
          if (!update) {
            baseKeyFrame = new KeyPosInfo(curPlayerTime - startTime,
                curFakePos.getCenterX(), curFakePos.getCenterY(), 0);
          }
        } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Rotation) {
          List<KeyRotationInfo> baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.rotationList;
          if (focusTs >= 0) {
            if (baseKeyFrames != null) {
              for (KeyRotationInfo item : baseKeyFrames) {
                if (item.relativeTime == focusTs) {
                  update = true;
                  item.rotation = new Ve3DDataF(0, 0, curFakePos.getDegrees());
                  baseKeyFrame = item;
                  break;
                }
              }
            }
          }
          if (!update) {
            baseKeyFrame = new KeyRotationInfo(curPlayerTime - startTime,
                0, 0, curFakePos.getDegrees());
          }
        } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Scale) {
          EffectPosInfo oriPosInfo = ((FloatEffect) baseEffect).mEffectPosInfo;
          List<KeyScaleInfo> baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.scaleList;
          if (focusTs >= 0) {
            if (baseKeyFrames != null) {
              for (KeyScaleInfo item : baseKeyFrames) {
                if (item.relativeTime == focusTs) {
                  update = true;
                  item.scale = new Ve3DDataF(curFakePos.getWidth() / oriPosInfo.size.x,
                      curFakePos.getHeight() / oriPosInfo.size.y, 1f);
                  baseKeyFrame = item;
                  break;
                }
              }
            }
          }
          if (!update) {
            baseKeyFrame = new KeyScaleInfo(curPlayerTime - startTime,
                curFakePos.getWidth() / oriPosInfo.size.x, curFakePos.getHeight() / oriPosInfo.size.y, 1f);
          }
        }
        if (baseKeyFrame != null) {
          EffectOPKeyFrameInsert effectOPKeyFrameInsert = new EffectOPKeyFrameInsert(groupId, effectIndex, baseKeyFrame);
          mWorkSpace.handleOperation(effectOPKeyFrameInsert);
        }
      }

      @Override public void onEffectMoveStart() {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
      }

      @Override public void onEffectMoveEnd(boolean moved) {
      }

      @Override public void checkEffectTouchHit(@NotNull PointF pointF) {
      }
    });
  }

  private void updateKeyFrameTimeline() {
    BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Position) {
      btnKeyPosition.setBackgroundResource(R.drawable.edit_item_bg_selected);
      btnKeyRotate.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnKeyScale.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnKeyAlpha.setBackgroundResource(R.drawable.edit_item_bg_normal);
      mCustomSeekbarPop.setVisibility(INVISIBLE);
      mKeyFrameTimeline.setKeyFrameData(ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.color_ff7b2e),
          ((AnimEffect) baseEffect).mEffectKeyFrameInfo.positionList);
    } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Rotation) {
      btnKeyPosition.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnKeyRotate.setBackgroundResource(R.drawable.edit_item_bg_selected);
      btnKeyScale.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnKeyAlpha.setBackgroundResource(R.drawable.edit_item_bg_normal);
      mCustomSeekbarPop.setVisibility(INVISIBLE);
      mKeyFrameTimeline.setKeyFrameData(ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.color_3493f2),
          ((AnimEffect) baseEffect).mEffectKeyFrameInfo.rotationList);
    } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Scale) {
      btnKeyPosition.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnKeyRotate.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnKeyScale.setBackgroundResource(R.drawable.edit_item_bg_selected);
      btnKeyAlpha.setBackgroundResource(R.drawable.edit_item_bg_normal);
      mCustomSeekbarPop.setVisibility(INVISIBLE);
      mKeyFrameTimeline.setKeyFrameData(ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.color_00b300),
          ((AnimEffect) baseEffect).mEffectKeyFrameInfo.scaleList);
    } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Alpha) {
      btnKeyPosition.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnKeyRotate.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnKeyScale.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnKeyAlpha.setBackgroundResource(R.drawable.edit_item_bg_selected);
      mCustomSeekbarPop.setVisibility(VISIBLE);
      mKeyFrameTimeline.setKeyFrameData(ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.color_ff45454d),
          ((AnimEffect) baseEffect).mEffectKeyFrameInfo.alphaList);
    }
    mKeyFrameTimeline.setCurOffsetTime(curPlayerTime - startTime);
    updateBtnEnable(mKeyFrameTimeline.getFocusTs());
  }

  private void updateBtnEnable(int focusTs) {
    btnDel.setEnabled(focusTs >= 0);
    btnDel.setAlpha(focusTs >= 0 ? 1f : 0.1f);
    if (focusTs >= 0) {
      BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
      List<? extends BaseKeyFrame> baseKeyFrames = null;
      if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Position) {
        baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.positionList;
      } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Rotation) {
        baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.rotationList;
      } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Scale) {
        baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.scaleList;
      } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Alpha) {
        baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.alphaList;
      }
      btnCurve.setEnabled(true);
      btnCurve.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnCurve.setAlpha(1f);
      if (baseKeyFrames != null) {
        for (BaseKeyFrame baseKeyFrame : baseKeyFrames) {
          if (baseKeyFrame.relativeTime == focusTs) {
            btnCurve.setBackgroundResource(baseKeyFrame.mKeyBezierCurve != null ?
                R.drawable.edit_item_bg_selected : R.drawable.edit_item_bg_normal);
            break;
          }
        }
      }
    } else {
      btnCurve.setEnabled(false);
      btnCurve.setBackgroundResource(R.drawable.edit_item_bg_normal);
      btnCurve.setAlpha(0.1f);
    }
  }

  private void updatePosAndAlpha(int curTime) {
    if (curTime < startTime || curTime > maxTime) {
      mFakeApi.setTarget(null, null);
      mCustomSeekbarPop.setVisibility(GONE);
    } else {
      EffectPosInfo effectPosInfo = mWorkSpace.getEffectPosInfoByTime(groupId, effectIndex, curTime - startTime);
      mFakeApi.setTarget(mKeyFramePosDraw, effectPosInfo);
      int alpha = mWorkSpace.getAlphaInfoByTime(groupId, effectIndex, curTime - startTime);
      mCustomSeekbarPop.setProgress(alpha);
      if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Alpha) {
        mCustomSeekbarPop.setVisibility(VISIBLE);
      }
    }
  }

  private View.OnClickListener mOnClickListener = new OnClickListener() {
    @Override public void onClick(View v) {
      if (v.equals(btnKeyPosition)) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        curKeyFrameType = BaseKeyFrame.KeyFrameType.Position;
        mKeyFramePosDraw.setNormalFake(0);
        updateKeyFrameTimeline();
      } else if (v.equals(btnKeyRotate)) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        curKeyFrameType = BaseKeyFrame.KeyFrameType.Rotation;
        mKeyFramePosDraw.setNormalFake(2);
        updateKeyFrameTimeline();
      } else if (v.equals(btnKeyScale)) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        curKeyFrameType = BaseKeyFrame.KeyFrameType.Scale;
        mKeyFramePosDraw.setNormalFake(1);
        updateKeyFrameTimeline();
      } else if (v.equals(btnKeyAlpha)) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        curKeyFrameType = BaseKeyFrame.KeyFrameType.Alpha;
        mKeyFramePosDraw.setNormalFake(3);
        updateKeyFrameTimeline();
      } else if (v.equals(btnCurve)) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        int focusTs = mKeyFrameTimeline.getFocusTs();
        if (focusTs >= 0) {
          BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
          List<? extends BaseKeyFrame> baseKeyFrames = null;
          if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Position) {
            baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.positionList;
          } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Rotation) {
            baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.rotationList;
          } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Scale) {
            baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.scaleList;
          } else if (curKeyFrameType == BaseKeyFrame.KeyFrameType.Alpha) {
            baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.alphaList;
          }
          if (baseKeyFrames != null) {
            for (BaseKeyFrame baseKeyFrame : baseKeyFrames) {
              if (baseKeyFrame.relativeTime == focusTs) {
                if (curKeyFrameType != BaseKeyFrame.KeyFrameType.Alpha) {
                  try {
                    BaseKeyFrame keyFrame = baseKeyFrame.clone();
                    if (keyFrame.mKeyBezierCurve == null) {
                      KeyBezierCurve keyBezierCurve = new KeyBezierCurve();
                      keyBezierCurve.start = new QPoint(0, 0);
                      keyBezierCurve.c0 = new QPoint(8600, 0);
                      keyBezierCurve.c1 = new QPoint(1300, 10000);
                      keyBezierCurve.stop = new QPoint(10000, 10000);
                      keyFrame.mKeyBezierCurve = keyBezierCurve;
                    } else {
                      keyFrame.mKeyBezierCurve = null;
                    }
                    EffectOPKeyFrameInsert effectOPKeyFrameInsert = new EffectOPKeyFrameInsert(groupId, effectIndex, keyFrame);
                    mWorkSpace.handleOperation(effectOPKeyFrameInsert);
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                } else {
                  if (baseKeyFrame.mKeyBezierCurve == null) {
                    KeyBezierCurve keyBezierCurve = new KeyBezierCurve();
                    keyBezierCurve.start = new QPoint(0, 0);
                    keyBezierCurve.c0 = new QPoint(8600, 0);
                    keyBezierCurve.c1 = new QPoint(1300, 10000);
                    keyBezierCurve.stop = new QPoint(10000, 10000);
                    baseKeyFrame.mKeyBezierCurve = keyBezierCurve;
                  } else {
                    baseKeyFrame.mKeyBezierCurve = null;
                  }
                  EffectOPKeyFrame effectOPKeyFrame =
                      new EffectOPKeyFrame(groupId, effectIndex, ((AnimEffect) baseEffect).mEffectKeyFrameInfo);
                  mWorkSpace.handleOperation(effectOPKeyFrame);
                }
                break;
              }
            }
          }
        }
      } else if (v.equals(btnDel)) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        btnDel.setEnabled(false);
        btnDel.setAlpha(0.1f);
        if (mKeyFrameTimeline.getFocusTs() >= 0) {
          if (curKeyFrameType != BaseKeyFrame.KeyFrameType.Alpha) {
            EffectOPKeyFrameRemove effectOPKeyFrameRemove = new EffectOPKeyFrameRemove(groupId, effectIndex,
                curKeyFrameType, mKeyFrameTimeline.getFocusTs());
            mWorkSpace.handleOperation(effectOPKeyFrameRemove);
          } else {
            BaseEffect baseEffect = mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
            List<? extends BaseKeyFrame> baseKeyFrames = ((AnimEffect) baseEffect).mEffectKeyFrameInfo.alphaList;
            int focusTs = mKeyFrameTimeline.getFocusTs();
            if (baseKeyFrames != null) {
              for (BaseKeyFrame baseKeyFrame : baseKeyFrames) {
                if (baseKeyFrame.relativeTime == focusTs) {
                  baseKeyFrames.remove(baseKeyFrame);
                  break;
                }
              }
              EffectOPKeyFrame effectOPKeyFrame =
                  new EffectOPKeyFrame(groupId, effectIndex, ((AnimEffect) baseEffect).mEffectKeyFrameInfo);
              mWorkSpace.handleOperation(effectOPKeyFrame);
            }
          }
        }
      }
    }
  };

  private QEPlayerListener mPlayerListener = new QEPlayerListener() {
    @Override public void onPlayerCallback(PlayerStatus playerStatus, int progress) {
      curPlayerTime = progress;
      if (playerStatus == PlayerStatus.STATUS_PAUSE
          || playerStatus == PlayerStatus.STATUS_PLAYING
          || playerStatus == PlayerStatus.STATUS_STOP
          || playerStatus == PlayerStatus.STATUS_SEEKING) {
        mKeyFrameTimeline.setCurOffsetTime(progress - startTime);
        updateBtnEnable(mKeyFrameTimeline.getFocusTs());
        updatePosAndAlpha(progress);
      }
    }

    @Override public void onPlayerRefresh() {
      if (mWorkSpace != null
          && mWorkSpace.getPlayerAPI() != null
          && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
        curPlayerTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
        mKeyFrameTimeline.setCurOffsetTime(curPlayerTime - startTime);
        updateBtnEnable(mKeyFrameTimeline.getFocusTs());
        updatePosAndAlpha(curPlayerTime);
      }
    }

    @Override public void onSizeChanged(Rect resultRect) {
    }
  };

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof EffectOPKeyFrame
          || operate instanceof EffectOPKeyFrameRemove
          || operate instanceof EffectOPKeyFrameUpdate
          || operate instanceof EffectOPKeyFrameUpdateOffset
          || operate instanceof EffectOPKeyFrameInsert) {
        // 刷新数据
        updateKeyFrameTimeline();
        updatePosAndAlpha(curPlayerTime);
      }
    }
  };

  @Override protected void releaseAll() {
    mWorkSpace.removeObserver(mBaseObserver);
    mWorkSpace.getPlayerAPI().unregisterListener(mPlayerListener);
  }

  @Override public void onClick(View v) {
    dismissMenu();
  }

  @Override
  protected String getBottomTitle() {
    return getContext().getString(R.string.mn_edit_keyframe_animator_title);
  }
}
