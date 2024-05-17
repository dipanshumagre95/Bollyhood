package com.app.bollyhood.repository

import com.app.bollyhood.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MainRepository @Inject constructor(val apiService: ApiService) {

    suspend fun getCategory() = apiService.getCategory()

    suspend fun getRecentCategory() = apiService.getRecentCategory()

    suspend fun doSignup(
        name: RequestBody, email: RequestBody,
        password: RequestBody,
        cat_id: RequestBody,
        mobile: RequestBody,
        user_type: RequestBody,
        subcatgory_Id: RequestBody,
        profilePath: MultipartBody.Part?
    ) = apiService.doSignup(
        name, email, password, cat_id, mobile, user_type,
        subcatgory_Id, profilePath
    )

    suspend fun sendOtp(id: String) = apiService.doSendOtp(id)

    suspend fun doLogout(uid: String) = apiService.doLogout(uid)

    suspend fun doLogin(
        mobile: String, otp: String,
        fcmToken: String, is_online: String
    ) = apiService.doLogin(mobile, otp, fcmToken, is_online)

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
        description: RequestBody,
        jobs_done: RequestBody,
        experince: RequestBody,
        reviews: RequestBody,
        work_Link: RequestBody?,
        categories: RequestBody?,
        profile_image: MultipartBody.Part?
    ) =
        apiService.updateProfile(
            name,
            email,
            uid,
            cat_id,
            mobileNumber,
            description,
            jobs_done,
            experince,
            reviews,
            work_Link,
            categories,
            profile_image
        )

    suspend fun getBanner() = apiService.getBanner()

    suspend fun getRecentExpertise(uid: String?) = apiService.getRecentExpertise(uid.toString())

    suspend fun getAllExpertise(uid: String?) = apiService.getAllExpertise(uid.toString())

    suspend fun getExpertiseProfile(id: String?, uid: String?) =
        apiService.getExpertiseProfileDetail(id, uid)

    suspend fun addRemoveBookMark(
        uid: String?, bookmark_uid: String?,
        bookmark_mode: String?
    ) = apiService.addRemoveBookMark(uid, bookmark_uid, bookmark_mode)

    suspend fun getBookMark(
        uid: String?
    ) = apiService.myBookMark(uid)

    suspend fun addCastingBookMark(
        uid: String?,
        casting_id: String?,
        bookmark_mode: String?
    ) = apiService.castingBookMark(uid, casting_id, bookmark_mode)

    suspend fun addBook(
        uid: String?, w_mobile: String?,
        purpose: String?, booking_date: String?,
        booking_time: String?, category_Id: String?,
        booking_uid: String?
    ) = apiService.bookProfile(
        uid,
        w_mobile,
        purpose,
        booking_date,
        booking_time,
        category_Id,
        booking_uid
    )

    suspend fun getBooking(uid: String?) = apiService.getBooking(uid)

    suspend fun getPlan() = apiService.getPlan()

    suspend fun checkSubscriptions(uid: String) = apiService.checkSubscription(uid)

    suspend fun getCMS(type: String) = apiService.getCms(type)

    suspend fun getSubCategory(
        category_Id: String
    ) = apiService.getSubCategory(category_Id)

    suspend fun getCastingCalls(uid: String) = apiService.getCastingCalls(uid)

    suspend fun getCastingBookMark(uid: String) = apiService.getCastingBookMark(uid)

    suspend fun getCastingApply(
        uid: RequestBody, casting_id: RequestBody,
        images: ArrayList<MultipartBody.Part>?,
        video: MultipartBody.Part?
    ) = apiService.getCastingApply(
        uid, casting_id, images, video
    )

    suspend fun getAgency(uid:String) = apiService.getAgency(
        uid
    )

    suspend fun getChat(uid: String, other_uid: String) = apiService.getChat(
        uid, other_uid
    )

    suspend fun sendMessage(
        uid: RequestBody, other_uid: RequestBody,
        text: RequestBody,
        image: MultipartBody.Part?
    ) = apiService.sendMessage(
        uid, other_uid, text, image
    )

    suspend fun sendPayment(payment_id: String, uid: String, plan_id: String) =
        apiService.sendPayment(
            payment_id, uid, plan_id
        )

}