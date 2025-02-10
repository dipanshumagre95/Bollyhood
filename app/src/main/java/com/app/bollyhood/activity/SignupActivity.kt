package com.app.bollyhood.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.Window
import android.widget.NumberPicker
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
import com.app.bollyhood.util.DialogsUtils.showCustomToast
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

        /*binding.acSelectToday.setOnTouchListener { _, _ ->
            binding.acSelectToday.showDropDown()
            false
        }*/

        /*binding.selectView.setOnClickListener(OnClickListener {
            showBottomSheet(companyList!!)
        })*/

        binding.companyView.setOnClickListener(this)
        binding.viewIndividual.setOnClickListener(this)
        binding.tvSignUp.setOnClickListener(this)
        binding.acSelectToday.setOnClickListener(this)
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
                    isindiviual=false
                    viewModel.getSignupCategory("2")
                }else{
                  companyList?.addAll(res.result)
                }
            } else {
                showCustomToast(this,StaticData.pleaseTryAgain,res.msg,StaticData.alert)
            }
        })


        viewModel.successData.observe(this, Observer {
            if (it.status == "1") {
                startActivity(Intent(mContext, LoginActivity::class.java))
                finishAffinity()
                showCustomToast(this,StaticData.successMsg,it.msg,StaticData.success)
            } else {
                showCustomToast(this,StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })
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
              //  setCategoryAdapter(companyList!!)
            }

            R.id.view_individual -> {
                binding.companyView.background=getDrawable(R.drawable.gray_bg_10dp)
                binding.companyBg.background=getDrawable(R.drawable.company_bg)
                binding.individualBg.background=getDrawable(R.drawable.round_pink_color)
                binding.viewIndividual.background=getDrawable(R.drawable.rectangle_app_background)
                binding.acSelectToday.setText("Select Category")
                categoryId=""
                user_type = "1"
             //   setCategoryAdapter(indiviaulList!!)
            }

            R.id.acSelectToday  ->{
                if (user_type=="1") {
                    showBottomSheet(indiviaulList!!)
                }
                else if (user_type=="2"){
                    showBottomSheet(companyList!!)
                }
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
                    cat_id,
                    mobileNumber,
                    type
                )
            }

        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showBottomSheet(list: ArrayList<CategoryModel>) {
        var posstion=0
        val dialog= Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_layout)

        val numberPicker = dialog.findViewById<NumberPicker>(R.id.numberPicker)
        val tvSelectedCategory = dialog.findViewById<TextView>(R.id.tvselectedCategory)
        val doneButton = dialog.findViewById<TextView>(R.id.tvDone)
        val cancelButton = dialog.findViewById<TextView>(R.id.tvCancel)

        tvSelectedCategory.text=list[0].category_name


        val displayedValues = list.map { it.category_name }.toTypedArray()
        numberPicker.minValue=0
        numberPicker.maxValue=list.size-1
        numberPicker.displayedValues=displayedValues

        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val selectedCategorys = list[newVal]
            posstion=newVal
            tvSelectedCategory.text= selectedCategorys.category_name
        }

        doneButton.setOnClickListener(OnClickListener {
            categoryId = list[posstion].id
            binding.acSelectToday.text=list[posstion].category_name.toString()
            dialog.dismiss()
        })

        cancelButton.setOnClickListener(OnClickListener {
            dialog.dismiss()
        })

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations=R.style.BottomSheetDialogTheme
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

}