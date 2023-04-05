package jp.co.yumemi.android.code_check

import android.content.Intent

class CameraLoginActivity : LoginActivity() {

    override fun onLoginSuccess() {
        startActivity(Intent(this@CameraLoginActivity, CameraActivity::class.java))
        finish()
    }

}