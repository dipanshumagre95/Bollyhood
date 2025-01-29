package com.app.bollyhood.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.app.bollyhood.repository.MainRepository
import com.app.bollyhood.util.StaticData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(@ApplicationContext val Mcontext :Context,val mainRepository: MainRepository) : ViewModel() {

    val isLoading = MutableLiveData(false)
    val splashLiveData = MutableLiveData(false)
    var categoryLiveData = MutableLiveData<CategoryResponse>()
    var categoryLiveDataforSignup = MutableLiveData<CategoryResponse>()
    var signupLiveData = MutableLiveData<SuccessResponse>()
    var otpLiveData = MutableLiveData<OtpResponse>()
    var logoutLiveData = MutableLiveData<SuccessResponse>()
    var forgotLiveData = MutableLiveData<OtpResponse>()
    var resetPasswordLiveData = MutableLiveData<SuccessResponse>()
    var loginLiveData = MutableLiveData<LoginResponse>()
    var changePasswordLiveData = MutableLiveData<SuccessResponse>()
    var castingUploadedLiveData = MutableLiveData<SuccessResponse>()
    var kycUploadLiveData = MutableLiveData<SuccessResponse>()
    var profileLiveData = MutableLiveData<ProfileResponse>()
    var updateProfileLiveData = MutableLiveData<ProfileResponse>()
    var bannerLiveData = MutableLiveData<BannerResponse>()
    var expertiseLiveData = MutableLiveData<ExpertiseResponse>()
    var expertiseProfileLiveData = MutableLiveData<ExpertiseResponse>()
    var addRemoveBookMarkLiveData = MutableLiveData<SuccessResponse>()
    var bookMarkLiveData = MutableLiveData<BookMarkResponse>()
    var addBookLiveData = MutableLiveData<SuccessResponse>()
    var myBookLiveData = MutableLiveData<BookingResponse>()
    var subscriptionPlanLiveData = MutableLiveData<PlanResponse>()
    var checkSubscriptionLiveData = MutableLiveData<SubscriptionResponse>()
    var cmsLiveData = MutableLiveData<CMSResponse>()
    var subCategoryLiveData = MutableLiveData<SubCategoryResponse>()
    var castingCallsLiveData = MutableLiveData<CastingCallResponse>()

    var castingBookMarkCallsLiveData = MutableLiveData<CastingCallResponse>()

    var castingCallsApplyLiveData = MutableLiveData<SuccessResponse>()
    var agencyLiveData = MutableLiveData<CastingListResponse>()

    var sendMessageLiveData = MutableLiveData<SendMessageResponse>()

    var sendPaymentLiveData = MutableLiveData<SuccessResponse>()

    var chatHistoryLiveData = MutableLiveData<ChatResponse>()

    var castingBookmark = MutableLiveData<SuccessResponse>()

    var actorsList = MutableLiveData<ActorsresponseModel>()

    fun splashTime() {

        viewModelScope.launch {
            delay(StaticData.SPALSH_TIME_OUT.toLong())
            splashLiveData.value = true
        }
    }

    fun getCategory() {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getCategory()
                if (response.isSuccessful && response.body() != null) {
                    categoryLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch categories: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun getAllActors(categorie: String,uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getAllActors(categorie,uid)
                if (response.isSuccessful && response.body() != null) {
                    actorsList.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch actors: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun getRecentCategory() {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getRecentCategory()
                if (response.isSuccessful && response.body() != null) {
                    categoryLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch recent categories: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun doSignup(
        name: String,
        password: String,
        cat_id: String,
        mobile: String,
        user_type: String,
        subCategoryId: String
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.doSignup(
                    name,
                    password,
                    cat_id,
                    mobile,
                    user_type,
                    subCategoryId
                )
                if (response.isSuccessful && response.body() != null) {
                    signupLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Signup failed: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun sendOtp(mobile: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.sendOtp(mobile)
                if (response.isSuccessful && response.body() != null) {
                    otpLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to send OTP: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun doLogout(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.doLogout(uid)
                if (response.isSuccessful && response.body() != null) {
                    logoutLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Logout failed: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun doLogin(
        mobile: String,
        otp: String,
        fcmToken: String,
        isOnline: String
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.doLogin(mobile, otp, fcmToken, isOnline)
                if (response.isSuccessful && response.body() != null) {
                    loginLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Login failed: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun getProfile(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getProfile(uid)
                if (response.isSuccessful && response.body() != null) {
                    Log.d("Profile", response.body().toString()) // Log for debugging
                    profileLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch profile: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun uploadCastingCall(
        uid: RequestBody,
        company_name: RequestBody,
        organization: RequestBody,
        requirement: RequestBody,
        shifting: RequestBody,
        gender: RequestBody,
        location: RequestBody,
        height: RequestBody,
        passport: RequestBody,
        body_type: RequestBody,
        skin_clor: RequestBody,
        age: RequestBody,
        price: RequestBody,
        role: RequestBody,
        priceType:RequestBody,
        castingFeeType:RequestBody,
        is_verify_casting:RequestBody,
        company_logo: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.uploadCastingCall(
                    uid,
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
                    company_logo
                )

                if (response.isSuccessful && response.body() != null) {
                    castingUploadedLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to upload casting call: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun updateProfile(
        name: RequestBody,
        email: RequestBody,
        uid: RequestBody,
        cat_id: RequestBody,
        mobile: RequestBody,
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
        imagefile: ArrayList<MultipartBody.Part>
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.updateProfile(
                    name,
                    email,
                    uid,
                    cat_id,
                    mobile,
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

                if (response.isSuccessful && response.body() != null) {
                    Log.d("okhttp", response.body().toString())
                    updateProfileLiveData.postValue(response.body() as ProfileResponse)
                } else {
                    val errorMessage = "Failed to update profile: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun uploadKyc(
        front_image: MultipartBody.Part?,
        back_image: MultipartBody.Part?,
        image: MultipartBody.Part?,
        user_Id: RequestBody
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.uploadKyc(
                    front_image,
                    back_image,
                    image,
                    user_Id
                )

                if (response.isSuccessful && response.body() != null) {
                    // Successful upload
                    kycUploadLiveData.postValue(response.body() as SuccessResponse)
                } else {
                    val errorMessage = "Failed to upload KYC: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun updateSingerProfile(
        name: RequestBody,
        email: RequestBody,
        uid: RequestBody,
        cat_id: RequestBody,
        mobile: RequestBody,
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
        imagefile: ArrayList<MultipartBody.Part>
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.updateSingerProfile(
                    name,
                    email,
                    uid,
                    cat_id,
                    mobile,
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

                if (response.isSuccessful && response.body() != null) {
                    // Successful profile update
                    Log.d("okhttp", response.body().toString())
                    updateProfileLiveData.postValue(response.body() as ProfileResponse)
                } else {
                    val errorMessage = "Failed to update singer profile: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun updateInfluencerProfile(
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
        imagefile: ArrayList<MultipartBody.Part>
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.updateInfluencerProfile(
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

                if (response.isSuccessful && response.body() != null) {
                    Log.d("okhttp", response.body().toString())
                    updateProfileLiveData.postValue(response.body() as ProfileResponse)
                } else {
                    val errorMessage = "Failed to update influencer profile: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun updateCompanyProfile(
        name: RequestBody,
        email: RequestBody,
        uid: RequestBody,
        mobileNumber: RequestBody,
        description: RequestBody,
        workLink: RequestBody?,
        categories: RequestBody?,
        location: RequestBody?,
        tag: RequestBody?,
        profile_image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.updateCompanyProfile(
                    name,
                    email,
                    uid,
                    mobileNumber,
                    description,
                    workLink,
                    categories,
                    location,
                    tag,
                    profile_image
                )
                if (response.isSuccessful && response.body() != null) {
                    Log.d("okhttp", response.body().toString())
                    updateProfileLiveData.postValue(response.body() as ProfileResponse)
                } else {
                    val errorMessage = "Failed to update company profile: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun getBanner() {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getBanner()
                if (response.isSuccessful && response.body() != null) {
                    bannerLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch banner: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun getRecentExpertise(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getRecentExpertise(uid)
                if (response.isSuccessful && response.body() != null) {
                    expertiseLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch expertise: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun getAllExpertise(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getAllExpertise(uid)
                if (response.isSuccessful && response.body() != null) {
                    expertiseLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch expertise: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun addRemoveBookMark(uid: String?, bookmark_uid: String?, bookmark_mode: String?,folder_id: String?,folder_name: String?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.addRemoveBookMark(uid, bookmark_uid, bookmark_mode,folder_id,folder_name)
                if (response.isSuccessful && response.body() != null) {
                    addRemoveBookMarkLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to add/remove bookmark: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun myBookMark(uid: String?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getBookMark(uid)
                if (response.isSuccessful && response.body() != null) {
                    bookMarkLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch bookmarks: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun castingBookmark(uid: String?, casting_id: String?, bookmark_mode: String?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.addCastingBookMark(uid, casting_id, bookmark_mode)
                if (response.isSuccessful && response.body() != null) {
                    castingBookmark.postValue(response.body())
                } else {
                    val errorMessage = "Failed to update bookmark: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun addBook(
        uid: String?, w_mobile: String?,
        purpose: String?, booking_date: String?,
        booking_time: String?, category_Id: String?, booking_uid: String?
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.addBook(
                    uid,
                    w_mobile,
                    purpose,
                    booking_date,
                    booking_time,
                    category_Id,
                    booking_uid
                )
                if (response.isSuccessful && response.body() != null) {
                    addBookLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to add booking: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun getPlan() {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getPlan()
                if (response.isSuccessful && response.body() != null) {
                    subscriptionPlanLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch subscription plans: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun checkSubscriptions(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.checkSubscriptions(uid)
                if (response.isSuccessful && response.body() != null) {
                    checkSubscriptionLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch subscriptions: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun getCMS(type: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getCMS(type)
                if (response.isSuccessful && response.body() != null) {
                    cmsLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch CMS data: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun getSubCategory(category_Id: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getSubCategory(category_Id)
                if (response.isSuccessful && response.body() != null) {
                    subCategoryLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch subcategory data: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun getAllCastingCalls(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getAllCastingCalls(uid)
                if (response.isSuccessful && response.body() != null) {
                    castingCallsLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch casting calls: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun getCastingCalls(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getCastingCalls(uid)
                if (response.isSuccessful && response.body() != null) {
                    castingCallsLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch casting calls: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun getCastingBookMarkCalls(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getCastingBookMark(uid)
                if (response.isSuccessful && response.body() != null) {
                    castingBookMarkCallsLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch casting bookmarks: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun getAgency(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getAgency(uid)
                if (response.isSuccessful && response.body() != null) {
                    agencyLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch agency details: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun getCastingCallApply(
        uid: RequestBody,
        casting_id: RequestBody,
        images: ArrayList<MultipartBody.Part>?,
        video: RequestBody
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getCastingApply(uid, casting_id, images, video)
                if (response.isSuccessful && response.body() != null) {
                    castingCallsApplyLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to apply for casting call: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun sendMessage(
        uid: RequestBody,
        other_uid: RequestBody,
        text: RequestBody,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.sendMessage(uid, other_uid, text, image)
                if (response.isSuccessful && response.body() != null) {
                    sendMessageLiveData.postValue(response.body() as SendMessageResponse)
                } else {
                    val errorMessage = "Failed to send message: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun sendPayment(
        payment_id: String,
        uid: String,
        plan_id: String
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.sendPayment(payment_id, uid, plan_id)

                if (response.isSuccessful && response.body() != null) {
                    sendPaymentLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to process payment: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun getSignupCategory(category_type: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getSignupCategory(category_type)
                if (response.isSuccessful && response.body() != null) {
                    categoryLiveDataforSignup.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch categories: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }



    fun getChatHistory(uid: String, other_uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.getChat(uid, other_uid)
                if (response.isSuccessful && response.body() != null) {
                    chatHistoryLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to fetch chat history: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun makeCastingPin(uid: String, castingId: String , status:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.makeCastingPin(uid, castingId,status)
                if (response.isSuccessful && response.body() != null) {
                    castingCallsLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to Get Updated List: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun updateCastingCall(
        uid: RequestBody,
        castingId: RequestBody,
        company_name: RequestBody,
        organization: RequestBody,
        requirement: RequestBody,
        shifting: RequestBody,
        gender: RequestBody,
        location: RequestBody,
        height: RequestBody,
        passport: RequestBody,
        body_type: RequestBody,
        skin_clor: RequestBody,
        age: RequestBody,
        price: RequestBody,
        role: RequestBody,
        priceType:RequestBody,
        castingFeeType:RequestBody,
        is_verify_casting:RequestBody,
        company_logo: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.updateCastingCall(
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
                    company_logo
                )

                if (response.isSuccessful && response.body() != null) {
                    castingUploadedLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to upload casting call: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }


    fun deleteCastingCall(uid: String, castingId: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            try {
                val response = mainRepository.deleteCastingCall(uid, castingId)
                if (response.isSuccessful && response.body() != null) {
                    castingCallsLiveData.postValue(response.body())
                } else {
                    val errorMessage = "Failed to Get Updated List: ${response.message()}"
                    Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("API_ERROR", errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Something went wrong. Please try again."
                Toast.makeText(Mcontext, errorMessage, Toast.LENGTH_LONG).show()
                Log.e("NETWORK_ERROR", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}


















































