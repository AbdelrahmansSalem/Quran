package com.quran.labs.androidquran

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Audio
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.quran.data.core.QuranInfo
import com.quran.data.pageinfo.common.MadaniDataSource
import com.quran.data.source.PageProvider
import com.quran.labs.androidquran.common.audio.model.playback.AudioRequest
import com.quran.labs.androidquran.common.audio.util.QariUtil
import com.quran.labs.androidquran.databinding.ActivityRemindMeBinding
import com.quran.labs.androidquran.presenter.audio.service.AudioQueue
import com.quran.labs.androidquran.service.AudioService
import com.quran.labs.androidquran.ui.QuranActivity
import com.quran.labs.androidquran.util.AudioUtils
import com.quran.labs.androidquran.util.QuranFileUtils
import java.util.Calendar
import java.util.Date


class RemindMeActivity : AppCompatActivity() {
  private lateinit var binding: ActivityRemindMeBinding

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    binding=ActivityRemindMeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    createNotificationChannel()

    // Set onClickListener for the submit button
    binding.submitButton.setOnClickListener {
      // Check if notification permissions are granted
      if (checkNotificationPermissions(this)) {
        // Schedule a notification
        scheduleNotification()
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.M)
  @SuppressLint("ScheduleExactAlarm")
  private fun scheduleNotification() {
    // Create an intent for the Notification BroadcastReceiver
    val intent = Intent(applicationContext, Notification::class.java)

    // Extract title and message from user input
    val title = "Quran Now"
    val message = "Time to read"

    // Add title and message as extras to the intent
    intent.putExtra(titleExtra, title)
    intent.putExtra(messageExtra, message)

    // Create a PendingIntent for the broadcast
    val pendingIntent = PendingIntent.getBroadcast(
      applicationContext,
      notificationID,
      intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Get the AlarmManager service
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Get the selected time and schedule the notification
    val time = getTime()
    alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      time,
      pendingIntent
    )

    // Show an alert dialog with information
    // about the scheduled notification
    showAlert(time, title, message)
  }

  private fun showAlert(time: Long, title: String, message: String) {
    // Format the time for display
    val date = Date(time)
//    val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
    val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

    // Create and show an alert dialog with notification details
    AlertDialog.Builder(this)
      .setTitle("Notification Scheduled")
      .setMessage(
        "Title: $title\nMessage: $message\nAt: ${timeFormat.format(date)}"
      )
      .setPositiveButton("Okay") { _, _ -> }
      .show()
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun getTime(): Long {
    // Get selected time from TimePicker and DatePicker
    val minute = binding.timePicker.minute
    val hour = binding.timePicker.hour


    // Create a Calendar instance and set the selected date and time
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)


    return calendar.timeInMillis
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createNotificationChannel() {
    // Create a notification channel for devices running
    // Android Oreo (API level 26) and above
    val name = "Notify Channel"
    val desc = "A Description of the Channel"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(channelID, name, importance)
    channel.description = desc

    // Get the NotificationManager service and create the channel
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }

  fun checkNotificationPermissions(context: Context): Boolean {
    // Check if notification permissions are granted
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      val isEnabled = notificationManager.areNotificationsEnabled()

      if (!isEnabled) {
        // Open the app notification settings if notifications are not enabled
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        context.startActivity(intent)

        return false
      }
    } else {
      val areEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

      if (!areEnabled) {
        // Open the app notification settings if notifications are not enabled
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        context.startActivity(intent)

        return false
      }
    }

    // Permissions are granted
    return true
  }
}




