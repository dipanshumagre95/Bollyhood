package com.app.bollyhood.repository

import com.app.bollyhood.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MainRepository @Inject constructor(val apiService: ApiService) {

    suspend fun getCategory() = apiService.getCategory()

   suspend fun getSignupCategory(category_type:String)=apiService.getSignupCategory(category_type)

    suspend fun getAllActors(categorie:String,uid: String) = apiService.getAllActors(categorie,uid)

    suspend fun getRecentCategory() = apiService.getRecentCategory()

    suspend fun doSignup(
        name: String,
        cat_id: String,
        mobile: String,
        user_type: String
    ) = apiService.doSignup(
        name,cat_id, mobile, user_type
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

    suspend fun uploadCastingCall(
        uid:RequestBody,
        company_name:RequestBody,
        organization:RequestBody,
        requirement:RequestBody,
        shifting:RequestBody,
        gender:RequestBody,
        location:RequestBody,
        height:RequestBody,
        passport:RequestBody,
        body_type:RequestBody,
        skin_clor:RequestBody,
        age:RequestBody,
        price:RequestBody,
        role:RequestBody,
        priceType:RequestBody,
        castingFeeType:RequestBody,
        is_verify_casting:RequestBody,
        company_logo:MultipartBody.Part?
    )=apiService.uploadCasting(uid,
        company_name,
        organization,
        requirement,
        shifting,
        gender,
        location,
        height,
        passport,
        body_type,
        skin_clor,
        age,
        price,
        role,
        priceType,
        castingFeeType,
        is_verify_casting,
        company_logo)


    suspend fun uploadKyc(
        front_image: MultipartBody.Part?,
        back_image: MultipartBody.Part?,
        image: MultipartBody.Part?,
        user_Id: RequestBody
      )=apiService.uploadKyc(
        front_image,
        back_image,
        image,
        user_Id
     )

    suspend fun updateProfile(
        name: RequestBody, email: RequestBody,
        uid: RequestBody, cat_id: RequestBody,
        mobileNumber: RequestBody,
        description: RequestBody,
        height: RequestBody,
        passport: RequestBody,
        body_type: RequestBody,
        skin_color: RequestBody,
        age: RequestBody,
        location: RequestBody,
        work_Link: RequestBody?,
        categories: RequestBody?,
        profile_image: MultipartBody.Part?,
        imagefile:ArrayList<MultipartBody.Part>
    ) =
        apiService.updateProfile(
            name,
            email,
            uid,
            cat_id,
            mobileNumber,
            description,
            height,
            passport,
            body_type,
            skin_color,
            age,
            location,
            work_Link,
            categories,
            profile_image,
            imagefile
        )


    suspend fun updateSingerProfile(
        name: RequestBody, email: RequestBody,
        uid: RequestBody, cat_id: RequestBody,
        mobileNumber: RequestBody,
        description: RequestBody,
        achievements: RequestBody,
        languages: RequestBody,
        location: RequestBody,
        dancer_form: RequestBody,
        what_i_do: RequestBody,
        events: RequestBody,
        genre: RequestBody,
        available: RequestBody,
        softwares: RequestBody,
        showreel: RequestBody,
        work_Link: RequestBody?,
        categories: RequestBody?,
        profile_image: MultipartBody.Part?,
        imagefile:ArrayList<MultipartBody.Part>
    ) =
        apiService.updateSingerProfile(
            name,
            email,
            uid,
            cat_id,
            mobileNumber,
            description,
            achievements,
            languages,
            location,
            dancer_form,
            what_i_do,
            events,
            genre,
            available,
            softwares,
            showreel,
            work_Link,
            categories,
            profile_image,
            imagefile
        )


    suspend fun updateInfluencerProfile(
        name: RequestBody,
        email: RequestBody,
        uid: RequestBody,
        cat_id: RequestBody,
        mobileNumber: RequestBody,
        description: RequestBody,
        collaborate: RequestBody,
        promotion: RequestBody,
        average_like: RequestBody,
        average_reel_like: RequestBody,
        instagram_link: RequestBody,
        facebook_link: RequestBody?,
        youtube_link: RequestBody?,
        workLink: RequestBody?,
        categories: RequestBody?,
        profile_image: MultipartBody.Part?,
        imagefile:ArrayList<MultipartBody.Part>
    ) =
        apiService.updateInfluencerProfile(
            name,
            email,
            uid,
            cat_id,
            mobileNumber,
            description,
            collaborate,
            promotion,
            average_like,
            average_reel_like,
            instagram_link,
            facebook_link,
            youtube_link,
            workLink,
            categories,
            profile_image,
            imagefile
        )



    suspend fun updateCompanyProfile(
        name: RequestBody,
        email: RequestBody,
        uid: RequestBody,
        mobileNumber: RequestBody,
        description: RequestBody,
        workLink: RequestBody?,
        categories: RequestBody?,
        location: RequestBody?,
        tag: RequestBody?,
        profile_image: MultipartBody.Part?,
    ) =
        apiService.updateCompanyProfile(
            name,
            email,
            uid,
            mobileNumber,
            description,
            workLink,
            categories,
            location,
            tag,
            profile_image,
        )


    suspend fun getBanner() = apiService.getBanner()

    suspend fun getRecentExpertise(uid: String?) = apiService.getRecentExpertise(uid.toString())

    suspend fun getAllExpertise(uid: String?) = apiService.getAllExpertise(uid.toString())

    suspend fun getExpertiseProfile(id: String?, uid: String?) =
        apiService.getExpertiseProfileDetail(id, uid)

    suspend fun addRemoveBookMark(
        uid: String?, bookmark_uid: String?,
        bookmark_mode: String?,
        folder_id: String?,
        folder_name: String?
    ) = apiService.addRemoveBookMark(uid, bookmark_uid, bookmark_mode,folder_id,folder_name)

    suspend fun getBookMark(
        uid: String?,
        folder_id: String?,
    ) = apiService.myBookMark(uid,folder_id)

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
    suspend fun getAllCastingCalls(uid: String) = apiService.getAllCastingCalls(uid)

    suspend fun getCastingBookMark(uid: String) = apiService.getCastingBookMark(uid)

    suspend fun getCastingApply(
        uid: RequestBody, casting_id: RequestBody,
        images: ArrayList<MultipartBody.Part>?,
        video: RequestBody
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

    suspend fun makeCastingPin(uid: String, castingId: String, status:String) =
        apiService.makeCastingPin(
            uid, castingId,status
    )

    suspend fun updateCastingCall(
        uid:RequestBody,
        castingId:RequestBody,
        company_name:RequestBody,
        organization:RequestBody,
        requirement:RequestBody,
        shifting:RequestBody,
        gender:RequestBody,
        location:RequestBody,
        height:RequestBody,
        passport:RequestBody,
        body_type:RequestBody,
        skin_clor:RequestBody,
        age:RequestBody,
        price:RequestBody,
        role:RequestBody,
        priceType:RequestBody,
        castingFeeType:RequestBody,
        is_verify_casting:RequestBody,
        company_logo:MultipartBody.Part?
    )=apiService.updateCastingCall(
        uid,
        castingId,
        company_name,
        organization,
        requirement,
        shifting,
        gender,
        location,
        height,
        passport,
        body_type,
        skin_clor,
        age,
        price,
        role,
        priceType,
        castingFeeType,
        is_verify_casting,
        company_logo)

    suspend fun deleteCastingCall(uid: String, castingId: String) =
        apiService.deleteCastingCall(
            uid, castingId
        )

    suspend fun getUserDetails(uid: String) =
        apiService.getUserDetails(
            uid
    )

    suspend fun getAppliedUserData(uid: String,castingId: String) =
        apiService.getAppliedUserData(
            uid,
            castingId
    )

    suspend fun addNewShootLocation(
         uid:RequestBody?,
         locationId:RequestBody?,
         property_name:RequestBody?,
         description:RequestBody?,
         phone:RequestBody?,
         email:RequestBody?,
         parking:RequestBody?,
         location:RequestBody?,
         security_deposit:RequestBody?,
         shift_type:RequestBody?,
         amount:RequestBody?,
         care_taker:RequestBody?,
         air_conditioner: RequestBody?,
         images: ArrayList<MultipartBody.Part>
    ) =
        apiService.addNewShootLocation(
            uid,
            locationId,
            property_name,
            description,
            phone,
            email,
            parking,
            location,
            security_deposit,
            shift_type,
            amount,
            care_taker,
            air_conditioner,
            images
        )

    suspend fun getShootLocationList(uid: String) =
        apiService.getShootLocationList(
            uid)

    suspend fun getShootLocation(locationId: String) =
        apiService.getShootLocation(
            locationId)

    suspend fun getFeatureLocationList() =
        apiService.getFeatureLocationList()
}