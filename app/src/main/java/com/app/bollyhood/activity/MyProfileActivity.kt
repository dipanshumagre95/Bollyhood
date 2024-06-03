package com.app.bollyhood.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityMyProfileBinding
import com.app.bollyhood.model.WorkLinkData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyProfileActivity : AppCompatActivity(), TextWatcher {

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
    private var workLinkList: ArrayList<WorkLinkData> = arrayListOf()
    private var heightList: ArrayList<String> = arrayListOf()
    private var skinColorList: ArrayList<String> = arrayListOf()
    private var bodyTypeList: ArrayList<String> = arrayListOf()
    private var passPortList: ArrayList<String> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)
        mContext = this

        binding.edtName.addTextChangedListener(this)
        binding.edtCategory.addTextChangedListener(this)
        binding.edtDescriptions.addTextChangedListener(this)
        binding.edtMobileNumber.addTextChangedListener(this)
        binding.edtEmail.addTextChangedListener(this)
        binding.edtAchievements.addTextChangedListener(this)
        binding.edtLanguages.addTextChangedListener(this)
        binding.edtEvents.addTextChangedListener(this)
        binding.edtGenre.addTextChangedListener(this)


        binding.ivBack.setOnClickListener(View.OnClickListener {
            binding.profilesinger.visibility=View.VISIBLE
            binding.profileactors.visibility=View.GONE
        })
       /* initUI()
        addListner()
        setHeightData()
        setSkinColorData()
        setBodyTypeData()
        setPassPortData()
        addObserevs()*/
        setSpannableString()
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

                binding.edtDescriptions.getText().hashCode() ->{
                    binding.llDescriptions.setHintEnabled(true)
                }

                binding.edtMobileNumber.getText().hashCode() ->{
                    binding.llphonenumber.setHintEnabled(true)
                }

                binding.edtEmail.getText().hashCode() ->{
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

                binding.edtDescriptions.getText().hashCode() ->{
                    binding.llDescriptions.setHintEnabled(false)
                }

                binding.edtMobileNumber.getText().hashCode() ->{
                    binding.llphonenumber.setHintEnabled(false)
                }

                binding.edtEmail.getText().hashCode() ->{
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

    /*   companion object {
           private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2

       }


       private fun initUI() {

           viewModel.getProfile(PrefManager(mContext).getvalue(StaticData.id).toString())
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



               ivBack.setOnClickListener {
                   finish()
               }

               tvAddWorkLink.setOnClickListener {
                   addNewView()
               }

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
                           )
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

                           val jobsDone: RequestBody = RequestBody.create(
                               "multipart/form-data".toMediaTypeOrNull(),
                               binding.edtJobsDone.text.toString().trim()
                           )

                           val experience: RequestBody = RequestBody.create(
                               "multipart/form-data".toMediaTypeOrNull(),
                               binding.edtExperience.text.toString().trim()
                           )

                           val reviews: RequestBody = RequestBody.create(
                               "multipart/form-data".toMediaTypeOrNull(), "5"
                           )


                           val categoryId: RequestBody = RequestBody.create(
                               "multipart/form-data".toMediaTypeOrNull(), category_Id
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


                           val count = binding.parentLinearLayout.childCount
                           var v: View?

                           for (i in 0 until count) {
                               v = binding.parentLinearLayout.getChildAt(i)

                               val edtWorkLinkName: EditText = v.findViewById(R.id.edtWorkLinkName)
                               val edtWorkLinkUrl: EditText = v.findViewById(R.id.edtAddWorkLink)

                               // create an object of Language class
                               val workLink = edtWorkLinkName.text.toString().trim()
                               val url = edtWorkLinkUrl.text.toString().trim()

                               // add the data to arraylist
                               workLinkList.add(WorkLinkData(workLink, url))

                           }

                           val jsonArray = JSONArray()

                           for (i in 0 until workLinkList.size) {
                               val jsonObject = JSONObject()
                               jsonObject.put("worklink_name", workLinkList[i].name)
                               jsonObject.put("worklink_url", workLinkList[i].workLinkUrl)
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
                               jobsDone,
                               experience,
                               reviews,
                               workLink,
                               categoryId,
                               profileBody,
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

       private fun setHeightData() {
           heightList.clear()
           heightList.add("4.2 Ft")
           heightList.add("4.3 Ft")
           heightList.add("4.4 Ft")
           heightList.add("4.5 Ft")
           heightList.add("4.6 Ft")
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


       private fun addNewView() {
           // this method inflates the single item layout
           // inside the parent linear layout
           val inflater = LayoutInflater.from(this).inflate(R.layout.add_work_link, null)
           binding.parentLinearLayout.addView(inflater, binding.parentLinearLayout.childCount)

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
           binding.edtJobsDone.setText(profileModel.jobs_done)
           binding.edtExperience.setText(profileModel.experience)
           binding.edtCategory.setText(profileModel.categories[0].category_name)
           category_Id = profileModel.categories[0].category_id


           // binding.parentLinearLayout.childCount = profileModel.work_links.size

           var v: View
           // binding.parentLinearLayout.addView(inflater,count)


           for (i in 0 until profileModel.work_links.size) {
               v = LayoutInflater.from(this).inflate(R.layout.add_work_link, null)

               val edtWorkLinkName: EditText = v.findViewById(R.id.edtWorkLinkName)
               val edtWorkLinkUrl: EditText = v.findViewById(R.id.edtAddWorkLink)
               val llMain: LinearLayout = v.findViewById(R.id.llMain)

               edtWorkLinkName.setText(profileModel.work_links[i].worklink_name)
               edtWorkLinkUrl.setText(profileModel.work_links[i].worklink_url)


               llMain.layoutParams =
                   LinearLayout.LayoutParams(
                       LinearLayout.LayoutParams.MATCH_PARENT,
                       LinearLayout.LayoutParams.WRAP_CONTENT
                   )
               llMain.orientation = LinearLayout.HORIZONTAL

               //  textView = TextView(this)
               // textView.setText(s.get(i))
               if (edtWorkLinkName.getParent() != null && edtWorkLinkUrl.parent != null) {
                   (edtWorkLinkName.getParent() as LinearLayout).removeView(edtWorkLinkName) // <- fix
                   (edtWorkLinkUrl.getParent() as LinearLayout).removeView(edtWorkLinkUrl) // <- fix
               }

               llMain.addView(edtWorkLinkName)
               llMain.addView(edtWorkLinkUrl)


               binding.parentLinearLayout.addView(llMain)
               // tViews.add(textView)
           }


           *//*
                for (i in 0 until count) {
                    v = binding.parentLinearLayout.getChildAt(i)

                    val edtWorkLinkName: EditText = v.findViewById(R.id.edtWorkLinkName)
                    val edtWorkLinkUrl: EditText = v.findViewById(R.id.edtAddWorkLink)

                    edtWorkLinkName.setText(profileModel.work_links[i].worklink_name)
                    edtWorkLinkUrl.setText(profileModel.work_links[i].worklink_url)

                }
        *//*


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
                        profilePath = uri!!.path.toString()
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
    }*/

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

}