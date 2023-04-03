package com.bumble.appyx.sandbox

import android.app.Application
import com.gu.toolargetool.TooLargeTool

class SandboxApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        TooLargeTool.startLogging(this)
    }
}
