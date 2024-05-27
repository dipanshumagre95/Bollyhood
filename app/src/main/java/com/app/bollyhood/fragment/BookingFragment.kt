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
import com.app.bollyhood.activity.BookingDetailActivity
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.adapter.BookAdapter
import com.app.bollyhood.databinding.FragmentBookingBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.BookingModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingFragment : Fragment(), BookAdapter.onItemClick {

    lateinit var binding: FragmentBookingBinding
    private val viewModel: DataViewModel by viewModels()
    private val bookList: ArrayList<BookingModel> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking, container, false)
        initUI()
        addObserevs()
        return binding.root
    }

    private fun initUI() {

        if (isNetworkAvailable(requireContext())) {
            viewModel.getBooking(PrefManager(requireContext()).getStringValue(StaticData.id))
        } else {
            Toast.makeText(
                requireContext(), getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).showToolbar(true)
        //(requireActivity() as MainActivity).binding.tvTitle.text = getString(R.string.str_my_bookings)
    }


    private fun addObserevs() {

        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.myBookLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                bookList.clear()
                bookList.addAll(it.result)
                if (bookList.size > 0) {
                    binding.rvBook.visibility = View.VISIBLE
                    binding.tvNoBookings.visibility = View.GONE
                    setAdapter(bookList)
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

    private fun setAdapter(bookList: ArrayList<BookingModel>) {
        binding.apply {
            rvBook.layoutManager = LinearLayoutManager(requireContext())
            rvBook.setHasFixedSize(true)
            adapter = BookAdapter(requireContext(), bookList, this@BookingFragment)
            rvBook.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(position: Int, bookingModel: BookingModel) {
        startActivity(
            Intent(requireContext(), BookingDetailActivity::class.java)
                .putExtra(StaticData.userModel,Gson().toJson(bookingModel))
        )
    }
}