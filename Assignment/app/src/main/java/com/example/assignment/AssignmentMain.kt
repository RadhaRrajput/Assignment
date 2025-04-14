package com.example.assignment

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class AssignmentMain : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
      //  ChiliPhotoPicker.init(GlideImageLoader(), "com.zudgezury.android.fileprov/ider")

    }
}
