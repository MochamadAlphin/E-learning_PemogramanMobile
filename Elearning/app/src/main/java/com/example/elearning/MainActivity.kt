package com.example.elearning

import com.example.elearning.databinding.ActivityMainBinding
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

        binding.submitButton.setOnClickListener {
            scheduleNotification()
        }
    }

    private fun scheduleNotification() {
        val title = binding.titleET.text.toString().ifEmpty { "Test Title" }
        val message = binding.messageET.text.toString().ifEmpty { "Test Message" }

        Log.d("MainActivity", "Scheduling notification with title: $title and message: $message")

        val intent = Intent(applicationContext, Notif::class.java).apply {
            putExtra("titleExtra", title)
            putExtra("messageExtra", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val time = getTime()

        Log.d("MainActivity", "Scheduled time: $time, Current time: ${System.currentTimeMillis()}")

        if (time > System.currentTimeMillis()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
            showAlert(time, title, message)
        } else {
            showAlertInvalidTime()
        }
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification scheduled")
            .setMessage("Title: $title\nMessage: $message\nAt: ${dateFormat.format(date)} ${timeFormat.format(date)}")
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    private fun showAlertInvalidTime() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Please select a future date and time.")
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    private fun getTime(): Long {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)

        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelID = "NotifChannel"
            val desc = "Notification channel description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, "Alarm Notifications", importance).apply {
                description = desc
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}