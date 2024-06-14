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
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.app.bollyhood.model.SenderDetails
import com.app.bollyhood.util.PathUtils
import com.app.bollyhood.util.PermissionUtils
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.devlomi.record_view.OnRecordListener
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
class ChatActivity : AppCompatActivity(),TextWatcher,ChatHistoryAdapter.ChatHistoryInterface {

    lateinit var binding: ActivityChatBinding
    lateinit var mContext: ChatActivity
    private val viewModel: DataViewModel by viewModels()
    private var chatModel: ArrayList<ChatModel> = arrayListOf()
    private var profileId: String? = ""
    private var isCamera = false
    private var isGallery = false
    private var isclick=""
    private var profilePath = ""
    private var senderDetails: SenderDetails? = null
    private var isNotification: Boolean? = false
    private var mediaRecorder:MediaRecorder?=null
    private var audioPath=""

    companion object {
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2

    }


    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val uid = intent.getStringExtra("id")
                val other_uid = intent.getStringExtra("other_uid")

                viewModel.getChatHistory(other_uid.toString(), uid.toString())

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

        binding.ivMicrophone.setRecordView(binding.recordingview)
        binding.ivMicrophone.isListenForRecord=false

        binding.ivMicrophone.setOnClickListener(View.OnClickListener {
            if (PermissionUtils.isRecording(this)){
                binding.ivMicrophone.isListenForRecord=true
            }else{
                PermissionUtils.requestRecordingPermission(this)
            }
        })

        binding.recordingview.setOnRecordListener(object : OnRecordListener {
            override fun onStart() {

                setUpForMediaRecorder()
                try {
                    mediaRecorder?.prepare()
                    mediaRecorder?.start()
                }catch (e:Exception){
                    e.printStackTrace()
                }

                binding.ivattechment.visibility=View.GONE
                binding.edtMessage.visibility=View.GONE
                binding.ivCamera.visibility=View.GONE
                binding.recordingview.visibility=View.VISIBLE
            }

            override fun onCancel() {
                try {
                    mediaRecorder?.reset()
                    mediaRecorder?.release()
                }catch (e:Exception){
                    e.printStackTrace()
                }

                val file=File(audioPath)
                if (file.exists())
                    file.delete()

                binding.ivattechment.visibility=View.VISIBLE
                binding.edtMessage.visibility=View.VISIBLE
                binding.ivCamera.visibility=View.VISIBLE
                binding.recordingview.visibility=View.GONE
            }

            override fun onFinish(recordTime: Long, limitReached: Boolean) {


                try {
                    mediaRecorder?.stop()
                    mediaRecorder?.release()
                }catch (e:Exception){
                    e.printStackTrace()
                }

                binding.ivattechment.visibility=View.VISIBLE
                binding.edtMessage.visibility=View.VISIBLE
                binding.ivCamera.visibility=View.VISIBLE
                binding.recordingview.visibility=View.GONE
                sendAudioFile()
            }

            override fun onLessThanSecond() {
                try {
                    mediaRecorder?.reset()
                    mediaRecorder?.release()
                }catch (e:Exception){
                    e.printStackTrace()
                }

                val file=File(audioPath)
                if (file.exists())
                    file.delete()

                binding.ivattechment.visibility=View.VISIBLE
                binding.edtMessage.visibility=View.VISIBLE
                binding.ivCamera.visibility=View.VISIBLE
                binding.recordingview.visibility=View.GONE
            }

            override fun onLock() {
                //When Lock gets activated
                Log.d("RecordView", "onLock")
            }
        })

        binding.edtMessage.addTextChangedListener(this)
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(receiver, IntentFilter("sendData"))


        if (intent.extras != null) {
            profileId = intent.getStringExtra("profileId")


        }


