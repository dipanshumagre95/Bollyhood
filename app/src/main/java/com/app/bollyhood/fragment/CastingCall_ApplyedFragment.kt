package com.app.bollyhood.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.activity.Upload_CastingCall
import com.app.bollyhood.adapter.CastingCallListAdapter
import com.app.bollyhood.databinding.FragmentCastingCallApplyedBinding
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.bumptech.glide.Glide

class CastingCall_ApplyedFragment : Fragment(),OnClickListener {

    lateinit var binding: FragmentCastingCallApplyedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_casting_call__applyed, container, false)

        initUi()
        return binding.root
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

        binding.ivBack.setOnClickListener(this)
        binding.cvProfile.setOnClickListener(this)


        setAdapter()
    }


    private fun setAdapter()
    {
        binding.apply {
            rvcatingcallListed.layoutManager =
                LinearLayoutManager(requireContext())
            rvcatingcallListed.setHasFixedSize(true)
            adapter = CastingCallListAdapter()
            rvcatingcallListed.adapter = adapter
            adapter?.notifyDataSetChanged()

        }
    }

    override fun onClick(view: View?) {
        when(view?.id)
        {
            R.id.cvProfile ->{
                startActivity(Intent(requireContext(), MyProfileActivity::class.java))
            }

            R.id.llPostCastingCall ->{
                startActivity(Intent(requireContext(), Upload_CastingCall::class.java))
            }
        }
    }


}