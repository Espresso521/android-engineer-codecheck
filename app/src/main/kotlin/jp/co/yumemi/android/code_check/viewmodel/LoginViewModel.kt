package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.api.Future
import jp.co.yumemi.android.code_check.api.IApiImpl
import jp.co.yumemi.android.code_check.api.apiFlow
import jp.co.yumemi.android.code_check.data.model.Login
import jp.co.yumemi.android.code_check.data.model.ResponseResult
import jp.co.yumemi.android.code_check.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) :
    ViewModel() {

    private val _loginResponse = MutableLiveData<Future<ResponseResult<Login>>>(Future.Proceeding)
    val loginResponse: LiveData<Future<ResponseResult<Login>>> = _loginResponse

    fun login(username: String, password: String) =
        viewModelScope.launch(ioDispatcher) {
            apiFlow {
                val requestBody = MultipartBody.Builder().apply {
                    setType(MultipartBody.FORM)
                    addFormDataPart("username", username)
                    addFormDataPart("password", password)
                }.build()
                IApiImpl.get().login(requestBody)
            }.collect {
                _loginResponse.postValue(it)
            }
        }
}