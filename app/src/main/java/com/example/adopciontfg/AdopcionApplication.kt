package com.example.adopciontfg

import android.app.Application
import com.example.adopciontfg.data.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AdopcionApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            DatabaseSeeder.seedIfEmpty(this)
        }
    }
}
