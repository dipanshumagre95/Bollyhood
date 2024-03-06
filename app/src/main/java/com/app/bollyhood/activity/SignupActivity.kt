package com.app.bollyhood.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivitySignupBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidBothPassword
import com.app.bollyhood.extensions.isvalidCategory
import com.app.bollyhood.extensions.isvalidConfirmPassword
import com.app.bollyhood.extensions.isvalidEmailAddress
import com.app.bollyhood.extensions.isvalidMobileNumber
import com.app.bollyhood.extensions.isvalidName
import com.app.bollyhood.extensions.isvalidPassword
import com.app.bollyhood.model.CategoryModel
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    lateinit var mContext: SignupActivity
    lateinit var binding: ActivitySignupBinding
    private val viewModel: DataViewModel by viewModels()
    private var categoryId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        mContext = this
        addListner()
        addObserevs()
    }

    private fun addListner() {
        binding.apply {
            tvLogin.setOnClickListener {
                startActivity(Intent(mContext, LoginActivity::class.java))
                finishAffinity()
            }

            acSelectToday.setOnTouchListener { _, _ ->
                acSelectToday.showDropDown()
                false
            }


            tvSignUp.setOnClickListener {
                if (isNetworkAvailable(mContext)) {
                    if (isvalidName(
                            mContext,
                            binding.edtName.text.toString().trim()
                        ) && isvalidEmailAddress(
                            mContext,
                            binding.edtEmailAddress.text.toString().trim()
                        ) && isvalidMobileNumber(
                            mContext,
                            binding.edtMobileNumber.text.toString().trim()
                        ) && isvalidCategory(
                            mContext,
                            binding.acSelectToday.text.toString().trim()
                        ) && isvalidPassword(
                            mContext,
                            binding.edtPassword.text.toString().trim()
                        ) && isvalidConfirmPassword(
                            mContext, binding.edtConfirmPassword.text.toString().trim()
                        ) && isvalidBothPassword(
                            mContext, binding.edtPassword.text.toString().trim(),
                            binding.edtConfirmPassword.text.toString().trim()
                        )

                    ) {
                        viewModel.doSignup(
                            binding.edtName.text.toString().trim(),
                            binding.edtEmailAddress.text.toString().trim(),
                            binding.edtPassword.text.toString().trim(),
                            categoryId,
                            binding.edtMobileNumber.text.toString().trim()
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
    }

    private fun addObserevs() {

        if (isNetworkAvailable(mContext)) {
            viewModel.getCategory()
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }


        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })


        viewModel.categoryLiveData.observe(this, Observer { res ->
            if (res.status == "1") {
                setCategoryAdapter(res.result)
            } else {
                Toast.makeText(mContext, res?.msg, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.signupLiveData.observe(this, Observer {
            if (it.status == "1") {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                startActivity(Intent(mContext, LoginActivity::class.java))
                finishAffinity()

            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()

            }
        })


    }

    private fun setCategoryAdapter(result: ArrayList<CategoryModel>) {

        val stringList = arrayListOf<String>()
        result.forEach {
            stringList.add(it.category_name)
        }

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, stringList)
        binding.acSelectToday.threshold = 0
        binding.acSelectToday.dropDownVerticalOffset = 0
        binding.acSelectToday.setAdapter(arrayAdapter)

        binding.acSelectToday.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                categoryId = result[position].id
            }

    }
}