package com.cloudin.monsterchicken.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Patterns
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

class DeviceUtils {

    companion object {

        @SuppressLint("HardwareIds")
        fun getAndroidID(context: Context): String? {
            var id: String?
            id = Settings.Secure.getString(
                context.applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            if (id == null) {
                id = ""
            }
            return id
        }

        /*
     * Detech the device offset value
     */
        fun getDeviceOffset(): Int {
            val calendar = Calendar.getInstance(
                TimeZone.getTimeZone("GMT"),
                Locale.getDefault()
            )
            val startTimeMillis =
                calendar[Calendar.HOUR] * 1000 * 60 * 60 + (calendar[Calendar.MINUTE] * 1000 * 60).toLong()
            val tz = TimeZone.getDefault()
            return tz.getOffset(startTimeMillis)
        }

        fun getBatteryStatus(context: Context): String? {
            try {
                val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                val batteryStatus = context.registerReceiver(null, intentFilter)
                val level = batteryStatus?.getIntExtra(
                    BatteryManager.EXTRA_LEVEL, -1
                ) ?: 0
                //            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//            ErrorLog("batteryStatus","" + level + "  " + scale);
//            float batteryPct = level / (float)scale;
                return level.toString()
            } catch (ignored: Exception) {
            }
            return ""
        }

        fun getGPSStatus(context: Context): Boolean {
            val manager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        fun displayPromptForEnablingGPS(activity: Activity) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            val action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            val message =
                "Enable either GPS or any other location" + " service to find current location.  Click OK to go to" + " location services settings to let you do so."
            builder.setMessage(message)
                .setPositiveButton("OK", DialogInterface.OnClickListener { d, id ->
                    activity.startActivity(Intent(action))
                    d.dismiss()
                }).setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { d, id -> d.cancel() })
            builder.create().show()
        }

        fun isValidEmail(target: CharSequence?): Boolean {
            return target != null && Patterns.EMAIL_ADDRESS.matcher(
                target.toString().trim { it <= ' ' }).matches()
        }


        fun composeEmail(
            context: Context,
            addresses: Array<String?>?,
            message: String?
        ) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, addresses)
            intent.putExtra(Intent.EXTRA_SUBJECT, message)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        }

        fun isPackageExisted(
            context: Context,
            targetPackage: String?
        ): Boolean {
            val pm = context.packageManager
            try {
                val info =
                    pm.getPackageInfo(targetPackage!!, PackageManager.GET_META_DATA)
            } catch (e: PackageManager.NameNotFoundException) {
                return false
            }
            return true
        }

        @SuppressLint("MissingPermission")
        fun getPhoneNumber(context: Context): String? {
            val tMgr =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (tMgr != null) {
                var ph =
                    if (tMgr.line1Number != null) tMgr.line1Number else ""
                if (ph != "" && ph.length > 10 && ph.startsWith("+91")) {
                    ph = ph.substring(3)
                } else if (ph != "" && ph.length > 10 && ph.startsWith("91")) {
                    ph = ph.substring(2)
                }
                return ph
            }
            return ""
        }


        @SuppressLint("Range")
        fun getUserName(context: Context): String? {
            var name = ""
            val columnNames = arrayOf(
                ContactsContract.Profile.DISPLAY_NAME
            )
            val c = context.contentResolver.query(
                ContactsContract.Profile.CONTENT_URI, columnNames, null, null, null
            )
            if (c != null) {
                val count = c.count
                c.moveToFirst()
                if (count > 0) {
                    name = c.getString(c.getColumnIndex("display_name"))
                }
                c.close()
                return name
            }
            return ""
        }


        fun getPhoneModel(): String? {
            return Build.MODEL
        }

        fun getVersion(): String? {
            return "" + Build.VERSION.SDK_INT
        }

        fun getCarrier(context: Context?): String? {
            if (context == null) return ""
            val manager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return if (manager != null && manager.simState == TelephonyManager.SIM_STATE_READY) manager.simOperatorName else ""
        }

        fun getManufacturer(): String? {
            return Build.MANUFACTURER
        }


        fun getNetworkType(context: Context): String? {
            var flag = ""
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo != null) {
                if (networkInfo.isConnectedOrConnecting) {
                    flag = if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                        "TYPE_WIFI"
                    } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                        "TYPE_MOBILE"
                    } else {
                        "No Internet"
                    }
                }
            }
            return flag
        }

        fun getNetworkSubType(context: Context?): String? {
            if (context != null) {
                val cm =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val ni = cm.activeNetworkInfo
                if (ni != null && ni.isConnected && ni.typeName != "WIFI") {
                    return ni.subtypeName
                }
            }
            return ""
        }

        fun getLocalIpAddress(): String? {
            try {
                val en =
                    NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr =
                        intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (ex: SocketException) {
                ex.printStackTrace()
            }
            return ""
        }
    }
}