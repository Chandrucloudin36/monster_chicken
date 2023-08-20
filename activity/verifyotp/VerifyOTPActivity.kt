package com.cloudin.monsterchicken.activity.verifyotp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityVerifyOtpBinding
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import java.util.regex.Pattern


class VerifyOTPActivity : CloudInBaseActivity() {

    private lateinit var verifyOtpBinding: ActivityVerifyOtpBinding
    private val verifyOTPViewModel: VerifyOTPViewModel by viewModels()
    private var countdownTimer: CountDownTimer? = null
    var timeInMilliSeconds = 0L
    private var smsVerifyCatcher = SmsVerifyCatcher(this) {
        if (!it.equals(""))
            getOtpFromMessage(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyOtpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_verify_otp)

        verifyOtpBinding.verifyOtpViewModel = verifyOTPViewModel
        verifyOtpBinding.lifecycleOwner = this
        setContentView(verifyOtpBinding.root)

        initView()
    }

    private fun initView() {
        verifyOTPViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        verifyOTPViewModel.appLogoutLiveData.observe(this) {
            logout(this@VerifyOTPActivity)
        }

        verifyOTPViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }

        verifyOTPViewModel.phoneNumber.value =
            intent?.extras?.getString("PhoneNumber", "").orEmpty()

        if (verifyOTPViewModel.phoneNumber.value != null)
            verifyOTPViewModel.phoneNumberText.value =
                getString(R.string.verify_otp_enter_the_otp_sent_to_you_on) + " " + verifyOTPViewModel.phoneNumber.value

        verifyOtpBinding.ivBackIcon.setOnClickListener { onBackPressed() }

        startTimer(60000L)

        verifyOtpBinding.tvResendOtp.setOnClickListener {
            verifyOTPViewModel.getResendOtp()
        }

        verifyOtpBinding.tvContinue.setOnClickListener {
            val otpString = verifyOtpBinding.otvOtp.otp.toString()
            if (otpString.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.verify_otp_please_enter_otp),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (otpString.length <= 5) {
                Toast.makeText(
                    this,
                    getString(R.string.verify_otp_please_enter_valid_otp),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                verifyOTPViewModel.validateOtp(otpString)
            }
        }

        verifyOTPViewModel.otpReceived.observe(this) {
            if (it) {
                verifyOTPViewModel.otpReceived.value = false
                startTimer(60000L)
            }

        }
        verifyOTPViewModel.otpValidated.observe(this) {
            if (it) {
                verifyOTPViewModel.otpValidated.value = false
                openCheckOutActivity()
            }
        }
    }

    private fun openCheckOutActivity() {
        val openCheckoutActivityIntent = Intent()
        setResult(Activity.RESULT_OK, openCheckoutActivityIntent)
        finish()
    }

    private fun startTimer(timeInSeconds: Long) {
        if (countdownTimer != null)
            countdownTimer!!.cancel()
        verifyOtpBinding.tvResendOtp.visibility = View.GONE
        verifyOtpBinding.tvTimer.visibility = View.VISIBLE
        countdownTimer = object : CountDownTimer(timeInSeconds, 1000) {
            override fun onFinish() {
                verifyOtpBinding.tvResendOtp.visibility = View.VISIBLE
                verifyOtpBinding.tvTimer.visibility = View.GONE
            }

            override fun onTick(p0: Long) {
                timeInMilliSeconds = p0
                updateTextUI()
            }
        }
        (countdownTimer as CountDownTimer).start()
    }

    override fun onStart() {
        super.onStart()
        smsVerifyCatcher.onStart();
    }

    override fun onStop() {
        super.onStop()
        smsVerifyCatcher.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun updateTextUI() {
        val minute = (timeInMilliSeconds / 1000) / 60
        val seconds = (timeInMilliSeconds / 1000) % 60
        val secondsString: String

        if (seconds < 10) {
            secondsString = "0$seconds"
            verifyOtpBinding.tvTimer.text = "0$minute : $secondsString"
        } else
            verifyOtpBinding.tvTimer.text = "0$minute : $seconds"

    }

    private fun getOtpFromMessage(message: String) {
        val pattern = Pattern.compile("(|^)\\d{6}")
        val matcher = pattern.matcher(message)
        if (matcher.find()) {
            verifyOtpBinding.otvOtp.setOTP(matcher.group(0)!!)
        }
    }
}