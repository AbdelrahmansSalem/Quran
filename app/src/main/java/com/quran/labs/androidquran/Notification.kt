package com.quran.labs.androidquran

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.quran.labs.androidquran.ui.PagerActivity
import com.quran.labs.androidquran.ui.QuranActivity


// Constants for notification
const val notificationID = 121
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"


// BroadcastReceiver for handling notifications
class Notification : BroadcastReceiver() {
  var isNotify=false
  // Method called when the broadcast is received
  override fun onReceive(context: Context, intent: Intent) {
    val intentActivity: Intent = Intent(
      context,
      QuranActivity::class.java
    )
//    intentActivity.putExtra("key","1")
    val contentIntent =
      PendingIntent.getActivity(context, 0, intentActivity, PendingIntent.FLAG_UPDATE_CURRENT)


    // Build the notification using NotificationCompat.Builder
    val pattern = longArrayOf(500, 500, 500, 500, 500, 500, 500, 500, 500)
    val notification = NotificationCompat.Builder(context, channelID)
      .setSmallIcon(R.drawable.icon)
      .setContentTitle(intent.getStringExtra(titleExtra)) // Set title from intent
      .setContentText(intent.getStringExtra(messageExtra))
      .setPriority(2)//
      .setVibrate(pattern)
      .setContentIntent(contentIntent)
      .setAutoCancel(true)

      // Set content text from intent
      .build()

    // Get the NotificationManager service
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Show the notification using the manager
    manager.notify(notificationID, notification)
  }
}
