package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

object NotificationHelper {
    const val CHANNEL_ID = "daily_earning_reminders_channel"
    private const val CHANNEL_NAME = "Daily Earning Reminders"
    private const val CHANNEL_DESC = "Notifications reminding users to watch ads and achieve their 500 Taka goal"
    private const val TAG = "NotificationHelper"
    const val TOPIC_DAILY_EARNING = "daily_earning_reminders"

    private const val PREFS_NAME = "fcm_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun initFcm(context: Context, onTokenReceived: (String) -> Unit = {}) {
        try {
            createNotificationChannel(context)
        } catch (e: Throwable) {
            Log.w(TAG, "Channel creation warning: ${e.message}")
        }

        // Initialize Firebase Messaging asynchronously in background to prevent UI freeze
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
                    com.google.firebase.FirebaseApp.initializeApp(context)
                }

                // Subscribe to topic for mass broadcasts
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_DAILY_EARNING)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Successfully subscribed to topic: $TOPIC_DAILY_EARNING")
                        } else {
                            Log.w(TAG, "Topic subscription failed", task.exception)
                        }
                    }

                // Retrieve FCM registration token
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result
                            Log.d(TAG, "FCM Token: $token")
                            saveFcmToken(context, token)
                            onTokenReceived(token)
                        } else {
                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        }
                    }
            } catch (e: Throwable) {
                Log.w(TAG, "FCM init warning (ignorable if running in preview emulator): ${e.message}")
            }
        }
    }

    fun saveFcmToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    fun getSavedFcmToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FCM_TOKEN, null)
    }

    fun showEarningReminderNotification(
        context: Context,
        title: String = "আজকের ৳৫০০ টাকার লক্ষ্য পূরণ করুন! 🪙",
        body: String = "প্রতি বিজ্ঞাপনে পাবেন ০.৫০ টাকা! এখন বিজ্ঞাপন দেখে আপনার ব্যালেন্স বৃদ্ধি করুন।"
    ) {
        createNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Notification permission not granted, skipping display")
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            1001,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while notifying: ${e.message}")
        }
    }
}
