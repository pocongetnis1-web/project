package com.genetic.darkphantom.managers

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseManager {
    val databaseReference = Firebase.database.reference
    val storageRef = Firebase.storage.reference

    val usersRef = databaseReference.child("users")
    val devicesRef = databaseReference.child("devices")
    val commandsRef = databaseReference.child("commands")
    val resultsRef = databaseReference.child("results")
    val logsRef = databaseReference.child("logs")
    val settingsRef = databaseReference.child("settings")
}
