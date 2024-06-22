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
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityCastingApplyBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CastingCallModel
import com.app.bollyhood.util.PathUtils
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.google.gson.Gson
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
class CastingApplyActivity : AppCompatActivity() {

    lateinit var binding: ActivityCastingApplyBinding
    lateinit var mContext: CastingApplyActivity
    private val viewModel: DataViewModel by viewModels()
    private var isCamera = false
    private var isGallery = false
    private var photoList: ArrayList<String> = arrayListOf()
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 2
    var currentPhotoPath: String? = null
    lateinit var castingCallModel: CastingCallModel
    private val imageResultLaunchers = mutableMapOf<Int, ActivityResultLauncher<Intent>>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_casting_apply)
        mContext = this
        initUI()
        initializeImageResultLaunchers()
        addListner()
        addObserevs()
    }

    private fun initUI() {

        if (intent.extras != null) {
            castingCallModel =
                Gson().fromJson(intent.getStringExtra("model"), CastingCallModel::class.java)
        }

    }

    private fun addListner() {

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



        binding.lltvApplyNow.setOnClickListener {
            callApi()
            //castingCallSuccessDialog()
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

        viewModel.castingCallsApplyLiveData.observe(this, Observer {
            if (it.status == "1") {
              //  Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                castingCallSuccessDialog()
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun callApi(){
        if (isNetworkAvailable(mContext)) {
            val uid: RequestBody = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                PrefManager(mContext).getvalue(StaticData.id).toString()
            )

            val casting_id: RequestBody = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                castingCallModel.id
            )

            val parts: ArrayList<MultipartBody.Part> = arrayListOf()

            for (i in photoList.indices) {
                if (!photoList[i].startsWith("https")) {
                    val file = File(photoList[i])
                    if (file.exists()) {
                        val imageBody =
                            RequestBody.create("image/*".toMediaTypeOrNull(), file)
                        parts.add(
                            MultipartBody.Part.createFormData(
                                "images[]", file.name, imageBody
                            )
                        )
                        Log.e("UploadImage>>filepath", file.path)
                    } else {
                        file.mkdirs()
                        val imageBody =
                            RequestBody.create("image/*".toMediaTypeOrNull(), file)
                        parts.add(
                            MultipartBody.Part.createFormData(
                                "images[]", file.name, imageBody
                            )
                        )
                        Log.e("UploadImage>>filepath", file.path)
                    }
                }

                Log.e("photoList", "=" + photoList[i])
            }


            var videoBody: MultipartBody.Part? = null

            /* if (currentPhotoPath?.isNotEmpty()!!) {
                 val requestFile: RequestBody =
                     File(currentPhotoPath).asRequestBody("multipart/form-data".toMediaTypeOrNull())
                 videoBody = MultipartBody.Part.createFormData(
                     "video", File(currentPhotoPath).name, requestFile
                 )
             }*/

            Log.e("photoList", "=$currentPhotoPath")


            viewModel.getCastingCallApply(uid, casting_id, parts,  videoBody)
        } else {
            Toast.makeText(
                mContext,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
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

    private fun hideImageBackground(imageNumber: Int, isVisible: Boolean) {
        val progressBar = when (imageNumber) {
            1 -> binding.firstBackground
            2 -> binding.secondBackground
            3 -> binding.thirdBackground
            4 -> binding.fourthBackground
            5 -> binding.fifthBackground
            6 -> binding.sixBackground
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
                hideImageBackground(imageNumber,false)
                if (isCamera) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val image = saveImageToStorage(imageBitmap).toString()
                    photoList.add(image)
                    setImage(imageNumber, Uri.parse(image))
                } else if (isGallery) {
                    val uri = data?.data
                    photoList.add(PathUtils.getRealPath(this, uri!!).toString())
                    setImage(imageNumber, uri)
                }
            }

            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun castingCallSuccessDialog(){
        val dialogView = Dialog(this)
        dialogView.setContentView(R.layout.castingcall_apply_dialog)


        dialogView.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialogView.window?.setBackgroundDrawableResource(android.R.color.transparent)


        dialogView.show()
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