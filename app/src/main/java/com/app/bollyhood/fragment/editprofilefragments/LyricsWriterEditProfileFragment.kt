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
import android.widget.EditText
import android.widget.ImageView
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.activity.CMSActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.activity.MyProfileActivity.Companion.REQUEST_ID_MULTIPLE_PERMISSIONS
import com.app.bollyhood.adapter.WorkAdapter
import com.app.bollyhood.databinding.FragmentLyricsWriterEditProfileBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.ProfileModel
import com.app.bollyhood.model.VideoLink
import com.app.bollyhood.model.WorkLinkProfileData
import com.app.bollyhood.util.DialogsUtils
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PathUtils
import com.app.bollyhood.util.PrefManager
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
class LyricsWriterEditProfileFragment : Fragment(), TextWatcher, WorkAdapter.onItemClick,
    OnClickListener {

    lateinit var binding: FragmentLyricsWriterEditProfileBinding
    private val viewModel: DataViewModel by viewModels()
    private lateinit var mContext: Context
    private var isCamera = false
    private var isGallery = false
    private var profilePath = ""
    private var category_Id: String = ""
    private var categories:String=""
    private var workLinkList: ArrayList<WorkLinkProfileData> = arrayListOf()
    private var showreelLinkList: ArrayList<VideoLink> = arrayListOf()
    private var showreelLinkListClone: ArrayList<WorkLinkProfileData> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lyrics_writer_edit_profile, container, false)
        mContext = requireContext()

        initUI()
        addListner()
        addObserevs()
        setSpannableString()
        return binding.root
    }

    private fun initUI() {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getProfile(PrefManager(mContext).getvalue(StaticData.id).toString())
        }else{
            showCustomToast(requireContext(),StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
        }
        binding.edtName.addTextChangedListener(this)
        binding.edtCategory.addTextChangedListener(this)
        binding.edtMobileNumber.addTextChangedListener(this)
        binding.edtDescriptions.addTextChangedListener(this)
        binding.edtEmailAddress.addTextChangedListener(this)
        binding.edtLocation.addTextChangedListener(this)
        binding.edtLanguages.addTextChangedListener(this)
        binding.edtExperience.addTextChangedListener(this)
        binding.edtSongsTypes.addTextChangedListener(this)
    }

    private fun addListner() {

        binding.apply {
            tvAddShowreel.setOnClickListener(this@LyricsWriterEditProfileFragment)
            tvAddSingerWorkLink.setOnClickListener(this@LyricsWriterEditProfileFragment)
            tvUpdateProfile.setOnClickListener(this@LyricsWriterEditProfileFragment)
            ivBack.setOnClickListener(this@LyricsWriterEditProfileFragment)
            rrProfile.setOnClickListener(this@LyricsWriterEditProfileFragment)
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.tvAddShowreel->{
                addShowreelDialog(false)
            }

            R.id.tvAddSingerWorkLink->{
                DialogsUtils.showWorkLinksDialog(
                    mContext,
                    workLinkList,
                    { updatedLinks ->
                        workLinkList.clear()
                        workLinkList.addAll(updatedLinks)
                        worklinkAdapter(binding.singerworklinkrecyclerview)
                    }
                )
            }

            R.id.tvUpdateProfile->{
                //  updateSingerProfile()
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

                binding.edtLocation.getText().hashCode() ->{
                    binding.llLocation.setHintEnabled(true)
                }

                binding.edtLanguages.getText().hashCode() ->{
                    binding.llLanguages.setHintEnabled(true)
                }

                binding.edtExperience.getText().hashCode() ->{
                    binding.llExperience.setHintEnabled(true)
                }

                binding.edtSongsTypes.getText().hashCode() ->{
                    binding.llSongsType.setHintEnabled(true)
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

                binding.edtLocation.getText().hashCode() ->{
                    binding.llLocation.setHintEnabled(false)
                }

                binding.edtLanguages.getText().hashCode() ->{
                    binding.llLanguages.setHintEnabled(false)
                }

                binding.edtExperience.getText().hashCode() ->{
                    binding.llExperience.setHintEnabled(false)
                }

                binding.edtSongsTypes.getText().hashCode() ->{
                    binding.llSongsType.setHintEnabled(false)
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
                setSingerProfileData(profileModel)
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

    private fun setSingerProfileData(profileModel: ProfileModel) {
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
        binding.edtExperience.setText(profileModel.events)
        binding.edtSongsTypes.setText(profileModel.events)
        binding.edtLanguages.setText(profileModel.languages)


        if (!profileModel.work_links.isNullOrEmpty()) {
            for (i in 0 until profileModel.work_links.size) {
                val item = profileModel.work_links[i]
                val workLink = WorkLinkProfileData(item.worklink_name, item.worklink_url)
                workLinkList.add(workLink)
            }
            worklinkAdapter(binding.singerworklinkrecyclerview)
        }

        if (!profileModel.videos_url.isNullOrEmpty()) {
            for (i in 0 until profileModel.videos_url.size) {
                val item = profileModel.videos_url[i]
                val workLink = WorkLinkProfileData("",item.video_url)
                showreelLinkListClone.add(workLink)
            }
            showreelAdapter()
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

    fun worklinkAdapter(view: RecyclerView){
        view.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        view.setHasFixedSize(true)
        val adapter =
            WorkAdapter(mContext, workLinkList, this)
        view.adapter = adapter
    }

    fun showreelAdapter(){
        binding.singerShowreelecyclerview.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        binding.singerShowreelecyclerview.setHasFixedSize(true)
        val adapter =
            WorkAdapter(mContext, showreelLinkListClone, this)
        binding.singerShowreelecyclerview.adapter = adapter
    }

    private fun addShowreelDialog(isDancer:Boolean){
        val dialogView = Dialog(mContext)
        dialogView.setContentView(R.layout.add_work_link)

        dialogView.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialogView.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val workLinkName1 = dialogView.findViewById<EditText>(R.id.edtWorkLinkName1)
        val workLinkName2 = dialogView.findViewById<EditText>(R.id.edtWorkLinkName2)
        val workLinkName3 = dialogView.findViewById<EditText>(R.id.edtWorkLinkName3)
        val linkName1 = dialogView.findViewById<TextView>(R.id.edtAddWorkLink1)
        val linkName2 = dialogView.findViewById<TextView>(R.id.edtAddWorkLink2)
        val linkName3 = dialogView.findViewById<TextView>(R.id.edtAddWorkLink3)
        val addbutton = dialogView.findViewById<TextView>(R.id.tvAddLinks)
        val hint = dialogView.findViewById<TextView>(R.id.tv_hint)
        val closebutton = dialogView.findViewById<ImageView>(R.id.close)

        if (isDancer){
            hint.text = "Update Dance Video"
            workLinkName1.setHint("Dance Video Link Name")
            workLinkName2.setHint("Dance Video Link Name")
            workLinkName3.setHint("Dance Video Link Name")
            linkName1.setHint("Add Dance Video Link Url")
            linkName2.setHint("Add Dance Video Link Url")
            linkName3.setHint("Add Dance Video Link Url")
        }else {
            hint.text = "Update Showreel"
            workLinkName1.setHint("Showreel Link Name")
            workLinkName2.setHint("Showreel Link Name")
            workLinkName3.setHint("Showreel Link Name")
            linkName1.setHint("Add Showreel Link Url")
            linkName2.setHint("Add Showreel Link Url")
            linkName3.setHint("Add Showreel Link Url")
        }


        if (showreelLinkListClone.size > 0)
        {
            for (index in 0 until showreelLinkListClone.size) {
                val worklink = showreelLinkListClone[index]
                when(index){
                    0 ->{
                        workLinkName1.setText(worklink.worklink_name)
                        linkName1.setText(worklink.worklink_url)
                    }

                    1->{
                        workLinkName2.setText(worklink.worklink_name)
                        linkName2.setText(worklink.worklink_url)
                    }

                    2->{
                        workLinkName3.setText(worklink.worklink_name)
                        linkName3.setText(worklink.worklink_url)
                    }
                }
            }
        }

        closebutton.setOnClickListener {
            dialogView.dismiss()
        }

        addbutton.setOnClickListener {
            showreelLinkList.clear()
            showreelLinkListClone.clear()
            if (!workLinkName1.text.isNullOrEmpty() || !workLinkName2.text.isNullOrEmpty() || !workLinkName3.text.isNullOrEmpty()) {
                if (!workLinkName1.text.isNullOrEmpty() && !linkName1.text.isNullOrEmpty()) {
                    if (isValidYouTubeUrl(linkName1.text.toString())) {
                        showreelLinkList.add(
                            VideoLink(
                                workLinkName1.text.toString(),
                                linkName1.text.toString()
                            )
                        )
                    }else{
                        Toast.makeText(mContext, "Invalid ${workLinkName1.text} YouTube URL", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                if (!workLinkName2.text.isNullOrEmpty() && !linkName2.text.isNullOrEmpty()) {
                    if (isValidYouTubeUrl(linkName2.text.toString())) {
                        showreelLinkList.add(
                            VideoLink(
                                workLinkName2.text.toString(),
                                linkName2.text.toString()
                            )
                        )
                    }else{
                        Toast.makeText(mContext, "Invalid ${workLinkName2.text} YouTube URL", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                if (!workLinkName3.text.isNullOrEmpty() && !linkName3.text.isNullOrEmpty()) {
                    if (isValidYouTubeUrl(linkName3.text.toString())) {
                        showreelLinkList.add(
                            VideoLink(
                                workLinkName3.text.toString(),
                                linkName3.text.toString()
                            )
                        )
                    }else{
                        Toast.makeText(mContext, "Invalid ${workLinkName3.text} YouTube URL", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }

                for (i in showreelLinkList){
                    val videolink=WorkLinkProfileData(i.video_name,i.video_url)
                    showreelLinkListClone.add(videolink)
                }
                showreelAdapter()
                dialogView.dismiss()
            } else {
                Toast.makeText(mContext, "Add at least one work link", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.show()
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

    private fun isValidYouTubeUrl(url: String): Boolean {
        val youtubeRegex = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+\$".toRegex()
        return youtubeRegex.matches(url)
    }

    /*private fun updateSingerProfile()
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

                val achievements: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtLocation.text.toString().trim()
                )

                val languages: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtLanguages.text.toString().trim()
                )

                val location: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    ""
                )

                val dancer_form: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    ""
                )

                val what_i_do: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    ""
                )

                val events: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtEvents.text.toString().trim()
                )

                val genre: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtGenre.text.toString().trim()
                )

                val categoryId: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), category_Id
                )


                var profileBody: MultipartBody.Part? = null
                if (profilePath.isNotEmpty()) {
                    val file = File(profilePath)
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

                val jsonSingerArray = JSONArray()
                for (i in 0 until showreelLinkList.size) {
                    val jsonObject = JSONObject()
                    jsonObject.put("video_name", showreelLinkList[i].video_name)
                    jsonObject.put("video_url", showreelLinkList[i].video_url)
                    jsonSingerArray.put(jsonObject)
                }


                val showreel: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(), jsonSingerArray.toString()
                )

                viewModel.updateSingerProfile(
                    name,
                    email,
                    user_Id,
                    cat_Id,
                    mobileNumber,
                    desc,
                    achievements,
                    languages,
                    location,
                    dancer_form,
                    what_i_do,
                    events,
                    genre,
                    showreel,
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
    }*/

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