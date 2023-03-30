package jp.co.yumemi.android.code_check

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.databinding.ActivityLoginBinding
import jp.co.yumemi.android.code_check.viewmodel.LoginViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel.login(binding.nameEditText.text.toString(), binding.passwordEditText.text.toString())
    }
}