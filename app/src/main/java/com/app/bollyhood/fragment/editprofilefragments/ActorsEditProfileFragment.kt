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
import android.widget.AutoCompleteTextView
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.activity.CMSActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.activity.MyProfileActivity.Companion.REQUEST_ID_MULTIPLE_PERMISSIONS
import com.app.bollyhood.adapter.WorkAdapter
import com.app.bollyhood.databinding.FragmentActorsEditProfileBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidDescriptions
import com.app.bollyhood.extensions.isvalidEmailAddress
import com.app.bollyhood.extensions.isvalidMobileNumber
import com.app.bollyhood.extensions.isvalidName
import com.app.bollyhood.extensions.isvalidTeamNCondition
import com.app.bollyhood.model.ProfileModel
import com.app.bollyhood.model.WorkLinkProfileData
import com.app.bollyhood.util.DialogsUtils
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.InbuildLIsts
import com.app.bollyhood.util.PathUtils
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ActorsEditProfileFragment : Fragment(), TextWatcher,WorkAdapter.onItemClick, OnClickListener {

    lateinit var binding: FragmentActorsEditProfileBinding
    private val viewModel: DataViewModel by viewModels()
    private lateinit var mContext: Context
    private var isCamera = false
    private var isGallery = false
    private var profilePath = ""
    private var category_Id: String = ""
    private var workLinkList: ArrayList<WorkLinkProfileData> = arrayListOf()
    private var imagesurl: ArrayList<String> = arrayListOf()
    private var passPortList: ArrayList<String> = arrayListOf()
    private val imageResultLaunchers = mutableMapOf<Int, ActivityResultLauncher<Intent>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_actors_edit_profile, container, false)
        mContext=requireContext()


        initUI()
        addListner()
        setHeightData()
        setSkinColorData()
        setBodyTypeData()
        setPassPortData()
        setAgeData()
        addObserevs()
        setSpannableString()
        return binding.root
    }

    private fun initUI() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getProfile(PrefManager(mContext).getvalue(StaticData.id).toString())
        } else {
            showCustomToast(requireContext(),StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
        }
        initializeImageResultLaunchers()
        binding.edtName.addTextChangedListener(this)
        binding.edtCategory.addTextChangedListener(this)
        binding.edtMobileNumber.addTextChangedListener(this)
        binding.edtDescriptions.addTextChangedListener(this)
        binding.edtEmailAddress.addTextChangedListener(this)
    }

    private fun addListner() {

        binding.apply {
            setupAutoCompleteTextView(acheight) { setHeightData() }
            setupAutoCompleteTextView(acSkinColor) { setSkinColorData() }
            setupAutoCompleteTextView(acBodyType) { setBodyTypeData() }
            setupAutoCompleteTextView(acPassPort) { setPassPortData() }
            setupAutoCompleteTextView(acage) { setAgeData() }

            tvAddWorkLink.setOnClickListener(this@ActorsEditProfileFragment)
            firstImage.setOnClickListener(this@ActorsEditProfileFragment)
            secondImage.setOnClickListener(this@ActorsEditProfileFragment)
            thirdImage.setOnClickListener(this@ActorsEditProfileFragment)
            fourthImage.setOnClickListener(this@ActorsEditProfileFragment)
            fifthimage.setOnClickListener(this@ActorsEditProfileFragment)
            siximage.setOnClickListener(this@ActorsEditProfileFragment)
            tvUpdateProfile.setOnClickListener(this@ActorsEditProfileFragment)
            ivBack.setOnClickListener(this@ActorsEditProfileFragment)
            rrProfile.setOnClickListener(this@ActorsEditProfileFragment)
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.tvAddWorkLink->{
                DialogsUtils.showWorkLinksDialog(
                    mContext,
                    workLinkList,
                    { updatedLinks ->
                        workLinkList.clear()
                        workLinkList.addAll(updatedLinks)
                        worklinkAdapter(binding.worklinkrecyclerview)
                    }
                )
            }

            R.id.firstImage->{
                if (checkPermission()){
                    dialogForImage(1)
                }else{
                    checkPermission()
                }
            }

            R.id.secondImage ->{
                if (checkPermission()){
                    dialogForImage(2)
                }else{
                    checkPermission()
                }
            }

            R.id.thirdImage ->{
                if (checkPermission()){
                    dialogForImage(3)
                }else{
                    checkPermission()
                }
            }

            R.id.fourthImage->{
                if (checkPermission()){
                    dialogForImage(4)
                }else{
                    checkPermission()
                }
            }

            R.id.fifthimage ->{
                if (checkPermission()){
                    dialogForImage(5)
                }else{
                    checkPermission()
                }
            }

            R.id.siximage ->{
                if (checkPermission()){
                    dialogForImage(6)
                }else{
                    checkPermission()
                }
            }

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

    private fun setHeightData() {

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, InbuildLIsts.getHeightList())
        binding.acheight.threshold = 0
        binding.acheight.dropDownVerticalOffset = 0
        binding.acheight.setAdapter(arrayAdapter)

        binding.acheight.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
            }
    }


    private fun setSkinColorData() {

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, InbuildLIsts.getSkinColorList())
        binding.acSkinColor.threshold = 0
        binding.acSkinColor.dropDownVerticalOffset = 0
        binding.acSkinColor.setAdapter(arrayAdapter)

        binding.acSkinColor.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
            }
    }

    private fun setBodyTypeData() {

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, InbuildLIsts.getBodyType())
        binding.acBodyType.threshold = 0
        binding.acBodyType.dropDownVerticalOffset = 0
        binding.acBodyType.setAdapter(arrayAdapter)

        binding.acBodyType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
            }
    }


    private fun setPassPortData() {
        passPortList.clear()
        passPortList.add("Yes")
        passPortList.add("No")

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, passPortList)
        binding.acPassPort.threshold = 0
        binding.acPassPort.dropDownVerticalOffset = 0
        binding.acPassPort.setAdapter(arrayAdapter)

        binding.acPassPort.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
            }
    }

    private fun setAgeData(){

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, InbuildLIsts.getAgeList())
        binding.acage.threshold = 0
        binding.acage.dropDownVerticalOffset = 0
        binding.acage.setAdapter(arrayAdapter)

        binding.acage.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
            }
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
                showCustomToast(requireContext(),StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })

        viewModel.updateProfileLiveData.observe(requireActivity()) {
            if (it.status == "1") {
                showCustomToast(requireContext(),StaticData.successMsg,it.msg,StaticData.success)
                setPrefData(it.result)
            } else {
                showCustomToast(requireContext(),StaticData.pleaseTryAgain,it.msg,StaticData.alert)
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
        binding.acage.setText(profileModel.age)
        binding.acheight.setText(profileModel.height)
        binding.acBodyType.setText(profileModel.body_type)
        binding.edtLocation.setText(profileModel.location)
        binding.acSkinColor.setText(profileModel.skin_color)
        binding.acPassPort.setText(profileModel.passport)

        if (!profileModel.work_links.isNullOrEmpty()) {
            for (i in 0 until profileModel.work_links.size) {
                val item = profileModel.work_links[i]
                val workLink = WorkLinkProfileData(item.worklink_name, item.worklink_url)
                workLinkList.add(workLink)
            }
            worklinkAdapter(binding.worklinkrecyclerview)
        }

        if (!profileModel.imagefile.isNullOrEmpty()) {
            val imageViews = listOf(binding.firstImage, binding.secondImage, binding.thirdImage, binding.fourthImage, binding.fifthimage, binding.siximage)

            for (i in 0 until minOf(profileModel.imagefile.size, 6)) {
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

    fun worklinkAdapter(view: RecyclerView){
        view.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        view.setHasFixedSize(true)
        val adapter =
            WorkAdapter(mContext, workLinkList, this)
        view.adapter = adapter
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

            showProgressBar(imageNumber, true)
            isCamera = true
            isGallery = false
            dialogView.dismiss()
        }

        txtGallery.setOnClickListener { v: View? ->
            ImagePickerUtil.pickImageFromGallery(requireActivity(),imageResultLaunchers[imageNumber] ?: return@setOnClickListener)

            showProgressBar(imageNumber, true)
            isCamera = false
            isGallery = true
            dialogView.dismiss()
        }

        txtCancel.setOnClickListener { v: View? -> dialogView.dismiss() }
        dialogView.show()
    }

    private fun showProgressBar(imageNumber: Int, isVisible: Boolean) {
        val progressBar = when (imageNumber) {
            1 -> binding.progressBar1
            2 -> binding.progressBar2
            3 -> binding.progressBar3
            4 -> binding.progressBar4
            5 -> binding.progressBar5
            6 -> binding.progressBar6
            else -> throw IllegalArgumentException("Invalid image number")
        }
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
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
                showProgressBar(imageNumber, false)
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

    private fun isValidYouTubeUrl(url: String): Boolean {
        val youtubeRegex = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+\$".toRegex()
        return youtubeRegex.matches(url)
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
                    binding.acheight.text.toString().trim()
                )

                val passport: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.acPassPort.text.toString().trim()
                )

                val body_type: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.acBodyType.text.toString().trim()
                )

                val age: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.acage.text.toString().trim()
                )

                val skin_color: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.acSkinColor.text.toString().trim()
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

                var imageBody: MultipartBody.Part?=null
                val imagefile: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
                for (image in imagesurl){
                    if (image.isNotEmpty()) {
                        val file = File(image)
                        if (file != null) {
                            val requestFile =
                                RequestBody.create(
                                    "multipart/form-data".toMediaTypeOrNull(),
                                    file
                                )

                            imageBody = MultipartBody.Part.createFormData(
                                "imagefile[]", file.name, requestFile
                            )
                            imagefile.add(imageBody)
                        }
                    }
                }

                val jsonArray = JSONArray()

                for (i in 0 until workLinkList.size) {
                    val jsonObject = JSONObject()
                    jsonObject.put("worklink_name", workLinkList[i].worklink_name)
                    jsonObject.put("worklink_url", workLinkList[i].worklink_url)
                    jsonArray.put(jsonObject)
                }


                val workLink: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), jsonArray.toString()
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
            showCustomToast(requireContext(),StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
        }
    }

    private fun setImage(imageNumber: Int, uri: Uri?) {
        when (imageNumber) {
            1 -> binding.firstImage.setImageURI(uri)
            2 -> binding.secondImage.setImageURI(uri)
            3 -> binding.thirdImage.setImageURI(uri)
            4 -> binding.fourthImage.setImageURI(uri)
            5 -> binding.fifthimage.setImageURI(uri)
            6 -> binding.siximage.setImageURI(uri)
            else -> throw IllegalArgumentException("Invalid image number")
        }
    }

    fun setupAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView, setData: () -> Unit) {
        autoCompleteTextView.setOnTouchListener { v, event ->
            setData()
            autoCompleteTextView.showDropDown()
            false
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
                        profilePath =PathUtils.getRealPath(requireContext(), uri!!).toString()
                        binding.cvProfile.setImageURI(uri)
                    }
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

}