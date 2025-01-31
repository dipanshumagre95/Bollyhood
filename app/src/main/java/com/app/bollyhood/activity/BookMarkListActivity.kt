package com.app.bollyhood.activity

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
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookMarkListActivity : AppCompatActivity(),BookMarkListAdapter.onItemClick, OnClickListener{

    lateinit var binding: ActivityBookMarkListBinding
    lateinit var mContext: MyBookMarkActivity
    private val viewModel: DataViewModel by viewModels()
    private val bookMarkList: ArrayList<BookMarkModel> = arrayListOf()
    lateinit var folder:Folder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_mark_list)

        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {

        if (intent.hasExtra("id"))
        {
            folder=Folder(intent.getStringExtra("id")!!,intent.getStringExtra("name")!!,"","")
        }

        if (isNetworkAvailable(mContext)) {
           viewModel.myBookMark(PrefManager(mContext).getvalue(StaticData.id),folder.folder_id)
           } else {
              Toast.makeText(
               mContext, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
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
    }

    private fun setAdapter(bookMarkList: ArrayList<BookMarkModel>) {
        binding.apply {
            rvbookmarklist.layoutManager = LinearLayoutManager(mContext)
            rvbookmarklist.setHasFixedSize(true)
            adapter = BookMarkListAdapter(mContext, bookMarkList, this@BookMarkListActivity)
            rvbookmarklist.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(position: Int, bookingModel: BookMarkModel) {

    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.ivBack ->{
                finish()
            }
        }
    }
}