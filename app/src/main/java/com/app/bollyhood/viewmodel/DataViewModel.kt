package com.app.bollyhood.viewmodel

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {

    val isLoading = MutableLiveData(false)
    val splashLiveData = MutableLiveData(false)
    var categoryLiveData = MutableLiveData<CategoryResponse>()
    var signupLiveData = MutableLiveData<SuccessResponse>()
    var otpLiveData = MutableLiveData<OtpResponse>()
    var logoutLiveData = MutableLiveData<SuccessResponse>()
    var forgotLiveData = MutableLiveData<OtpResponse>()
    var resetPasswordLiveData = MutableLiveData<SuccessResponse>()
    var loginLiveData = MutableLiveData<LoginResponse>()
    var changePasswordLiveData = MutableLiveData<SuccessResponse>()
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
            mainRepository.getCategory().let {
                if (it.body() != null) {
                    categoryLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }

    fun getAllActors(){
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getAllActors().let{
                if (it.body()!=null){
                    actorsList.postValue(it.body())
                    isLoading.postValue(false)
                }else{
                    isLoading.postValue(false)
                }
            }
        }
    }

    fun getRecentCategory() {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getRecentCategory().let {
                if (it.body() != null) {
                    categoryLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }


    fun doSignup(
        name: String,
        password: String,
        cat_id: String,
        mobile: String,
        user_type: String,
        subCategroyId: String
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.doSignup(
                name,
                password,
                cat_id,
                mobile,
                user_type,
                subCategroyId
            )
                .let {
                    if (it.body() != null) {
                        signupLiveData.postValue(it.body())
                        isLoading.postValue(false)
                    } else {
                        isLoading.postValue(false)
                    }
                }
        }
    }


    fun sendOtp(mobile: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.sendOtp(mobile).let {
                if (it.body() != null) {
                    otpLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }

    fun doLogout(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.doLogout(uid).let {
                if (it.body() != null) {
                    logoutLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }


    fun doLogin(mobile: String, otp: String, fcmToken: String, is_online: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.doLogin(mobile, otp, fcmToken, is_online).let {
                if (it.body() != null) {
                    loginLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }

    fun doForgotPassword(mobile: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.doForgotPassword(mobile).let {
                if (it.body() != null) {
                    forgotLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }

    fun doResetPassword(mobile: String, password: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.doResetPassword(mobile, password).let {
                if (it.body() != null) {
                    resetPasswordLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }

    fun doChangePassword(uid: String, old_password: String, new_password: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.doChangePassword(uid, old_password, new_password).let {
                if (it.body() != null) {
                    changePasswordLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }


    fun getProfile(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getProfile(uid).let {
                if (it.body() != null) {
                    profileLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
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
        jobs_done: RequestBody,
        experience: RequestBody,
        reviews: RequestBody,
        work_Link: RequestBody?,
        categories: RequestBody?,
        profile_image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.updateProfile(
                name, email, uid, cat_id, mobile, description, jobs_done, experience, reviews,
                work_Link, categories, profile_image
            ).let {
                if (it.body() != null) {
                    updateProfileLiveData.postValue(it.body() as ProfileResponse)
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }


    fun getBanner() {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getBanner().let {
                if (it.body() != null) {
                    bannerLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }


    fun getRecentExpertise(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getRecentExpertise(uid).let {
                if (it.body() != null) {
                    expertiseLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }

    fun getAllExpertise(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getAllExpertise(uid).let {
                if (it.body() != null) {
                    expertiseLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }


    fun getExpertiseProfileDetail(id: String?, uid: String?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getExpertiseProfile(id, uid).let {
                if (it.body() != null) {
                    expertiseProfileLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }


    fun addRemoveBookMark(uid: String?, bookmark_uid: String?, bookmark_mode: String?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.addRemoveBookMark(uid, bookmark_uid, bookmark_mode).let {
                if (it.body() != null) {
                    addRemoveBookMarkLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }

    fun myBookMark(uid: String?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getBookMark(uid).let {
                if (it.body() != null) {
                    bookMarkLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }


    fun castingBookmark(uid: String?, casting_id: String?, bookmark_mode: String?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.addCastingBookMark(uid, casting_id, bookmark_mode).let {
                if (it.body() != null) {
                    castingBookmark.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
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
            mainRepository.addBook(
                uid,
                w_mobile,
                purpose,
                booking_date,
                booking_time,
                category_Id,
                booking_uid
            ).let {
                if (it.body() != null) {
                    addBookLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }


    fun getBooking(uid: String?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getBooking(uid).let {
                if (it.body() != null) {
                    myBookLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }

    fun getPlan() {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getPlan().let {
                if (it.body() != null) {
                    subscriptionPlanLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }


    fun checkSubscriptions(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.checkSubscriptions(uid).let {
                if (it.body() != null) {
                    checkSubscriptionLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }
    }

    fun getCMS(type: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getCMS(type).let {
                if (it.body() != null) {
                    cmsLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }


    fun getSubCategory(category_Id: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getSubCategory(category_Id).let {
                if (it.body() != null) {
                    subCategoryLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }


    fun getCastingCalls(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getCastingCalls(uid).let {
                if (it.body() != null) {
                    castingCallsLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }


    fun getCastingBookMarkCalls(uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getCastingBookMark(uid).let {
                if (it.body() != null) {
                    castingBookMarkCallsLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }


    fun getAgency(uid:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getAgency(uid).let {
                if (it.body() != null) {
                    agencyLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }

    fun getCastingCallApply(
        uid: RequestBody, casting_id: RequestBody,
        images: ArrayList<MultipartBody.Part>?, video: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getCastingApply(uid, casting_id, images, video).let {
                if (it.body() != null) {
                    castingCallsApplyLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }


    fun sendMessage(
        uid: RequestBody, other_uid: RequestBody,
        text: RequestBody,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.sendMessage(uid, other_uid, text, image).let {
                if (it.body() != null) {
                    sendMessageLiveData.postValue(it.body() as SendMessageResponse)
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }

    fun sendPayment(
        payment_id: String, uid: String,
        plan_id: String
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.sendPayment(payment_id, uid, plan_id).let {
                if (it.body() != null) {
                    sendPaymentLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }


    fun getChatHistory(uid: String, other_uid: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.getChat(uid, other_uid).let {
                if (it.body() != null) {
                    chatHistoryLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }
        }

    }


}




















































