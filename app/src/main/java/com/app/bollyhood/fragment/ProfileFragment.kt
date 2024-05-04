package com.app.bollyhood.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.activity.CMSActivity
import com.app.bollyhood.activity.ChangePasswordActivity
import com.app.bollyhood.activity.LoginActivity
import com.app.bollyhood.activity.MyBookMarkActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.databinding.FragmentProfileBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val viewModel: DataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        initUI()
        addListner()
        addObsereves()
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
                startActivityForResult(Intent(requireContext(), MyProfileActivity::class.java), 201)
            }

            rrBookMark.setOnClickListener {
                startActivity(Intent(requireContext(), MyBookMarkActivity::class.java))
            }

            rrAboutUs.setOnClickListener {
                startActivity(
                    Intent(requireContext(), CMSActivity::class.java)
                        .putExtra("mFrom", "about-us")
                )
            }

            rrLogout.setOnClickListener {
                logoutDialog()

            }
        }
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.logoutLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                val fcmToken = PrefManager(requireContext()).getvalue(StaticData.fcmToken)
                PrefManager(requireContext()).clearValue()
                PrefManager(requireContext()).setvalue(StaticData.fcmToken, fcmToken)

                startActivity(Intent(requireContext(), LoginActivity::class.java))
                activity?.finishAffinity()
            } else {
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                binding.tvUserName.text = PrefManager(requireContext()).getvalue(StaticData.name)
                if (PrefManager(requireContext()).getvalue(StaticData.image)
                        ?.isNotEmpty() == true
                ) {
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

            if (isNetworkAvailable(requireContext())) {
                viewModel.doLogout(PrefManager(requireContext()).getvalue(StaticData.id).toString())


            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.str_error_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }
}