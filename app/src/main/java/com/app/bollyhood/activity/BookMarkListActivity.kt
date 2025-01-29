package com.app.bollyhood.activity

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.BookMarkListAdapter
import com.app.bollyhood.databinding.ActivityBookMarkListBinding
import com.app.bollyhood.model.BookMarkModel
import com.app.bollyhood.model.BookingModel
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookMarkListActivity : AppCompatActivity(),BookMarkListAdapter.onItemClick, OnClickListener{

    lateinit var binding: ActivityBookMarkListBinding
    lateinit var mContext: MyBookMarkActivity
    private val viewModel: DataViewModel by viewModels()
    private val bookMarkList: ArrayList<BookMarkModel> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_mark_list)

        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {
    }

    private fun addListner() {
        binding.apply {
            ivBack.setOnClickListener(this@BookMarkListActivity)
        }
    }

    private fun addObserevs() {}

    private fun setAdapter(bookMarkList: ArrayList<BookingModel>) {
        binding.apply {
            rvbookmarklist.layoutManager = LinearLayoutManager(mContext)
            rvbookmarklist.setHasFixedSize(true)
            adapter = BookMarkListAdapter(mContext, bookMarkList, this@BookMarkListActivity)
            rvbookmarklist.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(position: Int, bookingModel: BookingModel) {

    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.ivBack ->{
                finish()
            }
        }
    }
}