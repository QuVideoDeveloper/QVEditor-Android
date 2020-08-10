package com.quvideo.application.editor.effect.plugin;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import com.quvideo.application.EditorApp;
import com.quvideo.application.editor.R;
import com.quvideo.application.editor.base.BaseMenuView;
import com.quvideo.application.editor.base.MenuContainer;
import com.quvideo.application.editor.effect.keyframe.KeyFrameTimeline;
import com.quvideo.application.widget.seekbar.CustomSeekbarPop;
import com.quvideo.application.widget.seekbar.DoubleSeekbar;
import com.quvideo.mobile.engine.model.AnimEffect;
import com.quvideo.mobile.engine.model.effect.SubPluginAttriItem;
import com.quvideo.mobile.engine.model.effect.SubPluginInfo;
import com.quvideo.mobile.engine.model.effect.keyframe.KeyAttributeInfo;
import com.quvideo.mobile.engine.model.effect.keyframe.KeyBezierCurve;
import com.quvideo.mobile.engine.player.QEPlayerListener;
import com.quvideo.mobile.engine.project.IQEWorkSpace;
import com.quvideo.mobile.engine.project.observer.BaseObserver;
import com.quvideo.mobile.engine.work.BaseOperate;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubPluginKeyFrame;
import com.quvideo.mobile.engine.work.operate.effect.EffectOPSubPluginModify;
import java.util.ArrayList;
import xiaoying.utils.QPoint;

public class EffectEditPluginDialog extends BaseMenuView {

  private int groupId = 0;
  private int effectIndex = 0;
  private int subType;
  private String name;

  private View btnCurve;

  private ImageView btnKeyFrameAdd;
  private ImageView btnKeyFrameDel;

  private KeyFrameTimeline mKeyFrameTimeline;
  private CustomSeekbarPop mCustomSeekbarPop;

  private int curPlayerTime = 0;
  private int startTime = 0;
  private int maxTime = 0;
  private boolean isSupportKeyFrame = false;

  public EffectEditPluginDialog(Context context, MenuContainer container, IQEWorkSpace workSpace,
      int groupId, int effectIndex, int subType, String name) {
    super(context, workSpace);
    this.groupId = groupId;
    this.effectIndex = effectIndex;
    this.subType = subType;
    this.name = name;
    showMenu(container, null);
  }

  @Override public MenuType getMenuType() {
    return MenuType.EffectPluginAttri;
  }

  @Override protected int getCustomLayoutId() {
    return R.layout.dialog_edit_effect_plugin_attri;
  }

