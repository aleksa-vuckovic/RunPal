package com.example.runpal

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {

    @Inject
    lateinit var receiver: EventReminderReceiver
    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(applicationContext)

        val reminderIntent = Intent(this, EventReminderReceiver::class.java)
        reminderIntent.action = ACTION_DAILY_REMINDER
        var reminder = PendingIntent.getBroadcast(this, REMINDER_REQUEST_CODE, reminderIntent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (reminder == null) {
            reminder = PendingIntent.getBroadcast(this, REMINDER_REQUEST_CODE, reminderIntent, PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            Log.d("REMINDER", "Creating reminder alarm.")
            val triggerAtMillis = System.currentTimeMillis() + 60000L
            alarmManager.setRepeating(AlarmManager.RTC, triggerAtMillis, 24*3600000L, reminder)
        }

        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(receiver, filter)
    }
}