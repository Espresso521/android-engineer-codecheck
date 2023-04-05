package jp.co.yumemi.android.code_check

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.api.HttpRequestState
import jp.co.yumemi.android.code_check.databinding.ActivityLoginBinding
import jp.co.yumemi.android.code_check.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    companion object {
        var TAG = LoginActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                loginViewModel.login(
                    binding.nameEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
            }
        })

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
                            onLoginSuccess()
                        }
                        is HttpRequestState.Error -> {
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
    }

    protected open fun onLoginSuccess() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
}