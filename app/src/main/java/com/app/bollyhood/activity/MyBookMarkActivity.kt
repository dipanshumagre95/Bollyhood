package com.app.bollyhood.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.BookMarkAdapter
import com.app.bollyhood.databinding.ActivityMyBookMarkBinding
import com.app.bollyhood.model.BookMarkModel
import com.app.bollyhood.model.Folder
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyBookMarkActivity : AppCompatActivity(), BookMarkAdapter.onItemClick,OnClickListener {

    lateinit var binding: ActivityMyBookMarkBinding
    lateinit var mContext: MyBookMarkActivity
    private val viewModel: DataViewModel by viewModels()
    private val bookMarkList: ArrayList<BookMarkModel> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_book_mark)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {

        try {
            val json = PrefManager(this).getvalue(StaticData.folderData)
            val folderList: ArrayList<Folder> = if (json != null) {
                val type = object : TypeToken<ArrayList<Folder>>() {}.type
                Gson().fromJson(json, type)
            } else {
                ArrayList()
            }

            if (!folderList.isNullOrEmpty()) {
                binding.rvBookMark.visibility = View.VISIBLE
                binding.NoFavourites.visibility = View.GONE
                setAdapter(folderList)
            } else {
                binding.rvBookMark.visibility = View.GONE
                binding.NoFavourites.visibility = View.VISIBLE
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener(this)
    }

    private fun setAdapter(folderList: ArrayList<Folder>) {
        binding.apply {
            rvBookMark.layoutManager = GridLayoutManager(mContext, 2)
            rvBookMark.setHasFixedSize(true)
            adapter = BookMarkAdapter(mContext, folderList, this@MyBookMarkActivity)
            rvBookMark.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }


    override fun onClick(model: Folder) {
        startActivity(
            Intent(mContext, BookMarkListActivity::class.java).putExtra(
                "id",
                model.folder_id
            ).putExtra("name",model.folder_name)
        )
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.ivBack  ->{
                finish()
            }
        }
    }
}