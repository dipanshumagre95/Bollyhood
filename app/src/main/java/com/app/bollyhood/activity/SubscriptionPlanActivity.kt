package com.app.bollyhood.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.adapter.PlanAdapter
import com.app.bollyhood.databinding.ActivitySubscriptionPlanBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.PlanModel
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SubscriptionPlanActivity : AppCompatActivity(), PlanAdapter.onItemClick,PaymentResultWithDataListener,OnClickListener {

    lateinit var binding: ActivitySubscriptionPlanBinding
    lateinit var mContext: SubscriptionPlanActivity
    private val viewModel: DataViewModel by viewModels()
    private val planList: ArrayList<PlanModel> = arrayListOf()
    private var checkout = Checkout()
    private var email:String=""
    private var mobileNumber:String=""
    private var orderId:String=""
    private var receipt = "receipt_${System.currentTimeMillis()}"

    var plan_id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subscription_plan)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {

        checkout.setKeyID(StaticData.RAZORPAY_API_KEY)
        email= PrefManager(this).getvalue(StaticData.email).toString()
        mobileNumber= PrefManager(this).getvalue(StaticData.mobile).toString()

        if (isNetworkAvailable(mContext)) {
            viewModel.getPlan()
        } else {
            showCustomToast(this,StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
        }
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnJoin.setOnClickListener(this)

        /*binding.tvCheckOut.setOnClickListener {
            if (plan_id.isEmpty()) {
                Toast.makeText(mContext, "Please select plan", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sendPayment(
                    System.currentTimeMillis().toString(),
                    PrefManager(mContext).getvalue(StaticData.id).toString(),
                    plan_id
                )
            }
        }*/
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.subscriptionPlanLiveData.observe(this, Observer {
            if (it.status.equals("1")) {
                planList.clear()
                planList.addAll(it.result)
                setAdapter(planList)
            } else {
                showCustomToast(this,StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })

        viewModel.successData.observe(this, Observer {
            if (it.status.equals("1")) {
                startActivity(Intent(mContext, PaymentSuccessActivity::class.java))
                finish()
                showCustomToast(this,StaticData.successMsg,it.msg,StaticData.success)
            } else {
                showCustomToast(this,StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })
    }

    private fun setAdapter(planList: ArrayList<PlanModel>) {
       /* binding.apply {
            rvPlan.layoutManager = LinearLayoutManager(mContext)
            rvPlan.setHasFixedSize(true)
            adapter = PlanAdapter(mContext, planList, this@SubscriptionPlanActivity)
            rvPlan.adapter = adapter
            adapter?.notifyDataSetChanged()
        }*/
    }

    private fun startPayment() {
        try {
            val options = JSONObject()
            options.put("name", "Bollyhood Private Limited")
            options.put("description", " Exclusive membership")
           // options.put("order_id", orderId)
            options.put("image","https://bollyhood.app/admin/resources/image/Logo.png")
            options.put("currency", StaticData.currency)
            options.put("theme.color", "#FE885B")
            options.put("amount", StaticData.amount)

            val prefill = JSONObject()
            prefill.put("email", email)
            prefill.put("contact", mobileNumber)

            options.put("prefill", prefill)
            checkout.open(this, options)
        } catch (e: Exception) {
            showCustomToast(this,"Error in payment:",e.message.toString(),StaticData.alert)
            e.printStackTrace()
        }
    }



    override fun onClick(position: Int, planModel: PlanModel) {
        plan_id = planModel.plan_id
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, p1: PaymentData?) {
        Toast.makeText(this,"onPaymentSuccess",Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(errorCode: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this,"onPaymentError",Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.btn_join ->{
               startPayment()
            }
        }
    }
}