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
import com.app.bollyhood.adapter.CastingCallsAdapter
import com.app.bollyhood.databinding.FragmentCastingCallBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CastingCallModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CastingCallFragment : Fragment(),OnClickListener {

    lateinit var binding: FragmentCastingCallBinding
    private val castingModels: ArrayList<CastingCallModel> = arrayListOf()
    private val viewModel: DataViewModel by viewModels()
    private lateinit var previousFragment:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_casting_call, container, false)

        initUI()
        addListner()
        addObsereves()

        return binding.root
    }

    private fun initUI() {

        val bundle = arguments

        if (bundle!=null) {
            previousFragment = bundle.getString(StaticData.previousFragment).toString()
        }

        binding.tvusername.text = "Hello " + PrefManager(requireContext()).getvalue(StaticData.name)+" \uD83D\uDC4B"
        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext()).load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        (requireActivity() as MainActivity).binding.apply{
            llBottom.setBackgroundResource(R.drawable.rectangle_black_top)
            tvHome.setTextColor(resources.getColor(R.color.darkgrey))
            tvBookings.setTextColor(resources.getColor(R.color.darkgrey))
            tvChat.setTextColor(resources.getColor(R.color.darkgrey))
            tvProfile.setTextColor(resources.getColor(R.color.darkgrey))
        }
        (requireActivity() as MainActivity).showToolbar(false)

        binding.cvProfile.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)

        setData(castingModels)
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable(requireContext())) {
            viewModel.getCastingCalls(PrefManager(requireContext()).getvalue(StaticData.id).toString())
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

    }
    private fun addListner() {

    }

    private fun addObsereves() {

        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.castingCallsLiveData.observe(requireActivity(), Observer {
            if (it.status.equals("1")) {
                castingModels.clear()
                castingModels.addAll(it.result)
                setData(castingModels)
            }
        })


    }

    private fun setData(castingModels: ArrayList<CastingCallModel>) {
        binding.apply {
            rvCastingCalls.layoutManager = LinearLayoutManager(requireContext())
            rvCastingCalls.setHasFixedSize(true)
            adapter = CastingCallsAdapter(requireContext(), castingModels)
            rvCastingCalls.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(item: View?) {
        when(item?.id){

            R.id.cvProfile ->{
                startActivity(Intent(requireContext(),MyProfileActivity::class.java))
            }

            R.id.ivBack  ->{
                if (previousFragment.equals("AllCategoryFragment")) {
                    backpress(AllCategoryFragment())
                }else{
                    backpress(HomeFragment())
                }
            }

        }
    }

    fun backpress(fragment:Fragment){
        parentFragmentManager.commit {
            replace(R.id.fragment_container,fragment)
        }
    }

}