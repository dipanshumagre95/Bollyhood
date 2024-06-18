package com.app.bollyhood.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityUploadCastingCallBinding

class Upload_CastingCall : AppCompatActivity(),TextWatcher,OnClickListener {

    lateinit var binding:ActivityUploadCastingCallBinding
    private var heightList: ArrayList<String> = arrayListOf()
    private var skinColorList: ArrayList<String> = arrayListOf()
    private var bodyTypeList: ArrayList<String> = arrayListOf()
    private var passPortList: ArrayList<String> = arrayListOf()
    private var ageList: ArrayList<String> = arrayListOf()
    private var height: String = ""
    private var skinColor: String = ""
    private var bodyType: String = ""
    private var passport: String = ""
    private var age: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil.setContentView(this, R.layout.activity_upload_casting_call)

        intiUi()
        addLisnter()
    }

    private fun intiUi()
    {
        binding.edtproductionHouse.addTextChangedListener(this)
        binding.edtSkillNRequiment.addTextChangedListener(this)
        binding.edtWhatYouRole.addTextChangedListener(this)

        setHeightData()
        setSkinColorData()
        setBodyTypeData()
        setPassPortData()
        setAgeData()
    }

    private fun addLisnter()
    {
        binding.acheight.setOnTouchListener { v, event ->
            binding.acheight.showDropDown()
            false
        }

        binding.acPassPort.setOnTouchListener { v, event ->
            binding.acPassPort.showDropDown()
            false
        }


        binding.acage.setOnTouchListener { v, event ->
            binding.acage.showDropDown()
            false
        }

        binding.acSkinColor.setOnTouchListener { v, event ->
            binding.acSkinColor.showDropDown()
            false
        }

        binding.acBodyType.setOnTouchListener { v, event ->
            binding.acBodyType.showDropDown()
            false
        }


        binding.edtproductionHouse.addTextChangedListener(this)
        binding.edtSkillNRequiment.addTextChangedListener(this)
        binding.edtWhatYouRole.addTextChangedListener(this)
        binding.llverifiedProfile.setOnClickListener(this)
        binding.llanyoneApply.setOnClickListener(this)

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (charSequence != null && charSequence.length >= 1) {
            when(charSequence.hashCode()){
                binding.edtproductionHouse.getText().hashCode() ->{
                    binding.lledtProductionHouse.setHintEnabled(true)
                }

                binding.edtWhatYouRole.text.hashCode() ->{
                    binding.textcount.visibility=View.VISIBLE
                    val value = 500 - binding.edtWhatYouRole.text.toString().length
                    binding.textcount.text="$value/500"
                }

                binding.edtSkillNRequiment.text.hashCode() ->{
                    binding.textcount.visibility=View.VISIBLE
                    val value = 500 - binding.edtSkillNRequiment.text.toString().length
                    binding.textcountforskillNrequiment.text="$value/500"
                }
            }
        }else if (charSequence?.length!! <= 0){
            when(charSequence.hashCode()){
                binding.edtproductionHouse.getText().hashCode() ->{
                    binding.lledtProductionHouse.setHintEnabled(false)
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }


    private fun setHeightData() {
        heightList.clear()
        heightList.add("4.7 Ft")
        heightList.add("4.8 Ft")
        heightList.add("4.9 Ft")
        heightList.add("5.0 Ft")
        heightList.add("5.1 Ft")
        heightList.add("5.2 Ft")
        heightList.add("5.3 Ft")
        heightList.add("5.4 Ft")
        heightList.add("5.5 Ft")
        heightList.add("5.6 Ft")
        heightList.add("5.7 Ft")
        heightList.add("5.8 Ft")
        heightList.add("5.9 Ft")
        heightList.add("6.0 Ft")
        heightList.add("6.1 Ft")
        heightList.add("6.2 Ft")
        heightList.add("6.3 Ft")
        heightList.add("6.4 Ft")
        heightList.add("6.5 Ft")
        heightList.add("6.6 Ft")
        heightList.add("6.7 Ft")


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, heightList)
        binding.acheight.threshold = 0
        binding.acheight.dropDownVerticalOffset = 0
        binding.acheight.setAdapter(arrayAdapter)

        binding.acheight.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                height = heightList[position]
            }


    }

    private fun setSkinColorData() {
        skinColorList.clear()
        skinColorList.add("Light Brown")
        skinColorList.add("Medium Brown")
        skinColorList.add("Dark Brown")
        skinColorList.add("Fair")
        skinColorList.add("Wheatish")


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, skinColorList)
        binding.acSkinColor.threshold = 0
        binding.acSkinColor.dropDownVerticalOffset = 0
        binding.acSkinColor.setAdapter(arrayAdapter)

        binding.acSkinColor.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                skinColor = skinColorList[position]
            }


    }

    private fun setBodyTypeData() {
        bodyTypeList.clear()
        bodyTypeList.add("Thin")
        bodyTypeList.add("Average Build")
        bodyTypeList.add("Muscular or Fit ( Gym-Goer )")
        bodyTypeList.add("Big or Healthy")


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, bodyTypeList)
        binding.acBodyType.threshold = 0
        binding.acBodyType.dropDownVerticalOffset = 0
        binding.acBodyType.setAdapter(arrayAdapter)

        binding.acBodyType.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                bodyType = bodyTypeList[position]
            }


    }


    private fun setPassPortData() {
        passPortList.clear()
        passPortList.add("Yes")
        passPortList.add("No")


        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, passPortList)
        binding.acPassPort.threshold = 0
        binding.acPassPort.dropDownVerticalOffset = 0
        binding.acPassPort.setAdapter(arrayAdapter)

        binding.acPassPort.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                passport = passPortList[position]
            }


    }

    private fun setAgeData(){
        ageList.clear()
        ageList.add("18")
        ageList.add("19")
        ageList.add("20")
        ageList.add("21")
        ageList.add("22")
        ageList.add("23")
        ageList.add("24")
        ageList.add("25")
        ageList.add("26")
        ageList.add("27")
        ageList.add("28")
        ageList.add("29")
        ageList.add("30")
        ageList.add("31")
        ageList.add("32")
        ageList.add("33")
        ageList.add("34")
        ageList.add("35")
        ageList.add("36")
        ageList.add("37")
        ageList.add("38")
        ageList.add("39")
        ageList.add("40")
        ageList.add("41")
        ageList.add("42")
        ageList.add("43")
        ageList.add("44")
        ageList.add("45")
        ageList.add("46")
        ageList.add("47")
        ageList.add("48")
        ageList.add("49")
        ageList.add("50")
        ageList.add("51")
        ageList.add("52")
        ageList.add("53")
        ageList.add("54")
        ageList.add("55")
        ageList.add("56")
        ageList.add("57")
        ageList.add("58")
        ageList.add("59")
        ageList.add("60")

        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, ageList)
        binding.acage.threshold = 0
        binding.acage.dropDownVerticalOffset = 0
        binding.acage.setAdapter(arrayAdapter)

        binding.acage.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                age = ageList[position]
            }
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.llverified_profile ->{
                binding.llverifiedProfile.setBackgroundResource(R.drawable.rectangle_black_button)
                binding.llanyoneApply.setBackgroundResource(R.drawable.rectangle_white)
                binding.tvVerifiedProfile.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.tvAnyoneCanApply.setTextColor(ContextCompat.getColor(this, R.color.black))
            }

            R.id.llanyone_apply ->{
                binding.llanyoneApply.setBackgroundResource(R.drawable.rectangle_black_button)
                binding.llverifiedProfile.setBackgroundResource(R.drawable.rectangle_white)
                binding.tvVerifiedProfile.setTextColor(ContextCompat.getColor(this, R.color.black))
                binding.tvAnyoneCanApply.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
        }
    }
}