package com.cloudin.monsterchicken.activity.notificationlist

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class NotificationListResponse(
    @SerializedName("message")
    @Expose
    var message: List<String>? = null,

    @SerializedName("response")
    @Expose
    var response: Response? = null,

    @SerializedName("status")
    @Expose
    var status: Boolean = false
) : Parcelable


@Parcelize
data class Response(
    @SerializedName("notifications")
    @Expose
    var notifications: List<Notifications> = arrayListOf(),
) : Parcelable

@Parcelize
data class Notifications(
    @SerializedName("notification_id")
    @Expose
    var notification_id: String = "",
    @SerializedName("notification")
    @Expose
    var notification: String = "",
    @SerializedName("is_read")
    @Expose
    var is_read: Int
) : Parcelable
