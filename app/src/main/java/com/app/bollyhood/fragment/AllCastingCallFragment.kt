package com.app.bollyhood.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.activity.Upload_CastingCall
import com.app.bollyhood.adapter.AllCastingCallListAdapter
import com.app.bollyhood.databinding.FragmentAllCastingCallBinding
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.bumptech.glide.Glide

class AllCastingCallFragment : Fragment(),OnClickListener,AllCastingCallListAdapter.OnClickInterface {

    lateinit var binding:FragmentAllCastingCallBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_all_casting_call, container, false)

        initUi()
        addListner()
        return binding.root
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




        setActiveAdapter()
    }

    private fun setActiveAdapter()
    {
        binding.apply {
            rvAllCatingcall.layoutManager =
                LinearLayoutManager(requireContext())
            rvAllCatingcall.setHasFixedSize(true)
            adapter = AllCastingCallListAdapter(requireContext(),this@AllCastingCallFragment)
            rvAllCatingcall.adapter = adapter
            adapter?.notifyDataSetChanged()

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

    override fun onItemClick() {
       (requireActivity() as MainActivity).loadFragment(CastingCall_ApplyedFragment())
    }
}