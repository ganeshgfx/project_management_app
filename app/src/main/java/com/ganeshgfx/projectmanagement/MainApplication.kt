package com.ganeshgfx.projectmanagement

import android.app.Application
import com.google.android.material.color.DynamicColors.applyToActivitiesIfAvailable
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        applyToActivitiesIfAvailable(this)
    }
}