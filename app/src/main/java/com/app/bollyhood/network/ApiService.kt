package com.app.bollyhood.network

import com.app.bollyhood.model.BannerModel
import com.app.bollyhood.model.BannerResponse
import com.app.bollyhood.model.CategoryResponse
import com.app.bollyhood.model.LoginResponse
import com.app.bollyhood.model.OtpResponse
import com.app.bollyhood.model.ProfileResponse
import com.app.bollyhood.model.SuccessResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @GET("category.php")
    suspend fun getCategory(): Response<CategoryResponse>

    @FormUrlEncoded
    @POST("sign_up.php")
    suspend fun doSignup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("cat_id") cat_id: String,
        @Field("mobile") mobile: String
    ): Response<SuccessResponse>

    @FormUrlEncoded
    @POST("login.php")
    suspend fun doSendOtp(
        @Field("id") id: String
    ): Response<OtpResponse>

    @FormUrlEncoded
    @POST("send_otp.php")
    suspend fun doLogin(
        @Field("mobile") mobile: String,
        @Field("otp") otp: String
    ): Response<LoginResponse>


    @FormUrlEncoded
    @POST("forgot_password.php")
    suspend fun doForgotPassword(
        @Field("mobile") mobile: String
    ): Response<OtpResponse>

    @FormUrlEncoded
    @POST("reset_password.php")
    suspend fun doResetPassword(
        @Field("mobile") mobile: String,
        @Field("new_password") new_password: String
    ): Response<SuccessResponse>

    @FormUrlEncoded
    @POST("change_password.php")
    suspend fun changePassword(
        @Field("uid") uid: String,
        @Field("old_password") old_password: String,
        @Field("new_password") new_password: String
    ): Response<SuccessResponse>

    @GET("profile.php")
    suspend fun getProfile(@Query("uid") uid: String): Response<ProfileResponse>

    @Multipart
    @POST("update_profile.php")
    suspend fun updateProfile(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("uid") uid: RequestBody,
        @Part("cat_id") cat_id: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part image: MultipartBody.Part?,
    ): Response<ProfileResponse>


    @GET("banner.php")
    suspend fun getBanner(): Response<BannerResponse>

}