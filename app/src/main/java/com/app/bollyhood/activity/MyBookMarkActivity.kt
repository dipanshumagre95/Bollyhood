package com.app.bollyhood.activity

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.BookMarkAdapter
import com.app.bollyhood.databinding.ActivityMyBookMarkBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.BookMarkModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
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
        addObserevs()
    }

    private fun initUI() {
    }


    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable(mContext)) {
            viewModel.myBookMark(PrefManager(mContext).getvalue(StaticData.id))
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun addListner() {
        binding.ivBack.setOnClickListener(this)
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
                    binding.tvNoFavourites.visibility = View.GONE
                    binding.rvBookMark.visibility = View.VISIBLE
                    setAdapter(bookMarkList)
                } else {
                    binding.tvNoFavourites.visibility = View.VISIBLE
                    binding.rvBookMark.visibility = View.GONE
                }
            } else {
                binding.tvNoFavourites.visibility = View.VISIBLE
                binding.rvBookMark.visibility = View.GONE
            }
        })
    }

    private fun setAdapter(bookMarkList: ArrayList<BookMarkModel>) {
        binding.apply {
            rvBookMark.layoutManager = GridLayoutManager(mContext, 2)
            rvBookMark.setHasFixedSize(true)
            adapter = BookMarkAdapter(mContext, bookMarkList, this@MyBookMarkActivity)
            rvBookMark.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }


    override fun onClick(position: Int, model: BookMarkModel) {
        /*startActivity(
            Intent(mContext, ProfileDetailActivity::class.java).putExtra(
                StaticData.userModel,
                Gson().toJson(model)
            )
        )*/
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.ivBack  ->{
                finish()
            }
        }
    }
}