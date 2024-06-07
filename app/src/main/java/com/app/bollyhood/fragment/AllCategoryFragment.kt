package com.app.bollyhood.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
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
import com.app.bollyhood.adapter.AllCategoryAdapter
import com.app.bollyhood.databinding.FragmentAllCategoryBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CategoryModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCategoryFragment : Fragment(),AllCategoryAdapter.onItemClick {

    lateinit var binding: FragmentAllCategoryBinding
    private val viewModel: DataViewModel by viewModels()
    private val categoryList: ArrayList<CategoryModel> = arrayListOf()
    private val filteredData: ArrayList<CategoryModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_all_category, container, false)

        initUI()
        addListner()
        addObserevs()
        return binding.root
    }

    private fun initUI() {

        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)

        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        if (isNetworkAvailable(requireContext())) {
            viewModel.getCategory()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.edsearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text?.length!! > 3) {
                    setFilteredData(text.toString())
                }else{
                    binding.categoryAdapter?.updateList(categoryList)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun setFilteredData(text: String) {
        filteredData.clear()
        if (text.isNullOrEmpty()){
            for (category in categoryList){
                if (category.category_name.contains(text,ignoreCase = true))
                {
                    filteredData.add(category)
                }
            }
            binding.categoryAdapter?.updateList(filteredData)
        }
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            backpress()
        }

        binding.cvProfile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(requireContext(), MyProfileActivity::class.java))
        })
    }

    private fun addObserevs() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
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
    }

    private fun setCategoryAdapter(categoryList: ArrayList<CategoryModel>) {
        binding.apply {
            rvCategory.layoutManager =
                GridLayoutManager(requireContext(), 3)
            rvCategory.setHasFixedSize(true)
            categoryAdapter = AllCategoryAdapter(requireContext(), categoryList,this@AllCategoryFragment)
            rvCategory.adapter = categoryAdapter
            categoryAdapter?.notifyDataSetChanged()
        }
    }

    override fun onItemClick(pos: Int, categoryModel: CategoryModel) {

        when(categoryModel.type){
            "1" ->{
                val bundle = Bundle()
                bundle.putString(StaticData.previousFragment, "AllCategoryFragment")

                val castingCallFragment = CastingCallFragment()
                castingCallFragment.arguments = bundle
                (activity as MainActivity).loadFragment(castingCallFragment)
            }

            "0" ->{
                val bundle = Bundle()
                  bundle.putString(StaticData.previousFragment, "AllCategoryFragment")

                 val allActorsFragment = AllActorsFragment()
                 allActorsFragment.arguments = bundle
                 (activity as MainActivity).loadFragment(allActorsFragment)
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).showToolbar(false)
    }

    override fun onResume() {
        super.onResume()

        binding.tvusername.text =
            "Hi " + (PrefManager(requireContext()).getvalue(StaticData.name)?.split(" ")?.getOrNull(0) ?: "User") + ","
    }

    fun backpress(){
        parentFragmentManager.commit {
            replace(R.id.fragment_container,HomeFragment())
        }
    }


}