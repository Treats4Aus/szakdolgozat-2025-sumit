package com.example.sumit

import android.app.Application
import com.example.sumit.data.AppContainer
import com.example.sumit.data.AppDataContainer

class SumItApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}