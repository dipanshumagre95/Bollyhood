package com.app.bollyhood.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.activity.Upload_CastingCall
import com.app.bollyhood.adapter.AllCastingCallListAdapter
import com.app.bollyhood.databinding.FragmentAllCastingCallBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CastingCallModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCastingCallFragment : Fragment(),OnClickListener,AllCastingCallListAdapter.OnClickInterface {

    lateinit var binding:FragmentAllCastingCallBinding
    private val viewModel: DataViewModel by viewModels()
    private val castingModels: ArrayList<CastingCallModel> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_all_casting_call, container, false)

        initUi()
        addListner()
        addObserver()
        return binding.root
    }

    private fun addObserver() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.castingCallsLiveData.observe(requireActivity(), Observer {
            if (it.status.equals("1")) {
                castingModels.clear()
                castingModels.addAll(it.result)
                setActiveAdapter(castingModels)
            }
        })
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener(this)
        binding.cvProfile.setOnClickListener(this)
        binding.llPostCastingCall.setOnClickListener(this)
        binding.tvActive.setOnClickListener(this)
        binding.tvClose.setOnClickListener(this)
    }

    private fun initUi()
    {
        (requireActivity() as MainActivity).showToolbar(false)
        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext()).load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)
    }

    private fun setActiveAdapter(castingModels: ArrayList<CastingCallModel>)
    {
        binding.apply {
            rvAllCatingcall.layoutManager =
                LinearLayoutManager(requireContext())
            rvAllCatingcall.setHasFixedSize(true)
            adapter = AllCastingCallListAdapter(requireContext(),this@AllCastingCallFragment,castingModels)
            rvAllCatingcall.adapter = adapter
            adapter?.notifyDataSetChanged()

        }
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable(requireContext())) {
            viewModel.getAllCastingCalls(PrefManager(requireContext()).getvalue(StaticData.id).toString())
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.cvProfile ->{
                startActivity(Intent(requireContext(),MyProfileActivity::class.java))
            }

            R.id.llPostCastingCall ->{
                startActivity(Intent(requireContext(),Upload_CastingCall::class.java))
            }

            R.id.ivBack ->{
                (requireActivity() as MainActivity).setHomeColor()
            }

            R.id.tvActive ->{
                binding.tvActive.setBackgroundResource(R.drawable.rectangle_black_button)
                binding.tvClose.setBackgroundResource(R.drawable.border_gray)
                binding.tvActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.tvClose.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            R.id.tvClose ->{
                binding.tvActive.setBackgroundResource(R.drawable.border_gray)
                binding.tvClose.setBackgroundResource(R.drawable.rectangle_black_button)
                binding.tvActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                binding.tvClose.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }
    }

    override fun onItemClick(castingModel:CastingCallModel) {
        val bundle = Bundle()
        bundle.putString(StaticData.userModel, Gson().toJson(castingModel))

        val castingDetailsFragment = CastingCall_ApplyedFragment()
        castingDetailsFragment.arguments = bundle
       (requireActivity() as MainActivity).loadFragment(castingDetailsFragment)
    }
}