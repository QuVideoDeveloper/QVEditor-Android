package com.quvideo.application.camera.recorder

/**
 * 记录录制的镜头数据
 */
class RecorderClipInfo(var filePath: String) {

  /**
   * 拍摄时间
   */
  var dateTaken: Long = 0

  /**
   * camera 方向
   */
  var orientation = 0

  /**
   * 镜头在录制文件中的位置
   * recorderPos[0]为起始位置
   * recorderPos[1]为结束位置
   */
  var recorderPos = arrayOf(0, 0)

  /**
   * 拍摄速度
   */
  var timeScale = 1.0f

  /**
   * 使用的滤镜信息
   */
  var effectItem: EffectItem? = null

  class EffectItem(var effectFilePath: String) {
    /**
     * effect config index
     */
    var effectConfigureIndex = 0
  }
}