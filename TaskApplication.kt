package com.cloudin.monsterchicken

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.multidex.MultiDex
import com.cloudin.monsterchicken.basedata.source.TaskRepository
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.Constant
import com.cloudin.monsterchicken.utils.ServiceLocator

class TaskApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: TaskApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    val taskRepository: TaskRepository
        get() = ServiceLocator.provideTasksRepository(this)


    fun checkInternet(): Boolean {
        var isInternetConnected = false
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        isInternetConnected =
            connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false
        if (!isInternetConnected)
            Toast.makeText(
                this,
                "Please check your internet connection and try again",
                Toast.LENGTH_SHORT
            ).show()
        return isInternetConnected
    }

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = this.applicationContext
            .getSharedPreferences(Constant.flavourName, Context.MODE_PRIVATE)
        CloudInPreferenceManager.initializePreferenceManager(sharedPreferences)
        val context: Context = applicationContext()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(newBase)
    }
}