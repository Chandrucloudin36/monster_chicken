package com.cloudin.monsterchicken.activity.landing

import android.app.Application
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel

class LandingViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
}