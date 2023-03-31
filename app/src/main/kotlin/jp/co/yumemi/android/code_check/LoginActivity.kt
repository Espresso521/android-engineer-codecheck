package jp.co.yumemi.android.code_check

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.api.Future
import jp.co.yumemi.android.code_check.databinding.ActivityLoginBinding
import jp.co.yumemi.android.code_check.viewmodel.LoginViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    companion object {
        var TAG = LoginActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener(object: OnClickListener {
            override fun onClick(v: View?) {
                loginViewModel.login(binding.nameEditText.text.toString(), binding.passwordEditText.text.toString())
            }
        })

        loginViewModel.loginResponse.observe(this) {
            when (it) {
                is Future.Proceeding -> {
                    Log.e(TAG, "app login is proceeding")
                }
                is Future.Success -> {
                    Log.d(TAG, "app login Success value is ${it.value}")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Future.Error -> {
                    Log.d(TAG, "app login is ERROR: ${it.error}")
                }
            }
        }
    }
}