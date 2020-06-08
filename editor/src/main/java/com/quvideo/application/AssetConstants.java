package com.quvideo.application;

import com.quvideo.application.editor.R;
import com.quvideo.application.editor.edit.EditFilterTemplate;
import com.quvideo.application.editor.sound.AudioTemplate;
import com.quvideo.application.slide.SlideTemplate;
import com.quvideo.mobile.engine.template.DefaultTemplateConstant;
import java.util.ArrayList;

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

  // 音乐
  public static ArrayList<String> mMusicTemplateList = new ArrayList<>();

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
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x09000000000000AF.xyt");
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x09000000000000B0.xyt");
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x09000000000000B1.xyt");
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x090000000000028A.xyt");
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x090000000000028B.xyt");
    mSubtitleTemplateList.add("assets_android://quvideo/subtitle/0x0900000000000133.xyt");
    // multisubtitle
    mMultiSubtitleTemplateList.add("assets_android://quvideo/multititle/0x0900000000000135.zip");
    mMultiSubtitleTemplateList.add("assets_android://quvideo/multititle/0x0900000000000136.zip");
    mMultiSubtitleTemplateList.add("assets_android://quvideo/multititle/0x0900000000000137.zip");
    mMultiSubtitleTemplateList.add("assets_android://quvideo/multititle/0x0900000000000138.zip");
    mMultiSubtitleTemplateList.add("assets_android://quvideo/multititle/0x0900000000000139.zip");
    mMultiSubtitleTemplateList.add("assets_android://quvideo/multititle/0x090000000000013A.zip");

    // 音乐
    mMusicTemplateList.add("assets_android://quvideo/music/dub_1.mp3");
    mMusicTemplateList.add("assets_android://quvideo/music/dub_2.mp3");
    mMusicTemplateList.add("assets_android://quvideo/music/music_1.mp3");
    mMusicTemplateList.add("assets_android://quvideo/music/music_2.mp3");
    mMusicTemplateList.add("assets_android://quvideo/music/music_3.mp3");

    mScanTemplateList.addAll(mFilterTemplateList);
    mScanTemplateList.addAll(mFxTemplateList);
    mScanTemplateList.addAll(mFxFilterTemplateList);
    mScanTemplateList.addAll(mTransTemplateList);
    mScanTemplateList.addAll(mSubtitleTemplateList);
    mScanTemplateList.addAll(mStickerTemplateList);

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

  public static final long[] TEST_FX_FILTER_TID = new long[] {
      0x0400000000500001L,
      0x0400000000500002L,
      0x0400000000500003L,
      0x0400000000500004L,
      0x0400000000500007L,
  };

  public static final EditFilterTemplate[] TEST_THEME_TID = new EditFilterTemplate[] {
      new EditFilterTemplate(0, " ", R.drawable.cam_icon_no_filter_nor),
      new EditFilterTemplate(0x01000000000002A3L),
      new EditFilterTemplate(0x01000000000002A5L)
  };

  public static final long[] TEST_TRANS_TID = new long[] {
      0x030000000000012AL,
      0x030000000000012BL,
      0x030000000000012CL,
      0x030000000000012DL,
      0x030000000000012EL
  };

  public static final EditFilterTemplate[] TEST_FX_TID = new EditFilterTemplate[] {
      new EditFilterTemplate(0x06000000000000D9L),
      new EditFilterTemplate(0x06000000000000DAL),
      new EditFilterTemplate(0x0600000000000141L),
      new EditFilterTemplate(0x0600000000000145L)
  };

  public static final EditFilterTemplate[] TEST_STICKER_TID = new EditFilterTemplate[] {
      new EditFilterTemplate(0x0500000000000480L),
      new EditFilterTemplate(0x0500000000000481L),
      new EditFilterTemplate(0x0500000000000482L),
      new EditFilterTemplate(0x0500000000000483L),
      new EditFilterTemplate(0x0500000000000484L)
  };

  public static final EditFilterTemplate[] TEST_SUBTITLE_TID = new EditFilterTemplate[] {
      new EditFilterTemplate(0x09000000000000AFL),
      new EditFilterTemplate(0x09000000000000B0L),
      new EditFilterTemplate(0x09000000000000B1L),
      new EditFilterTemplate(0x090000000000028AL),
      new EditFilterTemplate(0x090000000000028BL),
      new EditFilterTemplate(0x0900000000000133L),
      new EditFilterTemplate(0x0900000000000135L),
      new EditFilterTemplate(0x0900000000000136L),
      new EditFilterTemplate(0x0900000000000137L),
      new EditFilterTemplate(0x0900000000000138L),
      new EditFilterTemplate(0x0900000000000139L),
      new EditFilterTemplate(0x090000000000013AL)
  };

  public static final EditFilterTemplate[] TEST_MOSIC_TID = new EditFilterTemplate[] {
      new EditFilterTemplate(DefaultTemplateConstant.EFFECT_DEFAULT_MOSAIC_GUASSIAN_ID),
      new EditFilterTemplate(DefaultTemplateConstant.EFFECT_DEFAULT_MOSAIC_PIXEL_ID)
  };

  public static final AudioTemplate[] TEST_DUB_TID = new AudioTemplate[] {
      new AudioTemplate(StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/music/dub_1.mp3", "音效1", R.drawable.dub_1),
      new AudioTemplate(StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/music/dub_2.mp3", "音效2", R.drawable.dub_2)
  };

  public static final AudioTemplate[] TEST_MUSIC_TID = new AudioTemplate[] {
      new AudioTemplate(StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/music/music_1.mp3", "音乐1", R.drawable.music_1),
      new AudioTemplate(StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/music/music_2.mp3", "音乐2", R.drawable.music_2),
      new AudioTemplate(StorageUtils.getTemplatePath(EditorApp.Companion.getInstance().getApp()) + "quvideo/music/music_3.mp3", "音乐3", R.drawable.music_3)
  };

  public static final EditFilterTemplate[] TEST_EDIT_FILTER_TID = new EditFilterTemplate[] {
      new EditFilterTemplate(0, " ", R.drawable.cam_icon_no_filter_nor),
      new EditFilterTemplate(0x040000001000001EL),
      new EditFilterTemplate(0x040000001000002BL),
      new EditFilterTemplate(0x040000001000002FL),
      new EditFilterTemplate(0x0400000010000025L),
      new EditFilterTemplate(0x0400000010000035L),
      new EditFilterTemplate(0x0400000010000039L),
      new EditFilterTemplate(0x0400000010000040L)
  };

  public static final EditFilterTemplate[] TEST_EDIT_TRANS_TID = new EditFilterTemplate[] {
      new EditFilterTemplate(0, " ", R.drawable.cam_icon_no_filter_nor),
      new EditFilterTemplate(TEST_TRANS_TID[0]),
      new EditFilterTemplate(TEST_TRANS_TID[1]),
      new EditFilterTemplate(TEST_TRANS_TID[2]),
      new EditFilterTemplate(TEST_TRANS_TID[3]),
      new EditFilterTemplate(TEST_TRANS_TID[4]),
  };

  public static final SlideTemplate[] TEST_SLIDE_THEME_TID = new SlideTemplate[] {
      new SlideTemplate(0x0100000000400411L, "520-1", R.drawable.thumbnail_520, 1, 6),
      new SlideTemplate(0x0100000000400423L, "pink", R.drawable.thumbnail_pink, 1, 7),
      new SlideTemplate(0x010000000040042EL, "musume", R.drawable.thumbnail_musume, 1, 7),
      new SlideTemplate(0x0100000000400435L, "SICKO", R.drawable.thumbnail_sicko, 4, 8),
      new SlideTemplate(0x0100000000400436L, "love", R.drawable.thumbnail_love, 1, 7)
  };
}
