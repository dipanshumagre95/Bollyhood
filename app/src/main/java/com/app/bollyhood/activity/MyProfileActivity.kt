package com.app.bollyhood.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.WorkAdapter
import com.app.bollyhood.databinding.ActivityMyProfileBinding
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
import com.github.dhaval2404.imagepicker.ImagePicker
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
class MyProfileActivity : AppCompatActivity(), TextWatcher,WorkAdapter.onItemClick{

    lateinit var binding: ActivityMyProfileBinding
    lateinit var mContext: MyProfileActivity
    private val viewModel: DataViewModel by viewModels()
    private var isCamera = false
    private var isGallery = false
    private var profilePath = ""
    private var category_Id: String = ""
    private var height: String = ""
    private var skinColor: String = ""
    private var bodyType: String = ""
    private var passport: String = ""
    private var workLinkList: ArrayList<WorkLinkProfileData> = arrayListOf()
    private var imagesurl: ArrayList<String> = arrayListOf()
    private var heightList: ArrayList<String> = arrayListOf()
    private var skinColorList: ArrayList<String> = arrayListOf()
    private var bodyTypeList: ArrayList<String> = arrayListOf()
    private var passPortList: ArrayList<String> = arrayListOf()
    private var ageList: ArrayList<String> = arrayListOf()
    private val imageResultLaunchers = mutableMapOf<Int, ActivityResultLauncher<Intent>>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)
        mContext = this
        initUI()
        addListner()
        setHeightData()
        setSkinColorData()
        setBodyTypeData()
        setPassPortData()
        setAgeData()
        addObserevs()
        setSpannableString()
    }

    companion object {
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2

    }


    private fun initUI() {
        viewModel.getProfile(PrefManager(mContext).getvalue(StaticData.id).toString())
        initializeImageResultLaunchers()
        binding.edtName.addTextChangedListener(this)
        binding.edtCategory.addTextChangedListener(this)
        binding.edtMobileNumber.addTextChangedListener(this)
        binding.edtDescriptions.addTextChangedListener(this)
        binding.edtEmailAddress.addTextChangedListener(this)
        binding.edtAchievements.addTextChangedListener(this)
        binding.edtLanguages.addTextChangedListener(this)
        binding.edtEvents.addTextChangedListener(this)
        binding.edtGenre.addTextChangedListener(this)
    }

    private fun addListner() {

        binding.apply {
            acheight.setOnTouchListener { v, event ->
                acheight.showDropDown()
                false
            }

            acSkinColor.setOnTouchListener { v, event ->
                acSkinColor.showDropDown()
                false
            }


            acBodyType.setOnTouchListener { v, event ->
                acBodyType.showDropDown()
                false
            }

            acPassPort.setOnTouchListener { v, event ->
                acPassPort.showDropDown()
                false
            }

            acage.setOnTouchListener { v, event ->
                acage.showDropDown()
                false
            }

            ivBack.setOnClickListener {
                finish()
            }

            tvAddWorkLink.setOnClickListener {
                addWorkLinksDialog()
            }

            binding.firstImage.setOnClickListener(View.OnClickListener {
                if (checkPermission()){
                dialogForImage(1)
                }else{
                    checkPermission()
                }
            })

            binding.secondImage.setOnClickListener(View.OnClickListener {
                if (checkPermission()){
                    dialogForImage(2)
                }else{
                    checkPermission()
                }
            })

            binding.thirdImage.setOnClickListener(View.OnClickListener {
                if (checkPermission()){
                    dialogForImage(3)
                }else{
                    checkPermission()
                }
            })

            binding.fourthImage.setOnClickListener(View.OnClickListener {
                if (checkPermission()){
                    dialogForImage(4)
                }else{
                    checkPermission()
                }
            })

            binding.fifthimage.setOnClickListener(View.OnClickListener {
                if (checkPermission()){
                    dialogForImage(5)
                }else{
                    checkPermission()
                }
            })

            binding.siximage.setOnClickListener(View.OnClickListener {
                if (checkPermission()){
                    dialogForImage(6)
                }else{
                    checkPermission()
                }
            })


            tvUpdateProfile.setOnClickListener {

                if (isNetworkAvailable(mContext)) {
                    workLinkList.clear()
                    if (isvalidName(
                            mContext, binding.edtName.text.toString().trim()
                        ) && isvalidEmailAddress(
                            mContext, binding.edtEmailAddress.text.toString().trim()
                        ) && isvalidMobileNumber(
                            mContext, binding.edtMobileNumber.text.toString().trim()
                        ) && isvalidDescriptions(
                            mContext, binding.edtDescriptions.text.toString().trim()
                        )&& isvalidTeamNCondition(mContext,binding.cbteamNcondition.isChecked)
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

                        val location:RequestBody=RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            binding.edtLocation.text.toString().trim()
                        )

                        val height:RequestBody=RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            binding.acheight.text.toString().trim()
                        )

                        val passport:RequestBody=RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            binding.acPassPort.text.toString().trim()
                        )

                        val body_type:RequestBody=RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            binding.acBodyType.text.toString().trim()
                        )

                        val age:RequestBody=RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            binding.acage.text.toString().trim()
                        )

                        val skin_color:RequestBody=RequestBody.create(
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

                        var imageBody:MultipartBody.Part?=null
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
                    Toast.makeText(
                        mContext,
                        getString(R.string.str_error_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            rrProfile.setOnClickListener {
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

                binding.edtAchievements.getText().hashCode() ->{
                    binding.llAchievements.setHintEnabled(true)
                }

                binding.edtLanguages.getText().hashCode() ->{
                    binding.llLanguages.setHintEnabled(true)
                }

                binding.edtEvents.getText().hashCode() ->{
                    binding.llEvents.setHintEnabled(true)
                }

                binding.edtGenre.getText().hashCode() ->{
                    binding.llGenre.setHintEnabled(true)
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

                binding.edtAchievements.getText().hashCode() ->{
                    binding.llAchievements.setHintEnabled(false)
                }

                binding.edtLanguages.getText().hashCode() ->{
                    binding.llLanguages.setHintEnabled(false)
                }

                binding.edtEvents.getText().hashCode() ->{
                    binding.llEvents.setHintEnabled(false)
                }

                binding.edtGenre.getText().hashCode() ->{
                    binding.llGenre.setHintEnabled(false)
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    private fun setHeightData() {
        heightList.clear()
        heightList.add("4.7 Ft")
        heightList.add("4.8 Ft")
        heightList.add("4.9 Ft")
        heightList.add("5.0 Ft")
        heightList.add("5.1 Ft")
        heightList.add("5.2 Ft")
        heightList.add("5.3 Ft")
        heightList.add("5.4 Ft")
        heightList.add("5.5 Ft")
        heightList.add("5.6 Ft")
        heightList.add("5.7 Ft")
        heightList.add("5.8 Ft")
        heightList.add("5.9 Ft")
        heightList.add("6.0 Ft")
        heightList.add("6.1 Ft")
        heightList.add("6.2 Ft")
        heightList.add("6.3 Ft")
        heightList.add("6.4 Ft")
        heightList.add("6.5 Ft")
        heightList.add("6.6 Ft")
        heightList.add("6.7 Ft")


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, heightList)
        binding.acheight.threshold = 0
        binding.acheight.dropDownVerticalOffset = 0
        binding.acheight.setAdapter(arrayAdapter)

        binding.acheight.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                height = heightList[position]
            }


    }


    private fun setSkinColorData() {
        skinColorList.clear()
        skinColorList.add("Light Brown")
        skinColorList.add("Medium Brown")
        skinColorList.add("Dark Brown")
        skinColorList.add("Fair")
        skinColorList.add("Wheatish")


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, skinColorList)
        binding.acSkinColor.threshold = 0
        binding.acSkinColor.dropDownVerticalOffset = 0
        binding.acSkinColor.setAdapter(arrayAdapter)

        binding.acSkinColor.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                skinColor = skinColorList[position]
            }


    }

    private fun setBodyTypeData() {
        bodyTypeList.clear()
        bodyTypeList.add("Thin")
        bodyTypeList.add("Average Build")
        bodyTypeList.add("Muscular or Fit ( Gym-Goer )")
        bodyTypeList.add("Big or Healthy")


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, bodyTypeList)
        binding.acBodyType.threshold = 0
        binding.acBodyType.dropDownVerticalOffset = 0
        binding.acBodyType.setAdapter(arrayAdapter)

        binding.acBodyType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                bodyType = bodyTypeList[position]
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
                passport = passPortList[position]
            }


    }

    private fun setAgeData(){
        ageList.clear()
        ageList.add("18")
        ageList.add("19")
        ageList.add("20")
        ageList.add("21")
        ageList.add("22")
        ageList.add("23")
        ageList.add("24")
        ageList.add("25")
        ageList.add("26")
        ageList.add("27")
        ageList.add("28")
        ageList.add("29")
        ageList.add("30")
        ageList.add("31")
        ageList.add("32")
        ageList.add("33")
        ageList.add("34")
        ageList.add("35")
        ageList.add("36")
        ageList.add("37")
        ageList.add("38")
        ageList.add("39")
        ageList.add("40")
        ageList.add("41")
        ageList.add("42")
        ageList.add("43")
        ageList.add("44")
        ageList.add("45")
        ageList.add("46")
        ageList.add("47")
        ageList.add("48")
        ageList.add("49")
        ageList.add("50")
        ageList.add("51")
        ageList.add("52")
        ageList.add("53")
        ageList.add("54")
        ageList.add("55")
        ageList.add("56")
        ageList.add("57")
        ageList.add("58")
        ageList.add("59")
        ageList.add("60")

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, ageList)
        binding.acage.threshold = 0
        binding.acage.dropDownVerticalOffset = 0
        binding.acage.setAdapter(arrayAdapter)

        binding.acage.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                passport = ageList[position]
            }
    }

    private fun addObserevs() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.profileLiveData.observe(this, Observer {
            if (it.status == "1") {
                val profileModel = it.result
                setProfileData(profileModel)
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.updateProfileLiveData.observe(this) {
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

        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    private fun setProfileData(profileModel: ProfileModel) {
        if (!profileModel.image.isNullOrEmpty()) {
            Glide.with(mContext).load(profileModel.image).error(R.drawable.ic_profile)
                .error(R.drawable.ic_profile).into(binding.cvProfile)
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
            workLinkList.addAll(profileModel.work_links)
            worklinkAdapter()
        }

        if (!profileModel.imagefile.isNullOrEmpty()) {
            val imageViews = listOf(binding.firstImage, binding.secondImage, binding.thirdImage, binding.fourthImage, binding.fifthimage, binding.siximage)

            for (i in 0 until profileModel.imagefile.size) {
                Glide.with(mContext).load(profileModel.imagefile[i])
                    .error(R.drawable.upload_to_the_cloud_svg)
                    .centerCrop()
                    .into(imageViews[i])
            }
        }
    }

    private fun checkPermission(): Boolean {
        val writePermission: Int
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            writePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)


        } else {
            writePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        }


        val listPermissionsNeeded = ArrayList<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }

            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA)
            }

        } else {
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA)
            }

        }




        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms = HashMap<String, Int>()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED

                    perms[Manifest.permission.READ_MEDIA_IMAGES] = PackageManager.PERMISSION_GRANTED

                } else {
                    perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED

                    perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                        PackageManager.PERMISSION_GRANTED

                }

                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) perms[permissions[i]] = grantResults[i]
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.READ_MEDIA_IMAGES] == PackageManager.PERMISSION_GRANTED) {

                            alertDialogForImagePicker()
                        } else {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.READ_MEDIA_IMAGES
                                )
                            ) {
                                ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.READ_MEDIA_IMAGES
                                )
                            } else {
                            }
                        }

                    } else {
                        if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {

                            alertDialogForImagePicker()
                        } else {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            } else {
                            }
                        }

                    }


                }
            }
        }

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
                          profilePath =PathUtils.getRealPath(this, uri!!).toString()
                        binding.cvProfile.setImageURI(uri)
                    }
                }

                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun saveImageToStorage(bitmap: Bitmap): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

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


    fun worklinkAdapter(){
        binding.worklinkrecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.worklinkrecyclerview.setHasFixedSize(true)
        val adapter =
            WorkAdapter(this, workLinkList, this)
        binding.worklinkrecyclerview.adapter = adapter
    }


    private fun addWorkLinksDialog(){
        val dialogView = Dialog(this)
        dialogView.setContentView(R.layout.add_work_link)
        val workLinkName1=dialogView.findViewById<EditText>(R.id.edtWorkLinkName1)
        val workLinkName2=dialogView.findViewById<EditText>(R.id.edtWorkLinkName2)
        val workLinkName3=dialogView.findViewById<EditText>(R.id.edtWorkLinkName3)
        val linkName1=dialogView.findViewById<TextView>(R.id.edtAddWorkLink1)
        val linkName2=dialogView.findViewById<TextView>(R.id.edtAddWorkLink2)
        val linkName3=dialogView.findViewById<TextView>(R.id.edtAddWorkLink3)
        val addbutton=dialogView.findViewById<TextView>(R.id.tvAddLinks)
        val closebutton=dialogView.findViewById<ImageView>(R.id.close)

        if (workLinkList.size==3){
            Toast.makeText(this,"You have already add three workLinks",Toast.LENGTH_SHORT).show()
            dialogView.dismiss()
        }else if(workLinkList.size==2){
            workLinkName3.visibility=View.GONE
            linkName3.visibility=View.GONE
            workLinkName2.visibility=View.GONE
            linkName2.visibility=View.GONE
        }else if (workLinkList.size==1){
            workLinkName3.visibility=View.GONE
            linkName3.visibility=View.GONE
        }

        closebutton.setOnClickListener(View.OnClickListener {
            dialogView.dismiss()
        })

        addbutton.setOnClickListener(View.OnClickListener {
            if (!workLinkName1.text.isNullOrEmpty()|| !workLinkName2.text.isNullOrEmpty()|| !workLinkName3.text.isNullOrEmpty()) {
                if (!workLinkName1.text.isNullOrEmpty() && !linkName1.text.isNullOrEmpty()) {
                    workLinkList.add(
                        WorkLinkProfileData(
                            workLinkName1.text.toString(),
                            linkName1.text.toString()
                        )
                    )
                }

                if (!workLinkName2.text.isNullOrEmpty() && !linkName2.text.isNullOrEmpty()) {
                    workLinkList.add(
                        WorkLinkProfileData(
                            workLinkName2.text.toString(),
                            linkName2.text.toString()
                        )
                    )
                }

                if (!workLinkName2.text.isNullOrEmpty() && !linkName3.text.isNullOrEmpty()) {
                    workLinkList.add(
                        WorkLinkProfileData(
                            workLinkName3.text.toString(),
                            linkName3.text.toString()
                        )
                    )
                }
                worklinkAdapter()
                dialogView.dismiss()
            }else{
                Toast.makeText(this,"Add Atlest One WorkLink",Toast.LENGTH_SHORT).show()
            }
        })

        dialogView.show()
    }

    private fun alertDialogForImagePicker() {
        val dialogView = Dialog(this)
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
            ImagePicker.with(mContext).compress(1024).maxResultSize(1080, 1080).galleryOnly()
                .createIntent {
                    startForProfileImageResult.launch(it)
                }
            isCamera = false
            isGallery = true
            dialogView.dismiss()
        }
        txtCancel.setOnClickListener { v: View? -> dialogView.dismiss() }
        dialogView.show()
    }


    private fun setSpannableString() {
        val spanTxt = SpannableStringBuilder(
            "By creating an account, you agree to our"
        )

        spanTxt.setSpan(
            ForegroundColorSpan(getColor(R.color.black)),
            0,
            spanTxt.length,
            0
        )

        spanTxt.append(" Term of service")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(
                    Intent(mContext,CMSActivity::class.java)
                        .putExtra("mFrom","terms-condition")
                )
            }
        }, spanTxt.length - " Term of service".length, spanTxt.length, 0)


        spanTxt.setSpan(
            ForegroundColorSpan(getColor(R.color.black)),
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
        val dialogView = Dialog(this)
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
            ImagePicker.with(mContext).compress(1024).maxResultSize(1080, 1080).galleryOnly()
                .createIntent {
                    imageResultLaunchers[imageNumber]?.launch(it)
                }

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
                    imagesurl.add(uri?.path.toString())
                    setImage(imageNumber, uri)
                }
            }

            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }

            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
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

}