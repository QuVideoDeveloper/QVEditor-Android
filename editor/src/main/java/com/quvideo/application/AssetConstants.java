package com.quvideo.application;

import com.quvideo.application.editor.R;
import com.quvideo.application.editor.edit.EditFilterTemplate;
import com.quvideo.application.editor.sound.AudioTemplate;
import com.quvideo.application.slide.SlideTemplate;
import com.quvideo.mobile.component.template.XytManager;
import com.quvideo.mobile.component.template.model.XytInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AssetConstants {

  public static ArrayList<String> mScanTemplateList = new ArrayList<>();
  public static ArrayList<String> mScanZipTemplateList = new ArrayList<>();

  // 卡点视频主题
  public static ArrayList<String> mSlideTemplateList = new ArrayList<>();
  // 滤镜
  public static ArrayList<String> mFilterTemplateList = new ArrayList<>();
  // 特效滤镜
  public static ArrayList<String> mFxFilterTemplateList = new ArrayList<>();
  // 主题
  public static ArrayList<String> mThemeTemplateList = new ArrayList<>();
  // 转场
  public static ArrayList<String> mTransTemplateList = new ArrayList<>();
  // 特效
  public static ArrayList<String> mFxTemplateList = new ArrayList<>();
  // 贴纸
  public static ArrayList<String> mStickerTemplateList = new ArrayList<>();
  // 字幕
  public static ArrayList<String> mSubtitleTemplateList = new ArrayList<>();
  // 组合字幕
  public static ArrayList<String> mMultiSubtitleTemplateList = new ArrayList<>();
  // 马赛克
  public static ArrayList<String> mMosaicTemplateList = new ArrayList<>();
  // 效果插件
  public static ArrayList<String> mPluginTemplateList = new ArrayList<>();

  // 字体
  public static ArrayList<String> mFontTemplateList = new ArrayList<>();

  // 音乐
  public static ArrayList<String> mMusicTemplateList = new ArrayList<>();
  // 水印
  public static ArrayList<String> mWaterTemplateList = new ArrayList<>();

  static {
    // slide
    mSlideTemplateList.add("assets_android://quvideo/slide/0x0100000000400411.zip");
    mSlideTemplateList.add("assets_android://quvideo/slide/0x0100000000400423.zip");
    mSlideTemplateList.add("assets_android://quvideo/slide/0x010000000040042E.zip");
    mSlideTemplateList.add("assets_android://quvideo/slide/0x0100000000400435.zip");
    mSlideTemplateList.add("assets_android://quvideo/slide/0x0100000000400436.zip");
    // Camera Test Filter
    mFilterTemplateList.add("assets_android://quvideo/imageeffect/0x040000001000001E.xyt");
    mFilterTemplateList.add("assets_android://quvideo/imageeffect/0x040000001000002B.xyt");
    mFilterTemplateList.add("assets_android://quvideo/imageeffect/0x040000001000002F.xyt");
    mFilterTemplateList.add("assets_android://quvideo/imageeffect/0x0400000010000025.xyt");
    mFilterTemplateList.add("assets_android://quvideo/imageeffect/0x0400000010000035.xyt");
    mFilterTemplateList.add("assets_android://quvideo/imageeffect/0x0400000010000039.xyt");
    mFilterTemplateList.add("assets_android://quvideo/imageeffect/0x0400000010000040.xyt");
    // fxfilter
    mFxFilterTemplateList.add("assets_android://quvideo/fxfilter/0x0400000000500001.xyt");
    mFxFilterTemplateList.add("assets_android://quvideo/fxfilter/0x0400000000500002.xyt");
    mFxFilterTemplateList.add("assets_android://quvideo/fxfilter/0x0400000000500003.xyt");
    mFxFilterTemplateList.add("assets_android://quvideo/fxfilter/0x0400000000500004.xyt");
    mFxFilterTemplateList.add("assets_android://quvideo/fxfilter/0x0400000000500007.xyt");
    // theme
    mThemeTemplateList.add("assets_android://quvideo/theme/0x01000000000002A3.zip");
    mThemeTemplateList.add("assets_android://quvideo/theme/0x01000000000002A5.zip");
    // trans
    mTransTemplateList.add("assets_android://quvideo/trans/0x030000000000012A.xyt");
    mTransTemplateList.add("assets_android://quvideo/trans/0x030000000000012B.xyt");
    mTransTemplateList.add("assets_android://quvideo/trans/0x030000000000012C.xyt");
    mTransTemplateList.add("assets_android://quvideo/trans/0x030000000000012D.xyt");
    mTransTemplateList.add("assets_android://quvideo/trans/0x030000000000012E.xyt");
    mTransTemplateList.add("assets_android://quvideo/trans/0x4A0000000000012A.xyt");
    mTransTemplateList.add("assets_android://quvideo/trans/0x4A0000000000012B.xyt");
    mTransTemplateList.add("assets_android://quvideo/trans/0x4A0000000000012C.xyt");
    mTransTemplateList.add("assets_android://quvideo/trans/0x4A0000000000012D.xyt");
    mTransTemplateList.add("assets_android://quvideo/trans/0x4A0000000000012E.xyt");
    // fx
    mFxTemplateList.add("assets_android://quvideo/fx/0x06000000000000D9.xyt");
    mFxTemplateList.add("assets_android://quvideo/fx/0x06000000000000DA.xyt");
    mFxTemplateList.add("assets_android://quvideo/fx/0x0600000000000141.xyt");
    mFxTemplateList.add("assets_android://quvideo/fx/0x0600000000000145.xyt");
    // Sticker
    mStickerTemplateList.add("assets_android://quvideo/sticker/0x0500000000000480.xyt");
    mStickerTemplateList.add("assets_android://quvideo/sticker/0x0500000000000481.xyt");
    mStickerTemplateList.add("assets_android://quvideo/sticker/0x0500000000000482.xyt");
    mStickerTemplateList.add("assets_android://quvideo/sticker/0x0500000000000483.xyt");
    mStickerTemplateList.add("assets_android://quvideo/sticker/0x0500000000000484.xyt");
    // subtitle
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x09000000000000B3.xyt");
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x09000000000000B4.xyt");
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x090000000000028A.xyt");
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x090000000000028B.xyt");
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x0900000000000133.xyt");
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x0900000000000280.xyt");
    // multisubtitle
    mMultiSubtitleTemplateList.add("assets_android://quvideo/multititle/0x090000000010000A.zip");
    mMultiSubtitleTemplateList.add("assets_android://quvideo/multititle/0x090000000010000B.zip");
    mMultiSubtitleTemplateList.add("assets_android://quvideo/multititle/0x0900000000100009.zip");
    // 马赛克
    mMosaicTemplateList.add("assets_android://quvideo/mosaic/0x0500000000300001.xyt");
    mMosaicTemplateList.add("assets_android://quvideo/mosaic/0x0500000000300002.xyt");
    //效果插件
    mPluginTemplateList.add("assets_android://quvideo/plugin/0x0400600000000496.zip");
    mPluginTemplateList.add("assets_android://quvideo/plugin/0x0400600000000507.zip");
    mPluginTemplateList.add("assets_android://quvideo/plugin/0x0400600000000508.zip");

    // 字体
    mFontTemplateList.add("assets_android://quvideo/font/2019122513524848.ttf");
    mFontTemplateList.add("assets_android://quvideo/font/2019122514034848.ttf");
    mFontTemplateList.add("assets_android://quvideo/font/2019122514043737.ttf");

    // 音乐
    mMusicTemplateList.add("assets_android://quvideo/music/dub_1.mp3");
    mMusicTemplateList.add("assets_android://quvideo/music/dub_2.mp3");
    mMusicTemplateList.add("assets_android://quvideo/music/music_1.mp3");
    mMusicTemplateList.add("assets_android://quvideo/music/music_2.mp3");
    mMusicTemplateList.add("assets_android://quvideo/music/music_3.mp3");
    //水印
    mWaterTemplateList.add("assets_android://quvideo/watermark/water_mark_logo.png");

    mScanTemplateList.addAll(mFilterTemplateList);
    mScanTemplateList.addAll(mFxTemplateList);
    mScanTemplateList.addAll(mFxFilterTemplateList);
    mScanTemplateList.addAll(mTransTemplateList);
    mScanTemplateList.addAll(mSubtitleTemplateList);
    mScanTemplateList.addAll(mStickerTemplateList);
    mScanTemplateList.addAll(mMosaicTemplateList);

    mScanZipTemplateList.addAll(mPluginTemplateList);
    mScanZipTemplateList.addAll(mSlideTemplateList);
    mScanZipTemplateList.addAll(mThemeTemplateList);
    mScanZipTemplateList.addAll(mMultiSubtitleTemplateList);
  }

  public static final long[] TEST_CAM_FILTER_TID = new long[] {
      0x040000001000001EL,
      0x040000001000002BL,
      0x040000001000002FL,
      0x0400000010000025L,
      0x0400000010000035L,
      0x0400000010000039L,
      0x0400000010000040L
  };

  public static final EditFilterTemplate[] TEST_MOSIC_TID = new EditFilterTemplate[] {
      new EditFilterTemplate(0x0500000000300001L),
      new EditFilterTemplate(0x0500000000300002L)
  };

  public static final AudioTemplate[] TEST_DUB_TID = new AudioTemplate[] {
      new AudioTemplate(StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/music/dub_1.mp3",
          "Dub1", R.drawable.dub_1),
      new AudioTemplate(StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/music/dub_2.mp3",
          "Dub2", R.drawable.dub_2)
  };

  public static final AudioTemplate[] TEST_MUSIC_TID = new AudioTemplate[] {
      new AudioTemplate(StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/music/music_1.mp3",
          "Music1", R.drawable.music_1),
      new AudioTemplate(StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/music/music_2.mp3",
          "Music2", R.drawable.music_2),
      new AudioTemplate(StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/music/music_3.mp3",
          "Music3", R.drawable.music_3)
  };

  public static final String[] TEST_FONT_TID = new String[] {
     StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/font/2019122513524848.ttf",
     StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/font/2019122514034848.ttf",
     StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/font/2019122514043737.ttf",
  };

  public static final String WATERMARK_LOG_PATH = StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp())
      + "quvideo/watermark/water_mark_logo.png";

  public static final SlideTemplate[] TEST_SLIDE_THEME_TID = new SlideTemplate[] {
      new SlideTemplate(0x0100000000400411L, "520-1", R.drawable.thumbnail_520, 1, 6),
      new SlideTemplate(0x0100000000400423L, "pink", R.drawable.thumbnail_pink, 1, 7),
      new SlideTemplate(0x010000000040042EL, "musume", R.drawable.thumbnail_musume, 1, 7),
      new SlideTemplate(0x0100000000400435L, "SICKO", R.drawable.thumbnail_sicko, 4, 8),
      new SlideTemplate(0x0100000000400436L, "love", R.drawable.thumbnail_love, 1, 7)
  };

  /**
   * 根据xyttype获取素材列表
   */
  public static SlideTemplate[] getSlideXytListByType() {
    List<SlideTemplate> result = new ArrayList<>();
    HashMap<Long, XytInfo> temp = XytManager.getAll();
    Set<Long> keySet = temp.keySet();
    XytInfo xytInfo;
    for (Long ttid : keySet) {
      xytInfo = temp.get(ttid);
      if (xytInfo != null && isCurrentType(XytType.Slide, xytInfo)) {
        result.add(new SlideTemplate(ttid, "", 0, 1, 4));
      }
    }
    return result.toArray(new SlideTemplate[result.size()]);
  }

  /**
   * 根据xyttype获取素材列表
   * 这种判断是非正规的，实际的id不一定会这样，仅测试素材是这样的规则。开发者请勿这样使用
   */
  public static EditFilterTemplate[] getXytListByType(XytType xytType) {
    List<EditFilterTemplate> result = new ArrayList<>();
    //ArrayList<EditFilterTemplate> result = new ArrayList<>(Arrays.asList(AssetConstants.TEST_SUBTITLE_TID));
    if (xytType == XytType.Transition
        || xytType == XytType.Filter
        || xytType == XytType.FxFilter
        || xytType == XytType.Theme) {
      // 无--主题/转场/滤镜/特效滤镜
      result.add(new EditFilterTemplate(0, " ", R.drawable.cam_icon_no_filter_nor));
    }
    HashMap<Long, XytInfo> temp = XytManager.getAll();
    Set<Long> keySet = temp.keySet();
    XytInfo xytInfo;
    for (Long ttid : keySet) {
      xytInfo = temp.get(ttid);
      if (xytInfo != null && isCurrentType(xytType, xytInfo)) {
        result.add(new EditFilterTemplate(ttid));
      }
    }
    return result.toArray(new EditFilterTemplate[result.size()]);
  }

  /**
   * 这种判断是非正规的，实际的id不一定会这样，仅测试素材是这样的规则。开发者请勿这样使用
   */
  private static boolean isCurrentType(XytType xytType, XytInfo xytInfo) {
    switch (xytType) {
      case Theme:
        return xytInfo.ttidHexStr.contains("0x01000000000");
      case Slide:
        return xytInfo.ttidHexStr.contains("0x01000000004");
      case FxPlugin:
        return xytInfo.ttidHexStr.contains("0x04006");
      case Filter:
        return xytInfo.ttidHexStr.contains("0x04")
            && !xytInfo.ttidHexStr.contains("0x04000000005")
            && !xytInfo.ttidHexStr.contains("0x04006");
      case FxFilter:
        return xytInfo.ttidHexStr.contains("0x04000000005");
      case Sticker:
        return xytInfo.ttidHexStr.contains("0x05") && !xytInfo.ttidHexStr.contains("0x05000000003");
      case Transition:
        return xytInfo.ttidHexStr.contains("0x03");
      case Subtitle:
        return xytInfo.ttidHexStr.contains("0x09");
      case Fx:
        return xytInfo.ttidHexStr.contains("0x06");
    }
    return false;
  }

  public enum XytType {
    Theme,
    FxFilter,
    Filter,
    Transition,
    Sticker,
    Subtitle,
    Fx,
    FxPlugin,
    Slide
  }
}
