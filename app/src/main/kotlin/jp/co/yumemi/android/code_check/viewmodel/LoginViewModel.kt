package jp.co.yumemi.android.code_check.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.api.Future
import jp.co.yumemi.android.code_check.api.IApiImpl
import jp.co.yumemi.android.code_check.api.apiFlow
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

    private val TAG = LoginViewModel::class.java.simpleName

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
                when (it) {
                    is Future.Proceeding -> {
                        Log.e(TAG, "app login is proceeding")
                    }
                    is Future.Success -> {
                        Log.d(TAG, "app login Success value is ${it.value}")
                    }
                    is Future.Error -> {
                        Log.d(TAG, "app login is ERROR: ${it.error}")
                    }
                }
            }
        }
}