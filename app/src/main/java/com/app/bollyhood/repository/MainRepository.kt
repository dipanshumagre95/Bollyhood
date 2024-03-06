package com.app.bollyhood.repository

import com.app.bollyhood.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MainRepository @Inject constructor(val apiService: ApiService) {

    suspend fun getCategory() = apiService.getCategory()

    suspend fun doSignup(
        name: String, email: String,
        password: String,
        cat_id: String,
        mobile: String
    ) = apiService.doSignup(name, email, password, cat_id, mobile)

    suspend fun sendOtp(id: String) = apiService.doSendOtp(id)

    suspend fun doLogin(mobile: String, otp: String) = apiService.doLogin(mobile, otp)

    suspend fun doForgotPassword(mobile: String) = apiService.doForgotPassword(mobile)

    suspend fun doResetPassword(mobile: String, new_password: String) = apiService.doResetPassword(
        mobile,
        new_password
    )

    suspend fun doChangePassword(uid: String, old_password: String, new_password: String) =
        apiService.changePassword(
            uid,
            old_password, new_password
        )

    suspend fun getProfile(uid: String) = apiService.getProfile(uid)

    suspend fun updateProfile(
        name: RequestBody, email: RequestBody,
        uid: RequestBody, cat_id: RequestBody,
        mobileNumber: RequestBody,
        profile_image: MultipartBody.Part?
    ) =
        apiService.updateProfile(name, email, uid, cat_id, mobileNumber, profile_image)

    suspend fun getBanner() = apiService.getBanner()
}