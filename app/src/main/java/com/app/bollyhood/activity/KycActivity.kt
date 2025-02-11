package com.app.bollyhood.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityKycBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

@AndroidEntryPoint
class KycActivity : AppCompatActivity(),OnClickListener,TextWatcher {

    lateinit var binding:ActivityKycBinding
    private val viewModel: DataViewModel by viewModels()
    private var isEditing=false

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
                showLineAnimation(binding.llLine,binding.percentageAdhaar)
                showWithSlideAnimation(binding.otpView, binding.addharView, false)
            } else {
                showCustomToast(this, StaticData.pleaseTryAgain,it.msg, StaticData.alert)
            }
        })
    }

    private fun addListner() {
        binding.apply {
            btnAgree.setOnClickListener(this@KycActivity)
            llBack.setOnClickListener(this@KycActivity)
            btnSubmitNumber.setOnClickListener(this@KycActivity)
            tvVerify.setOnClickListener(this@KycActivity)
            btnBacktohome.setOnClickListener(this@KycActivity)
            edtAdharNumber.addTextChangedListener(this@KycActivity)
        }
    }

    private fun initUI() {
        setFrontView(true)
        setSpannableString()
        setOtpView()
    }

    private fun setFrontView(isFont:Boolean) {
        binding.apply {
            if (isFont) {
                firtpageview.visibility=View.VISIBLE
                adharCardView.visibility=View.GONE
            } else {
                showWithSlideAnimation(adharCardView, firtpageview, false)
            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.llBack ->{
                finish()
            }

            R.id.btnAgree ->{
                setFrontView(false)
            }

            R.id.btnSubmitNumber ->{
               // sendNumberForApi()
                showLineAnimation(binding.llLine,binding.percentageAdhaar)
                showWithSlideAnimation(binding.otpView, binding.addharView, false)
            }

            R.id.tvVerify ->{
                showWithSlideAnimation(binding.succssView,binding.adharCardView,false)
                StartAnimation()
            }

            R.id.btn_backtohome ->{
                finish()
            }
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

    fun showWithSlideAnimation(viewToShow: View, viewToHide: View, isLeftToRight: Boolean) {
        ObjectAnimator.ofFloat(viewToHide, "translationX", 0f, if (isLeftToRight) viewToHide.width.toFloat() else -viewToHide.width.toFloat()).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
            start()
        }.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                viewToHide.visibility = View.GONE
                viewToShow.translationX = if (isLeftToRight) -viewToShow.width.toFloat() else viewToShow.width.toFloat()
                viewToShow.visibility = View.VISIBLE

                ObjectAnimator.ofFloat(viewToShow, "translationX", viewToShow.translationX, 0f).apply {
                    duration = 300
                    interpolator = DecelerateInterpolator()
                    start()
                }
            }
        })
    }

    fun showLineAnimation(parentView:View,parsentageView:View)
    {
        parentView.post {
            val parentWidth = parentView.width
            val targetWidth = parentWidth / 2

            parsentageView.visibility = View.VISIBLE

            val animator = ValueAnimator.ofInt(parsentageView.width, targetWidth)
            animator.duration = 200
            animator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                val params = parsentageView.layoutParams
                params.width = animatedValue
                parsentageView.layoutParams = params
            }
            animator.start()
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

    private fun sendNumberForApi()
    {
        if (isNetworkAvailable(this)) {

            val user_Id: RequestBody = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                PrefManager(this).getvalue(StaticData.id).toString()
            )

            /*viewModel.uploadKyc(
                "frontImageBody",
                "backImageBody",
                "selfeePhotoBody",
                user_Id
            )*/
        } else {
            showCustomToast(this,StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
        }
    }

    private fun setOtpView()
    {
        binding.edtNumber1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable != null) {
                    if (editable.length == 1) binding.edtNumber2.requestFocus()
                }
            }
        })

        binding.edtNumber2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable != null) {
                    if (editable.length == 1) binding.edtNUmber3.requestFocus()
                }
            }
        })


        binding.edtNUmber3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable != null) {
                    if (editable.length == 1) binding.edtNumber4.requestFocus()
                }
            }
        })

        binding.edtNumber4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable != null) {
                    if (editable.length == 1) binding.edtNumber5.requestFocus()
                }
            }
        })

        binding.edtNumber5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable != null) {
                    if (editable.length == 1) binding.edtNumber6.requestFocus()
                }
            }
        })


        binding.edtNumber1.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })


        binding.edtNumber2.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (binding.edtNumber2.text.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.edtNumber1.requestFocus()
                }
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })


        binding.edtNUmber3.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (binding.edtNUmber3.text.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.edtNumber2.requestFocus()
                }
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })

        binding.edtNumber4.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (binding.edtNumber4.text.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.edtNUmber3.requestFocus()
                }
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })

        binding.edtNumber5.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (binding.edtNumber5.text.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.edtNumber4.requestFocus()
                }
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })

        binding.edtNumber6.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                //this is for backspace
                if (binding.edtNumber6.text.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.edtNumber5.requestFocus()
                }
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return@OnKeyListener false
            }
            false
        })

    }

    override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(text: Editable?) {
        if (isEditing || text.isNullOrEmpty()) return

        isEditing = true

        val cleanText = text.toString().replace("-", "") // Remove existing dashes

        val limitedText = if (cleanText.length > 12) cleanText.substring(0, 12) else cleanText

        val formattedText = StringBuilder()

        for (i in limitedText.indices) {
            formattedText.append(limitedText[i])
            if ((i + 1) % 4 == 0 && i + 1 < limitedText.length) {
                formattedText.append("-") // Insert dash after every 4 characters
            }
        }

        binding.edtAdharNumber.setText(formattedText.toString())
        binding.edtAdharNumber.setSelection(formattedText.length) // Move cursor to end

        isEditing = false
    }
}