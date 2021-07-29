package com.quvideo.application.camera.recorder

interface IRecorderListener {

  fun onRecording(
    filePath: String,
    duration: Long
  )

  fun onRecorderPaused()

  fun onRecorderStopped()
}