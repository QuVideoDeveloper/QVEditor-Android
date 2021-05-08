package com.quvideo.application

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.quvideo.application.db.DraftDBHelper
import com.quvideo.application.glidedecoder.EffectThumbParams
import com.quvideo.application.glidedecoder.XytModelLoaderFactory
import com.quvideo.application.sp.DemoSharedPref
import com.quvideo.application.superedit.SuperEditManager
import com.quvideo.application.utils.FileUtils
import com.quvideo.mobile.component.template.XytInstallListener
import com.quvideo.mobile.component.template.XytManager
import com.quvideo.mobile.engine.QEEngineClient
import com.quvideo.mobile.engine.QEInitData.Builder
import java.io.File
import java.util.ArrayList

class EditorApp private constructor() {

  companion object {
    val instance: EditorApp by lazy { EditorApp() }
  }

  lateinit var app: Application

  var editorConfig = EditorConfig()

  fun init(
    ctx: Application,
    licensePath: String
  ) {
    app = ctx

    DraftDBHelper.setAppContext(app)
    QEEngineClient.init(app, Builder(licensePath).build())
    SuperEditManager.initAIComponent(app)
    val lastCersionCode = DemoSharedPref.getInstance()
        .lastVersionCode
    val currentVersion: Long = getVersionCode(app)
    if (currentVersion != lastCersionCode) {
      // 版本更新的时候才需要覆盖安装
      // 安装一些asset的xyt素材文件
      XytManager.installAsset(
          AssetConstants.mScanTemplateList, object : XytInstallListener {
        override fun onSuccess() {}
        override fun onFailed(filePaths: MutableList<String>?, errorCode: Int) {}
      })
      val zipPaths = ArrayList<String>()
      // 拷贝音频文件到sdcard
      for (assetPath in AssetConstants.mMusicTemplateList) {
        copyAssetFile(assetPath)
      }
      // 拷贝字体font到sdcard
      for (assetPath in AssetConstants.mFontTemplateList) {
        copyAssetFile(assetPath)
      }
      // 拷贝水印文件到sdcard
      for (assetPath in AssetConstants.mWaterTemplateList) {
        copyAssetFile(assetPath)
      }
      // 拷贝zip素材到sd卡进行解压并安装
      for (assetPath in AssetConstants.mScanZipTemplateList) {
        val zipPath: String? = copyAssetFile(assetPath)
        if (zipPath != null) {
          zipPaths.add(zipPath)
        }
      }
      //      for (zipPath in zipPaths) {
      XytManager.install(zipPaths, null)
      //      }
      DemoSharedPref.getInstance().saveLastVersion(currentVersion)
    }
    // 使用Glide加载素材缩略图
    Glide.get(ctx)
        .registry
        .prepend(
            EffectThumbParams::class.java, Bitmap::class.java,
            XytModelLoaderFactory()
        )
  }

  /**
   * 如果是Asset文件，迁移到SDCARD
   */
  private fun copyAssetFile(strFile: String): String? {
    if (TextUtils.isEmpty(strFile)) {
      return null
    }
    val newFilePath = strFile.replace(
        "assets_android://",
        StorageUtils.getTemplatePath(app)
    )
    if (!FileUtils.isFileExisted(newFilePath)) {
      //copy from assets
      val fileParentPath = File(newFilePath)
      FileUtils.createMultilevelDirectory(fileParentPath.parent)
      FileUtils.copyFileFromAssets(
          strFile.substring("assets_android://".length), newFilePath, app.assets
      )
    }
    return newFilePath
  }

  private fun getVersionCode(context: Context): Long {
    try {
      val pm = context.packageManager
      val pi = pm.getPackageInfo(context.packageName, 0)
      return pi.versionCode.toLong()
    } catch (ignore: Exception) {
    }
    return -1
  }
}