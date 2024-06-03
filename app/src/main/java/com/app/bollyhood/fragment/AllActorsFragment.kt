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
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.adapter.AllActorsAdapter
import com.app.bollyhood.databinding.FragmentAllActorsBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.SingleCategoryModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllActorsFragment : Fragment(),OnClickListener,AllActorsAdapter.onItemCLick {

    lateinit var binding:FragmentAllActorsBinding
    private val viewModel: DataViewModel by viewModels()
    private lateinit var previousFragment:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_all_actors, container, false)

        initUI()
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

        viewModel.actorsList.observe(requireActivity(), Observer {
            if (it.status == "1") {
                setCategoryAdapter(it.result)
            }else{
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun initUI() {

        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        val bundle = arguments
        if (bundle != null) {
            previousFragment= bundle.getString(StaticData.previousFragment).toString()
        }

        if (isNetworkAvailable(requireContext())) {
            viewModel.getAllActors()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.ivBack.setOnClickListener(this)
        binding.cvProfile.setOnClickListener(this)
    }


    override fun onResume() {
        super.onResume()
        binding.tvusername.text =
            "Hi " + PrefManager(requireContext()).getvalue(StaticData.name) + ","

    }

    override fun onClick(item: View?) {
        when(item?.id) {
            R.id.ivBack -> {
                if (previousFragment.equals("AllCategoryFragment"))
                    backpress(AllCategoryFragment())
                else
                    backpress(HomeFragment())
            }

            R.id.cvProfile -> {
                 startActivity(Intent(requireContext(),MyProfileActivity::class.java))
            }
        }
    }

    private fun setCategoryAdapter(profileList: ArrayList<SingleCategoryModel>) {
        binding.apply {
            rvprofilesList.layoutManager =
                GridLayoutManager(requireContext(), 3)
            rvprofilesList.setHasFixedSize(true)
            actorsAdapter = AllActorsAdapter(requireContext(), profileList,this@AllActorsFragment)
            rvprofilesList.adapter = actorsAdapter
            actorsAdapter?.notifyDataSetChanged()
        }
    }

    fun backpress(fragment:Fragment){
        parentFragmentManager.commit {
            replace(R.id.fragment_container,fragment)
        }
    }

    override fun onClick(singleCategoryModel: SingleCategoryModel) {
        if (singleCategoryModel!=null){
            val bundle = Bundle()
            bundle.putString(StaticData.userModel, Gson().toJson(singleCategoryModel))
            bundle.putString(StaticData.previousFragment,"AllActosFragment")

           val profileDetailFragment = ProfileDetailFragment()
           profileDetailFragment.arguments = bundle
           (requireActivity() as MainActivity).loadFragment(profileDetailFragment)
        }
    }

}