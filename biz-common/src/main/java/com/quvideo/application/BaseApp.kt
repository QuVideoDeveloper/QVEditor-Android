package com.quvideo.application

import android.app.Application

class BaseApp private constructor() {

  companion object {
    val instance: BaseApp by lazy { BaseApp() }
  }

  lateinit var app: Application

  fun init(
    ctx: Application
  ) {
    app = ctx

  }
}