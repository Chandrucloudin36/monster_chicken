package com.cloudin.monsterchicken.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.JsonReader
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.cloudin.monsterchicken.R
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.math.BigInteger
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object NativeUtils {

    init {
        System.loadLibrary("cloudin-jni")
    }

    private external fun getDevUrl(): String?
    private external fun getProdUrl(): String?
    private external fun getTestUrl(): String?

    private fun getAppDomain(): String? {
        return when (Constant.flavour) {
            "dev" -> {
                getDevUrl()!!
            }
            "prod" -> {
                getProdUrl()!!
            }
            "test" -> {
                getTestUrl()!!
            }
            else -> ""
        }
    }

    fun checkValidatePassword(text: String?): Boolean {
        val pattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$"
        return Pattern.matches(pattern, text!!)
    }


    fun isAppInstalled(context: Context, packageName: String?): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName!!, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun addEditTextObservers(
        textInputEditText: TextInputEditText,
        mutableLiveData: MutableLiveData<String>
    ) {
        textInputEditText.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mutableLiveData.value = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    private fun getAppDomainProtocol(): String? {
        return if (Constant.flavour == "prod")
            "https://"
        else "https://"
    }


    fun getAppDomainURL(): String? {
        return getAppDomainProtocol() + getAppDomain()
    }

    fun getAppDomainURL(path: String): String? {
        return getAppDomainProtocol() + getAppDomain() + path
    }


    fun checkValidatePanNumber(text: String?): Boolean {
        val pattern: String? = "(([A-Za-z]{5})([0-9]{4})([a-zA-Z]))"
        return Pattern.matches(pattern!!, text!!)
    }

    fun checkValidateAccountNumber(text: String?): Boolean {
        val pattern: String? = "[0-9]{9,18}"
        return Pattern.matches(pattern!!, text!!)
    }

    fun checkValidateIFSC(text: String?): Boolean {
        val pattern: String? = "^[A-Z]{4}0[A-Z0-9]{6}\$"
        return Pattern.matches(pattern!!, text!!)
    }


    private const val PLAY_SERVICES_RESOLUTION_REQUEST = 1000


    fun getNameAlias(name: String): String {
        return if (name.split(" ").size > 1)
            name.split(" ")[0][0].toString() + name.split(" ")[1][0].toString()
        else
            name.split(" ")[0][0].toString()
    }

    fun loadImage(view: ImageView, url: String) {
//            if (url.isNotEmpty() && url != "NULL")
//                Glide.with(view).load(url).into(view)
    }

    fun setLoadImage(imageView: ImageView, imgUrl: String) {
        if (imgUrl != null) {
            if (imgUrl.endsWith("svg")) {
                val imageLoader = ImageLoader.Builder(imageView.context)
                    .components {
                        add(SvgDecoder.Factory())
                    }
                    .build()

                val request = ImageRequest.Builder(imageView.context)
                    .crossfade(true)
                    .crossfade(500)
                    .data(imgUrl)
                    .target(imageView)
                    .build()

                imageLoader.enqueue(request)
            } else
                Glide.with(imageView.context).load(imgUrl)
                    .into(imageView)
        }
    }

    fun isInternetOn(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    fun showNoInternet(context: Context) {
        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
    }

    fun changeDateFormat(dateString: String): String {
        var dateFormat = ""
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val sdf1 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val mDate: Date = sdf.parse(dateString)!!
            dateFormat = sdf1.format(mDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dateFormat
    }

    fun showConfirmAlert(
        context: Context,
        message: String,
        cancelableFlag: Boolean,
        positiveText: String,
        negativeText: String,
        listener: NativeDialogClickListener
    ) {
        val builder = AlertDialog.Builder(context)
        with(builder) {
            setMessage(message)
            setCancelable(cancelableFlag)
            setPositiveButton(
                positiveText,
                DialogInterface.OnClickListener { dialog, which -> listener.onPositive(dialog) })
            setNegativeButton(
                negativeText,
                DialogInterface.OnClickListener { dialog, which -> listener.onNegative(dialog) })
            show()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    fun isLogin(): Boolean {
        return !CloudInPreferenceManager.getUserId().equals("")
    }

    fun isLessThanMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT < 23
    }


    fun isLessThanLollipop(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
    }

    fun getStatusBarHeight(activity: Activity): Int {
        var result = 0
        val resourceId =
            activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = activity.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun checkIsImage(url: String?): Boolean {
        try {
            val connection = URL(url).openConnection()
            val contentType = connection.getHeaderField("Content-Type")
            return contentType.startsWith("image/")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    fun hideKeyboard(activity: Activity) {
        val imm =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (activity.currentFocus != null) imm.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken,
            0
        )
    }

    fun ErrorLog(key: String?, value: String?) {
        val maxLength = 1000
        if (Constant.debug) {
            if (value != null) {
                var i = 0
                while (i < value.length) {
                    Log.e(
                        key,
                        value.substring(i, Math.min(value.length, i + maxLength))
                    )
                    i += maxLength
                }
            }
        }
    }

    fun ApiLog(key: String?, value: String?) {
        val maxLength = 1000
        if (Constant.debug) {
            if (value != null) {
                var i = 0
                while (i < value.length) {
                    Log.d(
                        key,
                        value.substring(i, Math.min(value.length, i + maxLength))
                    )
                    i += maxLength
                }
            }
        }
    }

    fun openDialScreen(context: Context, number: String?) {
        if (number!!.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:<${number}>")
            context.startActivity(intent)
        }
    }

    fun openSMSScreen(context: Context, number: String?) {
        if (number!!.isNotEmpty()) {
            val uri = Uri.parse("smsto:${number}")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            context.startActivity(intent)
        }
    }


    fun openEmailScreen(context: Context, mailId: String?) {
        if (mailId!!.isNotEmpty()) {

            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$mailId")
            }
            try {
                context.startActivity(
                    Intent.createChooser(
                        emailIntent,
                        "Choose Email Client..."
                    )
                )
            } catch (e: Exception) {
                ErrorValidation(context, e.message.toString())
            }
        }
    }

    fun getStringFromJson(jsonObject: JSONObject, key: String?): String? {
        try {
            if (jsonObject.has(key) && jsonObject.getString(key) != "null") {
                return jsonObject.getString(key)
            }
        } catch (ignored: Exception) {
        }
        return ""
    }

    fun getIntFromJson(jsonObject: JSONObject, key: String?): Int? {
        try {
            if (jsonObject.has(key) && jsonObject.getInt(key) > 0) {
                return jsonObject.getInt(key)
            }
        } catch (ignored: Exception) {
        }
        return 0
    }

    fun getJsonFromJson(jsonObject: JSONObject?, key: String?): JSONObject? {
        try {
            if (jsonObject != null && jsonObject.has(key) && jsonObject.getJSONObject(key) != null) {
                return jsonObject.getJSONObject(key)
            }
        } catch (ignored: Exception) {
        }
        return null
    }

    fun getArrayFromJson(jsonObject: JSONObject?, key: String?): JSONArray? {
        try {
            if (jsonObject != null && jsonObject.has(key) && jsonObject.getJSONArray(key) != null) {
                return jsonObject.getJSONArray(key)
            }
        } catch (ignored: Exception) {
        }
        return null
    }

    fun launchMarket(activity: Activity) {
        val uri =
            Uri.parse("market://details?id=${Constant.flavourName}")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            activity.startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Unable to find market app", Toast.LENGTH_LONG).show()
        }
    }

    fun launchMarketByPackageName(
        activity: Activity,
        packageName: String
    ) {
        val uri = Uri.parse("market://details?id=$packageName")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            activity.startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Unable to find market app", Toast.LENGTH_LONG).show()
        }
    }

    /*fun checkPlayServices(mActivity: Activity): Boolean {
        val googleAPI: GoogleApiAvailability = GoogleApiAvailability.getInstance()
        val result: Int = googleAPI.isGooglePlayServicesAvailable(mActivity)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(
                    mActivity,
                    result,
                    NativeUtils.PLAY_SERVICES_RESOLUTION_REQUEST
                ).show()
            } else {
                mActivity.finish()
            }
            return false
        }
        return true
    }*/

    fun checkSelfPermission(
        activity: Context,
        permission: String
    ): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun enableHelprPermission(
        activity: Activity,
        permission: Array<String>,
        requestCode: Int,
        cloudInPermissionChecker: CloudInPermissionChecker
    ) {
        val permissions: MutableList<String> =
            ArrayList()
        // Here, thisActivity is the current activity
        for (aPermission in permission) {
            addPermission(permissions, activity, aPermission)
        }
        if (permissions.size > 0) {
            ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), requestCode)
        } else {
            cloudInPermissionChecker.onPermissionSuccess()
        }
    }


    private fun addPermission(
        permissionsNeeded: MutableList<String>,
        activity: Activity,
        permission: String
    ): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity,
                permission
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(permission)
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission
                )
            ) return true
        }
        return false
    }

    fun getJsonReader(data: String?): JsonReader? {
        var data = data
        data = data?.substring(0, data.lastIndexOf("}") + 1)
        val reader =
            JsonReader(StringReader(data))
        reader.isLenient = true
        return reader
    }

    fun noInternetAlert(activity: Activity) {
        Toast.makeText(
            activity,
            activity.getString(R.string.no_internet_alert),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun successNoAlert(activity: Activity?, error: String?) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    fun errorAlert(activity: Activity, error: String?) {
        var error = error
        error = activity.getString(R.string.something_went_wrong)
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    fun getBitmapFromUrl(url: String?): Bitmap? {
        val remote_picture: Bitmap
        try {
            remote_picture =
                BitmapFactory.decodeStream(URL(url).content as InputStream)
            return remote_picture
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun callNativeAlertWithTitle(
        activity: Context,
        title: String?,
        message: String?,
        cancelableFlag: Boolean,
        positiveButtonText: String?,
        negativeButtonText: String,
        nativeDialogClickListener: NativeDialogClickListener
    ) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(message).setCancelable(cancelableFlag)
            .setPositiveButton(
                positiveButtonText
            ) { dialog, whichButton -> nativeDialogClickListener.onPositive(dialog) }
        if (negativeButtonText != "") {
            builder.setNegativeButton(
                negativeButtonText
            ) { dialog, which -> nativeDialogClickListener.onNegative(dialog) }
        }
        //  builder.create().show();
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
    }


    fun callNativeAlertwithoutTitle(
        activity: Context,
        message: String?,
        cancelableFlag: Boolean,
        positiveButtonText: String?,
        negativeButtonText: String,
        nativeDialogClickListener: NativeDialogClickListener
    ) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(message).setCancelable(cancelableFlag)
            .setPositiveButton(
                positiveButtonText
            ) { dialog, whichButton -> nativeDialogClickListener.onPositive(dialog) }
        if (negativeButtonText != "") {
            builder.setNegativeButton(
                negativeButtonText
            ) { dialog, which -> nativeDialogClickListener.onNegative(dialog) }
        }
        builder.create().show()
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentDateNew(): String {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun convertStringToMd5(pass: String): String? {
        var pass = pass
        var password = ""
        val mdEnc: MessageDigest
        try {
            mdEnc = MessageDigest.getInstance("MD5")
            mdEnc.update(pass.toByteArray(), 0, pass.length)
            pass = BigInteger(1, mdEnc.digest()).toString(16)
            while (pass.length < 32) {
                pass = "0$pass"
            }
            password = pass
        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        }
        return password
    }


    fun base64toFileConversion(
        base64String: String?,
        fileName: String?
    ): File? {
        var file: File? = null
        val imgBytesData =
            Base64.decode(base64String, Base64.DEFAULT)
        val root = Environment.getExternalStorageDirectory()
        val dir = File(root.absolutePath + "/MyKotlin")
        if (dir.exists() && dir.isDirectory) {
            file = File(dir, fileName)
            val fileOutputStream: FileOutputStream
            fileOutputStream = try {
                FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return null
            }
            val bufferedOutputStream =
                BufferedOutputStream(fileOutputStream)
            try {
                bufferedOutputStream.write(imgBytesData)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                try {
                    bufferedOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return file
        } else {
            dir.mkdir()
            base64toFileConversion(base64String, fileName)
        }
        return file
    }


    fun encodeDecode(encryptedData: String): String? {
        var encodeDecodeData: String? = ""
        try {
            encodeDecodeData = URLEncoder.encode(encryptedData, "utf-8")
            encodeDecodeData = URLDecoder.decode(encodeDecodeData, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            encodeDecodeData = ""
            e.printStackTrace()
        }
        return encodeDecodeData
    }

    fun formatAmountString(amount: Float): String? {
        val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###")
        return formatter.format(amount.toString())
    }

    fun <T> convertStringToPojo(data: String?, tClass: Class<T>?): T {
        return Gson().fromJson(data, tClass)
    }

    fun <T> pojoToStringParser(data: T): String? {
        return Gson().toJson(data)
    }

    fun dp2px(context: Context, dp: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun sp2px(context: Context, sp: Float): Float {
        val scale = context.resources.displayMetrics.scaledDensity
        return sp * scale
    }

    @Throws(JSONException::class)
    fun JSONObject.toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val keysItr: Iterator<String> = this.keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            var value: Any = this.get(key)
            when (value) {
                is JSONArray -> value = value.toList()
                is JSONObject -> value = value.toMap()
            }
            map[key] = value.toString()
        }
        return map
    }

    @Throws(JSONException::class)
    fun JSONArray.toList(): List<Any> {
        val list = mutableListOf<Any>()
        for (i in 0 until this.length()) {
            var value: Any = this[i]
            when (value) {
                is JSONArray -> value = value.toList()
                is JSONObject -> value = value.toMap()
            }
            list.add(value)
        }
        return list
    }
}