        if (isNetworkAvailable(mContext)) {
            isNotification = intent.getBooleanExtra("isNotifications", false)
            if (!isNotification!!) {
                viewModel.getChatHistory(
                    PrefManager(this@ChatActivity).getvalue(StaticData.id).toString(),
                    profileId.toString()
                )

            } else {
                val uid = intent.getStringExtra("id")
                val other_uid = intent.getStringExtra("other_uid")
                viewModel.getChatHistory(other_uid.toString(), uid.toString())
            }
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
            ).show()
        }

        binding.ivCall.setOnClickListener {
            if (!senderDetails?.mobile.isNullOrEmpty()) {
                val intent =
                    Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", senderDetails?.mobile, null))
                startActivity(intent)
            }

            /*val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${expertiseModel.mobile}")
            startActivity(intent)*/
        }

        binding.ivattechment.setOnClickListener(View.OnClickListener {
            attechmentDialog()
        })

    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            BackPressed()
        }

        binding.ivCamera.setOnClickListener {
            if (checkPermission()) {
                imagePickerFromCamera()
            } else {
                isclick="camera"
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
            "multipart/form-data".toMediaTypeOrNull(), profileId.toString()
        )

        val text: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(), binding.edtMessage.text.toString().trim()
        )


        var profileBody: MultipartBody.Part? = null
        if (profilePath.isNotEmpty()) {
            val file = File(profilePath)
            // create RequestBody instance from file
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            profileBody = MultipartBody.Part.createFormData(
                "image", file.name, requestFile
            )
        }
        viewModel.sendMessage(uid, other_uid, text, profileBody)
    }


    private fun AudioMessageCallSendMessageAPI() {
        val uid: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            PrefManager(mContext).getvalue(StaticData.id).toString()
        )

        val other_uid: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(), profileId.toString()
        )

        val text: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(), binding.edtMessage.text.toString().trim()
        )


        var profileBody: MultipartBody.Part? = null
        if (audioPath.isNotEmpty()) {
            val file = File(audioPath)
            // create RequestBody instance from file
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
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
                senderDetails = it.sender_details
                setData(senderDetails!!)
                chatModel.clear()
                chatModel.addAll(it.result)

                if (chatModel.size > 0) {
                    binding.rvChatHistory.visibility = View.VISIBLE
                    binding.tvNoChatHistory.visibility = View.GONE
                    setAdapter(chatModel,senderDetails!!)

                } else {
                    binding.rvChatHistory.visibility = View.GONE
                    binding.tvNoChatHistory.visibility = View.VISIBLE
                }
            } else {
                senderDetails = it.sender_details
                setData(senderDetails!!)
                binding.rvChatHistory.visibility = View.GONE
                binding.tvNoChatHistory.visibility = View.VISIBLE
            }
        })

        viewModel.sendMessageLiveData.observe(this, Observer {
            if (it.status == "1") {
                val result = it.result
                binding.edtMessage.setText("")
                profilePath = ""
                val insertChatItem = ChatModel(
                    result.id,
                    result.uid,
                    result.other_uid,
                    result.text,
                    result.image,
                    result.added_on,
                    result.user_type
                )
                chatModel.add(insertChatItem)
                val position = chatModel.size - 1
                binding.adapter?.notifyItemInserted(position)
                if (binding.tvNoChatHistory.visibility==View.VISIBLE) {
                    binding.tvNoChatHistory.visibility = View.GONE
                }
                if (chatModel.size > 2) {
                    binding.rvChatHistory.scrollToPosition(binding.adapter?.itemCount!!.toInt() - 1)
                }


            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setData(senderDetails: SenderDetails) {
        Glide.with(mContext).load(senderDetails.image).placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile).into(binding.cvProfile)
        binding.tvName.setText(senderDetails.name)

        if (senderDetails.is_online == "1") {
            binding.tvStatus.text = "Online"
            binding.ivStatus.setImageResource(R.drawable.circle_green)
        } else {
            binding.tvStatus.text = "Offline"
            binding.ivStatus.setImageResource(R.drawable.circle_grey)
        }

    }

    private fun setAdapter(chatModel: ArrayList<ChatModel>,senderDetails: SenderDetails) {
        binding.apply {
            rvChatHistory.layoutManager = LinearLayoutManager(this@ChatActivity)
            rvChatHistory.setHasFixedSize(true)
            adapter = ChatHistoryAdapter(this@ChatActivity, chatModel,senderDetails,this@ChatActivity)
            rvChatHistory.adapter = adapter
            adapter?.notifyDataSetChanged()
            rvChatHistory.scrollToPosition(adapter?.itemCount!!.toInt() - 1)
        }
    }


    private fun checkPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

        val listPermissionsNeeded = ArrayList<String>()

        // Determine the storage permission based on Android version
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        // Check permissions and add to list if not granted
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }

        if (audioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }

        // Request permissions if needed
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
                            when(isclick){
                                "camera" ->{
                                    imagePickerFromCamera()
                                }

                                "gellery" ->{
                                    imagePickerFromGallery()
                                }
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

                            when(isclick){
                                "camera" ->{
                                    imagePickerFromCamera()
                                }

                                "gellery" ->{
                                    imagePickerFromGallery()
                                }
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
                        profilePath = PathUtils.getRealPath(this, uri!!).toString()
                        mNetworkCallSendMessageAPI()
                    }else{
                        val uri = data!!.data
                        profilePath = PathUtils.getRealPath(this, uri!!).toString()
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

     fun BackPressed() {
        if (isNotification == true) {
            // If started from notification, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            finish()
        } else {
            onBackPressed()
        }
    }


    private fun imagePickerFromCamera()
    {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startForProfileImageResult.launch(intent)
        isCamera = true
        isGallery = false
    }

    private fun sendAudioFile(){
        val uri=Uri.fromFile(File(audioPath))
        audioPath= PathUtils.getRealPath(this,uri).toString()
        AudioMessageCallSendMessageAPI()
    }

    private fun documentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"  // Allow all types of files
        startForProfileImageResult.launch(intent)
        isCamera = false
        isGallery = false
    }

    private fun imagePickerFromGallery()
    {
        ImagePicker.with(mContext).compress(1024).maxResultSize(1080, 1080).galleryOnly()
            .createIntent {
                startForProfileImageResult.launch(it)
            }
        isCamera = false
        isGallery = true
    }


    private fun attechmentDialog(){
        val dialogView = Dialog(this)
        dialogView.setContentView(R.layout.attechment_dialog)

        dialogView.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialogView.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val close=dialogView.findViewById<ImageView>(R.id.close)
        val doc=dialogView.findViewById<RelativeLayout>(R.id.iv_doc)
        val camera=dialogView.findViewById<RelativeLayout>(R.id.iv_camera)
        val gallery=dialogView.findViewById<RelativeLayout>(R.id.iv_gallery)

        close.setOnClickListener(View.OnClickListener {
            dialogView.dismiss()
        })

        doc.setOnClickListener(View.OnClickListener {
            documentPicker()
            dialogView.dismiss()
        })

        camera.setOnClickListener(View.OnClickListener {
            if (checkPermission()) {
                imagePickerFromCamera()
            } else {
                isclick="camera"
                checkPermission()
            }
            dialogView.dismiss()
        })

        gallery.setOnClickListener(View.OnClickListener {

            if (checkPermission()) {
                imagePickerFromGallery()
            } else {
                isclick="gellery"
                checkPermission()
            }
            dialogView.dismiss()
        })



        dialogView.show()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (charSequence != null && charSequence.length >= 1) {
            binding.apply {
                ivattechment.visibility=View.GONE
                ivCamera.visibility=View.GONE
                ivMicrophone.visibility=View.GONE
                ivSend.visibility=View.VISIBLE
            }
        }else if (charSequence?.length!! <= 0) {
            binding.apply {
                ivattechment.visibility=View.VISIBLE
                ivCamera.visibility=View.VISIBLE
                ivMicrophone.visibility=View.VISIBLE
                ivSend.visibility=View.GONE
            }
        }
    }

    private fun setUpForMediaRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        // Use getExternalFilesDir() to get the directory for your app's private external files
        val directory = File(
            getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            "BollyHood/Media/Recording"
        )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        audioPath = "${directory.absolutePath}${File.separator}${System.currentTimeMillis()}.mp3"
        mediaRecorder?.setOutputFile(audioPath)
    }

    override fun isDestroyed(): Boolean {
        return super.isDestroyed()
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        }catch (e:Exception){
            e.printStackTrace()
        }
        binding.adapter?.unregisterReceiver()
    }

    override fun afterTextChanged(p0: Editable?) {

    }


    override fun download(fileName: String) {

    }
}