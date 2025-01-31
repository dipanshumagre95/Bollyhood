package com.app.bollyhood.activity

import Categorie
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityBookmarkProfilesBinding
import com.app.bollyhood.fragment.CompanyProfileFragment
import com.app.bollyhood.fragment.ProfileDetailFragment
import com.app.bollyhood.fragment.profileDetailsFragments.ActorsProfileDetailsFragment
import com.app.bollyhood.fragment.profileDetailsFragments.DancerProfileDetailFragment
import com.app.bollyhood.fragment.profileDetailsFragments.DjProfileDetailFragment
import com.app.bollyhood.fragment.profileDetailsFragments.InfluencerProfileDetailFragment
import com.app.bollyhood.fragment.profileDetailsFragments.SingerProfileDetailsFragment
import com.app.bollyhood.model.ProfileModel
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkProfilesActivity : AppCompatActivity() {

    lateinit var binding: ActivityBookmarkProfilesBinding
    private val viewModel: DataViewModel by viewModels()
    lateinit var profileModel: ProfileModel
    lateinit var categorie_name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bookmark_profiles)

        InitUi()
    }

    private fun InitUi() {
        profileModel = Gson().fromJson(
            intent.getStringExtra(StaticData.userModel),
            ProfileModel::class.java
        )
        categorie_name= intent.getStringExtra(StaticData.cate_name).toString()
        setProfileUi(profileModel)
    }

    private fun setProfileUi(model:ProfileModel) {
        if (model!=null){
            when(categorie_name){
                Categorie.CAMERALIGHT.toString(),Categorie.EVENTPLANNER.toString(),Categorie.MUSICLABEL.toString(),
                Categorie.LOCATIONMANAGER.toString()->{
                    val bundle = Bundle()
                    bundle.putString(StaticData.userModel, Gson().toJson(model))
                    bundle.putString(StaticData.previousFragment,"BookMark")

                    val companyProfileFragment = CompanyProfileFragment()
                    companyProfileFragment.arguments = bundle
                    loadFragment(companyProfileFragment)
                }

                Categorie.ACTOR.toString() ->{
                    val bundle = Bundle()
                    bundle.putString(StaticData.userModel, Gson().toJson(model))
                    bundle.putString(StaticData.previousFragment,"BookMark")

                    val actorsProfileDetailsFragment = ActorsProfileDetailsFragment()
                    actorsProfileDetailsFragment.arguments = bundle
                    loadFragment(actorsProfileDetailsFragment)
                }

                Categorie.SINGER.toString() ->{
                    val bundle = Bundle()
                    bundle.putString(StaticData.userModel, Gson().toJson(model))
                    bundle.putString(StaticData.previousFragment,"BookMark")

                    val singerProfileDetailFragment = SingerProfileDetailsFragment()
                    singerProfileDetailFragment.arguments = bundle
                    loadFragment(singerProfileDetailFragment)
                }

                Categorie.DJ.toString() ->{
                    val bundle = Bundle()
                    bundle.putString(StaticData.userModel, Gson().toJson(model))
                    bundle.putString(StaticData.previousFragment,"BookMark")

                    val djProfileDetailFragment = DjProfileDetailFragment()
                    djProfileDetailFragment.arguments = bundle
                    loadFragment(djProfileDetailFragment)
                }

                Categorie.DANCER.toString() ->{
                    val bundle = Bundle()
                    bundle.putString(StaticData.userModel, Gson().toJson(model))
                    bundle.putString(StaticData.previousFragment,"BookMark")

                    val dancerProfileDetailFragment = DancerProfileDetailFragment()
                    dancerProfileDetailFragment.arguments = bundle
                    loadFragment(dancerProfileDetailFragment)
                }

                Categorie.INFLUENCER.toString() ->{
                    val bundle = Bundle()
                    bundle.putString(StaticData.userModel, Gson().toJson(model))
                    bundle.putString(StaticData.previousFragment,"BookMark")

                    val influencerProfileDetailFragment = InfluencerProfileDetailFragment()
                    influencerProfileDetailFragment.arguments = bundle
                    loadFragment(influencerProfileDetailFragment)
                }

                else->{
                    val bundle = Bundle()
                    bundle.putString(StaticData.userModel, Gson().toJson(model))
                    bundle.putString(StaticData.previousFragment,"BookMark")

                    val profileDetailFragment = ProfileDetailFragment()
                    profileDetailFragment.arguments = bundle
                    loadFragment(profileDetailFragment)
                }
            }
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_containers, fragment)
            .commit()
    }

    fun closeActivity(){
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}