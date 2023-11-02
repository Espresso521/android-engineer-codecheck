package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.api.HttpRequestState
import jp.co.yumemi.android.code_check.api.IApi
import jp.co.yumemi.android.code_check.api.apiFlow
import jp.co.yumemi.android.code_check.data.model.Login
import jp.co.yumemi.android.code_check.data.model.ResponseResult
import jp.co.yumemi.android.code_check.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val httpsAPI: IApi
) :
    ViewModel() {

    val nameText = MutableLiveData<String>()
    val passwordText = MutableLiveData<String>()

    private val _loginResponse = MutableStateFlow<HttpRequestState<ResponseResult<Login>>>(HttpRequestState.Proceeding)
    val loginResponse: StateFlow<HttpRequestState<ResponseResult<Login>>> = _loginResponse

    private fun login(username: String, password: String) =
        viewModelScope.launch(ioDispatcher) {
            apiFlow {
                val requestBody = MultipartBody.Builder().apply {
                    setType(MultipartBody.FORM)
                    addFormDataPart("username", username)
                    addFormDataPart("password", password)
                }.build()
                httpsAPI.login(requestBody)
            }.collect {
                _loginResponse.value = it
            }
        }

    fun onLogin() {
        nameText.value?.let { name ->
            passwordText.value?.let { password ->
                login(name, password)
            }
        }
    }
}