package com.app.bollyhood.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivitySignupBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidCategory
import com.app.bollyhood.extensions.isvalidName
import com.app.bollyhood.extensions.isvalidTeamNCondition
import com.app.bollyhood.model.CategoryModel
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : AppCompatActivity(),OnClickListener {

    lateinit var mContext: SignupActivity
    lateinit var binding: ActivitySignupBinding
    private val viewModel: DataViewModel by viewModels()
    private var categoryId: String = ""
    private var subCategoryId: String = ""
    private var user_type: String = "1"
    private var mobileNumber: String? = ""
    private var profilePath = ""
    private var isindiviual=false
    private var indiviaulList:ArrayList<CategoryModel>?= arrayListOf()
    private var companyList:ArrayList<CategoryModel>?= arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        mContext = this
        addListner()
        addObserevs()
        setSpannableString()
    }

    private fun addListner() {

        if (intent.extras != null) {
            mobileNumber = intent.getStringExtra(StaticData.mobileNumber)
        }

        binding.acSelectToday.setOnTouchListener { _, _ ->
            binding.acSelectToday.showDropDown()
            false
        }

        binding.companyView.setOnClickListener(this)
        binding.viewIndividual.setOnClickListener(this)
        binding.tvSignUp.setOnClickListener(this)
    }

    private fun addObserevs() {

        if (isNetworkAvailable(mContext)) {
            viewModel.getSignupCategory("1")
            isindiviual=true
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


        viewModel.categoryLiveDataforSignup.observe(this, Observer { res ->
            if (res.status == "1") {
                if (isindiviual){
                    indiviaulList?.addAll(res.result)
                    setCategoryAdapter(indiviaulList!!)
                    isindiviual=false
                    viewModel.getSignupCategory("2")
                }else{
                  companyList?.addAll(res.result)
                }
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

    private fun setSpannableString() {
        val spanTxt = SpannableStringBuilder(
            "By creating an account, you agree to our"
        )

        spanTxt.setSpan(
            ForegroundColorSpan(getColor(R.color.black)),
            0,
            spanTxt.length,
            0
        )

        spanTxt.append(" Term of service")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(mContext,CMSActivity::class.java)
                    .putExtra("mFrom","terms-condition")
                )
            }
        }, spanTxt.length - " Term of service".length, spanTxt.length, 0)


        spanTxt.setSpan(
            ForegroundColorSpan(getColor(R.color.black)),
            spanTxt.length - " Term of service".length,
            spanTxt.length,
            0
        )

        binding.tvteamCondition.setMovementMethod(LinkMovementMethod.getInstance())
        binding.tvteamCondition.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    override fun onClick(item: View?) {

        when(item?.id){

            R.id.company_view  -> {
                binding.companyView.background=getDrawable(R.drawable.rectangle_app_background)
                binding.companyBg.background=getDrawable(R.drawable.round_pink_color)
                binding.individualBg.background=getDrawable(R.drawable.company_bg)
                binding.viewIndividual.background=getDrawable(R.drawable.gray_bg_10dp)
                user_type = "2"
                binding.acSelectToday.setText("Select Category")
                categoryId=""
                setCategoryAdapter(companyList!!)
            }

            R.id.view_individual -> {
                binding.companyView.background=getDrawable(R.drawable.gray_bg_10dp)
                binding.companyBg.background=getDrawable(R.drawable.company_bg)
                binding.individualBg.background=getDrawable(R.drawable.round_pink_color)
                binding.viewIndividual.background=getDrawable(R.drawable.rectangle_app_background)
                binding.acSelectToday.setText("Select Category")
                categoryId=""
                user_type = "1"
                setCategoryAdapter(indiviaulList!!)
            }

            R.id.tvSignUp  ->{
                sendSignUpData()
            }
        }

    }

    private fun sendSignUpData(){
        if (isNetworkAvailable(mContext)) {
            if (
                isvalidName(
                    mContext,
                    binding.edtName.text.toString().trim()
                )&& isvalidCategory(
                    mContext,
                    binding.acSelectToday.text.toString().trim()
                ) && isvalidTeamNCondition(
                    mContext,binding.cbteamNcondition.isChecked)

            ) {
                val name = binding.edtName.text.toString().trim()+" "+binding.edtLastName.text.toString().trim()
                val password = ""
                val cat_id = categoryId
                val subCatId= subCategoryId
                val mobileNumber= mobileNumber!!
                val type = user_type



                viewModel.doSignup(
                    name,
                    password,
                    cat_id,
                    mobileNumber,
                    type,
                    subCatId
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