package com.app.bollyhood.fragment.editprofilefragments

import ImagePickerUtil
import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.activity.CMSActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.activity.MyProfileActivity.Companion.REQUEST_ID_MULTIPLE_PERMISSIONS
import com.app.bollyhood.adapter.WorkAdapter
import com.app.bollyhood.databinding.FragmentShootLocationManagerEditProfileBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidDescriptions
import com.app.bollyhood.extensions.isvalidEmailAddress
import com.app.bollyhood.extensions.isvalidMobileNumber
import com.app.bollyhood.extensions.isvalidName
import com.app.bollyhood.extensions.isvalidTeamNCondition
import com.app.bollyhood.model.ProfileModel
import com.app.bollyhood.model.WorkLinkProfileData
import com.app.bollyhood.util.PathUtils
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ShootLocationManagerEditProfile : Fragment(), TextWatcher, WorkAdapter.onItemClick,
    OnClickListener {

    lateinit var binding: FragmentShootLocationManagerEditProfileBinding
    private val viewModel: DataViewModel by viewModels()
    private lateinit var mContext: Context
    private var isCamera = false
    private var isGallery = false
    private var profilePath = ""
    private var category_Id: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shoot_location_manager_edit_profile, container, false)
        mContext=requireContext()


        initUI()
        addListner()
        addObserevs()
        setSpannableString()
        return binding.root
    }

    private fun initUI() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getProfile(PrefManager(mContext).getvalue(StaticData.id).toString())
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.edtName.addTextChangedListener(this)
        binding.edtCategory.addTextChangedListener(this)
        binding.edtMobileNumber.addTextChangedListener(this)
        binding.edtDescriptions.addTextChangedListener(this)
        binding.edtEmailAddress.addTextChangedListener(this)
    }

    private fun addListner() {
        binding.apply {
            tvUpdateProfile.setOnClickListener(this@ShootLocationManagerEditProfile)
            ivBack.setOnClickListener(this@ShootLocationManagerEditProfile)
            rrProfile.setOnClickListener(this@ShootLocationManagerEditProfile)
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.tvUpdateProfile->{
                updateActorProfile()
            }

            R.id.ivBack ->{
                (requireActivity() as? MyProfileActivity)?.closeActivity()
            }

            R.id.rrProfile->{
                if (checkPermission()) {
                    alertDialogForImagePicker()
                } else {
                    checkPermission()
                }
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (charSequence != null && charSequence.length >= 1) {
            when(charSequence.hashCode()){
                binding.edtName.getText().hashCode() ->{
                    binding.lledtName.setHintEnabled(true)
                }

                binding.edtCategory.getText().hashCode() ->{
                    binding.llCategory.setHintEnabled(true)
                }

                binding.edtMobileNumber.getText().hashCode() ->{
                    binding.llphonenumber.setHintEnabled(true)
                }

                binding.edtDescriptions.text.hashCode() ->{
                    binding.textcount.visibility=View.VISIBLE
                    val value = 250 - binding.edtDescriptions.text.toString().length
                    binding.textcount.text="$value/250"
                }

                binding.edtEmailAddress.getText().hashCode() ->{
                    binding.llEmail.setHintEnabled(true)
                }
            }
        }else if (charSequence?.length!! <= 0){
            when(charSequence.hashCode()){
                binding.edtName.getText().hashCode() ->{
                    binding.lledtName.setHintEnabled(false)
                }

                binding.edtCategory.getText().hashCode() ->{
                    binding.llCategory.setHintEnabled(false)
                }

                binding.edtMobileNumber.getText().hashCode() ->{
                    binding.llphonenumber.setHintEnabled(false)
                }

                binding.textcount.text.hashCode() ->{
                    binding.textcount.visibility=View.GONE
                }

                binding.edtEmailAddress.getText().hashCode() ->{
                    binding.llEmail.setHintEnabled(false)
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    private fun addObserevs() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.profileLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                val profileModel = it.result
                setProfileData(profileModel)
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.updateProfileLiveData.observe(requireActivity()) {
            if (it.status == "1") {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                setPrefData(it.result)
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setPrefData(result: ProfileModel) {
        PrefManager(mContext).setvalue(StaticData.isLogin, true)
        PrefManager(mContext).setvalue(StaticData.id, result.id)
        PrefManager(mContext).setvalue(StaticData.name, result.name)
        PrefManager(mContext).setvalue(StaticData.email, result.email)
        PrefManager(mContext).setvalue(StaticData.mobile, result.mobile)
        PrefManager(mContext).setvalue(StaticData.image, result.image)
    }

    private fun setProfileData(profileModel: ProfileModel) {
        if (!profileModel.image.isNullOrEmpty()) {
            Glide.with(mContext).load(profileModel.image).error(R.drawable.ic_profile)
                .error(R.drawable.ic_profile).into(requireActivity().findViewById(R.id.cvProfile))
        }
        binding.edtName.setText(profileModel.name)
        binding.edtEmailAddress.setText(profileModel.email)
        binding.edtMobileNumber.setText(profileModel.mobile)
        binding.edtDescriptions.setText(profileModel.description)
        binding.edtCategory.setText(profileModel.categories[0].category_name)
        category_Id = profileModel.categories[0].category_id
        binding.edtLocation.setText(profileModel.location)
    }

    private fun checkPermission(): Boolean {
        val permissionsNeeded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.CAMERA to ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA),
                Manifest.permission.READ_MEDIA_IMAGES to ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_MEDIA_IMAGES)
            )
        } else {
            listOf(
                Manifest.permission.CAMERA to ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA),
                Manifest.permission.WRITE_EXTERNAL_STORAGE to ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        }

        val listPermissionsNeeded = permissionsNeeded
            .filter { it.second != PackageManager.PERMISSION_GRANTED }
            .map { it.first }

        return if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(), listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            false
        } else {
            true
        }
    }


    private fun saveImageToStorage(bitmap: Bitmap): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir: File? = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return try {
            val imageFile = File(storageDir, imageFileName)
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.close()
            imageFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun setSpannableString() {
        val spanTxt = SpannableStringBuilder(
            "By Updating an account, you agree to our"
        )

        spanTxt.setSpan(
            ForegroundColorSpan(mContext.getColor(R.color.black)),
            0,
            spanTxt.length,
            0
        )

        spanTxt.append(" Term of service")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(
                    Intent(mContext, CMSActivity::class.java)
                        .putExtra("mFrom","terms-condition")
                )
            }
        }, spanTxt.length - " Term of service".length, spanTxt.length, 0)


        spanTxt.setSpan(
            ForegroundColorSpan(requireContext().getColor(R.color.black)),
            spanTxt.length - " Term of service".length,
            spanTxt.length,
            0
        )

        binding.tvteamCondition.setMovementMethod(LinkMovementMethod.getInstance())
        binding.tvteamCondition.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    override fun onitemClick(pos: Int, work: WorkLinkProfileData) {

    }

    private fun updateActorProfile()
    {
        if (isNetworkAvailable(mContext)) {
            if (isvalidName(
                    mContext, binding.edtName.text.toString().trim()
                ) && isvalidEmailAddress(
                    mContext, binding.edtEmailAddress.text.toString().trim()
                ) && isvalidMobileNumber(
                    mContext, binding.edtMobileNumber.text.toString().trim()
                ) && isvalidDescriptions(
                    mContext, binding.edtDescriptions.text.toString().trim()
                ) && isvalidTeamNCondition(mContext,binding.cbteamNcondition.isChecked)
            ) {

                val name: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtName.text.toString().trim()
                )
                val email: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtEmailAddress.text.toString().trim()
                )

                val mobileNumber: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtMobileNumber.text.toString().trim()
                )


                val user_Id: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    PrefManager(mContext).getvalue(StaticData.id).toString()
                )

                val cat_Id: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), "1"
                )


                val desc: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtDescriptions.text.toString().trim()
                )

                val categoryId: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), category_Id
                )

                val location: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtLocation.text.toString().trim()
                )

                val height: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    ""
                )

                val passport: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    ""
                )

                val body_type: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    ""
                )

                val age: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    ""
                )

                val skin_color: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    ""
                )


                var profileBody: MultipartBody.Part? = null
                if (profilePath.isNotEmpty()) {
                    val file = File(profilePath)
                    // create RequestBody instance from file
                    val requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    profileBody = MultipartBody.Part.createFormData(
                        "image", file.name, requestFile
                    )
                }

                val imagefile: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()

                val workLink: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), ""
                )

                viewModel.updateProfile(
                    name,
                    email,
                    user_Id,
                    cat_Id,
                    mobileNumber,
                    desc,
                    height,
                    passport,
                    body_type,
                    skin_color,
                    age,
                    location,
                    workLink,
                    categoryId,
                    profileBody,
                    imagefile
                )


            }
        } else {
            Toast.makeText(
                mContext,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun alertDialogForImagePicker() {
        val dialogView = Dialog(requireContext())
        dialogView.setContentView(R.layout.image_picker)
        dialogView.setCancelable(false)
        val txtcamera = dialogView.findViewById<TextView>(R.id.txtcamera)
        val txtGallery = dialogView.findViewById<TextView>(R.id.txtGallery)
        val txtCancel = dialogView.findViewById<TextView>(R.id.txtCancel)
        txtcamera.setOnClickListener { v: View? ->
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startForProfileImageResult.launch(intent)

            isCamera = true
            isGallery = false
            dialogView.dismiss()
        }
        txtGallery.setOnClickListener { v: View? ->
            ImagePickerUtil.pickImageFromGallery(requireActivity(),startForProfileImageResult)
            isCamera = false
            isGallery = true
            dialogView.dismiss()
        }
        txtCancel.setOnClickListener { v: View? -> dialogView.dismiss() }
        dialogView.show()
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    if (isCamera) {
                        val imageBitmap = result.data?.extras?.get("data") as Bitmap
                        profilePath = saveImageToStorage(imageBitmap).toString()
                        binding.cvProfile.setImageURI(Uri.parse(profilePath))
                    } else if (isGallery) {
                        val uri = data!!.data
                        profilePath = PathUtils.getRealPath(requireContext(), uri!!).toString()
                        binding.cvProfile.setImageURI(uri)
                    }
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

}