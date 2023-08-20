package com.cloudin.monsterchicken.activity.checkout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.LottieAnimationView
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.addaddress.AddAddressActivity
import com.cloudin.monsterchicken.activity.addresslist.AddressListActivity
import com.cloudin.monsterchicken.activity.dashboard.DashboardActivity
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityCheckoutBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CheckoutActivity : CloudInBaseActivity() {

    private lateinit var todayBoxView: TextView
    private lateinit var tomorrowBoxView: TextView
    private lateinit var timeSlotsLayout: LinearLayout
    private lateinit var selectedTextView: TextView
    private lateinit var checkoutBinding: ActivityCheckoutBinding


    private val schedule_date = MutableLiveData<String>("")
    private val schedule_from_time = MutableLiveData<String>("")
    private val schedule_to_time = MutableLiveData<String>("")

    private val checkoutViewModel: CheckoutViewModel by viewModels()
    private lateinit var checkoutCartListAdapter: CheckoutCartListAdapter
    private var isAddAddressListOpened = false
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val openCheckoutIntent =
                        Intent(this@CheckoutActivity, AddAddressActivity::class.java)
                    startActivity(openCheckoutIntent)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkoutBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_checkout)

        checkoutBinding.checkoutViewModel = checkoutViewModel
        checkoutBinding.lifecycleOwner = this
        setContentView(checkoutBinding.root)


        initView()

        todayBoxView = findViewById(R.id.todayBoxView)
        tomorrowBoxView = findViewById(R.id.tomorrowBoxView)
        timeSlotsLayout = findViewById(R.id.timeSlotsLayout)

        // Set the text for today's and tomorrow's date
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val today = Calendar.getInstance().time
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)

        todayBoxView.text = dateFormat.format(today)
        tomorrowBoxView.text = dateFormat.format(tomorrow.time)

        // Select today's date and show time slots
        selectToday()


        todayBoxView.setOnClickListener {
            selectToday()
        }

        tomorrowBoxView.setOnClickListener {
            selectTomorrow()
        }
    }

    private var day=1;

    private var selectedDate: Date? = null

    fun formatDateToString(date: Calendar): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date.time)
    }

    private fun selectToday() {
        todayBoxView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        tomorrowBoxView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        selectedDate = Calendar.getInstance().time
        //schedule_date.value=Calendar.getInstance().time.toString()
        val formattedDate = formatDateToString(Calendar.getInstance())
        println("Formatted Date: $formattedDate")
        schedule_date.value=formattedDate
        day=1;
        showSelectedDateBox()
        showTimeSlots()
    }

    private fun selectTomorrow() {
        todayBoxView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        tomorrowBoxView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        day=2;
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        selectedDate = calendar.time
        val formattedDate = formatDateToString(calendar)
        println("Formatted Date: $formattedDate")
        schedule_date.value=formattedDate
        showSelectedDateBox()
        showTimeSlots()
    }

    /*private fun showTimeSlots() {
        // Clear previous time slots
        timeSlotsLayout.removeAllViews()

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateDisplayFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance().apply {
            time = selectedDate ?: Date()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
        }

        val endTimeCalendar = Calendar.getInstance().apply {
            time = selectedDate ?: Date()
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 0)
        }
        while (calendar.time.before(endTimeCalendar.time) && calendar.time.after(currentTime)) {
            val startTime = dateFormat.format(calendar.time)
            calendar.add(Calendar.HOUR_OF_DAY, 2)
            val endTime = dateFormat.format(calendar.time)
            val timeSlotText = "$startTime - $endTime"
            val timeSlotView = TextView(this)
            timeSlotView.text = timeSlotText
            timeSlotView.setBackgroundResource(R.drawable.box_view_background)
            timeSlotView.setPadding(16, 16, 16, 16)
            timeSlotView.setOnClickListener {
                updateSelectedTimeSlotView(timeSlotView)
            }
            timeSlotsLayout.addView(timeSlotView)
        }
    }*/

    private fun showSelectedDateBox() {
        val today = Calendar.getInstance().time
        //val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        todayBoxView.isSelected = selectedDate?.equals(today) == true
        tomorrowBoxView.isSelected = selectedDate?.equals(today) == false

    }


    private fun showTimeSlots() {
        // Clear previous time slots
        timeSlotsLayout.removeAllViews()


        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance().apply {
            time = selectedDate ?: Date()
            set(Calendar.HOUR_OF_DAY, 8)  // Set the initial hour to 8
            set(Calendar.MINUTE, 0)       // Set the initial minute to 0
        }
        val curcalendar = Calendar.getInstance().apply {
            time = Calendar.getInstance().time
           // set(Calendar.HOUR_OF_DAY, 8)  // Set the initial hour to 8
            set(Calendar.MINUTE, 0)       // Set the initial minute to 0
        }
        val endTimeCalendar = Calendar.getInstance().apply {
            time = selectedDate ?: Date()
            set(Calendar.HOUR_OF_DAY, 19) // Set the end hour to 19 (7 PM)
            set(Calendar.MINUTE, 0)       // Set the end minute to 0
        }
        val endTimeCalendar1 = Calendar.getInstance().apply {
            time = selectedDate ?: Date()
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 19) // Set the end hour to 19 (7 PM)
            set(Calendar.MINUTE, 0)       // Set the end minute to 0
        }



        if(day==1) {
            if(curcalendar.time.before(calendar.time)){
                Log.d("c time ", "The value is: true")
                while (calendar.time.before(endTimeCalendar.time)) {

                    val startTime = dateFormat.format(calendar.time)
                    calendar.add(
                        Calendar.HOUR_OF_DAY,
                        2
                    ) // Move to the next time slot (increment by 2 hours)
                    val endTime = dateFormat.format(calendar.time)

                    val timeSlotText = "$startTime - $endTime"

                    val timeSlotView = TextView(this)
                    timeSlotView.text = timeSlotText
                    timeSlotView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                    timeSlotView.setBackgroundResource(R.drawable.time_view)
                    timeSlotView.setPadding(16, 16, 16, 16)
                    timeSlotView.setOnClickListener {
                        it.isSelected = !it.isSelected

                        val times = timeSlotView.text.split(" - ")

                        schedule_from_time.value=times[0]
                        schedule_to_time.value=times[1]


                        if (::selectedTextView.isInitialized) {
                            selectedTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                          //  selectedTextView.setBackgroundResource(R.drawable.time_view)
                        }
                        timeSlotView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                      //  timeSlotView.setBackgroundResource(R.drawable.time_view_border)
                        selectedTextView = timeSlotView

                        //  timeSlotView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                        updateSelectedTimeSlotView(timeSlotView)
                    }

                    timeSlotsLayout.addView(timeSlotView)
                }

            }else{
                Log.d("c time ", "The value is: false")
                while (curcalendar.time.before(endTimeCalendar.time)) {

                    val startTime = dateFormat.format(curcalendar.time)
                    curcalendar.add(
                        Calendar.HOUR_OF_DAY,
                        2
                    ) // Move to the next time slot (increment by 2 hours)
                    val endTime = dateFormat.format(curcalendar.time)

                    val timeSlotText = "$startTime - $endTime"

                    val timeSlotView = TextView(this)
                    timeSlotView.text = timeSlotText

                    timeSlotView.setBackgroundResource(R.drawable.time_view)
                    timeSlotView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                    timeSlotView.setPadding(16, 16, 16, 16)
                    timeSlotView.setOnClickListener {
                        it.isSelected = !it.isSelected

                        if (::selectedTextView.isInitialized) {
                            selectedTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                           // selectedTextView.setBackgroundResource(R.drawable.time_view)
                        }

                        val times = timeSlotView.text.split(" - ")

                        schedule_from_time.value=times[0]
                        schedule_to_time.value=times[1]

                        timeSlotView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                        //timeSlotView.setBackgroundResource(R.drawable.time_view_border)
                        selectedTextView = timeSlotView

                        //  timeSlotView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                        updateSelectedTimeSlotView(timeSlotView)
                    }

                    timeSlotsLayout.addView(timeSlotView)
                }
            }


        }else{
            while (calendar.time.before(endTimeCalendar.time)) {

                val startTime = dateFormat.format(calendar.time)
                calendar.add(
                    Calendar.HOUR_OF_DAY,
                    2
                ) // Move to the next time slot (increment by 2 hours)
                val endTime = dateFormat.format(calendar.time)

                val timeSlotText = "$startTime - $endTime"

                val timeSlotView = TextView(this)
                timeSlotView.text = timeSlotText
                timeSlotView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                timeSlotView.setBackgroundResource(R.drawable.time_view)
                timeSlotView.setPadding(16, 16, 16, 16)
                timeSlotView.setOnClickListener {
                    it.isSelected = !it.isSelected

                    if (::selectedTextView.isInitialized) {
                        selectedTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                    }

                    val times = timeSlotView.text.split(" - ")

                    schedule_from_time.value=times[0]
                    schedule_to_time.value=times[1]

                    timeSlotView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                            selectedTextView = timeSlotView

                  //  timeSlotView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                    updateSelectedTimeSlotView(timeSlotView)
                }

                timeSlotsLayout.addView(timeSlotView)
            }
        }
        val childView = timeSlotsLayout.getChildAt(0)
        childView.performClick()
    }


    private fun updateSelectedTimeSlotView(selectedView: View) {
        for (i in 0 until timeSlotsLayout.childCount) {
            val childView = timeSlotsLayout.getChildAt(i)
            childView.isSelected = childView == selectedView
        }
    }



        private fun initView() {
        checkoutViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        checkoutViewModel.appLogoutLiveData.observe(this) {
            logout(this@CheckoutActivity)
        }

        checkoutViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }
        checkoutBinding.ivBackIcon.setOnClickListener { onBackPressed() }
        checkoutCartListAdapter = CheckoutCartListAdapter(checkoutViewModel)
        checkoutBinding.rvCheckoutList.adapter = checkoutCartListAdapter

        if (intent.getStringArrayListExtra("CartItemList") != null)
            checkoutViewModel.cartItemList = intent.getStringArrayListExtra("CartItemList")!!

        checkoutBinding.llAddAddress.setOnClickListener {
            val addAddressIntent = Intent(this@CheckoutActivity, AddAddressActivity::class.java)
            startForResult.launch(addAddressIntent)
        }

        checkoutBinding.llTotalPrice.setOnClickListener {
            checkoutBinding.nsvCheckoutData.smoothScrollTo(
                0,
                checkoutBinding.nsvCheckoutData.getChildAt(0).height
            )
        }

        checkoutViewModel.isOrderConfirmed.observe(this) {
            showOrderConfirmedDialog()
        }

        checkoutViewModel.isExistAddress.observe(this) {
            if (it) {
                checkoutBinding.rlExsistingAddress.visibility = View.VISIBLE
                checkoutBinding.rlAddAddress.visibility = View.GONE

                if (checkoutViewModel.addressType.value.equals("Home"))
                    checkoutBinding.ivAddressType.setImageResource(R.drawable.ic_home_black_24dp)
                else if (checkoutViewModel.addressType.value.equals("Work"))
                    checkoutBinding.ivAddressType.setImageResource(R.drawable.ic_work)
                else
                    checkoutBinding.ivAddressType.setImageResource(R.drawable.ic_pin)

            } else {
                checkoutBinding.rlAddAddress.visibility = View.VISIBLE
                checkoutBinding.rlExsistingAddress.visibility = View.GONE
            }
        }

        checkoutBinding.tvChange.setOnClickListener {
            openAddressListActivity()
        }

        checkoutViewModel.discountAmountString.observe(this@CheckoutActivity) {
            if (!it.equals("0") && !it.equals("")) {
                checkoutBinding.llApplyOffer.visibility = View.GONE
                checkoutBinding.llAppliedOffer.visibility = View.VISIBLE
                checkoutBinding.tvTotalEffectivePrice.text =
                    getString(R.string.add_address_activity_total_effective_saving) + " " + checkoutViewModel.discountAmount.value
            } else {
                checkoutBinding.llApplyOffer.visibility = View.VISIBLE
                checkoutBinding.llAppliedOffer.visibility = View.GONE
            }
        }

        checkoutBinding.tetApplyCoupon.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > 2) {
                    checkoutBinding.tvOfferApply.visibility = View.VISIBLE
                } else {
                    checkoutBinding.tvOfferApply.visibility = View.GONE
                }

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        checkoutBinding.tvOfferApply.setOnClickListener {
            checkoutViewModel.applyOffer()
        }

        checkoutBinding.tvCouponRemove.setOnClickListener {
            checkoutViewModel.appliedCoupon.value = ""
            checkoutViewModel.getCartData()
        }

        checkoutBinding.llCheckout.setOnClickListener {
            showPaymentGatewayDialog()
        }
        checkoutViewModel.addressListReceived.observe(this@CheckoutActivity) {
            if (it) {
                if (checkoutViewModel.addressesListValues.value!!.size > 0) {
                    for (addressListItem in checkoutViewModel.addressesListValues.value!!) {
                        if (addressListItem.defaultAddress == 1) {
                            checkoutViewModel.isDefaultAddressSelected.value = true
                            break
                        }
                    }
                }

                if (checkoutViewModel.isDefaultAddressSelected.value == true)
                    checkoutViewModel.getCartData()
                else
                    openAddressListActivity()
            }
        }

        checkoutViewModel.getAddressList()
    }

    private fun showOrderConfirmedDialog() {
        val customDialog = Dialog(this@CheckoutActivity)
        customDialog.setContentView(R.layout.dialog_order_confirmed)
        customDialog.setCancelable(false)
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val lavOrderConfirmed =
            customDialog.findViewById<LottieAnimationView>(R.id.lavOrderConfirmed)

        lavOrderConfirmed.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                customDialog.dismiss()
                openDashboardActivity()
            }
        })

        customDialog.show()
    }

    private fun showPaymentGatewayDialog() {
        val customDialog = Dialog(this@CheckoutActivity)
        customDialog.setContentView(R.layout.dialog_payment_method)
        customDialog.setCancelable(false)
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val cvPayOnDelivery = customDialog.findViewById<CardView>(R.id.cvPayOnDelivery)
        val cvPayOnOnline = customDialog.findViewById<CardView>(R.id.cvPayOnOnline)
        val tvPlaceOrder = customDialog.findViewById<TextView>(R.id.tvPlaceOrder)

        cvPayOnDelivery.setOnClickListener {
            cvPayOnDelivery.setCardBackgroundColor(
                resources.getColor(R.color.cFFE6E7)
            )
            cvPayOnOnline.setCardBackgroundColor(
                resources.getColor(R.color.white)
            )
        }

        cvPayOnOnline.setOnClickListener {
            cvPayOnOnline.setCardBackgroundColor(
                resources.getColor(R.color.cFFE6E7)
            )
            cvPayOnDelivery.setCardBackgroundColor(
                resources.getColor(R.color.white)
            )
        }

        tvPlaceOrder.setOnClickListener {
            customDialog.dismiss()
           // schedule_from_time.value
            checkoutViewModel.callPlaceOrder(schedule_date.value.toString(),schedule_from_time.value.toString(),schedule_to_time.value.toString())
        }

        customDialog.show()
    }

    fun openDashboardActivity() {
        val loginActivityIntent = Intent(this, DashboardActivity::class.java)
        loginActivityIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginActivityIntent)
        finish()
    }

    private fun openAddressListActivity() {
        isAddAddressListOpened = true
        val addressListIntent =
            Intent(this@CheckoutActivity, AddressListActivity::class.java)
        startActivity(addressListIntent)
    }

    override fun onResume() {
        super.onResume()
        if (checkoutViewModel.isDefaultAddressSelected.value == true)
            checkoutViewModel.getCartData()
        else if (isAddAddressListOpened)
            checkoutViewModel.getAddressList()
    }
}