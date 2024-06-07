package com.app.bollyhood.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.activity.CastingDetailsActivity
import com.app.bollyhood.databinding.AdpCastingCallsBinding
import com.app.bollyhood.model.CastingCallModel
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson


class CastingCallsAdapter(
    val mContext: Context,
    val castingModels: ArrayList<CastingCallModel>
) : RecyclerView.Adapter<CastingCallsAdapter.MyViewHolder>() {

    var expanded:Boolean=true

    private val backgroundColors = intArrayOf(
        R.drawable.rectangle_castingcall1,
        R.drawable.rectangle_castingcall2,
        R.drawable.rectangle_castingcall3
    )

    private val locationColors = intArrayOf(
        R.drawable.rectangle_loc_blue,
        R.drawable.rectangle_loc_red,
        R.drawable.rectangle_loc_yellow
    )


    private val verifiedColors = intArrayOf(
        R.drawable.rectangle_loc_blue,
        R.drawable.rectangle_loc_red,
        R.drawable.rectangle_loc_yellow
    )


    private val shiftColors = intArrayOf(
        R.drawable.rectangle_loc_blue,
        R.drawable.rectangle_loc_red,
        R.drawable.rectangle_loc_yellow
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            AdpCastingCallsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    private fun setSpannableString(
        text: String,
        button: String,
        fulltext: String,
        tvDesc: MaterialTextView
    ) {
        val spanTxt = SpannableStringBuilder(text).append(" $button")

        val start = spanTxt.length - button.length
        val end = spanTxt.length

        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (expanded) {
                    setSpannableString(fulltext, "read less", text, tvDesc)
                    expanded=false
                }else{
                    setSpannableString(fulltext, "read more", text, tvDesc)
                    expanded=true
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spanTxt.setSpan(ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvDesc.movementMethod = LinkMovementMethod.getInstance()
        tvDesc.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    override fun getItemCount(): Int {
        return castingModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.model = castingModels[position]

        if (castingModels[position].company_logo.isNotEmpty()) {
            Glide.with(mContext).load(castingModels[position].company_logo)
                .into(holder.binding.ivLogo)
        }

        holder.binding.tvShift.text = castingModels[position].shifting + " Hr Shift"

        if (castingModels[position].organization.length > 27){
            val name=castingModels[position].organization
            holder.binding.tvName.text = name.split(" ").get(0)+" "+name.split(" ").get(1)
        }else {
            holder.binding.tvName.text = castingModels[position].organization
        }

        if (castingModels[position].company_name.length > 35){
            val name=castingModels[position].company_name
            holder.binding.tvcompanyname.text = name?.take(35)
        }else {
            holder.binding.tvcompanyname.text = castingModels[position].company_name
        }

        if (castingModels[position].description.length > 150){
            val shorttext=castingModels[position].description.substring(0,150)
            setSpannableString(shorttext,"read more",castingModels[position].description,holder.binding.tvDesc)
        }else{
            holder.binding.tvDesc.text = castingModels[position].description
        }
        holder.binding.tvDate.text = castingModels[position].date

        if (castingModels[position].price_discussed.equals("1")) {
            holder.binding.tvSalary.text = "T B D"
        } else {
            holder.binding.tvSalary.text = "₹" + castingModels[position].price+"/pd"
        }

        when (castingModels[position].type) {
            "blue" -> {
                holder.binding.llMain.setBackgroundResource(R.drawable.main_blue)
                holder.binding.lldescriptions.setBackgroundResource(R.drawable.description_blue)
                holder.binding.lllocation.setBackgroundResource(R.drawable.rectangle_loc_blue)
                holder.binding.llverified.setBackgroundResource(R.drawable.rectangle_loc_blue)
                holder.binding.llshift.setBackgroundResource(R.drawable.rectangle_loc_blue)

            }
            "red" -> {
                holder.binding.llMain.setBackgroundResource(R.drawable.main_red)
                holder.binding.lldescriptions.setBackgroundResource(R.drawable.description_red)
                holder.binding.lllocation.setBackgroundResource(R.drawable.rectangle_loc_red)
                holder.binding.llverified.setBackgroundResource(R.drawable.rectangle_loc_red)
                holder.binding.llshift.setBackgroundResource(R.drawable.rectangle_loc_red)
            }

            "black" ->{
                holder.binding.llMain.setBackgroundResource(R.drawable.main_black)
                holder.binding.lldescriptions.setBackgroundResource(R.drawable.description_black)
                holder.binding.lllocation.setBackgroundResource(R.drawable.rectangle_loc_black)
                holder.binding.llverified.setBackgroundResource(R.drawable.rectangle_loc_black)
                holder.binding.llshift.setBackgroundResource(R.drawable.rectangle_loc_black)
            }

            else -> {
                holder.binding.llMain.setBackgroundResource(R.drawable.main_yellow)
                holder.binding.lldescriptions.setBackgroundResource(R.drawable.description_yellow)
                holder.binding.lllocation.setBackgroundResource(R.drawable.rectangle_loc_yellow)
                holder.binding.llverified.setBackgroundResource(R.drawable.rectangle_loc_yellow)
                holder.binding.llshift.setBackgroundResource(R.drawable.rectangle_loc_yellow)
            }
        }


        holder.binding.tvView.setOnClickListener {
            mContext.startActivity(
                Intent(mContext, CastingDetailsActivity::class.java)
                    .putExtra("type", castingModels[position].type)
                    .putExtra("model", Gson().toJson(castingModels[position]))
            )
        }
    }


    class MyViewHolder(val binding: AdpCastingCallsBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

    }
}