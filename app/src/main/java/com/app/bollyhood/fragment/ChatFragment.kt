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
import com.app.bollyhood.activity.ChatActivity
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.adapter.ActiveChatUserAdapter
import com.app.bollyhood.adapter.ChatAdapter
import com.app.bollyhood.databinding.FragmentChatBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.ExpertiseModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment(), ChatAdapter.onItemClick,OnClickListener,ActiveChatUserAdapter.ActiveUserOnClick {

    lateinit var binding: FragmentChatBinding
    private val viewModel: DataViewModel by viewModels()
    var chatList: ArrayList<ExpertiseModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        initUi()
        addObserevs()
        return binding.root
    }

    private fun initUi()
    {
        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext()).load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)

        binding.ivBack.setOnClickListener(this)
        binding.cvProfile.setOnClickListener(this)
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
                 //   binding.tvMyChats.visibility = View.GONE
                    setAdapter(chatList)
                    setActiveUserAdapter(chatList)

                } else {
                 //   binding.tvMyChats.visibility = View.GONE
                    binding.rvExpertise.visibility = View.GONE
                    binding.tvNoChatHistory.visibility = View.VISIBLE
                }

            } else {
             //   binding.tvMyChats.visibility = View.GONE
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
            adapter = ChatAdapter(requireContext(), chatList, this@ChatFragment)
            rvExpertise.adapter = adapter
            adapter?.notifyDataSetChanged()

        }
    }


    private fun setActiveUserAdapter(chatList: ArrayList<ExpertiseModel>) {
        binding.apply {
            rvActiveuser.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvActiveuser.setHasFixedSize(true)
            chatAdapter = ActiveChatUserAdapter(requireContext(), chatList,this@ChatFragment)
            rvActiveuser.adapter = adapter
            adapter?.notifyDataSetChanged()

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).showToolbar(false)
     //   (requireActivity() as MainActivity).binding.tvTitle.text = "My Chats"
    }


    fun backpress(){
        parentFragmentManager.commit {
            replace(R.id.fragment_container,HomeFragment())
        }
    }


    override fun onChatItemClick(pos: Int, expertiseModel: ExpertiseModel) {
        lunchChatHistoryActivity(pos,expertiseModel)
    }

    fun lunchChatHistoryActivity(pos: Int, expertiseModel: ExpertiseModel)
    {
        startActivity(
            Intent(requireContext(), ChatActivity::class.java)
                .putExtra("profileId", expertiseModel.id)
        )
    }

    override fun onClick(item: View?) {
        when(item?.id){

            R.id.ivBack ->{
                backpress()
            }

            R.id.cvProfile ->{
                startActivity(Intent(requireContext(),MyProfileActivity::class.java))
            }
        }
    }

    override fun activeChatItemClick(pos: Int, expertiseModel: ExpertiseModel) {
        lunchChatHistoryActivity(pos,expertiseModel)
    }
}