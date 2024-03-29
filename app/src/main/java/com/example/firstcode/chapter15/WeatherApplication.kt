package com.example.firstcode.chapter15

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class WeatherApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        const val TOKEN = "aFpW97l8jNjnO3Lc"
    }
}