package com.app.bollyhood.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.TalentManagmentAdapter
import com.app.bollyhood.databinding.FragmentTalentManagmentCompanyBinding
import com.app.bollyhood.viewmodel.DataViewModel

class Talent_Managment_Company : Fragment() {

    lateinit var binding: FragmentTalentManagmentCompanyBinding
    private val viewModel: DataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_talent__managment__company, container, false)

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
    }

    private fun addListner() {

    }

    private fun initUi() {
        setActiveAdapter()
    }

    private fun setActiveAdapter()
    {
        binding.apply {
            talentManagmentList.layoutManager =
                LinearLayoutManager(requireContext())
            talentManagmentList.setHasFixedSize(true)
            adapter = TalentManagmentAdapter()
            talentManagmentList.adapter = adapter
            adapter?.notifyDataSetChanged()

        }
    }

}