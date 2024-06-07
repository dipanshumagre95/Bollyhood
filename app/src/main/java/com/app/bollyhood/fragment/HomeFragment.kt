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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.AllExpertiseProfileActivity
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.adapter.BannerAdapter
import com.app.bollyhood.adapter.CategoryAdapter
import com.app.bollyhood.adapter.ExpertiseAdapter
import com.app.bollyhood.databinding.FragmentHomeBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.BannerModel
import com.app.bollyhood.model.CategoryModel
import com.app.bollyhood.model.ExpertiseModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(), ExpertiseAdapter.onItemClick, CategoryAdapter.onItemClick {

    lateinit var binding: FragmentHomeBinding

    var bannerList: ArrayList<BannerModel> = arrayListOf()

    var categoryList: ArrayList<CategoryModel> = arrayListOf()

    var experiseList: ArrayList<ExpertiseModel> = arrayListOf()

    val viewModel: DataViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)
        initUI()
        addListner()
        return binding.root
    }

    private fun initUI() {

        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)

        if (isNetworkAvailable(requireContext())) {
            viewModel.getBanner()
            viewModel.getRecentCategory()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
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

        viewModel.categoryLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                categoryList.clear()
                categoryList.addAll(it.result)
                setCategoryAdapter(categoryList)
            } else {
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.expertiseLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                experiseList.clear()
                experiseList.addAll(it.result)

                if (experiseList.size > 0) {
                    binding.rrExpertise.visibility = View.VISIBLE
                    setExpertiseAdapter(experiseList)
                } else {
                    binding.rrExpertise.visibility = View.GONE
                }

            } else {
                binding.rrExpertise.visibility = View.GONE
            }
        })


    }


    private fun addListner() {
        binding.tvAllCategory.setOnClickListener {
          //  startActivity(Intent(requireActivity(), AllCategoryActivity::class.java))
            (activity as MainActivity?)!!.loadFragment(AllCategoryFragment())

        }
        binding.tvAllExpertise.setOnClickListener {
            startActivity(Intent(requireContext(), AllExpertiseProfileActivity::class.java))
        }

    }

    private fun setCategoryAdapter(categoryList: ArrayList<CategoryModel>) {
        binding.apply {
            rvCategory.layoutManager =
                GridLayoutManager(requireContext(), 4)
            rvCategory.setHasFixedSize(true)
            categoryAdapter = CategoryAdapter(requireContext(), categoryList, this@HomeFragment)
            rvCategory.adapter = categoryAdapter
            categoryAdapter?.notifyDataSetChanged()
        }
    }


    private fun setAdapter(bannerList: ArrayList<BannerModel>) {
        binding.apply {
            rvBanner.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvBanner.setHasFixedSize(true)
            adapter = BannerAdapter(requireContext(), bannerList)
            rvBanner.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    private fun setExpertiseAdapter(experiseList: ArrayList<ExpertiseModel>) {
        binding.apply {
            rvExpertise.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvExpertise.setHasFixedSize(true)
            expertiseAdapter = ExpertiseAdapter(requireActivity(), experiseList, this@HomeFragment)
            rvExpertise.adapter = expertiseAdapter
            expertiseAdapter?.notifyDataSetChanged()

        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getRecentExpertise(
            PrefManager(requireContext()).getvalue(StaticData.id).toString()
        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).showToolbar(true)
      //  (requireActivity() as MainActivity).binding.tvTitle.text = getString(R.string.str_home)
    }

    override fun onClick(pos: Int, expertiseModel: ExpertiseModel) {
        val bundle = Bundle()
        bundle.putString(StaticData.userModel, Gson().toJson(expertiseModel))

        val profileDetailFragment = ProfileDetailFragment()
        profileDetailFragment.arguments = bundle
        (activity as MainActivity).loadFragment(profileDetailFragment)
    }

    override fun onClick(pos: Int, categoryModel: CategoryModel) {

        when(categoryModel.type){
            "0" ->{
                val bundle = Bundle()
                bundle.putString(StaticData.previousFragment, "HomeFragment")

                val allActorsFragment = AllActorsFragment()
                allActorsFragment.arguments = bundle
                (activity as MainActivity).loadFragment(allActorsFragment)
            }

            "1" ->{

                val bundle = Bundle()
                bundle.putString(StaticData.previousFragment, "HomeFragment")

                val castingCallFragment = CastingCallFragment()
                castingCallFragment.arguments = bundle
                (activity as MainActivity).loadFragment(castingCallFragment)
            }
        }
    }

}