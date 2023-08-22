package com.cloudin.monsterchicken.utils

import android.content.Context
import com.cloudin.monsterchicken.basedata.source.DefaultTaskRepository
import com.cloudin.monsterchicken.basedata.source.remote.CloudInCommonApi

object ServiceLocator {

    private val apiCall = CloudInCommonApi()

    @Volatile
    var taskRepository: DefaultTaskRepository? = null

    fun provideTasksRepository(context: Context): DefaultTaskRepository {
        synchronized(this) {
            return taskRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): DefaultTaskRepository {
        val newRepo = DefaultTaskRepository(apiCall)
        taskRepository = newRepo
        return newRepo
    }
}