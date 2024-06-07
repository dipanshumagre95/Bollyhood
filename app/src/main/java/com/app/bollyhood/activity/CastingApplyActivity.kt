package com.app.bollyhood.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.ImagesAdapter
import com.app.bollyhood.databinding.ActivityCastingApplyBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CastingCallModel
import com.app.bollyhood.model.PhotoModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.util.UriPathHelper
import com.app.bollyhood.viewmodel.DataViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CastingApplyActivity : AppCompatActivity(), ImagesAdapter.onItemClick {

    lateinit var binding: ActivityCastingApplyBinding
    lateinit var mContext: CastingApplyActivity
    private val viewModel: DataViewModel by viewModels()
    private var isCamera = false
    private var isGallery = false
    private var photoList: ArrayList<PhotoModel> = arrayListOf()
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 2
    var currentPhotoPath: String? = null
    var bitmaps: ArrayList<Bitmap> = ArrayList()
    lateinit var photoURI: Uri
    private var selectType: String = ""
    lateinit var castingCallModel: CastingCallModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_casting_apply)
        mContext = this
        initUI()
      //  addListner()
        addObserevs()
    }

    private fun initUI() {

        if (intent.extras != null) {
            castingCallModel =
                Gson().fromJson(intent.getStringExtra("model"), CastingCallModel::class.java)
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addListner() {
        binding.tvAddPhoto.setOnClickListener {
            selectType = "image"
            if (checkPermission()) {

                if (photoList.size < 4) {
                    alertDialogForImagePicker()
                } else {
                    Toast.makeText(
                        mContext,
                        "Max 4 photos uploaded at a time",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

            } else {
                checkPermission()
            }

        }
        binding.tvAddVideo.setOnClickListener {
            selectType = "video"
            if (checkPermission()) {
                openVideo()
            } else {
                checkPermission()
            }
        }

        binding.tvApplyNow.setOnClickListener {
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
                    if (!photoList[i].url.startsWith("https")) {
                        val file = File(photoList[i].url)
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

                    Log.e("photoList", "=" + photoList[i].url)
                }


                var videoBody: MultipartBody.Part? = null

                if (currentPhotoPath!!.isNotEmpty()) {
                    val requestFile: RequestBody =
                        File(currentPhotoPath).asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    videoBody = MultipartBody.Part.createFormData(
                        "video", File(currentPhotoPath).name, requestFile
                    )
                }

                Log.e("photoList", "=$currentPhotoPath")


                viewModel.getCastingCallApply(uid, casting_id, images = parts, video = videoBody)
            } else {
                Toast.makeText(
                    mContext,
                    getString(R.string.str_error_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }
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
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
       //         startActivity(Intent(mContext, CastingCallsActivity::class.java))
                finishAffinity()
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun openVideo() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "video/*"
       // startVideoResult.launch(pickIntent)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private val startVideoResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    photoURI = data?.data!!
                    val uriPathHelper = UriPathHelper()
                    currentPhotoPath = uriPathHelper.getPath(this, photoURI)
                    try {

                        if (currentPhotoPath!!.endsWith(".mp4")) {

                            val bitmap = ThumbnailUtils.createVideoThumbnail(
                                File(currentPhotoPath), Size(120, 120), null
                            )
                            bitmaps.add(bitmap)
                            binding.frameVideo.visibility = View.VISIBLE
                            binding.ivImage.setImageBitmap(bitmap)

                        }


                    } catch (e: IOException) {
                        e.printStackTrace()
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

    @RequiresApi(Build.VERSION_CODES.Q)
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

                            if (selectType == "video") {
                                openVideo()
                            } else {
                                alertDialogForImagePicker()
                            }

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

                            if (selectType == "video") {
                                openVideo()
                            } else {
                                alertDialogForImagePicker()
                            }

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


    @RequiresApi(Build.VERSION_CODES.Q)
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    if (isCamera) {
                        val imageBitmap = result.data?.extras?.get("data") as Bitmap
                        currentPhotoPath = saveImageToStorage(imageBitmap).toString()
                        bitmaps.add(imageBitmap)
                        Log.e("currentPhotoPath", "=$currentPhotoPath")
                        photoList.add(PhotoModel(0, currentPhotoPath!!))


                    } else if (isGallery) {
                        val uri = data!!.data
                        currentPhotoPath = uri!!.path.toString()
                        val mImageBitmap =
                            BitmapFactory.decodeFile(currentPhotoPath) //MediaStore.Images.Media.getBitmap(this.contentResolver,Uri.parse(currentPhotoPath))
                        bitmaps.add(mImageBitmap)
                        Log.e("currentPhotoPath", "=$currentPhotoPath")
                        photoList.add(PhotoModel(0, currentPhotoPath!!))


                    }

                    binding.apply {
                        binding.rvUploadImages.layoutManager = LinearLayoutManager(
                            mContext, LinearLayoutManager.HORIZONTAL, false
                        )
                        binding.rvUploadImages.setHasFixedSize(true)
                        adapter = ImagesAdapter(mContext, photoList, mContext)
                        binding.rvUploadImages.adapter = adapter
                        adapter?.notifyDataSetChanged()


                        Log.e("PhotoListSize", "=" + photoList.size)

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


    @RequiresApi(Build.VERSION_CODES.Q)
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

    override fun onRemoveImage(pos: Int, photoModel: PhotoModel) {
        openDialog(pos, photoModel)
    }

    private fun openDialog(pos: Int, photoModel: PhotoModel) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete image?")

        builder.setPositiveButton(getString(R.string.str_yes)) { dialog, _ -> // Do nothing but close the dialog
            dialog.dismiss()
            photoList.removeAt(pos)
            binding.adapter?.notifyDataSetChanged()
        }
        builder.setNegativeButton(
            getString(R.string.str_no)
        ) { dialog: DialogInterface, _: Int ->

            // Do nothing
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }


}