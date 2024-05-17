package com.app.bollyhood.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityMainBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.fragment.AgencyFragment
import com.app.bollyhood.fragment.BookingFragment
import com.app.bollyhood.fragment.ChatFragment
import com.app.bollyhood.fragment.HomeFragment
import com.app.bollyhood.fragment.ProfileFragment
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var mContext: MainActivity
    private var isOpenScreen = "Home"
    private val viewModel: DataViewModel by viewModels()
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mContext = this
        askNotificationPermission()
        addListner()
        addObsereves()
        setHomeColor()
        setNavigationDrawer()
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
//                Log.e(TAG, "PERMISSION_GRANTED")
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

            ivMenu.setOnClickListener {
                binding.drawerlayout.openDrawer(GravityCompat.START)
            }


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

        viewModel.logoutLiveData.observe(this, Observer {
            if (it.status == "1") {
                val fcmToken = PrefManager(this).getvalue(StaticData.fcmToken)
                PrefManager(this).clearValue()
                PrefManager(this).setvalue(StaticData.fcmToken, fcmToken)
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            } else {
                Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
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

    private fun setNavigationDrawer() {
        val menu = binding.navigationview.menu
        binding.navigationview.bringToFront()
        binding.navigationview.setNavigationItemSelectedListener { item ->
            val id = item.itemId
            when (id) {
                R.id.menu_home -> {
                    if (isOpenScreen != "Home") {
                        setHomeColor()
                    }

                }

                R.id.menu_bookings -> {
                    if (isOpenScreen != "Bookings") {
                        setBookingColor()
                    }

                }

                R.id.menu_chat -> {
                    if (isOpenScreen != "Chat") {
                        setChatColor()
                    }


                }

                R.id.menu_profile -> {
                    startActivity(
                        Intent(
                            mContext, MyProfileActivity::class.java
                        )
                    )
                }

                R.id.menu_savejobs -> {
                    startActivity(Intent(mContext, CastingBookMarkActivity::class.java))
                }

                R.id.menu_applyjobs -> {
                    startActivity(Intent(mContext, AllAgencyActivity::class.java))
                }

                R.id.menu_logout -> {
                    logoutDialog()
                }

            }
            binding.drawerlayout.closeDrawer(GravityCompat.START)
            true
        }
        actionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerlayout,
            null,
            R.string.open_drawer,
            R.string.close_drawer
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                binding.drawerlayout.openDrawer(GravityCompat.START)
                setHeaderData()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                binding.drawerlayout.closeDrawer(GravityCompat.START)
            }
        }
        binding.drawerlayout.setDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle?.syncState()
    }

    private fun logoutDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to Logout?")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->

            if (isNetworkAvailable(this)) {
                viewModel.doLogout(PrefManager(this).getvalue(StaticData.id).toString())


            } else {
                Toast.makeText(
                    this,
                    getString(R.string.str_error_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }


    private fun setHeaderData() {
        val hView = binding.navigationview.getHeaderView(0)
        val cvProfile = hView.findViewById<CircleImageView>(R.id.ivImage)
        val name = hView.findViewById<TextView>(R.id.tvName)
        if (PrefManager(mContext).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(mContext)
                .load(PrefManager(mContext).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                .into(cvProfile)
        }

        name.setText(PrefManager(mContext).getvalue(StaticData.name))

        val tvVersion = hView.findViewById<TextView>(R.id.tvAppVersion)
        tvVersion.text = "Version:- " + "1.0"
    }


    private fun setHomeColor() {
        isOpenScreen = "Home"
        binding.apply {
            ivHome.setColorFilter(
                ContextCompat.getColor(
                    this@MainActivity,
                    android.R.color.holo_red_light
                )
            )
            tvHome.setTextColor(ContextCompat.getColor(mContext, R.color.black))

            ivBookings.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvBookings.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))

            ivChat.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvChat.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))

            ivProfile.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvProfile.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
        }

        loadFragment(HomeFragment())

    }

    private fun setBookingColor() {
        isOpenScreen = "Bookings"
        binding.apply {
            ivHome.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvHome.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))

            ivBookings.setColorFilter(
                ContextCompat.getColor(
                    this@MainActivity,
                    android.R.color.holo_red_light
                )
            )
            tvBookings.setTextColor(ContextCompat.getColor(mContext, R.color.black))

            ivChat.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvChat.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))

            ivProfile.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvProfile.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
        }


        if (PrefManager(mContext).getvalue(StaticData.user_type).equals("2")) {
            loadFragment(BookingFragment())

        } else {
            loadFragment(BookingFragment())

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

            ivBookings.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvBookings.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))

            ivChat.setColorFilter(
                ContextCompat.getColor(
                    this@MainActivity,
                    android.R.color.holo_red_light
                )
            )
            tvChat.setTextColor(ContextCompat.getColor(mContext, R.color.black))

            ivProfile.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvProfile.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))
        }
        loadFragment(ChatFragment())

    }

    private fun setProfileColor() {
        isOpenScreen = "Profile"
        binding.apply {
            ivHome.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvHome.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))

            ivBookings.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvBookings.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))

            ivChat.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.darkgrey))
            tvChat.setTextColor(ContextCompat.getColor(mContext, R.color.darkgrey))

            ivProfile.setColorFilter(
                ContextCompat.getColor(
                    this@MainActivity,
                    android.R.color.holo_red_light
                )
            )
            tvProfile.setTextColor(ContextCompat.getColor(mContext, R.color.black))
        }

        loadFragment(ProfileFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}