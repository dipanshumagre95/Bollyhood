package com.app.bollyhood.fragment

import android.content.Intent
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
import com.app.bollyhood.activity.AllAgencyActivity
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.adapter.AgencyUserAdapter
import com.app.bollyhood.databinding.FragmentAgencyBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.castinglist.CastingUserData
import com.app.bollyhood.viewmodel.DataViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgencyFragment : Fragment(),AgencyUserAdapter.onItemClick {

    lateinit var binding: FragmentAgencyBinding
    private val viewModel: DataViewModel by viewModels()
    private var list: ArrayList<CastingUserData> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_agency, container, false)
        initUI()
        addObserevs()
        return binding.root
    }

    private fun initUI() {

        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)

        if (isNetworkAvailable(requireContext())) {
          //  viewModel.getAgency()
        } else {
            Toast.makeText(
                requireContext(), getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addObserevs() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.agencyLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                list.clear()
             //   list.addAll(it.result)
                if (list.size > 0) {
                    binding.rvBook.visibility = View.VISIBLE
                    binding.tvNoBookings.visibility = View.GONE
                    setAdapter(list)
                } else {
                    binding.rvBook.visibility = View.GONE
                    binding.tvNoBookings.visibility = View.VISIBLE
                }
            } else {
                binding.rvBook.visibility = View.GONE
                binding.tvNoBookings.visibility = View.VISIBLE
            }
        })
    }

    private fun setAdapter(list: ArrayList<CastingUserData>) {
        binding.apply {
            rvBook.layoutManager = LinearLayoutManager(requireContext())
            rvBook.setHasFixedSize(true)
            adapter = AgencyUserAdapter(requireContext(), list,this@AgencyFragment)
            rvBook.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(castingUserData: CastingUserData, pos: Int) {
        startActivity(Intent(requireContext(),AllAgencyActivity::class.java)
            .putExtra("model",Gson().toJson(castingUserData)))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).showToolbar(true)
        //(requireActivity() as MainActivity).binding.tvTitle.text = getString(R.string.str_my_bookings)
    }
}