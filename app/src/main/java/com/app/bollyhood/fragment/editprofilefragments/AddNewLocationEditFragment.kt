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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
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
import com.app.bollyhood.databinding.FragmentAddNewLocationEditBinding
import com.app.bollyhood.model.ProfileModel
import com.app.bollyhood.model.WorkLinkProfileData
import com.app.bollyhood.util.PathUtils
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddNewLocationEditFragment : Fragment(), TextWatcher, WorkAdapter.onItemClick,
    OnClickListener {

    lateinit var binding: FragmentAddNewLocationEditBinding
    private val viewModel: DataViewModel by viewModels()
    lateinit var mContext: Context
    private var isCamera = false
    private var isGallery = false
    private var isEdit=false
    private var category_Id: String = ""
    private var shiftTimeList: ArrayList<String> = arrayListOf()
    private var depositList: ArrayList<String> = arrayListOf()
    private var imagesurl: ArrayList<String> = arrayListOf()
    private val imageResultLaunchers = mutableMapOf<Int, ActivityResultLauncher<Intent>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_location_edit, container, false)
        mContext=requireContext()


        initUI()
        addListner()
        addObserevs()
        setShiftTimeData()
        setSpannableString()
        setSecurityDepositData()
        return binding.root
    }

    private fun initUI() {
        val bundle = arguments
        if (bundle!=null) {
            isEdit=if(bundle.getString(StaticData.edit).toString()=="edit")true else false
        }else{
            //viewModel.getProfile(PrefManager(mContext).getvalue(StaticData.id).toString())
        }
        initializeImageResultLaunchers()
        binding.edtName.addTextChangedListener(this)
        binding.edtDescriptions.addTextChangedListener(this)
        binding.edtEmailAddress.addTextChangedListener(this)
        binding.edtLocation.addTextChangedListener(this)
        binding.edtAcCount.addTextChangedListener(this)
        binding.edtAmount.addTextChangedListener(this)
        binding.edtParkings.addTextChangedListener(this)
    }

    private fun addListner() {

        binding.apply {
            tvUpdateProfile.setOnClickListener(this@AddNewLocationEditFragment)
            firstImage.setOnClickListener(this@AddNewLocationEditFragment)
            secondimage.setOnClickListener(this@AddNewLocationEditFragment)
            thirdimage.setOnClickListener(this@AddNewLocationEditFragment)
            fourthImage.setOnClickListener(this@AddNewLocationEditFragment)
            fifthimage.setOnClickListener(this@AddNewLocationEditFragment)
            siximage.setOnClickListener(this@AddNewLocationEditFragment)
            ivBack.setOnClickListener(this@AddNewLocationEditFragment)
        }

        binding.acSecurityDeposit.setOnTouchListener { v, event ->
            binding.acSecurityDeposit.showDropDown()
            false
        }

        binding.acShiftstype.setOnTouchListener { v, event ->
            binding.acShiftstype.showDropDown()
            false
        }
    }

    private fun setShiftTimeData() {
        shiftTimeList.clear()
        shiftTimeList.add("12Hr")
        shiftTimeList.add("24Hr")

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.dropdown, shiftTimeList)
        binding.acShiftstype.threshold = 0
        binding.acShiftstype.dropDownVerticalOffset = 0
        binding.acShiftstype.setAdapter(arrayAdapter)

        binding.acShiftstype.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                shiftTimeList[position]
            }
    }

    private fun setSecurityDepositData() {
        depositList.clear()
        depositList.add("12Hr")
        depositList.add("24Hr")

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.dropdown, depositList)
        binding.acSecurityDeposit.threshold = 0
        binding.acSecurityDeposit.dropDownVerticalOffset = 0
        binding.acSecurityDeposit.setAdapter(arrayAdapter)

        binding.acSecurityDeposit.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                depositList[position]
            }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.firstImage->{
                if (checkPermission()){
                    dialogForImage(1)
                }else{
                    checkPermission()
                }
            }

            R.id.secondimage->{
                if (checkPermission()){
                    dialogForImage(2)
                }else{
                    checkPermission()
                }
            }

            R.id.thirdimage->{
                if (checkPermission()){
                    dialogForImage(3)
                }else{
                    checkPermission()
                }
            }

            R.id.fourthImage->{
                if (checkPermission()){
                    dialogForImage(1)
                }else{
                    checkPermission()
                }
            }

            R.id.fifthimage->{
                if (checkPermission()){
                    dialogForImage(2)
                }else{
                    checkPermission()
                }
            }

            R.id.siximage->{
                if (checkPermission()){
                    dialogForImage(3)
                }else{
                    checkPermission()
                }
            }

            R.id.ivBack ->{
                (requireActivity() as MyProfileActivity).loadFragment(ShootLocationManagerEditProfile())
            }

            R.id.tvUpdateProfile->{
                if (isEdit){

                }else{

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

                binding.edtDescriptions.text.hashCode() ->{
                    binding.textcount.visibility=View.VISIBLE
                    val value = 250 - binding.edtDescriptions.text.toString().length
                    binding.textcount.text="$value/250"
                }

                binding.edtEmailAddress.getText().hashCode() ->{
                    binding.llEmail.setHintEnabled(true)
                }

                binding.edtLocation.getText().hashCode() ->{
                    binding.llLocation.setHintEnabled(true)
                }

                binding.edtAcCount.getText().hashCode() ->{
                    binding.llAcCount.setHintEnabled(true)
                }

                binding.edtAmount.getText().hashCode() ->{
                    binding.llAmount.setHintEnabled(true)
                }

                binding.edtParkings.getText().hashCode() ->{
                    binding.llParkings.setHintEnabled(true)
                }
            }
        }else if (charSequence?.length!! <= 0){
            when(charSequence.hashCode()){
                binding.edtName.getText().hashCode() ->{
                    binding.lledtName.setHintEnabled(false)
                }

                binding.textcount.text.hashCode() ->{
                    binding.textcount.visibility=View.GONE
                }

                binding.edtEmailAddress.getText().hashCode() ->{
                    binding.llEmail.setHintEnabled(false)
                }

                binding.edtLocation.getText().hashCode() ->{
                    binding.llLocation.setHintEnabled(false)
                }

                binding.edtAcCount.getText().hashCode() ->{
                    binding.llAcCount.setHintEnabled(false)
                }

                binding.edtAmount.getText().hashCode() ->{
                    binding.llAmount.setHintEnabled(false)
                }

                binding.edtParkings.getText().hashCode() ->{
                    binding.llParkings.setHintEnabled(false)
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
                setShootLocationData(profileModel)
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setShootLocationData(profileModel: ProfileModel) {
        if (!profileModel.image.isNullOrEmpty()) {
            Glide.with(mContext).load(profileModel.image).error(R.drawable.ic_profile)
                .error(R.drawable.ic_profile).into(requireActivity().findViewById(R.id.cvProfile))
        }

        binding.edtName.setText(profileModel.name)
        binding.edtEmailAddress.setText(profileModel.email)
        binding.edtDescriptions.setText(profileModel.description)
        category_Id = profileModel.categories[0].category_id
        binding.edtParkings.setText(profileModel.achievements)
        binding.edtLocation.setText(profileModel.events)
        binding.acShiftstype.setText("")
        binding.acSecurityDeposit.setText(profileModel.events)

        if (!profileModel.imagefile.isNullOrEmpty()) {
            val imageViews = listOf(binding.firstImage, binding.secondimage, binding.thirdimage,binding.fourthImage,binding.fifthimage,binding.siximage)

            for (i in 0 until profileModel.imagefile.size) {
                Glide.with(mContext).load(profileModel.imagefile[i])
                    .error(R.drawable.upload_to_the_cloud_svg)
                    .centerCrop()
                    .into(imageViews[i])
            }
        }
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
            ForegroundColorSpan(mContext.getColor(R.color.black)),
            spanTxt.length - " Term of service".length,
            spanTxt.length,
            0
        )

        binding.tvteamCondition.setMovementMethod(LinkMovementMethod.getInstance())
        binding.tvteamCondition.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    override fun onitemClick(pos: Int, work: WorkLinkProfileData) {

    }

    private fun dialogForImage(imageNumber: Int) {
        val dialogView = Dialog(mContext)
        dialogView.setContentView(R.layout.image_picker)
        dialogView.setCancelable(false)

        val txtCamera = dialogView.findViewById<TextView>(R.id.txtcamera)
        val txtGallery = dialogView.findViewById<TextView>(R.id.txtGallery)
        val txtCancel = dialogView.findViewById<TextView>(R.id.txtCancel)

        txtCamera.setOnClickListener { v: View? ->
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imageResultLaunchers[imageNumber]?.launch(intent)

            isCamera = true
            isGallery = false
            dialogView.dismiss()
        }

        txtGallery.setOnClickListener { v: View? ->
            ImagePickerUtil.pickImageFromGallery(requireActivity(),imageResultLaunchers[imageNumber] ?: return@setOnClickListener)

            isCamera = false
            isGallery = true
            dialogView.dismiss()
        }

        txtCancel.setOnClickListener { v: View? -> dialogView.dismiss() }
        dialogView.show()
    }

    private fun initializeImageResultLaunchers() {
        for (i in 1..6) {
            val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                handleImageResult(result, i)
            }
            imageResultLaunchers[i] = launcher
        }
    }

    private fun handleImageResult(result: ActivityResult, imageNumber: Int) {
        val resultCode = result.resultCode
        val data = result.data

        when (resultCode) {
            Activity.RESULT_OK -> {
                if (isCamera) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val image = saveImageToStorage(imageBitmap).toString()
                    imagesurl.add(image)
                    setImage(imageNumber, Uri.parse(image))
                } else if (isGallery) {
                    val uri = data?.data
                    imagesurl.add(PathUtils.getRealPath(mContext, uri!!).toString())
                    setImage(imageNumber, uri)
                }
            }

            else -> {
                Toast.makeText(mContext, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setImage(imageNumber: Int, uri: Uri?) {
        when (imageNumber) {
            1 -> binding.firstImage.setImageURI(uri)
            2 -> binding.secondimage.setImageURI(uri)
            3 -> binding.thirdimage.setImageURI(uri)
            4 -> binding.fourthImage.setImageURI(uri)
            5 -> binding.fifthimage.setImageURI(uri)
            6 -> binding.siximage.setImageURI(uri)
            else -> throw IllegalArgumentException("Invalid image number")
        }
    }

}