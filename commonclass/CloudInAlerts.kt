package com.cloudin.monsterchicken.commonclass

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.R

object CloudInAlerts {

    lateinit var dialog: Dialog

    fun showErrorDialog(context: Context, errorResponseList: List<String>) {
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_api_error_dialog)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = layoutParams


        val rvErrorList = dialog.findViewById<RecyclerView>(R.id.rv_error_list)
        val tvClose = dialog.findViewById<TextView>(R.id.tv_close)
        val errorListAdapter = ErrorListAdapter(errorResponseList)
        rvErrorList.adapter = errorListAdapter
        tvClose.setOnClickListener { closeDialog() }

        dialog.show()
    }

    private fun closeDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

}