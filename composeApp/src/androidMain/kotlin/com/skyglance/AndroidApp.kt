package com.skyglance

import android.app.Application
import com.skyglance.di.initKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}
