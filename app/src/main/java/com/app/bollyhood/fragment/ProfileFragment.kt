package com.app.bollyhood.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.bollyhood.R
import com.app.bollyhood.activity.ChangePasswordActivity
import com.app.bollyhood.activity.LoginActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.databinding.FragmentProfileBinding
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.bumptech.glide.Glide
import kotlin.math.log

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        initUI()
        addListner()
        return binding.root
    }

    private fun initUI() {
        binding.tvUserName.text = PrefManager(requireContext()).getvalue(StaticData.name)

        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile).into(binding.cvProfile)
        }
    }

    private fun addListner() {
        binding.apply {
            rrprivacy.setOnClickListener {
                startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
            }
            rrProfile.setOnClickListener {
                startActivityForResult(Intent(requireContext(), MyProfileActivity::class.java),201)
            }

            rrLogout.setOnClickListener {
                logoutDialog()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==201){
            if (resultCode==Activity.RESULT_OK){
                binding.tvUserName.text = PrefManager(requireContext()).getvalue(StaticData.name)

                if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
                    Glide.with(requireContext())
                        .load(PrefManager(requireContext()).getvalue(StaticData.image))
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile).into(binding.cvProfile)
                }
            }
        }
    }

    private fun logoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to Logout?")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            PrefManager(requireContext()).clearValue()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finishAffinity()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }
}