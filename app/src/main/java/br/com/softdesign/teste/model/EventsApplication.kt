package br.com.softdesign.teste.model

import android.app.Application
import android.os.Build
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EventsApplication : Application() {
    override fun onCreate() {
        if (Build.VERSION.SDK_INT < 21) {
           MultiDex.install(this)
        }
        super.onCreate()
    }
}