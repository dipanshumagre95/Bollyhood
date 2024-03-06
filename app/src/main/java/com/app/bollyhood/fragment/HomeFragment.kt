package com.app.bollyhood.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.BannerAdapter
import com.app.bollyhood.databinding.FragmentHomeBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.BannerModel
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    var bannerList: ArrayList<BannerModel> = arrayListOf()

    val viewModel: DataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {

        if (isNetworkAvailable(requireContext())){
            viewModel.getBanner()
        }else{
            Toast.makeText(requireContext(),getString(R.string.str_error_internet_connections),Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.bannerLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                bannerList.clear()
                bannerList.addAll(it.result)
                setAdapter(bannerList)
            }
        })
    }

    private fun setAdapter(bannerList: ArrayList<BannerModel>) {
        binding.apply {
            rvBanner.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            rvBanner.setHasFixedSize(true)
            adapter= BannerAdapter(requireContext(),bannerList)
            rvBanner.adapter=adapter
            adapter?.notifyDataSetChanged()
        }
    }

}