package com.app.bollyhood.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.ChatActivity
import com.app.bollyhood.adapter.ChatAdapter
import com.app.bollyhood.databinding.FragmentChatBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.ExpertiseModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment(),ChatAdapter.onItemClick {

    lateinit var binding: FragmentChatBinding
    private val viewModel: DataViewModel by viewModels()
    var chatList: ArrayList<ExpertiseModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        addObserevs()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable(requireContext())) {
            viewModel.getAllExpertise(
                PrefManager(requireContext()).getvalue(StaticData.id).toString()
            )
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
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

        viewModel.expertiseLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                chatList.clear()
                chatList.addAll(it.result)

                if (chatList.size > 0) {
                    binding.rvExpertise.visibility = View.VISIBLE
                    binding.tvNoChatHistory.visibility = View.GONE
                    binding.tvMyChats.visibility=View.VISIBLE
                    setAdapter(chatList)

                } else {
                    binding.tvMyChats.visibility=View.GONE
                    binding.rvExpertise.visibility = View.GONE
                    binding.tvNoChatHistory.visibility = View.VISIBLE
                }

            } else {
                binding.tvMyChats.visibility=View.GONE
                binding.rvExpertise.visibility = View.GONE
                binding.tvNoChatHistory.visibility = View.VISIBLE
            }
        })
    }

    private fun setAdapter(chatList: ArrayList<ExpertiseModel>) {
        binding.apply {
            rvExpertise.layoutManager =
                LinearLayoutManager(requireContext())
            rvExpertise.setHasFixedSize(true)
            adapter = ChatAdapter(requireContext(), chatList,this@ChatFragment)
            rvExpertise.adapter = adapter
            adapter?.notifyDataSetChanged()

        }
    }

    override fun onClick(pos: Int, expertiseModel: ExpertiseModel) {
        startActivity(
            Intent(requireContext(), ChatActivity::class.java)
            .putExtra("profileId",expertiseModel.id)
            .putExtra("model", Gson().toJson(expertiseModel)))

    }


}