package com.app.bollyhood.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityMainBinding
import com.app.bollyhood.fragment.AgencyFragment
import com.app.bollyhood.fragment.BookingFragment
import com.app.bollyhood.fragment.ChatFragment
import com.app.bollyhood.fragment.HomeFragment
import com.app.bollyhood.fragment.ProfileFragment
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var mContext: MainActivity
    private var isOpenScreen = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mContext = this
        askNotificationPermission()
        addListner()
        setHomeColor()
    }


    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
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




        binding.apply {
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
            loadFragment(AgencyFragment())

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