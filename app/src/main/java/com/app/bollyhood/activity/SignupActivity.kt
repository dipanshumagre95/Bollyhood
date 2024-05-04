package com.app.bollyhood.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivitySignupBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidBothPassword
import com.app.bollyhood.extensions.isvalidCategory
import com.app.bollyhood.extensions.isvalidConfirmPassword
import com.app.bollyhood.extensions.isvalidEmailAddress
import com.app.bollyhood.extensions.isvalidMobileNumber
import com.app.bollyhood.extensions.isvalidName
import com.app.bollyhood.extensions.isvalidPassword
import com.app.bollyhood.extensions.isvalidUploadProfile
import com.app.bollyhood.model.CategoryModel
import com.app.bollyhood.model.SubcategoryModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
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
class SignupActivity : AppCompatActivity() {

    lateinit var mContext: SignupActivity
    lateinit var binding: ActivitySignupBinding
    private val viewModel: DataViewModel by viewModels()
    private var categoryId: String = ""
    private var subCategoryId: String = ""
    private var user_type: String = "1"

    private var isCamera = false
    private var isGallery = false
    private var profilePath = ""

    companion object {
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        mContext = this
        addListner()
        addObserevs()
    }

    private fun addListner() {
        binding.apply {
            tvLogin.setOnClickListener {
                startActivity(Intent(mContext, LoginActivity::class.java))
                finishAffinity()
            }

            acSelectToday.setOnTouchListener { _, _ ->
                acSelectToday.showDropDown()
                false
            }

            acSubCategory.setOnTouchListener { _, _ ->
                acSubCategory.showDropDown()
                false
            }

            rbIndividual.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    user_type = "1"
                    rbIndividual.isChecked = true
                    rbAgency.isChecked = false
                }
            }

            rbAgency.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    user_type = "2"
                    rbAgency.isChecked = true
                    rbIndividual.isChecked = false
                }
            }

            rrProfile.setOnClickListener {
                if (checkPermission()) {
                    alertDialogForImagePicker()
                } else {
                    checkPermission()
                }
            }


            tvSignUp.setOnClickListener {
                if (isNetworkAvailable(mContext)) {
                    if (isvalidUploadProfile(mContext, profilePath) &&
                        isvalidName(
                            mContext,
                            binding.edtName.text.toString().trim()
                        ) && isvalidEmailAddress(
                            mContext,
                            binding.edtEmailAddress.text.toString().trim()
                        ) && isvalidMobileNumber(
                            mContext,
                            binding.edtMobileNumber.text.toString().trim()
                        ) && isvalidCategory(
                            mContext,
                            binding.acSelectToday.text.toString().trim()
                        ) && isvalidPassword(
                            mContext,
                            binding.edtPassword.text.toString().trim()
                        ) && isvalidConfirmPassword(
                            mContext, binding.edtConfirmPassword.text.toString().trim()
                        ) && isvalidBothPassword(
                            mContext, binding.edtPassword.text.toString().trim(),
                            binding.edtConfirmPassword.text.toString().trim()
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

                        val password: RequestBody = RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            binding.edtPassword.text.toString().trim()
                        )

                        val cat_id: RequestBody = RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            categoryId
                        )

                        val subCatId: RequestBody = RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            subCategoryId
                        )


                        val mobileNumber: RequestBody = RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            binding.edtMobileNumber.text.toString().trim()
                        )


                        val type: RequestBody = RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(),
                            user_type
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



                        viewModel.doSignup(
                            name,
                            email,
                            password,
                            cat_id,
                            mobileNumber,
                            type,
                            subCatId,
                            profileBody
                        )
                    }

                } else {
                    Toast.makeText(
                        mContext, getString(R.string.str_error_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        }
    }

    private fun addObserevs() {

        if (isNetworkAvailable(mContext)) {
            viewModel.getCategory()
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }


        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })


        viewModel.categoryLiveData.observe(this, Observer { res ->
            if (res.status == "1") {
                setCategoryAdapter(res.result)
            } else {
                Toast.makeText(mContext, res?.msg, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.subCategoryLiveData.observe(this, Observer { res ->
            if (res.status == "1") {
                setSubCategoryAdapter(res.result)
            } else {
                Toast.makeText(mContext, res?.msg, Toast.LENGTH_SHORT).show()
            }
        })


        viewModel.signupLiveData.observe(this, Observer {
            if (it.status == "1") {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                startActivity(Intent(mContext, LoginActivity::class.java))
                finishAffinity()

            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()

            }
        })


    }

    private fun setCategoryAdapter(result: ArrayList<CategoryModel>) {

        val stringList = arrayListOf<String>()
        result.forEach {
            stringList.add(it.category_name)
        }

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, stringList)
        binding.acSelectToday.threshold = 0
        binding.acSelectToday.dropDownVerticalOffset = 0
        binding.acSelectToday.setAdapter(arrayAdapter)

        binding.acSelectToday.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                categoryId = result[position].id
                subCategoryId = ""
                binding.acSubCategory.setText("")
                if (isNetworkAvailable(mContext)) {
                    viewModel.getSubCategory(categoryId)
                } else {
                    Toast.makeText(
                        mContext, getString(R.string.str_error_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }


    private fun setSubCategoryAdapter(result: ArrayList<SubcategoryModel>) {

        val stringList = arrayListOf<String>()
        result.forEach {
            stringList.add(it.sub_cat_name)
        }

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(mContext, R.layout.dropdown, stringList)
        binding.acSubCategory.threshold = 0
        binding.acSubCategory.dropDownVerticalOffset = 0
        binding.acSubCategory.setAdapter(arrayAdapter)

        binding.acSubCategory.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                subCategoryId = result[position].sub_cat_id
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
                REQUEST_ID_MULTIPLE_PERMISSIONS
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
    }

}