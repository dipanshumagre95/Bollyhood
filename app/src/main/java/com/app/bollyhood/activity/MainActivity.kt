package com.app.bollyhood.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityMainBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.fragment.AllCastingCallFragment
import com.app.bollyhood.fragment.AllCategoryFragment
import com.app.bollyhood.fragment.ChatFragment
import com.app.bollyhood.fragment.HomeFragment
import com.app.bollyhood.fragment.ProfileFragment
import com.app.bollyhood.model.CategoryModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var mContext: MainActivity
    private var isOpenScreen = "Home"
    private val viewModel: DataViewModel by viewModels()
    private var fragment:Fragment=HomeFragment()
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mContext = this
        if (isNetworkAvailable(this)){
            setFragment()
            askNotificationPermission()
            addListner()
            addObsereves()
            setHomeColor()
        }else{
            binding.noInternetPage.visibility=View.VISIBLE
        }
    }

    private fun setFragment() {
        if (PrefManager(mContext).getvalue(StaticData.category)?.isNotEmpty()==true){
            val gson = Gson()
            val categoryListType = object : TypeToken<List<CategoryModel>>() {}.type
            val categories: List<CategoryModel> = gson.fromJson(PrefManager(mContext).getvalue(StaticData.category), categoryListType)

            try {
                when (categories[0].category_name) {
                    "Casting Calls" -> {
                        fragment = AllCastingCallFragment()
                    }

                    else -> {
                        fragment = HomeFragment()
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
               // Log.e(TAG, "PERMISSION_GRANTED")
              // FCM SDK (and your app) can post notifications.
            } else {
//                Log.e(TAG, "NO_PERMISSION")
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
//            Toast.makeText(
//                this, "${getString(R.string.app_name)} can't post notifications without Notification permission",
//                Toast.LENGTH_LONG
//            ).show()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(settingsIntent)
            }

        }
    }

    private fun addListner() {

        if (PrefManager(mContext).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(mContext)
                .load(PrefManager(mContext).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        binding.apply {

            binding.cvProfile.setOnClickListener {
                startActivity(Intent(this@MainActivity, MyProfileActivity::class.java))
            }


            llHome.setOnClickListener {
                if (isOpenScreen != "Home") {
                    setHomeColor()
                }
            }
            llBookings.setOnClickListener {
                if (isOpenScreen != "Bookings") {
                    setBookingColor()
                }
            }
            llChat.setOnClickListener {
                if (isOpenScreen != "Chat") {
                    setChatColor()
                }
            }
            llProfile.setOnClickListener {
                if (isOpenScreen != "Profile") {
                    setProfileColor()
                }
            }
        }
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })
    }

    fun showToolbar(isVisible: Boolean) {
        if (isVisible) {
            binding.rrMain.visibility = View.VISIBLE
        } else {
            binding.rrMain.visibility = View.GONE
        }
    }



     fun setHomeColor() {
        isOpenScreen = "Home"
        binding.apply {
            ivHome.setColorFilter(
                ContextCompat.getColor(
                    this@MainActivity,
                    android.R.color.holo_red_light
                )
            )
            tvHome.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            vHome.visibility=View.VISIBLE

            ivBookings.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvBookings.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vBooking.visibility=View.GONE

            ivChat.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvChat.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vChat.visibility=View.GONE

            ivProfile.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvProfile.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vProfile.visibility=View.GONE
        }

        loadFragment(fragment)

    }

    private fun setBookingColor() {
        isOpenScreen = "Bookings"
        binding.apply {
            ivHome.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvHome.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vHome.visibility=View.GONE

            ivBookings.setColorFilter(
                ContextCompat.getColor(
                    this@MainActivity,
                    android.R.color.holo_red_light
                )
            )
            tvBookings.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            vBooking.visibility=View.VISIBLE

            ivChat.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvChat.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vChat.visibility=View.GONE

            ivProfile.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvProfile.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vProfile.visibility=View.GONE
        }


        if (PrefManager(mContext).getvalue(StaticData.user_type).equals("2")) {
            loadFragment(AllCategoryFragment())
        } else {
            loadFragment(AllCategoryFragment())

        }
    }

    override fun onBackPressed() {
        showExitAppDialog()
    }

    private fun showExitAppDialog() {
        val alert = AlertDialog.Builder(this@MainActivity)
        alert.setMessage("Are you sure you want to exit app?")
        alert.setPositiveButton("Yes") { dialog, which ->
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finishAffinity()
        }
        alert.setNegativeButton(
            "No"
        ) { dialog, which -> dialog.dismiss() }
        alert.show()
    }


    private fun setChatColor() {
        isOpenScreen = "Chat"
        binding.apply {
            ivHome.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvHome.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vHome.visibility=View.GONE

            ivBookings.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvBookings.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vBooking.visibility=View.GONE

            ivChat.setColorFilter(
                ContextCompat.getColor(
                    this@MainActivity,
                    android.R.color.holo_red_light
                )
            )
            tvChat.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            vChat.visibility=View.VISIBLE

            ivProfile.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvProfile.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vBooking.visibility=View.GONE
        }
        loadFragment(ChatFragment())
    }

    private fun setProfileColor() {
        isOpenScreen = "Profile"
        binding.apply {
            ivHome.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvHome.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vHome.visibility=View.GONE

            ivBookings.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvBookings.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vBooking.visibility=View.GONE

            ivChat.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvChat.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
            vChat.visibility=View.GONE

            ivProfile.setColorFilter(
                ContextCompat.getColor(
                    this@MainActivity,
                    android.R.color.holo_red_light
                )
            )
            tvProfile.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            vProfile.visibility=View.VISIBLE
        }

        loadFragment(ProfileFragment())
    }

    override fun onResume() {
        super.onResume()
        binding.tvusername.text = "Hi " + (PrefManager(this).getvalue(StaticData.name)?.split(" ")?.getOrNull(0) ?: "User") + ","
    }

    fun loadFragment(fragment: Fragment) {
        // load fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}