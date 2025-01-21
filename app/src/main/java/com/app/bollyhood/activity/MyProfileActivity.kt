package com.app.bollyhood.activity

import Categorie
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityMyProfileBinding
import com.app.bollyhood.fragment.editprofilefragments.ActorsEditProfileFragment
import com.app.bollyhood.fragment.editprofilefragments.AnchorEditProfileFragment
import com.app.bollyhood.fragment.editprofilefragments.CompanyEditProfileFragment
import com.app.bollyhood.fragment.editprofilefragments.DancerEditProfileFragment
import com.app.bollyhood.fragment.editprofilefragments.DjEditProfileFragment
import com.app.bollyhood.fragment.editprofilefragments.DopNDirectorEditFragment
import com.app.bollyhood.fragment.editprofilefragments.InfluencerEditProfileFragment
import com.app.bollyhood.fragment.editprofilefragments.LyricsWriterEditProfileFragment
import com.app.bollyhood.fragment.editprofilefragments.MusicialBandEditProfileFragment
import com.app.bollyhood.fragment.editprofilefragments.SingerEditProfileFragment
import com.app.bollyhood.model.CategoryModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileActivity : AppCompatActivity(){

    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 2
    }

    lateinit var binding: ActivityMyProfileBinding
    lateinit var mContext: MyProfileActivity
    private var categories:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)
        mContext = this

        setProfileUi()
    }

    private fun setProfileUi() {
        if (PrefManager(mContext).getvalue(StaticData.category)?.isNotEmpty()==true) {
            val gson = Gson()
            val categoryListType = object : TypeToken<List<CategoryModel>>() {}.type
            val categoriesList: List<CategoryModel> =
                gson.fromJson(PrefManager(mContext).getvalue(StaticData.category), categoryListType)
            categories=categoriesList[0].category_name
            when(categories){
                Categorie.SINGER.toString()->{
                    loadFragment(SingerEditProfileFragment())
                }

               Categorie.DJ.toString()->{
                   loadFragment(DjEditProfileFragment())
                }

                Categorie.ACTOR.toString() ->{
                    loadFragment(ActorsEditProfileFragment())
                }

                Categorie.DANCER.toString() ->{
                    loadFragment(DancerEditProfileFragment())
                }

                Categorie.INFLUENCER.toString() ->{
                    loadFragment(InfluencerEditProfileFragment())
                }

                Categorie.DOP.toString() ->{
                    loadFragment(DopNDirectorEditFragment())
                }

                Categorie.ANCHORS.toString() ->{
                    loadFragment(AnchorEditProfileFragment())
                }

                Categorie.LYRICSWRITER.toString() ->{
                    loadFragment(LyricsWriterEditProfileFragment())
                }

                Categorie.MUSICBAND.toString() ->{
                    loadFragment(MusicialBandEditProfileFragment())
                }

                Categorie.CAMERALIGHT.toString(),Categorie.EVENTPLANNER.toString()
                    ,Categorie.CASTINGCALLS.toString(),Categorie.MUSICLABEL.toString(),
                    Categorie.LOCATIONMANAGER.toString()->{
                    loadFragment(CompanyEditProfileFragment())
                }

                Categorie.PRODUCTIONHOUSE.toString()->{
                    loadFragment(CompanyEditProfileFragment())
                }
            }
        }
    }

    fun loadFragment(fragment: Fragment) {
        // load fragment
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