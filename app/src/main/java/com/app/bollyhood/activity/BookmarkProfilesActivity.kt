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
            val bundle = Bundle().apply {
                putString(StaticData.userModel, Gson().toJson(model))
                putString(StaticData.previousFragment, "BookMark")
            }

            val fragment = when (categorie_name) {
                Categorie.CAMERALIGHT.toString(),
                Categorie.EVENTPLANNER.toString(),
                Categorie.MUSICLABEL.toString(),
                Categorie.LOCATIONMANAGER.toString() -> CompanyProfileFragment()
                Categorie.ACTOR.toString() -> ActorsProfileDetailsFragment()
                Categorie.SINGER.toString() -> SingerProfileDetailsFragment()
                Categorie.DJ.toString() -> DjProfileDetailFragment()
                Categorie.DANCER.toString() -> DancerProfileDetailFragment()
                Categorie.INFLUENCER.toString() -> InfluencerProfileDetailFragment()

                else -> ProfileDetailFragment()
            }

            fragment.arguments = bundle
            loadFragment(fragment)
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