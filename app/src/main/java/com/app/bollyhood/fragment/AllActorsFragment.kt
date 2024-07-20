package com.app.bollyhood.fragment

import Categorie
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.adapter.AllActorsAdapter
import com.app.bollyhood.databinding.FragmentAllActorsBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.SingleCategoryModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllActorsFragment : Fragment(),OnClickListener,AllActorsAdapter.onItemCLick {

    lateinit var binding:FragmentAllActorsBinding
    private val viewModel: DataViewModel by viewModels()
    private val profileList: ArrayList<SingleCategoryModel> = arrayListOf()
    private val profileSubList: ArrayList<SingleCategoryModel> = arrayListOf()
    private val filteredData: ArrayList<SingleCategoryModel> = arrayListOf()
    private var categorie:String=""
    private var categorie_name:String=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_all_actors, container, false)

        initUI()
        setSearchView()
        addObserver()
        addListner()

        return binding.root
    }

    private fun addListner() {
        binding.let {
            it.ivBack.setOnClickListener(this)
            it.cvProfile.setOnClickListener(this)
            it.tvloadMore.setOnClickListener(this)
        }
    }

    private fun addObserver() {

        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.actorsList.observe(requireActivity(), Observer {
            if (it.status == "1"&&it.result.isNotEmpty()) {
                    binding.tvNodata.visibility=View.GONE
                    binding.tvloadMore.visibility=View.VISIBLE
                    binding.rvprofilesList.visibility=View.VISIBLE
                    profileList.clear()
                    profileList.addAll(it.result)
                    binding.tvListsize.setText("${profileList.size} recent profiles found")
                    if (profileList.size >= 9) {
                        profileSubList.clear()
                        profileSubList.addAll(profileList.subList(0, 9))
                        setCategoryAdapter(profileSubList)
                        binding.tvloadMore.visibility=View.VISIBLE
                    } else {
                        setCategoryAdapter(profileList)
                        binding.tvloadMore.visibility=View.GONE
                    }
            }else{
                binding.tvNodata.visibility=View.VISIBLE
                binding.tvloadMore.visibility=View.GONE
                binding.rvprofilesList.visibility=View.GONE
            }
        })
    }

    private fun initUI() {

        val bundle = arguments
        if (bundle!=null) {
            PrefManager(requireContext()).setvalue(StaticData.previousFragment,bundle.getString(StaticData.previousFragment).toString())
            categorie=bundle.getString(StaticData.categorie).toString()
            categorie_name=bundle.getString(StaticData.name).toString()
            PrefManager(requireContext()).setvalue(StaticData.cate_id,categorie)
            PrefManager(requireContext()).setvalue(StaticData.cate_name,categorie_name)
        }else{
            categorie=PrefManager(requireContext()).getvalue(StaticData.cate_id).toString()
            categorie_name=PrefManager(requireContext()).getvalue(StaticData.cate_name).toString()
        }

        binding.tvHeaderText.setText("Explore 500+ $categorie_name From Mumbai")
        binding.edsearch.setHint("Search $categorie_name here...")

        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)

        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        if (isNetworkAvailable(requireContext())) {
            viewModel.getAllActors(categorie)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun setSearchView()
    {
        binding.edsearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text?.length!! > 3) {
                    setFilteredData(text.toString())
                }else{
                    if (profileList.size >= 9) {
                        profileSubList.clear()
                        profileSubList.addAll(profileList.subList(0, 9))
                        setCategoryAdapter(profileSubList)
                        binding.tvloadMore.visibility=View.VISIBLE
                    } else {
                        setCategoryAdapter(profileList)
                        binding.tvloadMore.visibility=View.GONE
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }


    override fun onResume() {
        super.onResume()
        binding.tvusername.text = "Hi " + (PrefManager(requireContext()).getvalue(StaticData.name)?.split(" ")?.getOrNull(0) ?: "User") + ","

        (requireActivity() as MainActivity).showToolbar(false)
    }

    override fun onClick(item: View?) {
        when(item?.id) {
            R.id.ivBack -> {
                if (PrefManager(requireContext()).getvalue(StaticData.previousFragment).equals("AllCategoryFragment")) {
                    backpress(AllCategoryFragment())
                }else{
                    (requireActivity() as MainActivity).setHomeColor()
                }
            }

            R.id.cvProfile -> {
                 startActivity(Intent(requireContext(),MyProfileActivity::class.java))
            }

            R.id.tvload_more ->{
                setCategoryAdapter(profileList)
                binding.tvloadMore.visibility=View.GONE
            }
        }
    }

    private fun setFilteredData(text: String) {
        filteredData.clear()
        if (!text.isNullOrEmpty()){
            for (category in profileList){
                if (category.name.contains(text,ignoreCase = true))
                {
                    filteredData.add(category)
                }
            }
            binding.actorsAdapter?.updateList(filteredData)
            binding.tvloadMore.visibility=View.GONE
        }
    }

    private fun setCategoryAdapter(profileList: ArrayList<SingleCategoryModel>) {
        binding.apply {
            rvprofilesList.layoutManager =
                GridLayoutManager(requireContext(), 3)
            rvprofilesList.setHasFixedSize(true)
            actorsAdapter = AllActorsAdapter(requireContext(), profileList,this@AllActorsFragment)
            rvprofilesList.adapter = actorsAdapter
            actorsAdapter?.notifyDataSetChanged()
        }
    }

    fun backpress(fragment:Fragment){
        parentFragmentManager.commit {
            replace(R.id.fragment_container,fragment)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onClick(singleCategoryModel: SingleCategoryModel) {
        if (singleCategoryModel!=null){
            when(singleCategoryModel.category_name){
                Categorie.CAMERALIGHT.toString(),Categorie.EVENTPLANNER.toString(),Categorie.MUSICLABEL.toString(),
                Categorie.LOCATIONMANAGER.toString()->{
                    val bundle = Bundle()
                    bundle.putString(StaticData.userModel, Gson().toJson(singleCategoryModel))
                    bundle.putString(StaticData.previousFragment,"AllActosFragment")

                    val companyProfileFragment = CompanyProfileFragment()
                    companyProfileFragment.arguments = bundle
                    (requireActivity() as MainActivity).loadFragment(companyProfileFragment)
                }

                Categorie.ACTOR.toString() ->{
                    val bundle = Bundle()
                    bundle.putString(StaticData.userModel, Gson().toJson(singleCategoryModel))
                    bundle.putString(StaticData.previousFragment,"AllActosFragment")

                    val actorsProfileDetailsFragment = ActorsProfileDetailsFragment()
                    actorsProfileDetailsFragment.arguments = bundle
                    (requireActivity() as MainActivity).loadFragment(actorsProfileDetailsFragment)
                }

                else->{
                    val bundle = Bundle()
                    bundle.putString(StaticData.userModel, Gson().toJson(singleCategoryModel))
                    bundle.putString(StaticData.previousFragment,"AllActosFragment")

                    val profileDetailFragment = ProfileDetailFragment()
                    profileDetailFragment.arguments = bundle
                    (requireActivity() as MainActivity).loadFragment(profileDetailFragment)
                }
            }
        }
    }

}