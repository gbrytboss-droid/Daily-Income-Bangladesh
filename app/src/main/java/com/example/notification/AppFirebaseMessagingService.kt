package com.example.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token received: $token")
        NotificationHelper.saveFcmToken(applicationContext, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "দৈনিক আয়ের রিমাইন্ডার! 🪙"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "প্রতি বিজ্ঞাপনে ০.৫০ টাকা আয় করে ৫০০ টাকার টার্গেট পূরণ করুন।"

        NotificationHelper.showEarningReminderNotification(
            context = applicationContext,
            title = title,
            body = body
        )
    }

    companion object {
        private const val TAG = "AppFCMService"
    }
}
