package com.genetic.darkphantom

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class GeneticApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "genetic_channel",
                "Genetic Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background service"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
