package com.app.bollyhood.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.AllCategoryAdapter
import com.app.bollyhood.adapter.CategoryAdapter
import com.app.bollyhood.databinding.ActivityAllCategoryBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CategoryModel
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCategoryActivity : AppCompatActivity(),AllCategoryAdapter.onItemClick {

    lateinit var mContext: AllCategoryActivity
    lateinit var binding: ActivityAllCategoryBinding
    private val viewModel: DataViewModel by viewModels()
    private val categoryList: ArrayList<CategoryModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_category)
        mContext = this
        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {

        if (isNetworkAvailable(mContext)) {
            viewModel.getCategory()
        } else {
            Toast.makeText(
                mContext,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun addObserevs() {
        viewModel.isLoading.observe(mContext, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.categoryLiveData.observe(mContext, Observer {
            if (it.status == "1") {
                categoryList.clear()
                categoryList.addAll(it.result)
                setCategoryAdapter(categoryList)
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setCategoryAdapter(categoryList: ArrayList<CategoryModel>) {
        binding.apply {
            rvCategory.layoutManager =
                GridLayoutManager(mContext, 3)
            rvCategory.setHasFixedSize(true)
            categoryAdapter = AllCategoryAdapter(mContext, categoryList,this@AllCategoryActivity)
            rvCategory.adapter = categoryAdapter
            categoryAdapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(pos: Int, categoryModel: CategoryModel) {
        if (categoryModel.type == "1"){
            startActivity(Intent(mContext, CastingCallsActivity::class.java))
        }

    }

}