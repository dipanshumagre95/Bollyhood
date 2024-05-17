package com.app.bollyhood.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.WorkAdapter
import com.app.bollyhood.databinding.ActivityProfileDetailBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.ExpertiseModel
import com.app.bollyhood.model.WorkLinks
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailActivity : AppCompatActivity(), WorkAdapter.onItemClick {

    lateinit var binding: ActivityProfileDetailBinding
    lateinit var mContext: ProfileDetailActivity
    private val viewModel: DataViewModel by viewModels()
    private var is_bookmark: Int = 0

    lateinit var expertiseModel: ExpertiseModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_detail)
        mContext = this
        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {

        if (intent.extras != null) {
            expertiseModel = Gson().fromJson(
                intent.getStringExtra(StaticData.userModel),
                ExpertiseModel::class.java
            )
            setData(expertiseModel)
        }

    }

    private fun addListner() {

        binding.llBack.setOnClickListener {
            finish()
        }
        binding.ivBookMark.setOnClickListener {
            if (isNetworkAvailable(mContext)) {
                if (expertiseModel.is_bookmarked == 0) {
                    viewModel.addRemoveBookMark(
                        PrefManager(mContext).getvalue(StaticData.id), expertiseModel.id, "1"
                    )
                } else {
                    removeBookMarkDialog()
                }
            } else {
                Toast.makeText(
                    mContext, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.llBook.setOnClickListener {
            startActivity(
                Intent(mContext, BookRequestActivity::class.java)
                    .putExtra(StaticData.userModel, Gson().toJson(expertiseModel))
            )


            /* if (isNetworkAvailable(mContext)) {
                 viewModel.addBook(PrefManager(mContext).getvalue(StaticData.id), "")
             } else {
                 Toast.makeText(
                     mContext, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                 ).show()
             }*/
        }

        binding.rrChat.setOnClickListener {
            startActivity(Intent(mContext, ChatActivity::class.java)
                .putExtra("profileId",expertiseModel.id))
        }

        binding.llCall.setOnClickListener {
            if (isNetworkAvailable(mContext)) {
                viewModel.checkSubscriptions(
                    PrefManager(mContext).getvalue(StaticData.id).toString()
                )

            } else {
                Toast.makeText(
                    mContext, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    private fun addObserevs() {
        /* viewModel.isLoading.observe(this, Observer {
             if (it) {
                 binding.pbLoadData.visibility = View.VISIBLE
             } else {
                 binding.pbLoadData.visibility = View.GONE
             }
         })

         viewModel.expertiseProfileLiveData.observe(this, Observer {
             if (it.status == "1") {
                 setData(it.result[0])
             } else {
                 Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
             }
         })
 */

        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })


        viewModel.addRemoveBookMarkLiveData.observe(this, Observer {
            if (it.status == "1") {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })


        viewModel.checkSubscriptionLiveData.observe(this, Observer {
            if (it.status == "1") {
                if (it.result.is_subscription == "0") {
                    startActivity(Intent(mContext, SubscriptionPlanActivity::class.java))
                }else{
                    if (expertiseModel.mobile.isNotEmpty()) {
                        val intent =
                            Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                expertiseModel.mobile, null))
                        startActivity(intent)
                    }

                }
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setData(expertiseModel: ExpertiseModel) {
        binding.apply {
            Glide.with(mContext).load(expertiseModel.image).into(ivImage)
            tvName.text = expertiseModel.name
            if (expertiseModel.is_verify == "1") {
                ivVerified.visibility = View.VISIBLE
            } else {
                ivVerified.visibility = View.GONE
            }


            val stringList = arrayListOf<String>()

            for (i in 0 until expertiseModel.categories.size) {
                stringList.add(expertiseModel.categories[i].category_name)
            }
            tvCategory.text = stringList.joinToString(separator = " / ")

            tvDescription.text = expertiseModel.description

            tvJobsDone.text = expertiseModel.jobs_done
            tvExperience.text = expertiseModel.experience
            tvReviews.text = expertiseModel.reviews

            is_bookmark = expertiseModel.is_bookmarked


            if (expertiseModel.category_type == "0") {
                llBook.visibility = View.INVISIBLE
                tvBook.visibility = View.GONE
            } else {
                if (expertiseModel.is_book == 1) {
                    llBook.visibility = View.INVISIBLE
                    tvBook.visibility = View.VISIBLE
                    tvBook.text = "You have already book this profile"
                } else {
                    llBook.visibility = View.VISIBLE
                    tvBook.visibility = View.GONE
                }

            }


            if (expertiseModel.is_bookmarked == 1) {
                ivBookMark.setImageResource(R.drawable.ic_addedbookmark)
            } else {
                ivBookMark.setImageResource(R.drawable.ic_bookmark)
            }

            rvWorkLinks.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            rvWorkLinks.setHasFixedSize(true)
            val adapter =
                WorkAdapter(mContext, expertiseModel.work_links, this@ProfileDetailActivity)
            rvWorkLinks.adapter = adapter
        }
    }

    override fun onClick(pos: Int, work: WorkLinks) {


        startActivity(
            Intent(mContext, YoutubeActivity::class.java).putExtra("videoId", work.worklink_url)
        )

        /*  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(work.worklink_url))
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          intent.setPackage("com.google.android.youtube")
          startActivity(intent)*/
    }

    private fun removeBookMarkDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to remove Bookmark?")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            if (isNetworkAvailable(mContext)) {
                viewModel.addRemoveBookMark(
                    PrefManager(mContext).getvalue(StaticData.id),
                    expertiseModel.id,
                    "2"
                )
            } else {
                Toast.makeText(
                    mContext, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                ).show()
            }
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

}