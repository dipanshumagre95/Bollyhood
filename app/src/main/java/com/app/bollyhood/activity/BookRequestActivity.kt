package com.app.bollyhood.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityBookRequestBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidBookNow
import com.app.bollyhood.model.ExpertiseModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class BookRequestActivity : AppCompatActivity() {

    lateinit var binding: ActivityBookRequestBinding
    lateinit var mContext: BookRequestActivity
    private val viewModel: DataViewModel by viewModels()
    private var mYear = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    lateinit var expertiseModel: ExpertiseModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_request)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        if (intent.extras != null) {
            expertiseModel = Gson().fromJson(
                intent.getStringExtra(StaticData.userModel),
                ExpertiseModel::class.java
            )
        }

        binding.edtFullName.setText(PrefManager(mContext).getStringValue(StaticData.name))
        binding.edtWhatsappNumber.setText(PrefManager(mContext).getStringValue(StaticData.mobile))


        binding.edtDate.setOnClickListener {
            getDatePicker()
        }
        binding.edtTime.setOnClickListener {
            showTimePicker()
        }
    }


    private fun addListner() {

        binding.ivClose.setOnClickListener {
            finish()
        }
        binding.tvBookNow.setOnClickListener {
            if (isNetworkAvailable(mContext)) {
                if (isvalidBookNow(
                        mContext,
                        binding.edtFullName.text.toString().trim(),
                        binding.edtWhatsappNumber.text.toString().trim(),
                        binding.edtPurposeOfTheBooking.text.toString().trim(),
                        binding.edtDate.text.toString().trim(),
                        binding.edtTime.text.toString().trim()
                    )
                ) {
                    if (isNetworkAvailable(mContext)) {
                        viewModel.addBook(
                            PrefManager(mContext).getvalue(StaticData.id),
                            binding.edtWhatsappNumber.text.toString().trim(),
                            binding.edtPurposeOfTheBooking.text.toString().trim(),
                            binding.edtDate.text.toString().trim(),
                            binding.edtTime.text.toString().trim(),
                            expertiseModel.catt,
                            expertiseModel.id
                        )
                    } else {
                        Toast.makeText(
                            mContext,
                            getString(R.string.str_error_internet_connections),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    mContext, getString(R.string.str_error_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun addObsereves() {

        viewModel.addBookLiveData.observe(this) {
            if (it.status == "1") {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                startActivity(Intent(mContext,MainActivity::class.java))
                finishAffinity()
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun getDatePicker() {

        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]

        // Launch Date Picker Dialog

        // Launch Date Picker Dialog
        val dpd = DatePickerDialog(
            mContext,
            { view, year, monthOfYear, dayOfMonth -> // Display Selected date in textbox
                binding.edtDate.setText(
                    year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()
                    /* dayOfMonth.toString() + "/"
                             + (monthOfYear + 1) + "/" + year*/
                )
                binding.edtTime.setText("")
            }, mYear, mMonth, mDay
        )
        dpd.datePicker.minDate = System.currentTimeMillis()
        dpd.show()
    }

    private fun showTimePicker() {
        val mTimePicker: TimePickerDialog
        val c = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]
        mTimePicker = TimePickerDialog(
            mContext,
            { timePicker, selectedHour, selectedMinute ->
                val time = "$selectedHour:$selectedMinute"
                val fmt = SimpleDateFormat("HH:mm")
                var date: Date? = null
                try {
                    date = fmt.parse(time)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val fmtOut = SimpleDateFormat("hh:mm:ss")
                val formattedTime = fmtOut.format(date)
                binding.edtTime.setText(formattedTime)
            }, hour, minute, false
        ) //No 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

}