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
import com.app.bollyhood.adapter.TalentManagmentUsersAdp
import com.app.bollyhood.databinding.FragmentTalentManagementListBinding
import com.app.bollyhood.viewmodel.DataViewModel

class TalentManagementListFragment : Fragment() {

    lateinit var binding: FragmentTalentManagementListBinding
    private val viewModel: DataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_talent_management_list, container, false)

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

    }

    private fun setActiveAdapter()
    {
        binding.apply {
            talentManagmentUserList.layoutManager =
                LinearLayoutManager(requireContext())
            talentManagmentUserList.setHasFixedSize(true)
            adapter = TalentManagmentUsersAdp()
            talentManagmentUserList.adapter = adapter
            adapter?.notifyDataSetChanged()

        }
    }
}