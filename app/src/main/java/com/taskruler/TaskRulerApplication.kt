package com.taskruler

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level

/**
 * Overwrites default Android app to start Koin
 */
class TaskRulerApplication : Application() {
    /**
     * Overrides Android onCreate to start Koin
     * Adds Android context Application name TaskRulerApplication
     * @property appModule
     */
    override fun onCreate() {
        super.onCreate()

        GlobalContext.startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@TaskRulerApplication)
            modules(appModule)
        }
    }
}