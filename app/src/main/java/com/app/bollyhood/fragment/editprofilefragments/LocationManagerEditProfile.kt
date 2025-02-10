package com.app.bollyhood.fragment.editprofilefragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.activity.CMSActivity
import com.app.bollyhood.adapter.WorkAdapter
import com.app.bollyhood.databinding.FragmentLocationManagerEditProfileBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidDescriptions
import com.app.bollyhood.extensions.isvalidEmailAddress
import com.app.bollyhood.extensions.isvalidMobileNumber
import com.app.bollyhood.extensions.isvalidName
import com.app.bollyhood.extensions.isvalidTeamNCondition
import com.app.bollyhood.model.ProfileModel
import com.app.bollyhood.model.WorkLinkProfileData
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class LocationManagerEditProfile : Fragment(), TextWatcher, WorkAdapter.onItemClick,
    OnClickListener {

    lateinit var binding: FragmentLocationManagerEditProfileBinding
    private val viewModel: DataViewModel by viewModels()
    lateinit var mContext: Context
    private var profilePath = ""
    private var category_Id: String = ""
    private var workLinkList: ArrayList<WorkLinkProfileData> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_manager_edit_profile, container, false)
        mContext=requireContext()

        initUI()
        addListner()
        addObserevs()
        setSpannableString()
        return binding.root
    }

    private fun initUI() {
        viewModel.getProfile(PrefManager(mContext).getvalue(StaticData.id).toString())
        binding.edtName.addTextChangedListener(this)
        binding.edtMobileNumber.addTextChangedListener(this)
        binding.edtDescriptions.addTextChangedListener(this)
        binding.edtEmailAddress.addTextChangedListener(this)
        binding.edtComLocation.addTextChangedListener(this)
    }

    private fun addListner() {

        binding.apply {
            tvUpdateProfile.setOnClickListener(this@LocationManagerEditProfile)
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.tvUpdateProfile->{
                updateCompanyProfile()
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (charSequence != null && charSequence.length >= 1) {
            when(charSequence.hashCode()){
                binding.edtName.getText().hashCode() ->{
                    binding.lledtName.setHintEnabled(true)
                }

                binding.edtMobileNumber.getText().hashCode() ->{
                    binding.llphonenumber.setHintEnabled(true)
                }

                binding.edtDescriptions.text.hashCode() ->{
                    binding.textcount.visibility=View.VISIBLE
                    val value = 250 - binding.edtDescriptions.text.toString().length
                    binding.textcount.text="$value/250"
                }

                binding.edtEmailAddress.getText().hashCode() ->{
                    binding.llEmail.setHintEnabled(true)
                }

                binding.edtComLocation.text.hashCode() ->{
                    binding.llLocation.setHintEnabled(true)
                }
            }
        }else if (charSequence?.length!! <= 0){
            when(charSequence.hashCode()){
                binding.edtName.getText().hashCode() ->{
                    binding.lledtName.setHintEnabled(false)
                }

                binding.edtMobileNumber.getText().hashCode() ->{
                    binding.llphonenumber.setHintEnabled(false)
                }

                binding.textcount.text.hashCode() ->{
                    binding.textcount.visibility=View.GONE
                }

                binding.edtEmailAddress.getText().hashCode() ->{
                    binding.llEmail.setHintEnabled(false)
                }

                binding.edtComLocation.text.hashCode() ->{
                    binding.llLocation.setHintEnabled(false)
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    private fun addObserevs() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.profileLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                val profileModel = it.result
                setCompanyProfileData(profileModel)
            } else {
                showCustomToast(requireContext(),StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })

        viewModel.updateProfileLiveData.observe(requireActivity()) {
            if (it.status == "1") {
                showCustomToast(requireContext(),StaticData.successMsg,it.msg,StaticData.success)
                setPrefData(it.result)
            } else {
                showCustomToast(requireContext(),StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        }

    }

    private fun setPrefData(result: ProfileModel) {
        PrefManager(mContext).setvalue(StaticData.isLogin, true)
        PrefManager(mContext).setvalue(StaticData.id, result.id)
        PrefManager(mContext).setvalue(StaticData.name, result.name)
        PrefManager(mContext).setvalue(StaticData.email, result.email)
        PrefManager(mContext).setvalue(StaticData.mobile, result.mobile)
        PrefManager(mContext).setvalue(StaticData.image, result.image)
    }

    private fun setCompanyProfileData(profileModel: ProfileModel) {
        if (!profileModel.image.isNullOrEmpty()) {
            Glide.with(mContext).load(profileModel.image).error(R.drawable.ic_profile)
                .error(R.drawable.ic_profile).into(requireActivity().findViewById(R.id.cvProfile))
        }

        binding.edtName.setText(profileModel.name)
        binding.edtEmailAddress.setText(profileModel.email)
        binding.edtMobileNumber.setText(profileModel.mobile)
        binding.edtDescriptions.setText(profileModel.description)
        category_Id = profileModel.categories[0].category_id
        binding.edtComLocation.setText(profileModel.location)
    }


    private fun setSpannableString() {
        val spanTxt = SpannableStringBuilder(
            "By Updating an account, you agree to our"
        )

        spanTxt.setSpan(
            ForegroundColorSpan(mContext.getColor(R.color.black)),
            0,
            spanTxt.length,
            0
        )

        spanTxt.append(" Term of service")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(
                    Intent(mContext, CMSActivity::class.java)
                        .putExtra("mFrom","terms-condition")
                )
            }
        }, spanTxt.length - " Term of service".length, spanTxt.length, 0)


        spanTxt.setSpan(
            ForegroundColorSpan(mContext.getColor(R.color.black)),
            spanTxt.length - " Term of service".length,
            spanTxt.length,
            0
        )

        binding.tvteamCondition.setMovementMethod(LinkMovementMethod.getInstance())
        binding.tvteamCondition.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    override fun onitemClick(pos: Int, work: WorkLinkProfileData) {

    }




    private fun updateCompanyProfile()
    {
        if (isNetworkAvailable(mContext)) {
            if (isvalidName(
                    mContext, binding.edtName.text.toString().trim()
                ) && isvalidEmailAddress(
                    mContext, binding.edtEmailAddress.text.toString().trim()
                ) && isvalidMobileNumber(
                    mContext, binding.edtMobileNumber.text.toString().trim()
                ) && isvalidDescriptions(
                    mContext, binding.edtDescriptions.text.toString().trim()
                ) && isvalidTeamNCondition(mContext,binding.cbteamNcondition.isChecked)
            ) {

                val name: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtName.text.toString().trim()
                )
                val email: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtEmailAddress.text.toString().trim()
                )

                val mobileNumber: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtMobileNumber.text.toString().trim()
                )


                val user_Id: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    PrefManager(mContext).getvalue(StaticData.id).toString()
                )

                val location: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtComLocation.text.toString().trim()
                )

                /*val tag: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtTag.text.toString().trim()
                )*/


                val description: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtDescriptions.text.toString().trim()
                )

                val categoryId: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), category_Id
                )


                var profileBody: MultipartBody.Part? = null
                if (profilePath.isNotEmpty()) {
                    val file = File(profilePath)
                    val requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    profileBody = MultipartBody.Part.createFormData(
                        "image", file.name, requestFile
                    )
                }

                val jsonArray = JSONArray()
                for (i in 0 until workLinkList.size) {
                    val jsonObject = JSONObject()
                    jsonObject.put("worklink_name", workLinkList[i].worklink_name)
                    jsonObject.put("worklink_url", workLinkList[i].worklink_url)
                    jsonArray.put(jsonObject)
                }


                val workLink: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), jsonArray.toString()
                )


                /*viewModel.updateCompanyProfile(
                    name,
                    email,
                    user_Id,
                    mobileNumber,
                    description,
                    workLink,
                    categoryId,
                    location,
                    tag,
                    profileBody,
                )*/
            }
        } else {
            Toast.makeText(
                mContext,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}