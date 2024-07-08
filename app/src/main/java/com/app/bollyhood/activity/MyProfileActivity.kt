package com.app.bollyhood.activity

import Categorie
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
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
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
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.adapter.WorkAdapter
import com.app.bollyhood.databinding.ActivityMyProfileBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidDescriptions
import com.app.bollyhood.extensions.isvalidEmailAddress
import com.app.bollyhood.extensions.isvalidMobileNumber
import com.app.bollyhood.extensions.isvalidName
import com.app.bollyhood.extensions.isvalidTeamNCondition
import com.app.bollyhood.model.CategoryModel
import com.app.bollyhood.model.ProfileModel
import com.app.bollyhood.model.VideoLink
import com.app.bollyhood.model.WorkLinkProfileData
import com.app.bollyhood.util.InbuildLIsts
import com.app.bollyhood.util.PathUtils
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
class MyProfileActivity : AppCompatActivity(), TextWatcher,WorkAdapter.onItemClick,OnClickListener{

    lateinit var binding: ActivityMyProfileBinding
    lateinit var mContext: MyProfileActivity
    private val viewModel: DataViewModel by viewModels()
    private var isCamera = false
    private var isGallery = false
    private var profilePath = ""
    private var category_Id: String = ""
    private var categories:String=""
    private var workLinkList: ArrayList<WorkLinkProfileData> = arrayListOf()
    private var showreelLinkList: ArrayList<VideoLink> = arrayListOf()
    private var showreelLinkListClone: ArrayList<WorkLinkProfileData> = arrayListOf()
    private var imagesurl: ArrayList<String> = arrayListOf()
    private var passPortList: ArrayList<String> = arrayListOf()
    private val imageResultLaunchers = mutableMapOf<Int, ActivityResultLauncher<Intent>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)
        mContext = this

        setProfileUi()
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

    private fun setProfileUi() {
        if (PrefManager(mContext).getvalue(StaticData.category)?.isNotEmpty()==true) {
            val gson = Gson()
            val categoryListType = object : TypeToken<List<CategoryModel>>() {}.type
            val categoriesList: List<CategoryModel> =
                gson.fromJson(PrefManager(mContext).getvalue(StaticData.category), categoryListType)
            categories=categoriesList[0].category_name
            when(categories){
                Categorie.SINGER.toString(),Categorie.DJ.toString()->{
                    binding.profileActors.visibility=View.GONE
                    binding.profileSinger.visibility=View.VISIBLE
                    binding.profileInfluencer.visibility=View.GONE
                }

                Categorie.ACTOR.toString() ->{
                    binding.profileActors.visibility=View.VISIBLE
                    binding.profileSinger.visibility=View.GONE
                    binding.profileInfluencer.visibility=View.GONE
                }

                Categorie.DANCER.toString() ->{
                    binding.profileActors.visibility=View.GONE
                    binding.profileSinger.visibility=View.VISIBLE
                    binding.profileInfluencer.visibility=View.GONE
                    binding.tvsingerShowreel.setText("Update Dance Videos")
                }

                Categorie.INFLUENCER.toString() ->{
                    binding.profileActors.visibility=View.GONE
                    binding.profileSinger.visibility=View.GONE
                    binding.profileInfluencer.visibility=View.VISIBLE
                }
            }
        }
    }

    companion object {
         const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2
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
                setHeightData()
                acheight.showDropDown()
                false
            }

            acSkinColor.setOnTouchListener { v, event ->
                setSkinColorData()
                acSkinColor.showDropDown()
                false
            }


            acBodyType.setOnTouchListener { v, event ->
                setBodyTypeData()
                acBodyType.showDropDown()
                false
            }

            acPassPort.setOnTouchListener { v, event ->
                setPassPortData()
                acPassPort.showDropDown()
                false
            }

            acage.setOnTouchListener { v, event ->
                setAgeData()
                acage.showDropDown()
                false
            }

            ivBack.setOnClickListener(this@MyProfileActivity)
            tvAddWorkLink.setOnClickListener(this@MyProfileActivity)
            tvAddShowreel.setOnClickListener(this@MyProfileActivity)
            tvAddSingerWorkLink.setOnClickListener(this@MyProfileActivity)
            tvinfluencerAddWorkLink.setOnClickListener(this@MyProfileActivity)
            firstImage.setOnClickListener(this@MyProfileActivity)
            secondImage.setOnClickListener(this@MyProfileActivity)
            thirdImage.setOnClickListener(this@MyProfileActivity)
            fourthImage.setOnClickListener(this@MyProfileActivity)
            fifthimage.setOnClickListener(this@MyProfileActivity)
            siximage.setOnClickListener(this@MyProfileActivity)
            tvUpdateProfile.setOnClickListener(this@MyProfileActivity)
            rrProfile.setOnClickListener(this@MyProfileActivity)
            singerfirstImage.setOnClickListener(this@MyProfileActivity)
            singersecondimage.setOnClickListener(this@MyProfileActivity)
            singerthirdimage.setOnClickListener(this@MyProfileActivity)
            influencerFirstImage.setOnClickListener(this@MyProfileActivity)
            influencerSecondImage.setOnClickListener(this@MyProfileActivity)
            influencerThirdImage.setOnClickListener(this@MyProfileActivity)
            influencerFourthImage.setOnClickListener(this@MyProfileActivity)
            influencerFifthimage.setOnClickListener(this@MyProfileActivity)
            influencerSiximage.setOnClickListener(this@MyProfileActivity)

        }
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.ivBack ->{
                finish()
            }

            R.id.tvAddShowreel->{
                when(categories){
                    Categorie.DANCER.toString() ->{
                        addShowreelDialog(true)
                    }

                    else ->{
                        addShowreelDialog(false)
                    }
                }
            }

            R.id.tvAddWorkLink->{
                addWorkLinksDialog()
            }

            R.id.tvAddSingerWorkLink->{
                addWorkLinksDialog()
            }

            R.id.tvinfluencerAddWorkLink ->{
                addWorkLinksDialog()
            }

            R.id.singerfirstImage->{
                if (checkPermission()){
                    dialogForImage(1)
                }else{
                    checkPermission()
                }
            }

            R.id.singersecondimage->{
                if (checkPermission()){
                    dialogForImage(2)
                }else{
                    checkPermission()
                }
            }

            R.id.singerthirdimage->{
                if (checkPermission()){
                    dialogForImage(3)
                }else{
                    checkPermission()
                }
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

            R.id.influencer_firstImage->{
                if (checkPermission()){
                    dialogForImage(1)
                }else{
                    checkPermission()
                }
            }

            R.id.influencer_secondImage ->{
                if (checkPermission()){
                    dialogForImage(2)
                }else{
                    checkPermission()
                }
            }

            R.id.influencer_thirdImage ->{
                if (checkPermission()){
                    dialogForImage(3)
                }else{
                    checkPermission()
                }
            }

            R.id.influencer_fourthImage->{
                if (checkPermission()){
                    dialogForImage(4)
                }else{
                    checkPermission()
                }
            }

            R.id.influencer_fifthimage ->{
                if (checkPermission()){
                    dialogForImage(5)
                }else{
                    checkPermission()
                }
            }

            R.id.influencer_siximage ->{
                if (checkPermission()){
                    dialogForImage(6)
                }else{
                    checkPermission()
                }
            }

            R.id.tvUpdateProfile->{
                when(categories){
                    Categorie.SINGER.toString(),Categorie.DJ.toString()->{
                        updateSingerProfile()
                    }

                    Categorie.ACTOR.toString() ->{
                        updateActorProfile()
                    }

                    Categorie.DANCER.toString() ->{
                        updateSingerProfile()
                    }

                    Categorie.INFLUENCER.toString() ->{
                        updateInfluencerProfile()
                    }
                }
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
                when(categories){
                    Categorie.SINGER.toString(),Categorie.DJ.toString()->{
                        setSingerProfileData(profileModel)
                    }

                    Categorie.ACTOR.toString() ->{
                        setProfileData(profileModel)
                    }

                    Categorie.DANCER.toString() ->{
                        setSingerProfileData(profileModel)
                    }

                    Categorie.INFLUENCER.toString() ->{
                        setInfluencerProfileData(profileModel)
                    }
                }
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
            val innerArrayStr = profileModel.work_links[0].worklink_url
            val innerArray = JSONArray(innerArrayStr)
            for (i in 0 until innerArray.length()) {
                val item = innerArray.getJSONObject(i)
                val workLink = WorkLinkProfileData(item.getString("worklink_name"), item.getString("worklink_url"))
                workLinkList.add(workLink)
            }
            worklinkAdapter(binding.worklinkrecyclerview)
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

    private fun setInfluencerProfileData(profileModel: ProfileModel) {
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
        binding.edtcollaboratedWith.setText(profileModel.collaborate)
        binding.edtpromotionType.setText(profileModel.promotion)
        binding.edtaverageLikes.setText(profileModel.average_like)
        binding.edtaverageReelViews.setText(profileModel.average_reel_like)
        binding.edtlinkInstagram.setText(profileModel.instagram_link)
        binding.edtlinkFacebook.setText(profileModel.facebook_link)
        binding.edtlinkYoutube.setText(profileModel.youtube_link)


        if (!profileModel.work_links.isNullOrEmpty()) {
            val innerArrayStr = profileModel.work_links[0].worklink_url
            val innerArray = JSONArray(innerArrayStr)
            for (i in 0 until innerArray.length()) {
                val item = innerArray.getJSONObject(i)
                val workLink = WorkLinkProfileData(item.getString("worklink_name"), item.getString("worklink_url"))
                workLinkList.add(workLink)
            }
            worklinkAdapter(binding.influencerworklinkrecyclerview)
        }

        if (!profileModel.imagefile.isNullOrEmpty()) {
            val imageViews = listOf(binding.influencerFirstImage, binding.influencerSecondImage, binding.influencerThirdImage, binding.influencerFourthImage, binding.influencerFifthimage, binding.influencerSiximage)

            for (i in 0 until profileModel.imagefile.size) {
                Glide.with(mContext).load(profileModel.imagefile[i])
                    .error(R.drawable.upload_to_the_cloud_svg)
                    .centerCrop()
                    .into(imageViews[i])
            }
        }
    }

    private fun setSingerProfileData(profileModel: ProfileModel) {
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
        binding.edtAchievements.setText(profileModel.achievements)
        binding.edtEvents.setText(profileModel.events)
        binding.edtLanguages.setText(profileModel.languages)
        binding.edtGenre.setText(profileModel.genre)

        if (!profileModel.work_links.isNullOrEmpty()) {
            val innerArrayStr = profileModel.work_links[0].worklink_url
            val innerArray = JSONArray(innerArrayStr)
            for (i in 0 until innerArray.length()) {
                val item = innerArray.getJSONObject(i)
                val workLink = WorkLinkProfileData(item.getString("worklink_name"), item.getString("worklink_url"))
                workLinkList.add(workLink)
            }
            worklinkAdapter(binding.singerworklinkrecyclerview)
        }

        if (!profileModel.videos_url.isNullOrEmpty()) {
            val innerArrayStr = profileModel.videos_url[0].video_url
            val innerArray = JSONArray(innerArrayStr)
            for (i in 0 until innerArray.length()) {
                val item = innerArray.getJSONObject(i)
                val workLink = WorkLinkProfileData(item.getString("video_name"), item.getString("video_url"))
                showreelLinkListClone.add(workLink)
            }
            showreelAdapter()
        }

        if (!profileModel.imagefile.isNullOrEmpty()) {
            val imageViews = listOf(binding.singerfirstImage, binding.singersecondimage, binding.singerthirdimage)

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

    fun worklinkAdapter(view:RecyclerView){
        view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        view.setHasFixedSize(true)
        val adapter =
            WorkAdapter(this, workLinkList, this)
        view.adapter = adapter
    }

    fun showreelAdapter(){
        binding.singerShowreelecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.singerShowreelecyclerview.setHasFixedSize(true)
        val adapter =
            WorkAdapter(this, showreelLinkListClone, this)
        binding.singerShowreelecyclerview.adapter = adapter
    }


    private fun addWorkLinksDialog(){
        val dialogView = Dialog(this)
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
        val closebutton = dialogView.findViewById<ImageView>(R.id.close)

        if (workLinkList.size > 0)
        {
            for (index in 0 until workLinkList.size) {
                val worklink = workLinkList[index]
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
            workLinkList.clear()
            if (!workLinkName1.text.isNullOrEmpty() || !workLinkName2.text.isNullOrEmpty() || !workLinkName3.text.isNullOrEmpty()) {
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

                if (!workLinkName3.text.isNullOrEmpty() && !linkName3.text.isNullOrEmpty()) {
                    workLinkList.add(
                        WorkLinkProfileData(
                            workLinkName3.text.toString(),
                            linkName3.text.toString()
                        )
                    )
                }

                when(categories){
                    Categorie.SINGER.toString(),Categorie.DJ.toString()->{
                        worklinkAdapter(binding.singerworklinkrecyclerview)
                    }

                    Categorie.ACTOR.toString() ->{
                        worklinkAdapter(binding.worklinkrecyclerview)
                    }

                    Categorie.DANCER.toString() ->{
                        worklinkAdapter(binding.singerworklinkrecyclerview)
                    }

                    Categorie.INFLUENCER.toString() ->{
                        worklinkAdapter(binding.influencerworklinkrecyclerview)
                    }
                }
                dialogView.dismiss()
            } else {
                Toast.makeText(this, "Add at least one work link", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.show()
    }


    private fun addShowreelDialog(isDancer:Boolean){
        val dialogView = Dialog(this)
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
                        Toast.makeText(this, "Invalid ${workLinkName1.text} YouTube URL", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this, "Invalid ${workLinkName2.text} YouTube URL", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this, "Invalid ${workLinkName3.text} YouTube URL", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Add at least one work link", Toast.LENGTH_SHORT).show()
            }
        }

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
            ImagePickerUtil.pickImageFromGallery(this,startForProfileImageResult)
            isCamera = false
            isGallery = true
            dialogView.dismiss()
        }
        txtCancel.setOnClickListener { v: View? -> dialogView.dismiss() }
        dialogView.show()
    }


    private fun setSpannableString() {
        val spanTxt = SpannableStringBuilder(
            "By Updating an account, you agree to our"
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
            ImagePickerUtil.pickImageFromGallery(this,imageResultLaunchers[imageNumber] ?: return@setOnClickListener)

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
                    imagesurl.add(PathUtils.getRealPath(this, uri!!).toString())
                    setImage(imageNumber, uri)
                }
            }

            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(
                mContext,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun updateSingerProfile()
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
                    binding.edtAchievements.text.toString().trim()
                )

                val languages: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtLanguages.text.toString().trim()
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
    }




    private fun setImage(imageNumber: Int, uri: Uri?) {
        when(categories){
            Categorie.SINGER.toString(),Categorie.DJ.toString(),Categorie.DANCER.toString()->{
                when (imageNumber) {
                    1 -> binding.singerfirstImage.setImageURI(uri)
                    2 -> binding.singersecondimage.setImageURI(uri)
                    3 -> binding.singerthirdimage.setImageURI(uri)
                    else -> throw IllegalArgumentException("Invalid image number")
                }
            }

            Categorie.INFLUENCER.toString()->{
                when (imageNumber) {
                    1 -> binding.influencerFirstImage.setImageURI(uri)
                    2 -> binding.influencerSecondImage.setImageURI(uri)
                    3 -> binding.influencerThirdImage.setImageURI(uri)
                    4 -> binding.influencerFourthImage.setImageURI(uri)
                    5 -> binding.influencerFifthimage.setImageURI(uri)
                    6 -> binding.influencerSiximage.setImageURI(uri)
                    else -> throw IllegalArgumentException("Invalid image number")
                }
            }

            Categorie.ACTOR.toString() ->{
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
    }

    private fun updateInfluencerProfile()
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

                val collaborate: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtcollaboratedWith.text.toString().trim()
                )

                val promotion: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtpromotionType.text.toString().trim()
                )

                val average_like: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtaverageLikes.text.toString().trim()
                )

                val average_reel_like: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtaverageReelViews.text.toString().trim()
                )

                val instagram_link: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtlinkInstagram.text.toString().trim()
                )

                val facebook_link: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtlinkFacebook.text.toString().trim()
                )

                val youtube_link: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    binding.edtlinkFacebook.text.toString().trim()
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


                viewModel.updateInfluencerProfile(
                    name,
                    email,
                    user_Id,
                    cat_Id,
                    mobileNumber,
                    desc,
                    collaborate,
                    promotion,
                    average_like,
                    average_reel_like,
                    instagram_link,
                    facebook_link,
                    youtube_link,
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


}