package com.cloudin.monsterchicken.utils.mappicker.presentation.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cloudin.monsterchicken.utils.mappicker.utils.SharedPrefs

class CloudinMapViewModelFactory(
    private val sharedPrefs: SharedPrefs,
    private val application: Application
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.cast(CloudinMapViewModel(sharedPrefs, application)) as T
    }
}