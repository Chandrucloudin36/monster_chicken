package com.cloudin.monsterchicken.firebasepush

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.notificationlist.NotificationListActivity
import com.cloudin.monsterchicken.utils.NativeUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class CloudInMessagingService : FirebaseMessagingService() {

    private val tag = "FirebaseMessagingService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("$tag token --> $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        try {
            NativeUtils.ErrorLog("imageUrl", remoteMessage.data.toString())
            val json = JSONObject((remoteMessage.data as Map<*, *>?)!!)
            showNotification(
                json.getString("title"),
                json.getString("id"),
                json.getString("body"),
                json.getString("imageUrl")
            )
        } catch (e: Exception) {
            println("$tag error -->${e.localizedMessage}")
        }
    }

    private fun showNotification(
        title: String?,
        notificationId: String?,
        body: String?,
        imageUrl: String
    ) {

        val notifyIntent = Intent(this, NotificationListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("DetailsFrom", "PushNotification")
            putExtra("NewsId", notificationId)
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

//        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
//            // Add the intent, which inflates the back stack
//            addNextIntentWithParentStack(notifyIntent)
//            // Get the PendingIntent containing the entire back stack
//            getPendingIntent(
//                0,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        }

        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = getString(R.string.default_notification_channel_name)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannels(channelId, channelName, notificationManager)
        }

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val randNotificationNumber = Random()
        val notificationId: Int = randNotificationNumber.nextInt(1000)

        if (imageUrl.isEmpty()) {
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.notification_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(notifyPendingIntent)

            notificationManager.notify(notificationId, notificationBuilder.build())
        } else {
            val bitmap: Bitmap = getBitmapFromURL(imageUrl)!!

            val bigPictureStyle = NotificationCompat.BigPictureStyle()
            bigPictureStyle.setBigContentTitle(title)
            bigPictureStyle.setSummaryText(Html.fromHtml(body).toString())
            bigPictureStyle.bigPicture(bitmap)

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.notification_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setStyle(bigPictureStyle)
                .setContentIntent(notifyPendingIntent)

            notificationManager.notify(notificationId, notificationBuilder.build())
        }


    }

    private fun getBitmapFromURL(strURL: String?): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannels(
        channelId: String,
        channelName: String,
        notificationManager: NotificationManager
    ) {

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }
}