package com.cloudin.monsterchicken.commonclass

import android.annotation.TargetApi
import android.app.DownloadManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.USER_PUSH_TOKEN
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

open class CloudInBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NativeUtils.ErrorLog("ClassName", javaClass.simpleName)
        disableAutofill()
    }

    @TargetApi(Build.VERSION_CODES.O)
    open fun disableAutofill() {
        window.decorView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }

    open fun generatePushToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                NativeUtils.ErrorLog(
                    "Fetching FCM registration token failed",
                    task.exception.toString()
                )
                return@OnCompleteListener
            }
            NativeUtils.ErrorLog("PushId", task.result.toString())
            CloudInPreferenceManager.setString(USER_PUSH_TOKEN, task.result.toString())
        })
    }

    open fun downloadTask(downloadLink: String, fileName: String) {
        Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show()
        val request =
            DownloadManager.Request(Uri.parse(downloadLink))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "/MonsterChicken/$fileName"
            )
            val dm =
                getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        } else {
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir("/MonsterChicken", fileName)
            val dm =
                getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
    }

}