  @Override protected void initCustomMenu(Context context, View view) {
    btnCurve = view.findViewById(R.id.btn_curve);
    btnKeyFrameAdd = view.findViewById(R.id.btn_keyframe_add);
    btnKeyFrameDel = view.findViewById(R.id.btn_keyframe_del);
    mKeyFrameTimeline = view.findViewById(R.id.v_keyframe_timeline);
    mCustomSeekbarPop = view.findViewById(R.id.seekbar);

    btnCurve.setOnClickListener(mOnClickListener);
    btnKeyFrameAdd.setOnClickListener(mOnClickListener);
    btnKeyFrameDel.setOnClickListener(mOnClickListener);

    curPlayerTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
    AnimEffect baseEffect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    startTime = baseEffect.destRange.getPosition();
    if (baseEffect.destRange.getTimeLength() > 0) {
      maxTime = baseEffect.destRange.getLimitValue();
    } else {
      maxTime = mWorkSpace.getPlayerAPI().getPlayerControl().getPlayerDuration();
    }

    mKeyFrameTimeline.setMaxOffsetTime(maxTime - startTime, new KeyFrameTimeline.OnKeyFrameListener() {
      @Override public void onKeyFrameClick(int focusTs) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime + focusTs);
        curPlayerTime = startTime + focusTs;
        mKeyFrameTimeline.setCurOffsetTime(focusTs);
        updateBtnEnable(focusTs);
        updateAttributeValue(curPlayerTime);
      }

      @Override public void onOtherClick(int offsetTime) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        mWorkSpace.getPlayerAPI().getPlayerControl().seek(startTime + offsetTime);
        curPlayerTime = startTime + offsetTime;
        mKeyFrameTimeline.setCurOffsetTime(offsetTime);
        updateBtnEnable(-1);
        updateAttributeValue(curPlayerTime);
      }
    });
    updateKeyFrameTimeline();

    mWorkSpace.addObserver(mBaseObserver);
    mWorkSpace.getPlayerAPI().registerListener(mPlayerListener);
    for (SubPluginInfo subPluginInfo : baseEffect.mEffectSubPluginList) {
      if (subPluginInfo.getSubType() == subType) {
        for (SubPluginAttriItem subPluginAttriItem : subPluginInfo.attributeList) {
          if (subPluginAttriItem.name.equals(name)) {
            isSupportKeyFrame = subPluginAttriItem.is_support_key;
            mCustomSeekbarPop.init(new CustomSeekbarPop.InitBuilder()
                .start("" + subPluginAttriItem.min_value)
                .end("" + subPluginAttriItem.max_value)
                .progress(subPluginAttriItem.cur_value)
                .seekRange(new CustomSeekbarPop.SeekRange(subPluginAttriItem.min_value, subPluginAttriItem.max_value))
                .seekOverListener(new DoubleSeekbar.OnSeekbarListener() {
                  @Override public void onSeekStart(boolean isFirst, int progress) {
                    mWorkSpace.getPlayerAPI().getPlayerControl().pause();
                  }

                  @Override public void onSeekOver(boolean isFirst, int progress) {
                    AnimEffect effect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
                    for (SubPluginInfo subPluginInfo : effect.mEffectSubPluginList) {
                      if (subPluginInfo.getSubType() == subType) {
                        for (SubPluginAttriItem subPluginAttriItem : subPluginInfo.attributeList) {
                          if (subPluginAttriItem.name.equals(name)) {
                            if (mKeyFrameTimeline.getFocusTs() >= 0) {
                              // 修改关键帧
                              if (subPluginAttriItem.keyAttriList != null) {
                                for (KeyAttributeInfo keyAttributeInfo : subPluginAttriItem.keyAttriList) {
                                  if (keyAttributeInfo.relativeTime == mKeyFrameTimeline.getFocusTs()) {
                                    keyAttributeInfo.attrValue = progress;
                                    EffectOPSubPluginKeyFrame effectOPSubPluginKeyFrame =
                                        new EffectOPSubPluginKeyFrame(groupId, effectIndex, subType, name, subPluginAttriItem.keyAttriList);
                                    mWorkSpace.handleOperation(effectOPSubPluginKeyFrame);
                                    break;
                                  }
                                }
                              }
                            } else {
                              if (subPluginAttriItem.keyAttriList != null
                                  && subPluginAttriItem.keyAttriList.size() > 0) {
                                // 已有关键帧，则新增关键帧
                                KeyAttributeInfo keyAttributeInfo = new KeyAttributeInfo(curPlayerTime - startTime, progress);
                                subPluginAttriItem.keyAttriList.add(keyAttributeInfo);
                                EffectOPSubPluginKeyFrame effectOPSubPluginKeyFrame =
                                    new EffectOPSubPluginKeyFrame(groupId, effectIndex, subPluginInfo);
                                mWorkSpace.handleOperation(effectOPSubPluginKeyFrame);
                              } else {
                                subPluginAttriItem.cur_value = progress;
                                EffectOPSubPluginModify effectOPSubPluginModify =
                                    new EffectOPSubPluginModify(groupId, effectIndex, subPluginInfo);
                                mWorkSpace.handleOperation(effectOPSubPluginModify);
                              }
                            }
                            break;
                          }
                        }
                        break;
                      }
                    }
                  }

                  @Override public void onSeekChange(boolean isFirst, int progress) {
                  }
                }));
            break;
          }
        }
        break;
      }
    }
  }

  private void updateKeyFrameTimeline() {
    mCustomSeekbarPop.setVisibility(VISIBLE);
    AnimEffect effect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
    for (SubPluginInfo subPluginInfo : effect.mEffectSubPluginList) {
      if (subPluginInfo.getSubType() == subType) {
        for (SubPluginAttriItem subPluginAttriItem : subPluginInfo.attributeList) {
          if (subPluginAttriItem.name.equals(name)) {
            isSupportKeyFrame = subPluginAttriItem.is_support_key;
            mKeyFrameTimeline.setKeyFrameData(ContextCompat.getColor(EditorApp.Companion.getInstance().getApp(), R.color.color_ff7b2e),
                subPluginAttriItem.keyAttriList);
            // 修改关键帧
            break;
          }
        }
        break;
      }
    }
    mKeyFrameTimeline.setCurOffsetTime(curPlayerTime - startTime);
    updateBtnEnable(mKeyFrameTimeline.getFocusTs());
  }

  private void updateBtnEnable(int focusTs) {
    if (!isSupportKeyFrame || curPlayerTime < startTime || curPlayerTime > maxTime) {
      btnKeyFrameAdd.setEnabled(false);
      btnKeyFrameAdd.setAlpha(0.1f);
    } else {
      btnKeyFrameAdd.setEnabled(focusTs < 0);
      btnKeyFrameAdd.setAlpha(focusTs < 0 ? 1f : 0.1f);
    }
    btnKeyFrameDel.setEnabled(isSupportKeyFrame && focusTs >= 0);
    btnKeyFrameDel.setAlpha((isSupportKeyFrame && focusTs >= 0) ? 1f : 0.1f);
    btnCurve.setEnabled(isSupportKeyFrame && focusTs >= 0);
    btnCurve.setAlpha((isSupportKeyFrame && focusTs >= 0) ? 1f : 0.1f);
    if (focusTs >= 0) {
      AnimEffect effect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
      for (SubPluginInfo subPluginInfo : effect.mEffectSubPluginList) {
        if (subPluginInfo.getSubType() == subType) {
          for (SubPluginAttriItem subPluginAttriItem : subPluginInfo.attributeList) {
            if (subPluginAttriItem.name.equals(name)) {
              // 修改关键帧
              if (subPluginAttriItem.keyAttriList != null) {
                for (KeyAttributeInfo keyAttributeInfo : subPluginAttriItem.keyAttriList) {
                  if (keyAttributeInfo.relativeTime == focusTs) {
                    btnCurve.setBackgroundResource(keyAttributeInfo.mKeyBezierCurve != null ?
                        R.drawable.edit_item_bg_selected : R.drawable.edit_item_bg_normal);
                    break;
                  }
                }
              }
              break;
            }
          }
          break;
        }
      }
    } else {
      btnCurve.setBackgroundResource(R.drawable.edit_item_bg_normal);
    }
  }

  private void updateAttributeValue(int curTime) {
    if (curTime < startTime || curTime > maxTime) {
      mCustomSeekbarPop.setVisibility(GONE);
    } else {
      AnimEffect effect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
      for (SubPluginInfo subPluginInfo : effect.mEffectSubPluginList) {
        if (subPluginInfo.getSubType() == subType) {
          for (SubPluginAttriItem subPluginAttriItem : subPluginInfo.attributeList) {
            if (subPluginAttriItem.name.equals(name)) {
              // 修改关键帧
              int attribute = 0;
              if (subPluginAttriItem.keyAttriList != null && subPluginAttriItem.keyAttriList.size() > 0) {
                attribute = (int) mWorkSpace.getAttriInfoByTime(groupId, effectIndex, subType, name, curTime - startTime);
              } else {
                attribute = subPluginAttriItem.cur_value;
              }
              mCustomSeekbarPop.setProgress(attribute);
            }
          }
          break;
        }
      }

      mCustomSeekbarPop.setVisibility(VISIBLE);
    }
  }

  private OnClickListener mOnClickListener = new OnClickListener() {
    @Override public void onClick(View v) {
      if (v.equals(btnKeyFrameAdd)) {
        // 新增关键帧
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        AnimEffect effect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        for (SubPluginInfo subPluginInfo : effect.mEffectSubPluginList) {
          if (subPluginInfo.getSubType() == subType) {
            for (SubPluginAttriItem subPluginAttriItem : subPluginInfo.attributeList) {
              if (subPluginAttriItem.name.equals(name)) {
                if (subPluginAttriItem.keyAttriList == null) {
                  subPluginAttriItem.keyAttriList = new ArrayList<>();
                }
                // 已有关键帧，则新增关键帧
                KeyAttributeInfo keyAttributeInfo = new KeyAttributeInfo(curPlayerTime - startTime,
                    mCustomSeekbarPop.getProgress());
                subPluginAttriItem.keyAttriList.add(keyAttributeInfo);
                EffectOPSubPluginKeyFrame effectOPSubPluginKeyFrame =
                    new EffectOPSubPluginKeyFrame(groupId, effectIndex, subPluginInfo);
                mWorkSpace.handleOperation(effectOPSubPluginKeyFrame);
                break;
              }
            }
            break;
          }
        }
      } else if (v.equals(btnKeyFrameDel)) {
        // 删除关键帧
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        AnimEffect effect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
        int focusTs = mKeyFrameTimeline.getFocusTs();
        if (focusTs >= 0) {
          for (SubPluginInfo subPluginInfo : effect.mEffectSubPluginList) {
            if (subPluginInfo.getSubType() == subType) {
              for (SubPluginAttriItem subPluginAttriItem : subPluginInfo.attributeList) {
                if (subPluginAttriItem.name.equals(name)) {
                  if (subPluginAttriItem.keyAttriList != null) {
                    for (KeyAttributeInfo keyAttributeInfo : subPluginAttriItem.keyAttriList) {
                      if (keyAttributeInfo.relativeTime == focusTs) {
                        subPluginAttriItem.keyAttriList.remove(keyAttributeInfo);
                        EffectOPSubPluginKeyFrame effectOPSubPluginKeyFrame =
                            new EffectOPSubPluginKeyFrame(groupId, effectIndex, subPluginInfo);
                        mWorkSpace.handleOperation(effectOPSubPluginKeyFrame);
                        break;
                      }
                    }
                  }
                  break;
                }
              }
              break;
            }
          }
        }
      } else if (v.equals(btnCurve)) {
        mWorkSpace.getPlayerAPI().getPlayerControl().pause();
        int focusTs = mKeyFrameTimeline.getFocusTs();
        if (focusTs >= 0) {
          AnimEffect effect = (AnimEffect) mWorkSpace.getEffectAPI().getEffect(groupId, effectIndex);
          for (SubPluginInfo subPluginInfo : effect.mEffectSubPluginList) {
            if (subPluginInfo.getSubType() == subType) {
              for (SubPluginAttriItem subPluginAttriItem : subPluginInfo.attributeList) {
                if (subPluginAttriItem.name.equals(name)) {
                  // 修改关键帧
                  for (KeyAttributeInfo keyAttributeInfo : subPluginAttriItem.keyAttriList) {
                    if (keyAttributeInfo.relativeTime == focusTs) {
                      KeyBezierCurve keyBezierCurve = new KeyBezierCurve();
                      keyBezierCurve.start = new QPoint(0, 0);
                      keyBezierCurve.c0 = new QPoint(0, 5000);
                      keyBezierCurve.c1 = new QPoint(10000, 5000);
                      keyBezierCurve.stop = new QPoint(10000, 10000);
                      keyAttributeInfo.mKeyBezierCurve = keyBezierCurve;
                      EffectOPSubPluginKeyFrame effectOPSubPluginKeyFrame =
                          new EffectOPSubPluginKeyFrame(groupId, effectIndex, subType, name, subPluginAttriItem.keyAttriList);
                      mWorkSpace.handleOperation(effectOPSubPluginKeyFrame);
                      break;
                    }
                  }
                  break;
                }
              }
              break;
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
        updateAttributeValue(progress);
      }
    }

    @Override public void onPlayerRefresh() {
      if (mWorkSpace != null
          && mWorkSpace.getPlayerAPI() != null
          && mWorkSpace.getPlayerAPI().getPlayerControl() != null) {
        curPlayerTime = mWorkSpace.getPlayerAPI().getPlayerControl().getCurrentPlayerTime();
        mKeyFrameTimeline.setCurOffsetTime(curPlayerTime - startTime);
        updateBtnEnable(mKeyFrameTimeline.getFocusTs());
        updateAttributeValue(curPlayerTime);
      }
    }

    @Override public void onSizeChanged(Rect resultRect) {
    }
  };

  private BaseObserver mBaseObserver = new BaseObserver() {
    @Override public void onChange(BaseOperate operate) {
      if (operate instanceof EffectOPSubPluginKeyFrame
          || operate instanceof EffectOPSubPluginModify) {
        // 刷新数据
        updateKeyFrameTimeline();
        updateAttributeValue(curPlayerTime);
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
    return name;
  }
}
