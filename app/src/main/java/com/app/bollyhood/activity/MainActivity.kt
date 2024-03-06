package com.app.bollyhood.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityMainBinding
import com.app.bollyhood.fragment.BookingFragment
import com.app.bollyhood.fragment.ChatFragment
import com.app.bollyhood.fragment.HomeFragment
import com.app.bollyhood.fragment.ProfileFragment
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
        addListner()
        setHomeColor()
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

        loadFragment(BookingFragment())


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