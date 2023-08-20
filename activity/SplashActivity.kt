package com.cloudin.monsterchicken.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.dashboard.DashboardActivity
import com.cloudin.monsterchicken.activity.landing.LandingActivity
import com.cloudin.monsterchicken.databinding.ActivitySplashBinding
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.USER_LOCATION_STRING


class SplashActivity : AppCompatActivity() {

    private lateinit var activitySplashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySplashBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_splash)

        activitySplashBinding.lifecycleOwner = this
        setContentView(activitySplashBinding.root)
        initView()
    }

    private fun initView() {
        val mHandler = Handler()
        val monitor = Runnable {
            if (CloudInPreferenceManager.getString(USER_LOCATION_STRING, "").equals("")) {
                val landingIntent = Intent(this@SplashActivity, LandingActivity::class.java)
                startActivity(landingIntent)
                finish()
            } else {
                val landingIntent = Intent(this@SplashActivity, DashboardActivity::class.java)
                startActivity(landingIntent)
                finish()
            }
        }

        mHandler.postDelayed(monitor, 3000)
    }
}