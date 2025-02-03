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
import com.app.bollyhood.adapter.FeaturedLocationsAdapter
import com.app.bollyhood.adapter.LocationListAdapter
import com.app.bollyhood.databinding.FragmentAllShootLocationBinding
import com.app.bollyhood.viewmodel.DataViewModel

class AllShootLocation_Fragment : Fragment(),LocationListAdapter.onItemClick {

    lateinit var binding: FragmentAllShootLocationBinding
    private val viewModel: DataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_all_shoot_location_, container, false)

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

    private fun setFeatureLocationAdapter()
    {
        binding.apply {
            rvfeaturedlocation.layoutManager =
                LinearLayoutManager(requireContext())
            rvfeaturedlocation.setHasFixedSize(true)
            featuredLocation = FeaturedLocationsAdapter()
            rvfeaturedlocation.adapter = featuredLocation
            featuredLocation?.notifyDataSetChanged()

        }
    }

    private fun setLocationListAdapter()
    {
        binding.apply {
            rvlocationImage.layoutManager =
                LinearLayoutManager(requireContext())
            rvlocationImage.setHasFixedSize(true)
            locationListAdapter = LocationListAdapter(true,this@AllShootLocation_Fragment)
            rvlocationImage.adapter = locationListAdapter
            locationListAdapter?.notifyDataSetChanged()
        }
    }

    override fun itemClicked() {

    }

    override fun editClicked() {

    }

}