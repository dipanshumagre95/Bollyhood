package com.app.bollyhood.network

import com.app.bollyhood.model.BannerResponse
import com.app.bollyhood.model.BookMarkResponse
import com.app.bollyhood.model.BookingResponse
import com.app.bollyhood.model.CMSResponse
import com.app.bollyhood.model.CastingCallResponse
import com.app.bollyhood.model.CategoryResponse
import com.app.bollyhood.model.ChatResponse
import com.app.bollyhood.model.ExpertiseResponse
import com.app.bollyhood.model.LoginResponse
import com.app.bollyhood.model.OtpResponse
import com.app.bollyhood.model.PlanResponse
import com.app.bollyhood.model.ProfileResponse
import com.app.bollyhood.model.SendMessageResponse
import com.app.bollyhood.model.SubCategoryResponse
import com.app.bollyhood.model.SubscriptionResponse
import com.app.bollyhood.model.SuccessResponse
import com.app.bollyhood.model.actors.ActorsresponseModel
import com.app.bollyhood.model.castinglist.CastingListResponse
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
    @GET("category_type.php")
    suspend fun getSignupCategory(@Query("category_type") category_type: String): Response<CategoryResponse>

    @GET("all_category_users.php")
    suspend fun getAllActors(@Query("category_id") category_id: String,@Query("uid") uid: String) : Response<ActorsresponseModel>

    @GET("recent_category.php")
    suspend fun getRecentCategory(): Response<CategoryResponse>

    @FormUrlEncoded
    @POST("sign_up.php")
    suspend fun doSignup(
        @Field("name") name: String,
        @Field("password") password: String,
        @Field("categories") cat_id: String,
        @Field("mobile") mobile: String,
        @Field("user_type") user_type: String,
        @Field("sub_categories") sub_categories: String,
    ): Response<SuccessResponse>

    @FormUrlEncoded
    @POST("login.php")
    suspend fun doSendOtp(
        @Field("id") id: String
    ): Response<OtpResponse>

    @FormUrlEncoded
    @POST("logout.php")
    suspend fun doLogout(
        @Field("uid") uid: String
    ): Response<SuccessResponse>

    @FormUrlEncoded
    @POST("send_otp.php")
    suspend fun doLogin(
        @Field("mobile") mobile: String,
        @Field("otp") otp: String,
        @Field("fcmtoken") fcmToken: String,
        @Field("is_online") is_online: String
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
        @Part("description") description: RequestBody,
        @Part("height") height: RequestBody,
        @Part("passport") passport: RequestBody,
        @Part("body_type") body_type: RequestBody,
        @Part("skin_color") skinColor: RequestBody,
        @Part("age") age: RequestBody,
        @Part("location") location: RequestBody,
        @Part("worklinks") worklinks: RequestBody?,
        @Part("categories") category_id: RequestBody?,
        @Part image: MultipartBody.Part?,
        @Part imagefile : ArrayList<MultipartBody.Part>
    ): Response<ProfileResponse>


    @Multipart
    @POST("update_profile.php")
    suspend fun updateSingerProfile(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("uid") uid: RequestBody,
        @Part("cat_id") cat_id: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("description") description: RequestBody,
        @Part("achievements") achievements: RequestBody,
        @Part("languages") languages: RequestBody,
        @Part("location") location: RequestBody,
        @Part("dancer_form") dancer_form: RequestBody,
        @Part("what_i_do") what_i_do: RequestBody,
        @Part("events") events: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("available") available: RequestBody,
        @Part("softwares") softwares: RequestBody,
        @Part("video_url") showreel: RequestBody,
        @Part("worklinks") worklinks: RequestBody?,
        @Part("categories") category_id: RequestBody?,
        @Part image: MultipartBody.Part?,
        @Part imagefile : ArrayList<MultipartBody.Part>
    ): Response<ProfileResponse>

    @Multipart
    @POST("update_profile.php")
    suspend fun updateInfluencerProfile(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("uid") uid: RequestBody,
        @Part("cat_id") cat_id: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("description") description: RequestBody,
        @Part("collaborate") collaborate: RequestBody,
        @Part("promotion") promotion: RequestBody,
        @Part("average_like") average_like: RequestBody,
        @Part("average_reel_like") average_reel_like: RequestBody,
        @Part("instagram_link") instagram_link: RequestBody,
        @Part("facebook_link") facebook_link: RequestBody?,
        @Part("youtube_link") youtube_link: RequestBody?,
        @Part("worklinks") workLink: RequestBody?,
        @Part("categories") category_id: RequestBody?,
        @Part image: MultipartBody.Part?,
        @Part imagefile : ArrayList<MultipartBody.Part>
    ): Response<ProfileResponse>

    @Multipart
    @POST("update_profile.php")
    suspend fun updateCompanyProfile(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("uid") uid: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("description") description: RequestBody,
        @Part("worklinks") workLink: RequestBody?,
        @Part("categories") category_id: RequestBody?,
        @Part("location") location: RequestBody?,
        @Part("tag_name") tag: RequestBody?,
        @Part image: MultipartBody.Part?,
    ): Response<ProfileResponse>


    @Multipart
    @POST("create_casting.php")
    suspend fun uploadCasting(
        @Part("uid") uid: RequestBody,
        @Part("company_name") company_name: RequestBody,
        @Part("organization") organization: RequestBody,
        @Part("requirement") requirement: RequestBody,
        @Part("shifting") shifting: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("location") location: RequestBody,
        @Part("height") height: RequestBody,
        @Part("passport") passport: RequestBody,
        @Part("body_type") body_type: RequestBody,
        @Part("skin_clor") skin_clor: RequestBody,
        @Part("age") age: RequestBody,
        @Part("price") price: RequestBody?,
        @Part("role") role: RequestBody?,
        @Part company_logo: MultipartBody.Part?,
    ): Response<SuccessResponse>


    @Multipart
    @POST("adhar_kyc.php")
    suspend fun uploadKyc(
        @Part front_image: MultipartBody.Part?,
        @Part back_image: MultipartBody.Part?,
        @Part image: MultipartBody.Part?,
        @Part("uid") uid: RequestBody,
    ):Response<SuccessResponse>

    @GET("banner.php")
    suspend fun getBanner(): Response<BannerResponse>

    @GET("recent_users.php")
    suspend fun getRecentExpertise(
        @Query("uid") uid: String
    ): Response<ExpertiseResponse>

    @GET("all_users.php")
    suspend fun getAllExpertise(
        @Query("uid") uid: String
    ): Response<ExpertiseResponse>

    @FormUrlEncoded
    @POST("expertise_profile.php")
    suspend fun getExpertiseProfileDetail(
        @Field("id") id: String?,
        @Field("uid") uid: String?
    ): Response<ExpertiseResponse>

    @FormUrlEncoded
    @POST("mod_bookmark.php")
    suspend fun addRemoveBookMark(
        @Field("uid") uid: String?,
        @Field("bookmark_uid") bookmark_uid: String?,
        @Field("bookmark_mode") bookmark_mode: String?
    ): Response<SuccessResponse>

    @FormUrlEncoded
    @POST("all_bookmark.php")
    suspend fun myBookMark(
        @Field("uid") uid: String?
    ): Response<BookMarkResponse>


    @FormUrlEncoded
    @POST("mod_casting_bookmark.php")
    suspend fun castingBookMark(
        @Field("uid") uid: String?,
        @Field("casting_id") casting_id: String?,
        @Field("bookmark_mode") bookmark_mode: String?
    ): Response<SuccessResponse>


    @FormUrlEncoded
    @POST("mod_booking.php")
    suspend fun bookProfile(
        @Field("uid") uid: String?,
        @Field("w_mobile") w_mobile: String?,
        @Field("purpose") purpose: String?,
        @Field("booking_date") booking_date: String?,
        @Field("booking_time") booking_time: String?,
        @Field("category_id") category_id: String?,
        @Field("booking_uid") booking_uid: String?,
    ): Response<SuccessResponse>

    @FormUrlEncoded
    @POST("all_booking.php")
    suspend fun getBooking(
        @Field("uid") uid: String?
    ): Response<BookingResponse>

    @GET("plan.php")
    suspend fun getPlan(

    ): Response<PlanResponse>

    @FormUrlEncoded
    @POST("check_subscription.php")
    suspend fun checkSubscription(
        @Field("uid") uid: String
    ): Response<SubscriptionResponse>

    @GET("cms_readme.php")
    suspend fun getCms(@Query("type") type: String): Response<CMSResponse>

    @GET("sub_category.php")
    suspend fun getSubCategory(
        @Query("category_id") category_id: String
    ): Response<SubCategoryResponse>

    @GET("all_casting.php")
    suspend fun getCastingCalls(
        @Query("uid") uid: String
    ): Response<CastingCallResponse>


    @GET("get_all_post.php")
    suspend fun getAllCastingCalls(
        @Query("uid") uid: String
    ): Response<CastingCallResponse>

    @FormUrlEncoded
    @POST("all_casting_bookmark.php")
    suspend fun getCastingBookMark(
        @Field("uid")uid:String
    ): Response<CastingCallResponse>

    @Multipart
    @POST("casting_apply.php")
    suspend fun getCastingApply(
        @Part("uid") uid: RequestBody,
        @Part("casting_id") casting_id: RequestBody,
        @Part image: ArrayList<MultipartBody.Part>?,
        @Part video: MultipartBody.Part?
    ): Response<SuccessResponse>

    @FormUrlEncoded
    @POST("get_agency.php")
    suspend fun getAgency(
        @Field("uid")uid:String
    ): Response<CastingListResponse>

    @FormUrlEncoded
    @POST("chat_list.php")
    suspend fun getChat(
        @Field("uid") uid: String,
        @Field("other_uid") other_uid: String
    ): Response<ChatResponse>

    @Multipart
    @POST("send_message.php")
    suspend fun sendMessage(
        @Part("uid") uid: RequestBody,
        @Part("other_uid") other_uid: RequestBody,
        @Part("text") text: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<SendMessageResponse>

    @FormUrlEncoded
    @POST("payment.php")
    suspend fun sendPayment(
        @Field("payment_id") payment_id: String,
        @Field("uid") uid: String,
        @Field("plan_id") plan_id: String
    ): Response<SuccessResponse>




}