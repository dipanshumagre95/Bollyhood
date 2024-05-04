package com.app.bollyhood.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.CastingCallsAdapter
import com.app.bollyhood.databinding.ActivityCastingCallsBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CastingCallModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CastingCallsActivity : AppCompatActivity() {

    lateinit var binding: ActivityCastingCallsBinding
    lateinit var mContext: CastingCallsActivity
    private val castingModels: ArrayList<CastingCallModel> = arrayListOf()
    private val viewModel: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_casting_calls)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {

        binding.tvName.text = "Hello " + PrefManager(mContext).getvalue(StaticData.name)
        if (PrefManager(mContext).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(mContext).load(PrefManager(mContext).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }



        setData(castingModels)
    }


    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable(mContext)) {
            viewModel.getCastingCalls(PrefManager(mContext).getvalue(StaticData.id).toString())
        } else {
            Toast.makeText(
                mContext,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

    }
    private fun addListner() {

    }

    private fun addObsereves() {

        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.castingCallsLiveData.observe(this, Observer {
            if (it.status.equals("1")) {
                castingModels.clear()
                castingModels.addAll(it.result)
                setData(castingModels)
            }
        })


    }

    private fun setData(castingModels: ArrayList<CastingCallModel>) {
        binding.apply {
            rvCastingCalls.layoutManager = LinearLayoutManager(mContext)
            rvCastingCalls.setHasFixedSize(true)
            adapter = CastingCallsAdapter(mContext, castingModels)
            rvCastingCalls.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(mContext,MainActivity::class.java))
        finishAffinity()
    }
}