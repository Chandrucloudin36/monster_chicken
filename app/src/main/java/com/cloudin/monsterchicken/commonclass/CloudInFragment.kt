package com.cloudin.monsterchicken.commonclass

import android.os.Bundle
import androidx.fragment.app.Fragment

open class CloudInFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CustomLoader.create(requireActivity())
    }
}