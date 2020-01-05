package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {
    private lateinit var buttonNotify:Button
    private lateinit var buttonUpdate:Button
    private lateinit var buttonCancel:Button
    private lateinit var mNotifyManager:NotificationManager
    private lateinit var mReceiver: NotificationReceiver

    companion object {
        const val PRIMARY_CHANNEL_ID:String = "primary_notification_channel"
        private const val NOTIFICATION_ID:Int = 0
        const val ACTION_UPDATE_NOTIFICATION:String =
            "com.example.notification.ACTION_UPDATE_NOTIFICATION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonNotify = findViewById(R.id.notify)
        buttonUpdate = findViewById(R.id.update)
        buttonCancel = findViewById(R.id.cancel)

        buttonNotify.setOnClickListener { sendNotification() }
        buttonUpdate.setOnClickListener { updateNotification()}
        buttonCancel.setOnClickListener { cancelNotification()}

        mReceiver = NotificationReceiver()

        createNotificationChannel()
        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
        setNotificationButtonState(true, false, false)
    }

    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        var updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID
            , updateIntent
            , PendingIntent.FLAG_ONE_SHOT)
        val notifyBuilder = this.getNotificationBuilder()
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent)
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(false, true, true)
    }

    fun updateNotification(){
        val androidImage: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
        var notifyBuilder = getNotificationBuilder()
        notifyBuilder.setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(androidImage)
            .setBigContentTitle("Notification Updated!"))
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(false, false, true)

    }

    fun cancelNotification(){
        mNotifyManager.cancel(NOTIFICATION_ID)
        setNotificationButtonState(true, false, false)
    }

    private fun createNotificationChannel(){
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) run {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Mascot Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor=Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description=getString(R.string.description)
            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationBuilder():NotificationCompat.Builder {
        val notificationIntent =  Intent(this, MainActivity::class.java)
        val notificationPendingIntent: PendingIntent = PendingIntent.getActivity(this,
            NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val cancelPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
            notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("You've been notified!")
            .setContentText("This is your notification text.")
            .setSmallIcon(R.drawable.ic_android)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .setDeleteIntent(cancelPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
    }

    fun setNotificationButtonState(isNotifyEnabled: Boolean,
                                   isUpdateEnabled: Boolean,
                                   isCancelEnabled: Boolean) {
        buttonNotify.setEnabled(isNotifyEnabled)
        buttonUpdate.setEnabled(isUpdateEnabled)
        buttonCancel.setEnabled(isCancelEnabled)
    }


    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    inner class NotificationReceiver() : BroadcastReceiver() {

        override fun onReceive(ctx: Context?, intent: Intent?) {
            updateNotification()
        }

    }

}


