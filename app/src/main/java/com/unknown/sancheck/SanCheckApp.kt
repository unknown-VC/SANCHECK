package com.unknown.sancheck

import android.app.Application
import com.unknown.sancheck.di.AppContainer

class SanCheckApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
