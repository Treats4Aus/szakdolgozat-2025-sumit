package com.example.sumit.services

import com.example.sumit.SumItApplication
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PushNotificationService : FirebaseMessagingService() {
    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val app = applicationContext as SumItApplication
        val userRepository = app.container.userRepository

        serviceScope.launch {
            userRepository.addDeviceToken(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
