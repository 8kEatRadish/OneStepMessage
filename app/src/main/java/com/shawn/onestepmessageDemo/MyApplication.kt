package com.shawn.onestepmessageDemo

import android.app.Application
import com.shawn.oneStepMessage.OSM

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        OSM.init(this)
    }
}