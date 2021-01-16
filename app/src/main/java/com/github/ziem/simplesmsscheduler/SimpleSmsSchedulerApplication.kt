package com.github.ziem.simplesmsscheduler

import android.app.Application
import android.os.StrictMode
import com.github.ziem.simplesmsscheduler.BuildConfig
import com.github.ziem.simplesmsscheduler.di.DatabaseModule
import com.github.ziem.simplesmsscheduler.di.ToothpickActivityLifecycleCallbacks
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieApplicationModule

class SimpleSmsSchedulerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }

        AndroidThreeTen.init(this)

        Toothpick.inject(this, openApplicationScope(this))
        registerActivityLifecycleCallbacks(ToothpickActivityLifecycleCallbacks())
    }

    private fun openApplicationScope(application: SimpleSmsSchedulerApplication): Scope {
        return Toothpick.openScope(application).apply {
            installModules(SmoothieApplicationModule(application))
            installModules(DatabaseModule)
        }
    }
}
