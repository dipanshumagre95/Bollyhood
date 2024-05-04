package com.app.bollyhood.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.ChatHistoryAdapter
import com.app.bollyhood.databinding.ActivityChatBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.ChatModel
import com.app.bollyhood.model.ExpertiseModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
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
class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var mContext: ChatActivity
    private val viewModel: DataViewModel by viewModels()
    private var chatModel: ArrayList<ChatModel> = arrayListOf()
    private var profileId: String? = ""
    private var isCamera = false
    private var isGallery = false
    private var profilePath = ""
    lateinit var expertiseModel: ExpertiseModel

    companion object {
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2

    }


    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val uid = intent.getStringExtra("id")
                val other_uid = intent.getStringExtra("other_uid")

                viewModel.getChatHistory(other_uid.toString(),uid.toString())

            }
        }
    }

    //casting calls bookmark
    //pdf casting calls
    //profile completed
    //video uploading
    //minor Ui


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        mContext = this
        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {

        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(receiver, IntentFilter("sendData"))


        if (intent.extras != null) {
            profileId = intent.getStringExtra("profileId")
            expertiseModel =
                Gson().fromJson(intent.getStringExtra("model"), ExpertiseModel::class.java)
            setData(expertiseModel)
        }


        if (isNetworkAvailable(mContext)) {
            viewModel.getChatHistory(
                PrefManager(this@ChatActivity).getvalue(StaticData.id).toString(),
                profileId.toString()
            )
        } else {
            Toast.makeText(
                mContext,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.ivCall.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", expertiseModel.mobile, null))
            startActivity(intent)

            /*val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${expertiseModel.mobile}")
            startActivity(intent)*/
        }

    }

    private fun setData(expertiseModel: ExpertiseModel) {
        Glide.with(mContext).load(expertiseModel.image).placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile).into(binding.cvProfile)

        binding.tvName.text = expertiseModel.name

        if (expertiseModel.is_online == "1") {
            binding.tvStatus.text = "Online"
            binding.ivStatus.setImageResource(R.drawable.circle_green)
        } else {
            binding.tvStatus.text = "Offline"
            binding.ivStatus.setImageResource(R.drawable.circle_grey)
        }
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivCamera.setOnClickListener {
            if (checkPermission()) {
                alertDialogForImagePicker()
            } else {
                checkPermission()
            }
        }

        binding.ivSend.setOnClickListener {
            if (binding.edtMessage.text.toString().trim().isEmpty() && profilePath.isEmpty()) {
                Toast.makeText(mContext, "Please send message or upload image", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (isNetworkAvailable(mContext)) {
                    it?.hideKeyboard()
                    mNetworkCallSendMessageAPI()
                } else {
                    Toast.makeText(
                        mContext,
                        getString(R.string.str_error_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun mNetworkCallSendMessageAPI() {
        val uid: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            PrefManager(mContext).getvalue(StaticData.id).toString()
        )

        val other_uid: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            profileId.toString()
        )

        val text: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            binding.edtMessage.text.toString().trim()
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


        viewModel.sendMessage(uid, other_uid, text, profileBody)
    }

    private fun addObserevs() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.chatHistoryLiveData.observe(this, Observer {
            if (it.status == "1") {
                binding.edtMessage.setText("")
                chatModel.clear()
                chatModel.addAll(it.result)

                if (chatModel.size > 0) {
                    binding.rvChatHistory.visibility = View.VISIBLE
                    binding.tvNoChatHistory.visibility = View.GONE
                    setAdapter(chatModel)

                } else {
                    binding.rvChatHistory.visibility = View.GONE
                    binding.tvNoChatHistory.visibility = View.VISIBLE
                }


            } else {
                binding.rvChatHistory.visibility = View.GONE
                binding.tvNoChatHistory.visibility = View.VISIBLE
            }
        })

        viewModel.sendMessageLiveData.observe(this, Observer {
            if (it.status == "1") {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                viewModel.getChatHistory(
                    PrefManager(mContext).getvalue(StaticData.id).toString(),
                    profileId.toString()
                )
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAdapter(chatModel: ArrayList<ChatModel>) {
        chatModel.reverse()
        binding.apply {
            rvChatHistory.layoutManager = LinearLayoutManager(this@ChatActivity)
            rvChatHistory.setHasFixedSize(true)
            adapter = ChatHistoryAdapter(this@ChatActivity, chatModel)
            rvChatHistory.adapter = adapter
            rvChatHistory.scrollToPosition(adapter?.itemCount!!.toInt() - 1)
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
                        mNetworkCallSendMessageAPI()
                    } else if (isGallery) {
                        val uri = data!!.data
                        profilePath = uri!!.path.toString()
                        mNetworkCallSendMessageAPI()
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

    override fun onDestroy() {
        // Unregister broadcast
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onDestroy()
    }


}