package jp.co.yumemi.android.code_check

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.ExperimentalAssetLoader
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.api.HttpRequestState
import jp.co.yumemi.android.code_check.databinding.ActivityLoginBinding
import jp.co.yumemi.android.code_check.viewmodel.LoginViewModel
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    companion object {
        var TAG = this::class.java.simpleName
    }

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var loginAV: RiveAnimationView

    @OptIn(ExperimentalAssetLoader::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.viewModel = loginViewModel
        setContentView(binding.root)

        // Start a coroutine in the lifecycle scope
        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginResponse.collect {
                    when (it) {
                        is HttpRequestState.Proceeding -> {
                            Log.e(TAG, "app login is proceeding")
                        }
                        is HttpRequestState.Success -> {
                            Log.d(TAG, "app login Success value is ${it.value}")
                            loginAV.fireState("LoginStateMachine", "success")
                            loginAV.postDelayed(Runnable { onLoginSuccess() }, 2000)
                        }
                        is HttpRequestState.Error -> {
                            loginAV.fireState("LoginStateMachine", "fail")
                            Toast.makeText(
                                this@LoginActivity,
                                "Username or password Error",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d(TAG, "app login is ERROR: ${it.error}")
                        }
                    }
                }
            }
        }

        loginAV = binding.loginAnimationView
        binding.loginButton.setOnClickListener {
            loginAV.setBooleanState("LoginStateMachine", "hands_up", false)
            loginAV.postDelayed(Runnable { loginViewModel.onLogin() }, 2000)
        }

        binding.testButton.setOnClickListener {
            startActivity(Intent(this@LoginActivity, TestActivity::class.java))
            finish()
        }

        binding.nameEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if(s.isNotEmpty()) {
                        loginAV.setBooleanState("LoginStateMachine", "Check", true)
                    } else {
                        loginAV.setBooleanState("LoginStateMachine", "Check", false)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // do nothing
            }
        })

        binding.passwordEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if(s.isNotEmpty()) {
                        loginAV.setBooleanState("LoginStateMachine", "hands_up", true)
                    } else {
                        loginAV.setBooleanState("LoginStateMachine", "hands_up", false)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // do nothing
            }
        })
    }

    protected open fun onLoginSuccess() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
}