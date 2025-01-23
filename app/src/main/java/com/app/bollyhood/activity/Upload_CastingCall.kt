package com.app.bollyhood.activity

import ImagePickerUtil
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
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityUploadCastingCallBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidField
import com.app.bollyhood.extensions.isvalidProductionHouseName
import com.app.bollyhood.extensions.isvalidUploadProfile
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
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
class Upload_CastingCall : AppCompatActivity(),TextWatcher,OnClickListener {

    lateinit var binding:ActivityUploadCastingCallBinding
    private val viewModel: DataViewModel by viewModels()
    private var heightList: ArrayList<String> = arrayListOf()
    private var daysList: ArrayList<String> = arrayListOf()
    private var shiftTimeList: ArrayList<String> = arrayListOf()
    private var genderList: ArrayList<String> = arrayListOf()
    private var skinColorList: ArrayList<String> = arrayListOf()
    private var bodyTypeList: ArrayList<String> = arrayListOf()
    private var passPortList: ArrayList<String> = arrayListOf()
    private var castingFeeList: ArrayList<String> = arrayListOf()
    private var ageList: ArrayList<String> = arrayListOf()
    private var height: String = ""
    private var gender: String = ""
    private var shift: String = ""
    private var skinColor: String = ""
    private var bodyType: String = ""
    private var passport: String = ""
    private var is_verify_profile="1"
    private var age: String = ""
    private var isCamera = false
    private var isGallery = false
    private var profilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil.setContentView(this, R.layout.activity_upload_casting_call)

