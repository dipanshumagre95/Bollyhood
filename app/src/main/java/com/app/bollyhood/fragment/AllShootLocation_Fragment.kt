package com.app.bollyhood.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.activity.ShootingLocationDetails
import com.app.bollyhood.adapter.FeaturedLocationsAdapter
import com.app.bollyhood.adapter.LocationListAdapter
import com.app.bollyhood.databinding.FragmentAllShootLocationBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.ShootingLocationModels.ShootLocationModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllShootLocation_Fragment : Fragment(),LocationListAdapter.onItemClick,OnClickListener,FeaturedLocationsAdapter.OnFeaturedItemClick {

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

        viewModel.shootLocationList.observe(requireActivity(), Observer {
            if (it.status == "1"&&!it.result.isNullOrEmpty()) {
                binding.tvNodata.visibility=View.GONE
                binding.rvlocationImage.visibility=View.VISIBLE
                setLocationListAdapter(it.result)
            } else {
                binding.tvNodata.visibility=View.VISIBLE
                binding.rvlocationImage.visibility=View.GONE
            }
        })

        viewModel.featureLocationList.observe(requireActivity(), Observer {
            if (it.status == "1"&&!it.result.isNullOrEmpty()) {
                binding.tvfeaturedata.visibility=View.GONE
                binding.rvfeaturedlocation.visibility=View.VISIBLE
                setFeatureLocationAdapter(it.result)
            } else {
                binding.tvfeaturedata.visibility=View.VISIBLE
                binding.rvfeaturedlocation.visibility=View.GONE
            }
        })
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener(this)
    }

    private fun initUi() {
        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }
    }

    private fun setFeatureLocationAdapter(shootLocationList: ArrayList<ShootLocationModel>)
    {
        binding.apply {
            rvfeaturedlocation.layoutManager =
                LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
            rvfeaturedlocation.setHasFixedSize(true)
            featuredLocation = FeaturedLocationsAdapter(requireContext(),shootLocationList,this@AllShootLocation_Fragment)
            rvfeaturedlocation.adapter = featuredLocation
            featuredLocation?.notifyDataSetChanged()

        }
    }

    private fun setLocationListAdapter(shootLocationList: ArrayList<ShootLocationModel>)
    {
        binding.apply {
            rvlocationImage.layoutManager =
                LinearLayoutManager(requireContext())
            rvlocationImage.setHasFixedSize(true)
            locationListAdapter = LocationListAdapter(requireContext(),true,shootLocationList,this@AllShootLocation_Fragment)
            rvlocationImage.adapter = locationListAdapter
            locationListAdapter?.notifyDataSetChanged()
        }
    }

    override fun itemClicked(locationId:String) {
        startActivity(
            Intent(requireContext(),ShootingLocationDetails::class.java)
            .putExtra(StaticData.id,locationId))
    }

    override fun editClicked(shootLocationModel: ShootLocationModel) {

    }

    override fun onResume() {
        super.onResume()
        binding.tvusername.text = "Hi " + (PrefManager(requireContext()).getvalue(StaticData.name)?.split(" ")?.getOrNull(0) ?: "User") + ","

        (requireActivity() as MainActivity).showToolbar(false)
        if (isNetworkAvailable(requireContext())) {
            viewModel.getShootLocationList("")
            viewModel.getFeatureLocationList()
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

            R.id.ivBack ->{
                (requireActivity() as MainActivity).onBackPressed()
            }

            R.id.cvProfile ->{
                startActivity(Intent(requireContext(), MyProfileActivity::class.java))
            }
        }
    }

    fun backpress(fragment:Fragment){
        parentFragmentManager.commit {
            replace(R.id.fragment_container,fragment)
        }
    }

    override fun tandingItemClicked(locationId: String) {
        startActivity(
            Intent(requireContext(),ShootingLocationDetails::class.java)
                .putExtra(StaticData.id,locationId))
    }
}