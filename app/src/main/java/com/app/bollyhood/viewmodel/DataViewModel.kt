package com.app.bollyhood.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bollyhood.model.BannerResponse
import com.app.bollyhood.model.CategoryResponse
import com.app.bollyhood.model.LoginResponse
import com.app.bollyhood.model.OtpResponse
import com.app.bollyhood.model.ProfileResponse
import com.app.bollyhood.model.SuccessResponse
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
    var forgotLiveData = MutableLiveData<OtpResponse>()
    var resetPasswordLiveData = MutableLiveData<SuccessResponse>()
    var loginLiveData = MutableLiveData<LoginResponse>()
    var changePasswordLiveData = MutableLiveData<SuccessResponse>()
    var profileLiveData = MutableLiveData<ProfileResponse>()
    var updateProfileLiveData = MutableLiveData<ProfileResponse>()
    var bannerLiveData = MutableLiveData<BannerResponse>()
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

    fun doSignup(name: String, email: String, password: String, cat_id: String, mobile: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.doSignup(name, email, password, cat_id, mobile).let {
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


    fun doLogin(mobile: String, otp: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.doLogin(mobile, otp).let {
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
        name: RequestBody, email: RequestBody, uid: RequestBody, cat_id: RequestBody,
        mobile: RequestBody, profile_image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            mainRepository.updateProfile(name, email, uid, cat_id, mobile, profile_image).let {
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


}