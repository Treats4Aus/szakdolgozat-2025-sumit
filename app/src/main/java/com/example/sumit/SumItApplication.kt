package com.example.sumit

import android.app.Application
import com.example.sumit.data.AppContainer
import com.example.sumit.data.AppDataContainer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SumItApplication : Application() {
    lateinit var container: AppContainer
    lateinit var languagePreference: String

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        runBlocking {
            languagePreference = container.preferencesRepository.langPreference.first()
        }
    }
}