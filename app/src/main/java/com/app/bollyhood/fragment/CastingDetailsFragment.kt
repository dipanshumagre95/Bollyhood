package com.app.bollyhood.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.activity.CastingApplyActivity
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.databinding.FragmentCastingDetailsBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CastingCallModel
import com.app.bollyhood.util.DateUtils
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CastingDetailsFragment : Fragment(),OnClickListener {

    lateinit var binding: FragmentCastingDetailsBinding
    private val viewModel: DataViewModel by viewModels()
    private var type: String? = ""
    var is_Bookmark=false
    lateinit var castingCallModel: CastingCallModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_casting_details, container, false)

        initUI()
        addListner()
        addObsereves()

        return binding.root
    }

    private fun initUI() {
        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext()).load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        (requireActivity() as MainActivity).binding.apply{
            llBottom.setBackgroundResource(R.drawable.rectangle_black_top)
            tvHome.setTextColor(resources.getColor(R.color.darkgrey))
            tvBookings.setTextColor(resources.getColor(R.color.darkgrey))
            tvChat.setTextColor(resources.getColor(R.color.darkgrey))
            tvProfile.setTextColor(resources.getColor(R.color.darkgrey))
        }
        (requireActivity() as MainActivity).showToolbar(false)

        val bundle = arguments
        if (bundle!=null) {
            castingCallModel = Gson().fromJson(
                bundle.getString(StaticData.userModel),
                CastingCallModel::class.java
            )
            type = bundle.getString("type").toString()
            setData(castingCallModel)
        }
    }

    private fun setData(castingCallModel: CastingCallModel) {
        binding.let {
            if (!castingCallModel.role.isNullOrEmpty()) {
                it.tvtitle.text = castingCallModel.role
            }

            if (!castingCallModel.company_name.isNullOrEmpty()) {
                it.tvCompunyName.text = castingCallModel.company_name
            }

            if (!castingCallModel.location.isNullOrEmpty()) {
                it.tvLocation.text = castingCallModel.location
            }

            if (!castingCallModel.shift_time.isNullOrEmpty()) {
                it.tvShift.text = castingCallModel.shift_time + " Shift"
            }

            if (!castingCallModel.modify_date.isNullOrEmpty()) {
                it.tvDate.text = DateUtils.getConvertDateTiemFormat(castingCallModel.modify_date)
            }

            if (!castingCallModel.requirement.isNullOrEmpty()) {
                it.tvCastingRequirement.text = castingCallModel.requirement
            }

            binding.llApplyNowButton.isClickable = when {
                castingCallModel.is_verify_casting == "1" && castingCallModel.is_aadhar_verified == "1" -> true
                castingCallModel.is_verify_casting == "1" && castingCallModel.is_aadhar_verified == "2" -> false
                castingCallModel.is_verify_casting == "2" -> true
                else -> false
            }


            if (!castingCallModel.requirement.isNullOrEmpty()) {
                it.tvSkillRequirement.text = castingCallModel.requirement
            }

            if (!castingCallModel.organization.isNullOrEmpty()) {
                it.tvRole.text = castingCallModel.organization
            }

            if (castingCallModel.casting_fee_type.equals("Yes")) {
                it.icRupes.visibility=View.VISIBLE
                it.icRupes1.visibility=View.VISIBLE
            }else{
                it.icRupes.visibility=View.GONE
                it.icRupes1.visibility=View.GONE
            }

            if (castingCallModel.is_casting_bookmark.equals("0")||castingCallModel.is_casting_bookmark==0){
                binding.tvSave.text="Save"
                is_Bookmark=false
            }else{
                binding.tvSave.text="Saved"
                is_Bookmark=true
            }

            if (castingCallModel.price_type.equals("Project Basis")) {
                it.tvprice.text="Project Basis"
            } else {
                it.tvprice.text = "₹" + castingCallModel.price+"/pd"
            }

            if (castingCallModel.company_logo.isNotEmpty()) {
                Glide.with(requireContext()).load(castingCallModel.company_logo)
                    .into(it.ivLogo)
            }

            if (castingCallModel.is_casting_apply == 1) {

                binding.tvApplied.visibility = View.GONE
                binding.tvApplyNow.visibility = View.VISIBLE
                /*
                binding.tvApplyNow.text = "Applied"
                binding.tvApplyNow.isFocusable = false
                binding.tvApplyNow.isFocusableInTouchMode = false
                binding.tvApplyNow.isClickable = false*/
            } else {
                binding.tvApplied.visibility = View.GONE
                binding.tvApplyNow.visibility = View.VISIBLE
            }


            /*if (castingCallModel.is_casting_bookmark == 1) {
                binding.tvSave.text = "Saved"
                binding.tvSave.isFocusable = false
                binding.tvSave.isFocusableInTouchMode = false
                binding.tvSave.isClickable = false
            }*/


        }

        when (castingCallModel.type) {
            "blue" -> {
                binding.llMain.setBackgroundResource(R.drawable.rectangle_castingcall1)
                binding.lllocation.setBackgroundResource(R.drawable.rectangle_loc_blue)
                binding.llverified.setBackgroundResource(R.drawable.rectangle_loc_blue)
                binding.llshift.setBackgroundResource(R.drawable.rectangle_loc_blue)
            }

            "red" -> {
                binding.llMain.setBackgroundResource(R.drawable.rectangle_castingcall2)
                binding.lllocation.setBackgroundResource(R.drawable.rectangle_loc_red)
                binding.llverified.setBackgroundResource(R.drawable.rectangle_loc_red)
                binding.llshift.setBackgroundResource(R.drawable.rectangle_loc_red)
            }

            else -> {
                binding.llMain.setBackgroundResource(R.drawable.rectangle_castingcall3)
                binding.lllocation.setBackgroundResource(R.drawable.rectangle_loc_yellow)
                binding.llverified.setBackgroundResource(R.drawable.rectangle_loc_yellow)
                binding.llshift.setBackgroundResource(R.drawable.rectangle_loc_yellow)
            }
        }
    }

    private fun addListner() {

        binding.apply {
            cvProfile.setOnClickListener(this@CastingDetailsFragment)
            ivBack.setOnClickListener(this@CastingDetailsFragment)
            tvSave.setOnClickListener(this@CastingDetailsFragment)
            llApplyNowButton.setOnClickListener(this@CastingDetailsFragment)
        }
    }

    private fun removeBookMarkDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to remove Save Jobs?")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            if (isNetworkAvailable(requireContext())) {
                viewModel.castingBookmark(
                    PrefManager(requireContext()).getvalue(StaticData.id),
                    castingCallModel.id,
                    "2"
                )
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                ).show()
            }
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })


        viewModel.successData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                if (is_Bookmark){
                    castingCallModel.is_casting_bookmark=0
                    binding.tvSave.text="Save"
                    is_Bookmark=false
                }else{
                    castingCallModel.is_casting_bookmark=1
                    binding.tvSave.text="Saved"
                    is_Bookmark=true
                }
                showCustomToast(requireContext(),StaticData.successMsg,it.msg,StaticData.success)
            } else {
                showCustomToast(requireContext(),StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })

    }

    override fun onClick(item: View?) {
        when(item?.id){
            R.id.cvProfile ->{
                startActivity(Intent(requireContext(),MyProfileActivity::class.java))
            }

            R.id.ivBack ->{
                (requireActivity() as MainActivity).onBackPressed()
            }

            R.id.tvSave ->{
                    if (isNetworkAvailable(requireContext())) {
                        if (castingCallModel.is_casting_bookmark == 0) {
                            viewModel.castingBookmark(
                                PrefManager(requireContext()).getvalue(StaticData.id),
                                castingCallModel.id,
                                "1"
                            )

                        } else {
                            removeBookMarkDialog()
                        }


                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.str_error_internet_connections),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            R.id.llApplyNowButton ->{
                startForBack.launch(
                    Intent(requireContext(), CastingApplyActivity::class.java)
                        .putExtra("model", Gson().toJson(castingCallModel))
                )
            }
        }
    }

    private val startForBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    (requireActivity() as MainActivity).loadFragment(CastingCallFragment())
                }
            }
        }
}