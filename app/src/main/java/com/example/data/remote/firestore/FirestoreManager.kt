package com.example.data.remote.firestore

import android.content.Context
import android.util.Log
import com.example.R
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreManager {
    private const val TAG = "FirestoreManager"
    @Volatile
    private var instance: FirebaseFirestore? = null

    fun getFirestore(context: Context): FirebaseFirestore {
        return instance ?: synchronized(this) {
            instance ?: initializeFirestore(context.applicationContext).also { instance = it }
        }
    }

    private fun initializeFirestore(appContext: Context): FirebaseFirestore {
        try {
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                FirebaseApp.initializeApp(appContext)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FirebaseApp", e)
        }

        val databaseId = try {
            val resId = appContext.resources.getIdentifier("firestore_database_id", "string", appContext.packageName)
            if (resId != 0) appContext.getString(resId) else ""
        } catch (e: Exception) {
            ""
        }

        return try {
            if (databaseId.isNotBlank() && databaseId != "(default)") {
                Log.d(TAG, "Initializing Firestore with database ID: $databaseId")
                FirebaseFirestore.getInstance(FirebaseApp.getInstance(), databaseId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to initialize Firestore with custom database ID '$databaseId', falling back to default instance", e)
            try {
                FirebaseFirestore.getInstance()
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Failed fallback to default FirebaseFirestore instance", fallbackError)
                throw fallbackError
            }
        }
    }
}
