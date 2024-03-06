package com.app.bollyhood.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivitySendOtpBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidOtp
import com.app.bollyhood.model.LoginModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SendOtpActivity : AppCompatActivity() {

    lateinit var mContext: SendOtpActivity
    lateinit var binding: ActivitySendOtpBinding
    private var mobileNumber: String? = ""

    private val viewModel: DataViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_otp)
        mContext = this
        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {
        if (intent.extras != null) {
            mobileNumber = intent.getStringExtra(StaticData.mobileNumber)
            binding.tvMobileNumber.text = "+91 $mobileNumber"
        }
    }

    private fun addListner() {
        binding.edtNumber1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable != null) {
                    if (editable.length == 1) binding.edtNumber2.requestFocus()
                }
            }
        })


        binding.edtNumber2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable != null) {
                    if (editable.length == 1) binding.edtNUmber3.requestFocus()
                }
            }
        })


        binding.edtNUmber3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable != null) {
                    if (editable.length == 1) binding.edtNumber4.requestFocus()
                }
            }
        })

        binding.edtNumber4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable != null) {
                    if (editable.length == 1) binding.edtNumber5.requestFocus()
                }
            }
        })

        binding.edtNumber5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable != null) {
                    if (editable.length == 1) binding.edtNumber6.requestFocus()
                }
            }
        })




        binding.edtNumber1.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //this is for backspace
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })


        binding.edtNumber2.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //this is for backspace
                if (binding.edtNumber2.text.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.edtNumber1.requestFocus()
                }
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })


        binding.edtNUmber3.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //this is for backspace
                if (binding.edtNUmber3.text.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.edtNumber2.requestFocus()
                }
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })

        binding.edtNumber4.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //this is for backspace
                if (binding.edtNumber4.text.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.edtNUmber3.requestFocus()
                }
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })

        binding.edtNumber5.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //this is for backspace
                if (binding.edtNumber5.text.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.edtNumber4.requestFocus()
                }
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })

        binding.edtNumber6.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //this is for backspace
                if (binding.edtNumber6.text.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.edtNumber5.requestFocus()
                }
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })

        binding.tvVerify.setOnClickListener {

            if (isNetworkAvailable(mContext)) {
                if (isvalidOtp(
                        mContext,
                        binding.edtNumber1.text.toString()
                            .trim() + "" + binding.edtNumber2.text.toString().trim() + ""
                                + binding.edtNUmber3.text.toString()
                            .trim() + "" + binding.edtNumber4.text.toString().trim() + ""
                                + binding.edtNumber5.text.toString()
                            .trim() + "" + binding.edtNumber6.text.toString().trim()
                    )
                ) {
                    viewModel.doLogin(
                        mobileNumber.toString(), binding.edtNumber1.text.toString()
                            .trim() + "" + binding.edtNumber2.text.toString().trim() + ""
                                + binding.edtNUmber3.text.toString()
                            .trim() + "" + binding.edtNumber4.text.toString().trim() + ""
                                + binding.edtNumber5.text.toString()
                            .trim() + "" + binding.edtNumber6.text.toString().trim()
                    )
                }

            } else {
                Toast.makeText(
                    mContext, getString(R.string.str_error_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }


        }


    }

    private fun addObserevs() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.loginLiveData.observe(this, Observer {
            if (it.status == "1") {
                setPrefData(it.result)
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()


            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setPrefData(result: LoginModel) {
        PrefManager(mContext).setvalue(StaticData.isLogin, true)
        PrefManager(mContext).setvalue(StaticData.id, result.id)
        PrefManager(mContext).setvalue(StaticData.name, result.name)
        PrefManager(mContext).setvalue(StaticData.email, result.email)
        PrefManager(mContext).setvalue(StaticData.mobile, result.mobile)
        //PrefManager(mContext).setvalue(StaticData.image,result.image)


        startActivity(Intent(mContext, MainActivity::class.java))
        finishAffinity()
    }
}