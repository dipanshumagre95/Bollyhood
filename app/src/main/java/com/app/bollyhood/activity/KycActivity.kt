package com.app.bollyhood.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityKycBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PermissionUtils
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
class KycActivity : AppCompatActivity(),OnClickListener {

    lateinit var binding:ActivityKycBinding
    private val viewModel: DataViewModel by viewModels()
    private var isCamera:String=""
    private var adharFrontPhoto:String=""
    private var adharBackPhoto:String=""
    private var selfeePhoto:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_kyc)

        initUI()
        addListner()
        addObserevs()
    }

    private fun addObserevs() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.successData.observe(this, Observer {
            if (it.status == "1") {
                   binding.photoView.visibility=View.GONE
                   binding.succssView.visibility=View.VISIBLE
                   StartAnimation()
            } else {
                showCustomToast(this,StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })
    }

    private fun addListner() {
        binding.apply {
            ivBack.setOnClickListener(this@KycActivity)
            btnAdharSubmit.setOnClickListener(this@KycActivity)
            btnPhotosubmit.setOnClickListener(this@KycActivity)
            btnphotoUpload.setOnClickListener(this@KycActivity)
            btnfrontUpload.setOnClickListener(this@KycActivity)
            btnbackUpload.setOnClickListener(this@KycActivity)
            btnBacktohome.setOnClickListener(this@KycActivity)
        }
    }

    private fun initUI() {
        setAdharView()
        setSpannableString()
        binding.photoView.visibility=View.VISIBLE
    }

    private fun setSelfeView()
    {
        binding.apply {
            tvStatus.setText("Smile Please \uD83E\uDD2D")

            percentagePhoto.visibility=View.VISIBLE
            lluploadphoto.visibility=View.VISIBLE
            succssView.visibility=View.GONE

            percentageAdhaar.visibility=View.GONE
            llfrontNback.visibility=View.GONE
        }
    }

    private fun setAdharView()
    {
        binding.apply {
            tvStatus.setText("Complete KYC")

            percentagePhoto.visibility=View.GONE
            lluploadphoto.visibility=View.GONE
            succssView.visibility=View.GONE

            percentageAdhaar.visibility=View.VISIBLE
            llfrontNback.visibility=View.VISIBLE
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.ivBack ->{
                onBackPressed()
            }

            R.id.btn_adharSubmit ->{
                binding.pbLoadData.visibility=View.VISIBLE
                GlobalScope.launch(Dispatchers.Main) {
                    delay(5000)
                    binding.pbLoadData.visibility=View.GONE
                    setSelfeView()
                }
            }

            R.id.btn_photosubmit ->{
                uploadKycApi()
            }

            R.id.btnphotoUpload ->{
                if (PermissionUtils.isCamera(this)){
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT)
                    startForProfileImageResult.launch(intent)
                    isCamera=Camera.SELFEE.toString()
                }else{
                    PermissionUtils.requestCameraPermission(this)
                }
            }

            R.id.btnbackUpload ->{
                if (PermissionUtils.isCamera(this)) {
                    openCamera()
                    isCamera = Camera.ADHARBACK.toString()
                }else{
                    PermissionUtils.requestCameraPermission(this)
                }
            }

            R.id.btnfrontUpload ->{
                if (PermissionUtils.isCamera(this)) {
                    openCamera()
                    isCamera = Camera.ADHARFRONT.toString()
                }else{
                    PermissionUtils.requestCameraPermission(this)
                }
            }

            R.id.btn_backtohome ->{
                onBackPressed()
            }
        }
    }

    private fun openCamera()
    {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startForProfileImageResult.launch(intent)
    }

    private fun uploadKycApi()
    {
        if (isNetworkAvailable(this)) {

            val user_Id: RequestBody = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                PrefManager(this).getvalue(StaticData.id).toString()
            )

            var frontImageBody: MultipartBody.Part? = null
            if (adharFrontPhoto.isNotEmpty()) {
                val file = File(adharFrontPhoto)
                val requestFile =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                frontImageBody = MultipartBody.Part.createFormData(
                    "front_image", file.name, requestFile
                )
            }

            var backImageBody: MultipartBody.Part? = null
            if (adharBackPhoto.isNotEmpty()) {
                val file = File(adharBackPhoto)
                val requestFile =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                backImageBody = MultipartBody.Part.createFormData(
                    "back_image", file.name, requestFile
                )
            }

            var selfeePhotoBody: MultipartBody.Part? = null
            if (selfeePhoto.isNotEmpty()) {
                val file = File(selfeePhoto)
                val requestFile =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                selfeePhotoBody = MultipartBody.Part.createFormData(
                    "image", file.name, requestFile
                )
            }

            viewModel.uploadKyc(
                frontImageBody,
                backImageBody,
                selfeePhotoBody,
                user_Id
            )
        } else {
            showCustomToast(this,StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
        }
    }

    private fun setSpannableString() {
        val spanTxt = SpannableStringBuilder(
            "If you are facing any difficulties, please get in \ntouch with us on"
        )

        spanTxt.setSpan(
            ForegroundColorSpan(Color.parseColor("#626262")),
            0,
            spanTxt.length,
            0
        )

        spanTxt.append(" Whatsapp")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWhatsAppChat()
            }
        }, spanTxt.length - " Whatsapp".length, spanTxt.length, 0)


        spanTxt.setSpan(
            ForegroundColorSpan(Color.parseColor("#0980F3")),
            spanTxt.length - " Whatsapp".length,
            spanTxt.length,
            0
        )

        binding.tvWhatsaap.setMovementMethod(LinkMovementMethod.getInstance())
        binding.tvWhatsaap.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode

            when (resultCode) {
                Activity.RESULT_OK -> {
                        val imageBitmap = result.data?.extras?.get("data") as Bitmap

                    when(isCamera){
                        Camera.SELFEE.toString() ->{
                            selfeePhoto=saveImageToStorage(imageBitmap).toString()
                           }

                        Camera.ADHARFRONT.toString() ->{
                            adharFrontPhoto=saveImageToStorage(imageBitmap).toString()
                           }

                        Camera.ADHARBACK.toString() ->{
                            adharBackPhoto=saveImageToStorage(imageBitmap).toString()
                           }
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

    fun StartAnimation()
    {
        val scaleX = ObjectAnimator.ofFloat(binding.successIcon, "scaleX", 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.successIcon, "scaleY", 0f, 1f)
        val fadeIn = ObjectAnimator.ofFloat(binding.tvkycsuccess, "alpha", 0f, 1f)
        val translateYView1 = ObjectAnimator.ofFloat(binding.tvthanksSuccess, "translationY", -100f, 0f)
        val fadeInView1 = ObjectAnimator.ofFloat(binding.tvthanksSuccess, "alpha", 0f, 1f)
        val translateYView2 = ObjectAnimator.ofFloat(binding.btnBacktohome, "translationY", 100f, 0f)
        val fadeInView2 = ObjectAnimator.ofFloat(binding.btnBacktohome, "alpha", 0f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, fadeIn, translateYView1, fadeInView1, translateYView2, fadeInView2)
        animatorSet.duration = 1000
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    private fun openWhatsAppChat() {
        val url = "https://wa.me/9920050859"
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
        }
    }
}

enum class Camera(val camera: String) {
    SELFEE("Selfee"),
    ADHARFRONT("AdharFront"),
    ADHARBACK("AdharBack");

    override fun toString(): String {
        return camera
    }
}