        intiUi()
        addLisnter()
        addObserver()
    }

    private fun addObserver() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.castingUploadedLiveData.observe(this, Observer{
            if (it.status == "1") {
                Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun intiUi()
    {
        setHeightData()
        setSkinColorData()
        setBodyTypeData()
        setPassPortData()
        setAgeData()
        setShiftTimeData()
        setGenderData()
        setamoutData()
        setCastingFeeData()
    }

    private fun addLisnter()
    {
        binding.acheight.setOnTouchListener { v, event ->
            binding.acheight.showDropDown()
            false
        }

        binding.acChooseType.setOnTouchListener { v, event ->
            binding.acChooseType.showDropDown()
            false
        }

        binding.acshift.setOnTouchListener { v, event ->
            binding.acshift.showDropDown()
            false
        }

        binding.acgender.setOnTouchListener { v, event ->
            binding.acgender.showDropDown()
            false
        }

        binding.acPassPort.setOnTouchListener { v, event ->
            binding.acPassPort.showDropDown()
            false
        }

        binding.acCastingFeesPplicable.setOnTouchListener { v, event ->
            binding.acCastingFeesPplicable.showDropDown()
            false
        }


        binding.acage.setOnTouchListener { v, event ->
            binding.acage.showDropDown()
            false
        }

        binding.acSkinColor.setOnTouchListener { v, event ->
            binding.acSkinColor.showDropDown()
            false
        }

        binding.acBodyType.setOnTouchListener { v, event ->
            binding.acBodyType.showDropDown()
            false
        }

        binding.edtproductionHouse.addTextChangedListener(this)
        binding.edttitle.addTextChangedListener(this)
        binding.edtSkillNRequiment.addTextChangedListener(this)
        binding.edtWhatYouRole.addTextChangedListener(this)
        binding.llverifiedProfile.setOnClickListener(this)
        binding.llanyoneApply.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.tvUpdateProfile.setOnClickListener(this)
        binding.rrProfile.setOnClickListener(this)

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (charSequence != null && charSequence.length >= 1) {
            when(charSequence.hashCode()){
                binding.edtproductionHouse.getText().hashCode() ->{
                    binding.lledtProductionHouse.setHintEnabled(true)
                }

                binding.edttitle.getText().hashCode() ->{
                    binding.lltitle.setHintEnabled(true)
                }

                binding.edtWhatYouRole.text.hashCode() ->{
                    binding.textcount.visibility=View.VISIBLE
                    val value = 500 - binding.edtWhatYouRole.text.toString().length
                    binding.textcount.text="$value/500"
                }

                binding.edtSkillNRequiment.text.hashCode() ->{
                    binding.textcountforskillNrequiment.visibility=View.VISIBLE
                    val value = 500 - binding.edtSkillNRequiment.text.toString().length
                    binding.textcountforskillNrequiment.text="$value/500"
                }
            }
        }else if (charSequence?.length!! <= 0){
            when(charSequence.hashCode()){
                binding.edtproductionHouse.getText().hashCode() ->{
                    binding.lledtProductionHouse.setHintEnabled(false)
                }

                binding.edttitle.getText().hashCode() ->{
                    binding.lltitle.setHintEnabled(false)
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }


    private fun setShiftTimeData() {
        shiftTimeList.clear()
        shiftTimeList.add("6Hr")
        shiftTimeList.add("7Hr")
        shiftTimeList.add("8Hr")
        shiftTimeList.add("9Hr")
        shiftTimeList.add("10Hr")
        shiftTimeList.add("11Hr")
        shiftTimeList.add("12Hr")
        shiftTimeList.add("13Hr")
        shiftTimeList.add("14Hr")
        shiftTimeList.add("15Hr")
        shiftTimeList.add("16Hr")
        shiftTimeList.add("17Hr")
        shiftTimeList.add("18Hr")
        shiftTimeList.add("19Hr")
        shiftTimeList.add("20Hr")
        shiftTimeList.add("21Hr")
        shiftTimeList.add("22Hr")
        shiftTimeList.add("23Hr")
        shiftTimeList.add("24Hr")

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, shiftTimeList)
        binding.acshift.threshold = 0
        binding.acshift.dropDownVerticalOffset = 0
        binding.acshift.setAdapter(arrayAdapter)

        binding.acshift.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                shift = shiftTimeList[position]
            }
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
            ArrayAdapter<String>(this, R.layout.dropdown, heightList)
        binding.acheight.threshold = 0
        binding.acheight.dropDownVerticalOffset = 0
        binding.acheight.setAdapter(arrayAdapter)

        binding.acheight.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                height = heightList[position]
            }
    }

    private fun setamoutData() {
        daysList.clear()
        daysList.add("Project Basis")
        daysList.add("Day Basis")


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, daysList)
        binding.acChooseType.threshold = 0
        binding.acChooseType.dropDownVerticalOffset = 0
        binding.acChooseType.setAdapter(arrayAdapter)

        binding.acChooseType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                if (daysList[position].equals("Project Basis"))
                {
                    binding.rrperday.visibility=View.GONE
                }else{
                    binding.rrperday.visibility=View.VISIBLE
                }
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
            ArrayAdapter<String>(this, R.layout.dropdown, skinColorList)
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
            ArrayAdapter<String>(this, R.layout.dropdown, bodyTypeList)
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
            ArrayAdapter<String>(this, R.layout.dropdown, passPortList)
        binding.acPassPort.threshold = 0
        binding.acPassPort.dropDownVerticalOffset = 0
        binding.acPassPort.setAdapter(arrayAdapter)

        binding.acPassPort.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                passport = passPortList[position]
            }
    }

    private fun setCastingFeeData() {
        castingFeeList.clear()
        castingFeeList.add("Yes")
        castingFeeList.add("No")


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, castingFeeList)
        binding.acCastingFeesPplicable.threshold = 0
        binding.acCastingFeesPplicable.dropDownVerticalOffset = 0
        binding.acCastingFeesPplicable.setAdapter(arrayAdapter)

        binding.acCastingFeesPplicable.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                passport = castingFeeList[position]
            }
    }

    private fun setGenderData() {
        genderList.clear()
        genderList.add("Male")
        genderList.add("Female")


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, genderList)
        binding.acgender.threshold = 0
        binding.acgender.dropDownVerticalOffset = 0
        binding.acgender.setAdapter(arrayAdapter)

        binding.acgender.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                gender = genderList[position]
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
            ArrayAdapter<String>(this, R.layout.dropdown, ageList)
        binding.acage.threshold = 0
        binding.acage.dropDownVerticalOffset = 0
        binding.acage.setAdapter(arrayAdapter)

        binding.acage.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                age = ageList[position]
            }
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.llverified_profile ->{
                binding.llverifiedProfile.setBackgroundResource(R.drawable.rectangle_black_button)
                binding.llanyoneApply.setBackgroundResource(R.drawable.border_gray)
                binding.tvVerifiedProfile.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.tvAnyoneCanApply.setTextColor(ContextCompat.getColor(this, R.color.black))
                is_verify_profile="1"
            }

            R.id.llanyone_apply ->{
                binding.llanyoneApply.setBackgroundResource(R.drawable.rectangle_black_button)
                binding.llverifiedProfile.setBackgroundResource(R.drawable.border_gray)
                binding.tvVerifiedProfile.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.tvAnyoneCanApply.setTextColor(ContextCompat.getColor(this, R.color.white))
                is_verify_profile="2"
            }

            R.id.ivBack ->{
                finish()
            }

            R.id.tvUpdateProfile ->{
                createCastingCallApi()
            }

            R.id.rrProfile ->{
                if (checkPermission()) {
                    alertDialogForImagePicker()
                } else {
                    checkPermission()
                }
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
                this, listPermissionsNeeded.toTypedArray(),
                MyProfileActivity.REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    private fun createCastingCallApi() {
        if (isNetworkAvailable(this)) {
            if (isvalidProductionHouseName(
                    this, binding.edtproductionHouse.text.toString().trim()
                )&& isvalidField(
                    this,binding.edttitle.text.toString().trim(),
                    "Please enter Casting Title"
                )&&isvalidUploadProfile(
                    this,profilePath
                ) &&isvalidField(
                    this,binding.edtSkillNRequiment.text.toString().trim()
                    ,"Please enter Skill & Requiment"
                )&&isvalidField(
                    this,binding.edtWhatYouRole.text.toString().trim()
                    ,"Please enter Role"
                )
            ) {

                val uid: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    PrefManager(this).getvalue(StaticData.id).toString()
                )

                val company_name: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtproductionHouse.text.toString().trim()
                )

                val role: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edttitle.text.toString().trim()
                )

                val organization: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtWhatYouRole.text.toString().trim()
                )

                val requirement: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtSkillNRequiment.text.toString().trim()
                )


                val shifting: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.acshift.text.toString().trim()
                )

                val gender: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.acgender.text.toString().trim()
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

                val skin_clor: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.acSkinColor.text.toString().trim()
                )

                val price: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtperday.text.toString().trim()
                )

                val priceType: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.acChooseType.text.toString().trim()
                )


                val castingFeeType: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.acCastingFeesPplicable.text.toString().trim()
                )

                val is_verify_casting: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    is_verify_profile
                )


                var company_logo: MultipartBody.Part? = null
                if (profilePath.isNotEmpty()) {
                    val file = File(profilePath)
                    // create RequestBody instance from file
                    val requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    company_logo = MultipartBody.Part.createFormData(
                        "company_logo", file.name, requestFile
                    )
                }

                viewModel.uploadCastingCall(
                    uid,
                    company_name,
                    organization,
                    requirement,
                    shifting,
                    gender,
                    location,
                    height,
                    passport,
                    body_type,
                    skin_clor,
                    age,
                    price,
                    role,
                    priceType,
                    castingFeeType,
                    is_verify_casting,
                    company_logo
                )
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
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
            ImagePickerUtil.pickImageFromGallery(this,startForProfileImageResult)
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
                        profilePath = getPath( uri!!)
                        binding.cvProfile.setImageURI(uri)
                    }
                }


                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    fun getPath(uri: Uri): String {
        var result = ""
        if (uri.toString().startsWith("file:")) {
            result = uri.path!!
        } else {
            val projections = arrayOf(MediaStore.Images.Media.DATA)
           contentResolver.query(uri, projections, null, null, null)?.let { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(projections.first())
                    if (index != -1) result = cursor.getString(index)?.let { it } ?: kotlin.run { "" }
                }
                cursor.close()
            }
        }
        return result
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
}