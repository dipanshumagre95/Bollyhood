package com.app.bollyhood.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.CastingBookMarkAdapter
import com.app.bollyhood.databinding.ActivityCastingBookMarkBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CastingCallModel
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CastingBookMarkActivity : AppCompatActivity() {

    lateinit var binding: ActivityCastingBookMarkBinding
    lateinit var mContext: CastingBookMarkActivity
    private val viewModel: DataViewModel by viewModels()
    private val castingBookMarkModels: ArrayList<CastingCallModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_casting_book_mark)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {

        if (isNetworkAvailable(mContext)) {
            viewModel.getCastingBookMarkCalls(
                PrefManager(mContext).getvalue(StaticData.id).toString()
            )
        } else {
            showCustomToast(this,StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
        }


    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }


    private fun addObsereves() {

        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.castingBookMarkCallsLiveData.observe(this, Observer {
            if (it.status.equals("1")) {
                castingBookMarkModels.clear()
                castingBookMarkModels.addAll(it.result.active)

                if (castingBookMarkModels.size > 0) {
                    binding.rvCastingCalls.visibility = View.VISIBLE
                    binding.tvNoFavourites.visibility = View.GONE
                    setData(castingBookMarkModels)

                } else {
                    binding.rvCastingCalls.visibility = View.GONE
                    binding.tvNoFavourites.visibility = View.VISIBLE
                }

            } else {
                binding.rvCastingCalls.visibility = View.GONE
                binding.tvNoFavourites.visibility = View.VISIBLE
            }
        })


    }

    private fun setData(castingModels: ArrayList<CastingCallModel>) {
        binding.apply {
            rvCastingCalls.layoutManager = LinearLayoutManager(mContext)
            rvCastingCalls.setHasFixedSize(true)
            adapter = CastingBookMarkAdapter(mContext, castingModels)
            rvCastingCalls.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }


}