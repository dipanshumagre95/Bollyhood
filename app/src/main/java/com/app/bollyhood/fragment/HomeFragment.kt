package com.app.bollyhood.fragment

import Categorie
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
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
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(), ExpertiseAdapter.onItemClick, CategoryAdapter.onItemClick {

    lateinit var binding: FragmentHomeBinding
    var bannerList: ArrayList<BannerModel> = arrayListOf()
    var categoryList: ArrayList<CategoryModel> = arrayListOf()
    var experiseList: ArrayList<ExpertiseModel> = arrayListOf()
    val viewModel: DataViewModel by viewModels()
    val param=LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(5,0,5,0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



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

        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                .centerCrop()
                .into(binding.cvProfile)
        }

        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)

        if (isNetworkAvailable(requireContext())) {
            viewModel.getBanner()
            viewModel.getRecentCategory()
            viewModel.getRecentExpertise(
            PrefManager(requireContext()).getvalue(StaticData.id).toString())
        } else {
            showCustomToast(requireContext(),StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
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
                binding.llMain.visibility=View.VISIBLE
                binding.tvNodata.visibility=View.GONE
                categoryList.addAll(it.result)
                setCategoryAdapter(categoryList)
            } else {
                binding.llMain.visibility=View.GONE
                binding.tvNodata.visibility=View.VISIBLE
                showCustomToast(requireContext(),StaticData.pleaseTryAgain,it.msg,StaticData.alert)
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
            adapter = BannerAdapter(requireContext(), bannerList)
            rvBanner.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
        setIndicactors(bannerList)
    }

    private fun setIndicactors(bannerList: ArrayList<BannerModel>) {
        binding.slideDortll.removeAllViews()
        val dostArray= Array(bannerList.size){
            ImageView(requireContext())
        }

        dostArray.forEach {
            it.setImageResource(R.drawable.not_active_dot)
            binding.slideDortll.addView(it,param)
        }

        dostArray[0].setImageResource(R.drawable.active_dot)

        binding.rvBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                dostArray.mapIndexed { index, imageView ->
                    if (position==index){
                        imageView.setImageResource(R.drawable.active_dot)
                    }else{
                        imageView.setImageResource(R.drawable.not_active_dot)
                    }
                }
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
    }

    private fun setExpertiseAdapter(experiseList: ArrayList<ExpertiseModel>) {
        val size=experiseList.size
        if (size!=null||size!=0){
            when(size){
                1 ->{
                    binding.llfirstimageTalent.visibility=View.VISIBLE
                    binding.llsecondimageTalent.visibility=View.GONE
                    Glide.with(requireContext()).load(experiseList[0].image)
                        .centerCrop()
                        .error(R.drawable.ic_profile)
                        .into(binding.firsttalentivImage)

                    binding.tvfirsttalentname.text=experiseList[0].name
                    if (experiseList[0].is_verify == "1") {
                        binding.ivfirsttalentBookMark.visibility = View.VISIBLE
                    } else {
                        binding.ivfirsttalentBookMark.visibility = View.GONE
                    }
                    val stringList = arrayListOf<String>()
                    for (i in 0 until experiseList[0].categories.size) {
                        stringList.add(experiseList[0].categories[i].category_name)
                    }

                    binding.tvfirsttalentCategory.text = stringList.joinToString(separator = "/")
                }

                else ->{
                    binding.llfirstimageTalent.visibility=View.VISIBLE
                    binding.llsecondimageTalent.visibility=View.VISIBLE
                    Glide.with(requireContext()).load(experiseList[0].image)
                        .centerCrop()
                        .error(R.drawable.ic_profile)
                        .into(binding.firsttalentivImage)

                    binding.tvfirsttalentname.text=experiseList[0].name
                    if (experiseList[0].is_verify == "1") {
                        binding.ivfirsttalentBookMark.visibility = View.VISIBLE
                    } else {
                        binding.ivfirsttalentBookMark.visibility = View.GONE
                    }
                    val stringList = arrayListOf<String>()
                    for (i in 0 until experiseList[0].categories.size) {
                        stringList.add(experiseList[0].categories[i].category_name)
                    }

                    binding.tvfirsttalentCategory.text = stringList.joinToString(separator = "/")

                    Glide.with(requireContext()).load(experiseList[1].image)
                        .centerCrop()
                        .error(R.drawable.ic_profile)
                        .into(binding.ivsecondtalentImage)

                    binding.tvsecondtalentname.text=experiseList[1].name
                    if (experiseList[1].is_verify == "1") {
                        binding.ivsecondtalentBookMark.visibility = View.VISIBLE
                    } else {
                        binding.ivsecondtalentBookMark.visibility = View.GONE
                    }
                    val stringList2 = arrayListOf<String>()
                    for (i in 0 until experiseList[1].categories.size) {
                        stringList2.add(experiseList[1].categories[i].category_name)
                    }

                    binding.tvsecondtalentCategory.text = stringList2.joinToString(separator = "/")
                }
            }
        }else{
            binding.llfirstimageTalent.visibility=View.GONE
            binding.llsecondimageTalent.visibility=View.GONE
            binding.rrExpertise.visibility=View.GONE
        }
    }

    override fun onResume() {
        super.onResume()

        binding.tvusername.text = "Hi " + (PrefManager(requireContext()).getvalue(StaticData.name)?.split(" ")?.getOrNull(0) ?: "User") + ","

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).showToolbar(false)
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

        when(categoryModel.category_name){

            Categorie.CASTINGCALLS.toString() ->{
                val bundle = Bundle()
                bundle.putString(StaticData.previousFragment, "HomeFragment")

                val castingCallFragment = CastingCallFragment()
                castingCallFragment.arguments = bundle
                (activity as MainActivity).loadFragment(castingCallFragment)
            }

            Categorie.PRODUCTIONHOUSE.toString() ->{
                val bundle = Bundle()
                bundle.putString(StaticData.previousFragment, "HomeFragment")

                val productionHouseFragment = ProductionHouseFragment()
                productionHouseFragment.arguments = bundle
                (activity as MainActivity).loadFragment(productionHouseFragment)
            }

            else ->{
                val bundle = Bundle()
                bundle.putString(StaticData.previousFragment, "HomeFragment")
                bundle.putString(StaticData.categorie,categoryModel.id)
                bundle.putString(StaticData.name,categoryModel.category_name)

                val allActorsFragment = AllActorsFragment()
                allActorsFragment.arguments = bundle
                (activity as MainActivity).loadFragment(allActorsFragment)
            }
        }
    }

}