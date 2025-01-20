package com.app.bollyhood.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.app.bollyhood.adapter.ProductionHouseAdapter
import com.app.bollyhood.databinding.FragmentProductionHouseBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.SingleCategoryModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductionHouseFragment : Fragment(),OnClickListener,ProductionHouseAdapter.OnProductionItemClick {

    lateinit var binding:FragmentProductionHouseBinding
    private val viewModel: DataViewModel by viewModels()
    private val productionList: ArrayList<SingleCategoryModel> = arrayListOf()
    private val productionSubList: ArrayList<SingleCategoryModel> = arrayListOf()
    private val filteredData: ArrayList<SingleCategoryModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_production_house, container, false)

        initUi()
        setSearchView()
        addObserver()
        addListener()
        return binding.root
    }

    private fun setSearchView() {
        binding.edsearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text?.length!! > 3) {
                    setFilteredData(text.toString())
                }else{
                    if (productionList.size >= 9) {
                        productionSubList.clear()
                        productionSubList.addAll(productionList.subList(0, 9))
                        setAdapter(productionSubList)
                        binding.tvloadMore.visibility=View.VISIBLE
                    } else {
                        setAdapter(productionList)
                        binding.tvloadMore.visibility=View.GONE
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun addListener() {
        binding.let {
            it.cvProfile.setOnClickListener(this)
            it.ivBack.setOnClickListener(this)
            it.tvloadMore.setOnClickListener(this)
        }
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
            if (it.status == "1"&&it.result.isNotEmpty()) {
                binding.tvNodata.visibility=View.GONE
                binding.tvloadMore.visibility=View.VISIBLE
                binding.rvproductionList.visibility=View.VISIBLE
                productionList.clear()
                productionList.addAll(it.result)
                if (productionList.size >= 9) {
                    productionSubList.clear()
                    productionSubList.addAll(productionList.subList(0, 9))
                    setAdapter(productionSubList)
                    binding.tvloadMore.visibility=View.VISIBLE
                } else {
                    setAdapter(productionList)
                    binding.tvloadMore.visibility=View.GONE
                }
            }else{
                binding.tvNodata.visibility=View.VISIBLE
                binding.tvloadMore.visibility=View.GONE
                binding.rvproductionList.visibility=View.GONE
            }
        })
    }

    private fun initUi() {

        if (isNetworkAvailable(requireContext())) {
            viewModel.getAllActors("26",PrefManager(requireContext()).getvalue(StaticData.id).toString())
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        val bundle = arguments
        if (bundle!=null) {
            PrefManager(requireContext()).setvalue(
                StaticData.previousFragment,
                bundle.getString(StaticData.previousFragment).toString()
            )
        }

        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)

        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }
    }

    fun setAdapter(productionList: ArrayList<SingleCategoryModel>) {
        binding.apply {
            rvproductionList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvproductionList.setHasFixedSize(true)
            productionHouseAdapter = ProductionHouseAdapter(requireContext(), productionList,this@ProductionHouseFragment)
            rvproductionList.adapter = productionHouseAdapter
            productionHouseAdapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.cvProfile ->{
                startActivity(Intent(requireContext(),MyProfileActivity::class.java))
            }

            R.id.ivBack ->{
                if (PrefManager(requireContext()).getvalue(StaticData.previousFragment).equals("AllCategoryFragment")) {
                    backpress(AllCategoryFragment())
                }else{
                    (requireActivity() as MainActivity).setHomeColor()
                }
            }

            R.id.tvload_more ->{
                setAdapter(productionList)
                binding.tvloadMore.visibility=View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.tvusername.text = "Hi " + (PrefManager(requireContext()).getvalue(StaticData.name)?.split(" ")?.getOrNull(0) ?: "User") + ","

        (requireActivity() as MainActivity).showToolbar(false)
    }

    fun backpress(fragment:Fragment){
        parentFragmentManager.commit {
            replace(R.id.fragment_container,fragment)
        }
    }

    override fun onItemClick(singleCategoryModel: SingleCategoryModel) {
        val bundle = Bundle()
        bundle.putString(StaticData.userModel, Gson().toJson(singleCategoryModel))
        bundle.putString(StaticData.previousFragment,"ProductionHouseFragment")

        val companyProfileFragment = CompanyProfileFragment()
        companyProfileFragment.arguments = bundle
        (requireActivity() as MainActivity).loadFragment(companyProfileFragment)
    }

    private fun setFilteredData(text: String) {
        filteredData.clear()
        if (!text.isNullOrEmpty()){
            for (category in productionList){
                if (category.name.contains(text,ignoreCase = true))
                {
                    filteredData.add(category)
                }
            }
            binding.productionHouseAdapter?.updateList(filteredData)
            binding.tvloadMore.visibility=View.GONE
        }
    }
}