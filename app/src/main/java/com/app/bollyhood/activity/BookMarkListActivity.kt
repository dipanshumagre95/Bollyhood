package com.app.bollyhood.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.BookMarkListAdapter
import com.app.bollyhood.databinding.ActivityBookMarkListBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.BookMarkModel
import com.app.bollyhood.model.Folder
import com.app.bollyhood.model.ProfileModel
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookMarkListActivity : AppCompatActivity(),BookMarkListAdapter.onItemClick, OnClickListener{

    lateinit var binding: ActivityBookMarkListBinding
    private val viewModel: DataViewModel by viewModels()
    private val bookMarkList: ArrayList<BookMarkModel> = arrayListOf()
    lateinit var folder:Folder
    lateinit var categorie_name:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_mark_list)

        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {
        if (PrefManager(this).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(this).load(PrefManager(this).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        if (intent.hasExtra("id"))
        {
            folder=Folder(intent.getStringExtra("id")!!,intent.getStringExtra("name")!!,"","")
        }

        if (isNetworkAvailable(this)) {
           viewModel.myBookMark(PrefManager(this).getvalue(StaticData.id),folder.folder_id)
           } else {
              Toast.makeText(
               this, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
               ).show()
       }
    }

    private fun addListner() {
        binding.apply {
            ivBack.setOnClickListener(this@BookMarkListActivity)
        }
    }

    private fun addObserevs() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.bookMarkLiveData.observe(this, Observer {
            if (it.status == "1") {
                bookMarkList.clear()
                bookMarkList.addAll(it.result)
                if (bookMarkList.size > 0) {
                    binding.tvnoData.visibility = View.GONE
                    binding.rvbookmarklist.visibility = View.VISIBLE
                    setAdapter(bookMarkList)
                } else {
                    binding.tvnoData.visibility = View.VISIBLE
                    binding.rvbookmarklist.visibility = View.GONE
                }
            } else {
                binding.tvnoData.visibility = View.VISIBLE
                binding.rvbookmarklist.visibility = View.GONE
            }
        })

        viewModel.profileLiveData.observe(this, Observer {
            if (it.status == "1") {
                setProfileDeta(it.result)
            } else {
                showCustomToast(this,StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })
    }

    private fun setProfileDeta(profileModel: ProfileModel) {
        startActivity(
            Intent(this, BookmarkProfilesActivity::class.java).
            putExtra(StaticData.userModel, Gson().toJson(profileModel)).
            putExtra(StaticData.cate_name,categorie_name)
        )
    }

    private fun setAdapter(bookMarkList: ArrayList<BookMarkModel>) {
        binding.apply {
            rvbookmarklist.layoutManager = LinearLayoutManager(this@BookMarkListActivity)
            rvbookmarklist.setHasFixedSize(true)
            adapter = BookMarkListAdapter(this@BookMarkListActivity, bookMarkList, this@BookMarkListActivity)
            rvbookmarklist.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(bookmarkModel: BookMarkModel) {
        categorie_name=bookmarkModel.category_name
        viewModel.getProfile(bookmarkModel.id)
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.ivBack ->{
                finish()
            }
        }
    }
}