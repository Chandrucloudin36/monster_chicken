package com.cloudin.monsterchicken.commonclass

import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.NonNull
import androidx.core.content.res.ResourcesCompat
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.databinding.CustomLoaderBinding
import com.cloudin.monsterchicken.utils.NativeUtils

object Loader {

    var dialog: Dialog? = null
    private lateinit var customLoaderBinding: CustomLoaderBinding
    fun hide() {
        try {
            if (dialog != null && dialog?.isShowing == true)
                dialog?.dismiss()
        } catch (e: Exception) {
            NativeUtils.ErrorLog("Error", e.toString())
            e.printStackTrace()
        }
    }

    fun show(context: Context, title: CharSequence? = null) {

        if (dialog != null && dialog?.isShowing!!) {
            dialog?.dismiss()
        }

        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customLoaderBinding = CustomLoaderBinding.inflate(inflator, null, false)
        val view = inflator.inflate(R.layout.custom_loader, null)
        if (title != null) {
            customLoaderBinding.cpTitle.text = title
        }
        customLoaderBinding.cpBgView.setBackgroundColor(Color.parseColor("#60000000")) //Background Color
        setColorFilter(
            customLoaderBinding.cpPbar.indeterminateDrawable,
            ResourcesCompat.getColor(context.resources, R.color.purple_200, null)
        ) //Progress Bar Color
        customLoaderBinding.cpTitle.setTextColor(Color.WHITE) //Text Color

        dialog = Dialog(context, R.style.CustomProgressBarTheme)
        dialog?.setContentView(view)
        dialog!!.show()
    }

    private fun setColorFilter(@NonNull drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
}

object CustomLoader {
    lateinit var dialog: Dialog

    fun create(context: Context): Dialog {
        return show(context, null)
    }

    fun show() {
        try {
            if (!dialog.isShowing)
                dialog.show()
        } catch (e: Exception) {
            NativeUtils.ErrorLog("Error", e.toString())
        }
    }

    fun hide() {
        if (dialog != null && dialog.isShowing)
            dialog.dismiss()
    }

    private fun show(context: Context, title: CharSequence?): Dialog {
        lateinit var customLoaderBinding: CustomLoaderBinding
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        customLoaderBinding = CustomLoaderBinding.inflate(inflator, null, false)
        val view = inflator.inflate(R.layout.custom_loader, null)
        if (title != null) {
            customLoaderBinding.cpTitle.text = title
        }
        customLoaderBinding.cpBgView.setBackgroundColor(Color.parseColor("#60000000")) //Background Color
        setColorFilter(
            customLoaderBinding.cpPbar.indeterminateDrawable,
            ResourcesCompat.getColor(context.resources, R.color.c961A1C, null)
        ) //Progress Bar Color
        customLoaderBinding.cpTitle.setTextColor(Color.WHITE)

        dialog = Dialog(context, R.style.CustomProgressBarTheme)
        dialog.setContentView(view)

        return dialog
    }

    private fun setColorFilter(@NonNull drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
}