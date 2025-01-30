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
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class AllCastingCallFragment : Fragment(),OnClickListener,AllCastingCallListAdapter.OnClickInterface {

    lateinit var binding:FragmentAllCastingCallBinding
    private val viewModel: DataViewModel by viewModels()
    private val activeCastingList: ArrayList<CastingCallModel> = arrayListOf()
    private val closedCastingList: ArrayList<CastingCallModel> = arrayListOf()
    private var isActive=true

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
                activeCastingList.clear()
                activeCastingList.addAll(it.result.active)
                closedCastingList.clear()
                closedCastingList.addAll(it.result.inactive)
                setView()
            }
        })
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener(this)
        binding.cvProfile.setOnClickListener(this)
        binding.llPostCastingCall.setOnClickListener(this)
        binding.tvActive.setOnClickListener(this)
        binding.tvClose.setOnClickListener(this)
        binding.llbookmark.setOnClickListener(this)
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
                isActive=true
                setView()
            }

            R.id.tvClose ->{
                isActive=false
                setView()
            }
        }
    }

    private fun setView() {
        if (isActive){
            binding.tvActive.setBackgroundResource(R.drawable.rectangle_black_button)
            binding.tvClose.setBackgroundResource(R.drawable.border_gray)
            binding.tvActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.tvClose.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            if (!activeCastingList.isNullOrEmpty()){
                binding.noData.visibility=View.GONE
                binding.rvAllCatingcall.visibility=View.VISIBLE
                setActiveAdapter(activeCastingList)
            }else{
                binding.rvAllCatingcall.visibility=View.GONE
                binding.noData.visibility=View.VISIBLE
            }
        }else{
            binding.tvActive.setBackgroundResource(R.drawable.border_gray)
            binding.tvClose.setBackgroundResource(R.drawable.rectangle_black_button)
            binding.tvActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.tvClose.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            if (!closedCastingList.isNullOrEmpty()){
                binding.noData.visibility=View.GONE
                binding.rvAllCatingcall.visibility=View.VISIBLE
                setActiveAdapter(closedCastingList)
            }else{
                binding.rvAllCatingcall.visibility=View.GONE
                binding.noData.visibility=View.VISIBLE
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

    override fun pinCasting(castingModel: CastingCallModel) {
        if (isNetworkAvailable(requireContext())) {
            viewModel.makeCastingPin(PrefManager(requireContext()).getvalue(StaticData.id).toString(),castingModel.id,castingModel.status)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun editCasting(castingModel: CastingCallModel) {
        lifecycleScope.launch {
            val castingString = withContext(Dispatchers.IO) {
                Gson().toJson(castingModel)
            }
            val intent = Intent(requireContext(), Upload_CastingCall::class.java).apply {
                putExtra(StaticData.userModel, castingString)
                putExtra(StaticData.edit, "edit")
            }
            startActivity(intent)
        }
    }

    override fun changeCastingStatus(castingModel: CastingCallModel,status:String) {
        if (isNetworkAvailable(requireContext())) {
            viewModel.makeCastingPin(PrefManager(requireContext()).getvalue(StaticData.id).toString(),castingModel.id,status)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun deleteCastingCall(castingModel: CastingCallModel) {
        if (isNetworkAvailable(requireContext())) {
            viewModel.deleteCastingCall(PrefManager(requireContext()).getvalue(StaticData.id).toString(),castingModel.id)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}