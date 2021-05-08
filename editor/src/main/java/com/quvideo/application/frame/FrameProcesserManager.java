package com.quvideo.application.frame;

import com.quvideo.mobile.engine.QEProcessTools;
import com.quvideo.mobile.engine.process.AbsProcessor;
import com.quvideo.mobile.engine.process.IProcessorManager;
import com.quvideo.mobile.engine.process.ProcessorType;
import com.quvideo.mobile.engine.process.param.BGParam;
import com.quvideo.mobile.engine.process.param.FilterParam;
import com.quvideo.mobile.engine.process.param.TransParam;
import xiaoying.engine.base.QXYEffect;

/**
 * @author wuzhongyou
 * @date 2021/1/28.
 */
public class FrameProcesserManager {

  private IProcessorManager mProcessorManager;

  private FilterParam mFilterParam;
  private BGParam mBGParam;
  private TransParam mTransParam;

  private AbsProcessor mFilterProcessor;
  private AbsProcessor mBGProcessor;
  private AbsProcessor mTransProcessor;

  private AbsProcessor mFilterTransProcessor1;
  private AbsProcessor mFilterTransProcessor2;

  public FrameProcesserManager() {
    mProcessorManager = QEProcessTools.createProcessorManager();
  }

  public FilterParam getFilterParam() {
    return mFilterParam;
  }

  public void setFilterParam(FilterParam filterParam) {
    mFilterParam = filterParam;
  }

  public BGParam getBGParam() {
    return mBGParam;
  }

  public void setBGParam(BGParam BGParam) {
    mBGParam = BGParam;
  }

  public TransParam getTransParam() {
    return mTransParam;
  }

  public void setTransParam(TransParam transParam) {
    mTransParam = transParam;
  }

  public QXYEffect.QXYEffectData handleFramePreview(QXYEffect.QXYEffectData inFrame) {
    float[] matrix = new float[] {
        0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.0f
    };
    int iRes = 0;
    boolean check = false;
    // 滤镜
    if (mFilterProcessor == null) {
      mFilterProcessor = mProcessorManager.createProcessor(ProcessorType.TYPE_FILTER);
    }
    QXYEffect.QXYEffectData filterOut = new QXYEffect.QXYEffectData();
    filterOut.width = inFrame.height;
    filterOut.height = inFrame.width;
    filterOut.matrix = matrix;
    iRes = mProcessorManager.process(mFilterProcessor, new QXYEffect.QXYEffectData[] { inFrame }, filterOut, mFilterParam);
    if (iRes != 0) {
      filterOut.texID = inFrame.texID;
    }
    check = checkOut(check, inFrame, filterOut);
    // 背景
    if (mBGProcessor == null) {
      mBGProcessor = mProcessorManager.createProcessor(ProcessorType.TYPE_BG);
    }
    QXYEffect.QXYEffectData bgOut = new QXYEffect.QXYEffectData();
    bgOut.width = inFrame.height;
    bgOut.height = inFrame.width;
    bgOut.matrix = matrix;
    iRes = mProcessorManager.process(mBGProcessor, new QXYEffect.QXYEffectData[] { filterOut }, bgOut, mBGParam);
    if (iRes != 0) {
      bgOut.texID = filterOut.texID;
    }
    check = check || checkOut(check, filterOut, bgOut);
    if (mTransParam == null) {
      return bgOut;
    }
    QEProcessTools.lockTexture(bgOut.texID);
    boolean oldCheck = check;
    // 给BG处理个滤镜
    if (mFilterTransProcessor1 == null) {
      mFilterTransProcessor1 = mProcessorManager.createProcessor(ProcessorType.TYPE_FILTER);
    }
    QXYEffect.QXYEffectData transFirst = new QXYEffect.QXYEffectData();
    transFirst.width = inFrame.height;
    transFirst.height = inFrame.width;
    transFirst.matrix = matrix;
    FilterParam transFilter = new FilterParam();
    transFilter.filterPath = "assets_android://quvideo/imageeffect/0x040000001000002B.xyt";
    iRes = mProcessorManager.process(mFilterTransProcessor1, new QXYEffect.QXYEffectData[] { bgOut }, transFirst, transFilter);
    if (iRes != 0) {
      transFirst.texID = bgOut.texID;
    }
    QEProcessTools.lockTexture(transFirst.texID);

    // 给BG处理个滤镜
    if (mFilterTransProcessor2 == null) {
      mFilterTransProcessor2 = mProcessorManager.createProcessor(ProcessorType.TYPE_FILTER);
    }
    QXYEffect.QXYEffectData transSec = new QXYEffect.QXYEffectData();
    transSec.width = inFrame.height;
    transSec.height = inFrame.width;
    transSec.matrix = matrix;
    FilterParam transFilterSec = new FilterParam();
    transFilterSec.filterPath = "assets_android://quvideo/imageeffect/0x0400000010000040.xyt";
    iRes = mProcessorManager.process(mFilterTransProcessor2, new QXYEffect.QXYEffectData[] { bgOut }, transSec, transFilterSec);
    if (iRes != 0) {
      transSec.texID = bgOut.texID;
    }
    QEProcessTools.lockTexture(transSec.texID);

    // 转场
    if (mTransProcessor == null) {
      mTransProcessor = mProcessorManager.createProcessor(ProcessorType.TYPE_TRANSITION);
    }
    QXYEffect.QXYEffectData transOut = new QXYEffect.QXYEffectData();
    transOut.width = inFrame.height;
    transOut.height = inFrame.width;
    transOut.matrix = matrix;
    iRes =
        mProcessorManager.process(mTransProcessor, new QXYEffect.QXYEffectData[] { transFirst, transSec }, transOut,
            mTransParam, mTransParam.duration / 2);
    if (iRes != 0) {
      transOut.texID = bgOut.texID;
    }
    QEProcessTools.unLockTexture(bgOut.texID);
    QEProcessTools.unLockTexture(transFirst.texID);
    QEProcessTools.unLockTexture(transSec.texID);
    return transOut;
  }

  /**
   * 如果未处理，则还原宽高属性
   */
  private boolean checkOut(boolean oldCheck, QXYEffect.QXYEffectData inFrame, QXYEffect.QXYEffectData outFrame) {
    if (!oldCheck) {
      if (inFrame.texID != outFrame.texID) {
        oldCheck = true;
      } else {
        int temp = outFrame.width;
        outFrame.width = outFrame.height;
        outFrame.height = temp;
        outFrame.matrix = inFrame.matrix;
      }
    }
    return oldCheck;
  }

  public synchronized void releaseAll() {
    mProcessorManager.destroy(null);
  }
}
