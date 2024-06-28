package com.app.bollyhood.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.activity.ChatActivity
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.activity.Upload_CastingCall
import com.app.bollyhood.adapter.CastingCallListAdapter
import com.app.bollyhood.adapter.ImagesAdapter
import com.app.bollyhood.databinding.FragmentCastingCallApplyedBinding
import com.app.bollyhood.model.CastingCallModel
import com.app.bollyhood.model.PhotoModel
import com.app.bollyhood.model.UserModel
import com.app.bollyhood.util.DateUtils
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.bumptech.glide.Glide
import com.google.gson.Gson

class CastingCall_ApplyedFragment : Fragment(),OnClickListener,CastingCallListAdapter.onClickItems,
    ImagesAdapter.onItemClick {

    lateinit var binding: FragmentCastingCallApplyedBinding
    lateinit var castingCallModel: CastingCallModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_casting_call__applyed, container, false)

        initUi()
        addLitsner()
        return binding.root
    }

    private fun addLitsner() {
        binding.ivBack.setOnClickListener(this)
        binding.cvProfile.setOnClickListener(this)
    }

    private fun initUi()
    {
        (requireActivity() as MainActivity).showToolbar(false)
        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext()).load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)

        val bundle = arguments
        if (bundle!=null) {
            castingCallModel = Gson().fromJson(
                bundle.getString(StaticData.userModel),
                CastingCallModel::class.java
            )

            setData()
        }
    }

    private fun setData() {

        if (!castingCallModel.role.isNullOrEmpty()) {
            binding.tvcastingName.text = castingCallModel.role
        }

        if (!castingCallModel.modify_date.isNullOrEmpty()) {
            binding.tvTime.text = DateUtils.getConvertDateTiemFormat(castingCallModel.modify_date)
        }

        if (!castingCallModel.apply_casting_count.isNullOrEmpty()) {
            binding.tvcount.text = castingCallModel.apply_casting_count
        }else{
            binding.tvcount.visibility =View.GONE
            binding.tvcount.text = "0"
        }

        if (castingCallModel.company_logo.isNotEmpty()) {
            Glide.with(requireContext()).load(castingCallModel.company_logo)
                .into(binding.ivImage)
        }

        if (castingCallModel.users.get(0).apply_images.isNotEmpty()){
            setAdapter(castingCallModel.users)
        }else{
            binding.tvnoData.visibility=View.VISIBLE
            binding.rvcatingcallListed.visibility=View.GONE
        }
    }


    private fun setAdapter(applyedUserList:ArrayList<UserModel>)
    {
        binding.apply {
            rvcatingcallListed.layoutManager =
                LinearLayoutManager(requireContext())
            rvcatingcallListed.setHasFixedSize(true)
            adapter = CastingCallListAdapter(requireContext(),applyedUserList,this@CastingCall_ApplyedFragment)
            rvcatingcallListed.adapter = adapter
            adapter?.notifyDataSetChanged()

        }
    }

    override fun onClick(view: View?) {
        when(view?.id)
        {
            R.id.cvProfile ->{
                startActivity(Intent(requireContext(), MyProfileActivity::class.java))
            }

            R.id.llPostCastingCall ->{
                startActivity(Intent(requireContext(), Upload_CastingCall::class.java))
            }

            R.id.ivBack ->{
                (requireActivity() as MainActivity).setHomeColor()
            }
        }
    }


    private fun castingCallSuccessDialog(userModel: UserModel){
        val photoList: ArrayList<PhotoModel> = ArrayList()

        if (!userModel.apply_images.isNullOrEmpty()) {
            for (i in userModel.apply_images) {
                val model = PhotoModel(0, i)
                photoList.add(model)
            }
        }
        val dialogView = Dialog(requireContext())
        dialogView.setContentView(R.layout.dialog_casting_selection)
        dialogView.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialogView.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val recyclerphoto=dialogView.findViewById<RecyclerView>(R.id.rv_photo)
        val btn_fit=dialogView.findViewById<TextView>(R.id.tvfit)
        val btn_notFit=dialogView.findViewById<TextView>(R.id.tvNotfit)
        val btn_maybe=dialogView.findViewById<TextView>(R.id.tvMaybe)
        val tvName=dialogView.findViewById<TextView>(R.id.tvuser_name)
        val tvRequestText=dialogView.findViewById<TextView>(R.id.tvtext)

        tvName.setText(userModel.name+" Applied For Role")
        tvRequestText.setText("Accept Casting Request From ${userModel.name} ?")

        btn_fit.setOnClickListener(OnClickListener {
            startActivity(
                Intent(requireContext(), ChatActivity::class.java)
                    .putExtra("profileId", userModel.id)
                    .putExtra("msg", "Congratulations you are shortlisted for this position we contact with you soon")
            )
            dialogView.dismiss()
        })

        btn_notFit.setOnClickListener(OnClickListener {
            startActivity(
                Intent(requireContext(), ChatActivity::class.java)
                    .putExtra("profileId", userModel.id)
                    .putExtra("msg", "You are not fit for this position Best of Luck for future")
            )
            dialogView.dismiss()
        })

        btn_maybe.setOnClickListener(OnClickListener {
            dialogView.dismiss()
        })

        recyclerphoto.layoutManager = GridLayoutManager(requireContext(),3)
        recyclerphoto.setHasFixedSize(true)
        val adapter = ImagesAdapter(requireContext(), photoList,this )
        recyclerphoto.adapter = adapter
        adapter?.notifyDataSetChanged()

        dialogView.show()
    }

    override fun onClick(userModel: UserModel) {
        castingCallSuccessDialog(userModel)
    }

    override fun onRemoveImage(pos: Int, photoModel: PhotoModel) {

    }


}