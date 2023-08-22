package com.cloudin.greenbound.utils.commondialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.utils.commondialog.CommonListAdapter
import com.cloudin.monsterchicken.utils.commondialog.CommonListResponse

object CloudInCommonListDialog {

    lateinit var dialog: Dialog

    fun showCloudInCommonListDialog(
        context: Context,
        dialogTitle: String,
        commonResponseList: List<CommonListResponse.CommonList>,
        listener: OnItemSelect,
    ) {
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.common_list_dialog)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = layoutParams


        val rvCommonListItems = dialog.findViewById<RecyclerView>(R.id.rvCommonListItems)
        val tvDialogHeading = dialog.findViewById<TextView>(R.id.tvDialogHeading)
        val commonListAdapter = CommonListAdapter(commonResponseList)
        rvCommonListItems.adapter = commonListAdapter
        tvDialogHeading.text = dialogTitle
        commonListAdapter.onItemClick = { it ->
            listener.onSelectItemClick(it)
            dialog.dismiss()
        }
        dialog.show()
    }

    interface OnItemSelect {
        fun onSelectItemClick(dialogModel: CommonListResponse.CommonList)
    }

}