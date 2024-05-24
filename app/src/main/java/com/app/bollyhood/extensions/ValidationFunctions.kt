package com.app.bollyhood.extensions

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.app.bollyhood.R




fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // For 29 api or above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
    // For below 29 api
    else {
        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
            return true
        }
    }
    return false
}

fun isvalidUploadProfile(context: Context, profilePath: String): Boolean {
    var isValid = true
    if (profilePath.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_upload_profile_picture),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}


fun isvalidName(context: Context, edtName: String): Boolean {
    var isValid = true
    if (edtName.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_name),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}


fun isvalidEmailAddress(context: Context, edtEmail: String): Boolean {
    var isValid = true
    if (edtEmail.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_email_address),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail).matches()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_valid_email_address),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}

fun isvalidCategory(context: Context, edtCategory: String): Boolean {
    var isValid = true
    if (edtCategory.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_category),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}

fun isvalidPassword(context: Context, edtPassword: String): Boolean {
    var isValid = true
    if (edtPassword.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_password),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}

fun isValidOldPassword(context: Context, edtoldPassword: String): Boolean {
    var isValid = true
    if (edtoldPassword.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_old_password),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}

fun isvalidConfirmPassword(context: Context, edtConfirmPassword: String): Boolean {
    var isValid = true
    if (edtConfirmPassword.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_confirm_password),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}

fun isvalidTeamNCondition(context: Context, isChecked: Boolean): Boolean {
    var isValid = true
    if (!isChecked) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_teamsNcondition_checkbox),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}


fun isvalidBothPassword(
    context: Context,
    edtPassword: String,
    edtConfirmPassword: String
): Boolean {
    var isValid = true
    if (edtPassword != edtConfirmPassword) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_both_password_not_match),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}


fun isvalidNewPassword(context: Context, edtNewPassword: String): Boolean {
    var isValid = true
    if (edtNewPassword.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_new_password),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}


fun isvalidConfirmNewPassword(context: Context, edtConfirmNewPassword: String): Boolean {
    var isValid = true
    if (edtConfirmNewPassword.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_confirm_new_password),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}


fun isvalidBothNewPassword(
    context: Context,
    edtNewPassword: String,
    edtConfirmNewPassword: String
): Boolean {
    var isValid = true
    if (edtNewPassword != edtConfirmNewPassword) {
        Toast.makeText(
            context,
            context.getString(R.string.str_both_new_password_not_match),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}

fun isvalidMobileNumber(context: Context, edtMobileNumber: String): Boolean {
    var isValid = true
    if (edtMobileNumber.isEmpty() || edtMobileNumber.length < 10) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_mobile_number),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}



fun isvalidDescriptions(context: Context, edtDescription: String): Boolean {
    var isValid = true
    if (edtDescription.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_descriptions),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}

fun isvalidOtp(context: Context, otp: String): Boolean {
    var isValid = true
    if (otp.isEmpty() || otp.length < 6) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_otp),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}

fun isvalidBookNow(
    context: Context,
    fullName: String,
    number: String,
    booking: String,
    date: String,
    time: String
): Boolean {
    var isValid = true
    if (fullName.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_enter_full_name),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    } else if (number.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_enter_whatsapp_number),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    } else if (booking.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_enter_booking),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }else if (date.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_select_date),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }else if (time.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.str_error_time),
            Toast.LENGTH_SHORT
        ).show()
        isValid = false
    }
    return isValid
}