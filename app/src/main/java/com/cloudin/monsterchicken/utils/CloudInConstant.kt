package com.cloudin.monsterchicken.utils

import com.cloudin.monsterchicken.BuildConfig

const val APP_VERSION = BuildConfig.VERSION_CODE
const val APP_NAME = BuildConfig.VERSION_NAME

const val INPUT_NAME = "input"
const val PREF_NAME = BuildConfig.APPLICATION_ID

const val USER_ID = PREF_NAME + "user_id"
const val USER_AUTH_TOKEN = PREF_NAME + "user_auth_token"
const val USER_IS_GOOGLE_AUTHENDICATED_USER = PREF_NAME + "is_google_authendicated_user"
const val USER_IS_ONBOARDED = PREF_NAME + "is_onboarded"
const val EMI_CALCULATOR_URL = PREF_NAME + "calculator_url"
const val USER_NAME = PREF_NAME + "user_name"
const val USER_PROFILE = PREF_NAME + "user_profile"
const val USER_OTP_USER_ID = PREF_NAME + "user_otp_user_id"
const val USER_TYPE = PREF_NAME + "user_type"
const val USER_EMAIL = PREF_NAME + "user_email"
const val USER_PHONE_NUMBER = PREF_NAME + "user_phone_number"
const val USER_PUSH_TOKEN = PREF_NAME + "push_token"
const val USER_LOCATION_STRING = PREF_NAME + "user_location_string"
const val USER_LOCATION_LOCALITY = PREF_NAME + "user_location_locality"
const val USER_LAT = PREF_NAME + "user_lat"
const val USER_LONG = PREF_NAME + "user_long"
const val USER_UNIQUE_TOKEN = PREF_NAME + "unique_token"
const val PUSH_NOTIFICATION = "pushNotification"
const val NOTIFICATION_ID = 100

const val CART_COUNT = "cart_count"
const val CART_AMOUNT = "cart_amount"

const val API_DASHBOARD = "api/v1.0/mc-web/mobile-dashboard"
const val API_GET_PRODUCT_LIST = "api/v1.0/mc-web/products"
const val API_ADD_TO_CART = "api/v1.0/mc-web/cart"
const val API_GET_CART_DATA_DASHBOARD = "api/v1.0/mc-web/cart/"
const val API_GET_CART_SUMMARY = "api/v1.0/mc-web/cart-summary"
const val API_REQUEST_OTP_FOR_API = "api/v1.0/mc-web/login-with-otp"
const val API_RESEND_OTP = "api/v1.0/mc-web/login/resend-otp"
const val API_VERIFY_OTP = "api/v1.0/mc-web/login/otp-verify"
const val API_ADD_ADDRESS = "api/v1.0/mc-web/address"
const val API_LOCATION_CHECK = "api/v1.0/mc-web/check-service-availability?latitude="
const val API_GET_UNIQUE_TOKEN = "api/v1.0/mc-web/get-unique-token"
const val API_PAYMENT_UPDATE = "api/v1.0/mc-web/cart-checkout"
const val API_ORDER_HISTORY = "api/v1.0/mc-web/my-orders"
const val API_GET_NOTIFICATION_LIST = "api/v1.0/mc-web/notification/my-notifications"
const val API_UPDATE_NOTIFICATION_STATUS = "api/v1.0/mc-web/notification/update/"
const val API_LOGOUT = "api/v1.0/logout"
const val API_GET_PROFILE_DETAILS = "api/v1.0/profile"
const val API_UPDATE_PROFILE_DETAILS = "api/v1.0/profile-update"
const val API_UPLOAD_DOCUMENT = "api/admin/file/upload"
const val API_PRODUCT_DETAILS = "api/v1.0/mc-web/product/"
const val API_GET_STATES_LIST = "api/v1.0/mc-web/states"
const val API_GET_DISTRICT_LIST = "api/v1.0/mc-web/districts/"
const val API_CHECK_PROMO_CODE = "api/v1.0/mc-web/check-promo-code?promo_code="
const val API_UPDATE_PAYMENT_STATUS = "api/v1.0/mc-web/payment/"