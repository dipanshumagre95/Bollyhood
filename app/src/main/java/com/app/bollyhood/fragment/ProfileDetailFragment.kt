package com.app.bollyhood.fragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.SubscriptionPlanActivity
import com.app.bollyhood.activity.YoutubeActivity
import com.app.bollyhood.adapter.ImagesAdapter
import com.app.bollyhood.adapter.WorkAdapter
import com.app.bollyhood.databinding.FragmentProfileDetailBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.ExpertiseModel
import com.app.bollyhood.model.PhotoModel
import com.app.bollyhood.model.SingleCategoryModel
import com.app.bollyhood.model.WorkLinkProfileData
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailFragment : Fragment(),WorkAdapter.onItemClick,OnClickListener,ImagesAdapter.onItemClick {

    lateinit var binding: FragmentProfileDetailBinding
    private val viewModel: DataViewModel by viewModels()
    private var is_bookmark: Int = 0
    private var expertiseModel: ExpertiseModel?=null
    private var singleCategoryModel: SingleCategoryModel? =null
    lateinit var previousFragment:String
    private var photolist:ArrayList<PhotoModel> = arrayListOf()
    var expanded:Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as MainActivity).showToolbar(false)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_profile_detail, container, false)

        initUI()
        addListner()
        addObserevs()

        return binding.root
    }

    private fun initUI() {
        val bundle = arguments
        if (bundle!=null&&bundle?.getString(StaticData.previousFragment)=="AllActosFragment"){
            singleCategoryModel = Gson().fromJson(
                bundle.getString(StaticData.userModel),
                SingleCategoryModel::class.java
            )
            previousFragment = bundle.getString(StaticData.previousFragment).toString()
            setDataforProfile(singleCategoryModel)
        }else {
            if (bundle != null) {
                expertiseModel = Gson().fromJson(
                    bundle.getString(StaticData.userModel),
                    ExpertiseModel::class.java
                )
                previousFragment = bundle.getString(StaticData.previousFragment).toString()
                setData(expertiseModel)
            }
        }

    }

    private fun setData(expertiseModel: ExpertiseModel?) {

        binding.apply {
            Glide.with(requireContext()).load(expertiseModel?.image).placeholder(R.drawable.ic_profile).into(ivImage)
            tvName.text = expertiseModel?.name
            if (expertiseModel?.is_verify == "1") {
                ivVerified.visibility = View.VISIBLE
            } else {
                ivVerified.visibility = View.GONE
            }


            val stringList = arrayListOf<String>()

            for (i in 0 until expertiseModel?.categories?.size!!) {
                stringList.add(expertiseModel?.categories?.get(i)?.category_name!!)
            }
            tvCategory.text = stringList.joinToString(separator = " / ")

            if (expertiseModel.description.length > 150){
                val shorttext=expertiseModel.description.substring(0,150)
                setSpannableString(shorttext,"read more",expertiseModel.description)
            }else{
                binding.tvDescription.text = expertiseModel.description
            }



            is_bookmark = expertiseModel.is_bookmarked

            if (expertiseModel.is_bookmarked == 1) {
                ivBookMark.setImageResource(R.drawable.ic_addedbookmark)
            } else {
                ivBookMark.setImageResource(R.drawable.ic_bookmark)
            }

            rvWorkLinks.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvWorkLinks.setHasFixedSize(true)
            val adapter =
                WorkAdapter(requireContext(), expertiseModel.work_links, this@ProfileDetailFragment)
            rvWorkLinks.adapter = adapter
        }
    }

    private fun setDataforProfile(singleCategoryModel: SingleCategoryModel?) {

        binding.apply {
            Glide.with(requireContext()).load(singleCategoryModel?.image).centerCrop().placeholder(R.drawable.ic_profile).into(ivImage)
            tvName.text = singleCategoryModel?.name
            tvage.text=singleCategoryModel?.age
            tvbodytype.text=singleCategoryModel?.body_type
            tvheight.text=singleCategoryModel?.height
            skinColor.text=singleCategoryModel?.skin_color
            tvpassport.text=singleCategoryModel?.passport
            tvlocation.text=singleCategoryModel?.location
            if (singleCategoryModel?.is_verify == "1") {
                ivVerified.visibility = View.VISIBLE
            } else {
                ivVerified.visibility = View.GONE
            }


            val stringList = arrayListOf<String>()

            for (i in 0 until singleCategoryModel?.categories?.size!!) {
                stringList.add(singleCategoryModel?.categories?.get(i)?.category_name!!)
            }
            tvCategory.text = stringList.joinToString(separator = " / ")

            if (singleCategoryModel.description.length > 150){
                val shorttext=singleCategoryModel.description.substring(0,150)
                setSpannableString(shorttext,"read more",singleCategoryModel.description)
            }else{
                binding.tvDescription.text = singleCategoryModel.description
            }



            is_bookmark = singleCategoryModel.is_bookmarked

            if (singleCategoryModel.is_bookmarked == 1) {
                ivBookMark.setImageResource(R.drawable.ic_addedbookmark)
            } else {
                ivBookMark.setImageResource(R.drawable.ic_bookmark)
            }


            for (i in 0 until singleCategoryModel.imagefile.size){
                 photolist.add(PhotoModel(i, singleCategoryModel.imagefile.get(i)))
            }

            recyclerviewPhotos.layoutManager = GridLayoutManager(requireContext(),3)
            recyclerviewPhotos.setHasFixedSize(true)
            adapter = ImagesAdapter(requireContext(), photolist, this@ProfileDetailFragment)
            recyclerviewPhotos.adapter = adapter
            adapter?.notifyDataSetChanged()

            rvWorkLinks.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvWorkLinks.setHasFixedSize(true)
            val adapter =
                WorkAdapter(requireContext(), singleCategoryModel.work_links, this@ProfileDetailFragment)
            rvWorkLinks.adapter = adapter
        }
    }

    override fun onitemClick(pos: Int, work: WorkLinkProfileData) {
        startActivity(
            Intent(requireContext(), YoutubeActivity::class.java).putExtra("videoId", work.worklink_url)
        )
    }

    private fun addListner() {
        binding.llBack.setOnClickListener(this)
        binding.llbookmark.setOnClickListener(this)
        binding.llCall.setOnClickListener(this)
    }

    private fun addObserevs() {

        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.addRemoveBookMarkLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                if (it.msg.equals("Bookmark Successfully")){
                    binding.ivBookMark.setImageResource(R.drawable.ic_addedbookmark)
                }else{
                    binding.ivBookMark.setImageResource(R.drawable.ic_bookmark)
                }
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.checkSubscriptionLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                if (it.result.is_subscription == "0") {
                    startActivity(Intent(requireContext(), SubscriptionPlanActivity::class.java))
                }else{
                    if (expertiseModel?.mobile?.isNotEmpty()!!) {
                        val intent =
                            Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                expertiseModel?.mobile, null))
                        startActivity(intent)
                    }

                }
            } else {
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeBookMarkDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to remove Bookmark?")

        builder.setPositiveButton(getString(R.string.str_yes)) { dialog, which ->
            if (isNetworkAvailable(requireContext())) {
                viewModel.addRemoveBookMark(
                    PrefManager(requireContext()).getvalue(StaticData.id),
                    expertiseModel?.id,
                    "2"
                )
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                ).show()
            }
        }

        builder.setNegativeButton(getString(R.string.str_no)) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun setSpannableString(text: String, button: String, fulltext: String) {
        val spanTxt = SpannableStringBuilder(text).append(" $button")

        val start = spanTxt.length - button.length
        val end = spanTxt.length

        // Set the clickable span
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (expanded) {
                    setSpannableString(fulltext, "read less", text)
                    expanded=false
                }else{
                    setSpannableString(fulltext, "read more", text)
                    expanded=true
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // Optional: remove underline
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spanTxt.setSpan(ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvDescription.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDescription.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }


    override fun onClick(item: View?) {
        when(item?.id){

            R.id.llBack ->{
                if (previousFragment.equals("AllActosFragment")){
                    (requireActivity() as MainActivity).loadFragment(AllActorsFragment())
                }else {
                    (requireActivity() as MainActivity).loadFragment(HomeFragment())
                }
            }

            R.id.llbookmark ->{
                if (isNetworkAvailable(requireContext())) {
                    if (singleCategoryModel?.is_bookmarked == 0) {
                        viewModel.addRemoveBookMark(
                            PrefManager(requireContext()).getvalue(StaticData.id), expertiseModel?.id, "1"
                        )
                    } else {
                        removeBookMarkDialog()
                    }
                } else {
                    Toast.makeText(
                        requireContext(), getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                    ).show()
                }
            }

            R.id.llCall ->{
                if (isNetworkAvailable(requireContext())) {
                    viewModel.checkSubscriptions(
                        PrefManager(requireContext()).getvalue(StaticData.id).toString()
                    )
                } else {
                    Toast.makeText(
                        requireContext(), getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onRemoveImage(pos: Int, photoModel: PhotoModel) {

    }

}