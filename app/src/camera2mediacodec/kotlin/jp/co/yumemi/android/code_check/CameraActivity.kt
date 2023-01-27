/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.camera.CameraCapture
import jp.co.yumemi.android.code_check.camera.ICameraDataListener
import jp.co.yumemi.android.code_check.codec.VideoEncoder
import jp.co.yumemi.android.code_check.databinding.ActivityCameraBinding
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CameraActivity : AppCompatActivity(R.layout.activity_camera), ICameraDataListener {

    private val TAG = CameraActivity::class.simpleName

    val CAMERA_REQUEST_RESULT = 1

    private var shouldProceedWithOnResume: Boolean = true

    @Inject
    lateinit var capture: CameraCapture

    @Inject
    lateinit var videoEncoder: VideoEncoder

    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!wasCameraPermissionWasGiven()) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_RESULT)
        }
        binding.surfaceView.holder.addCallback(surfaceViewCallBack)
        capture.startBackgroundThread()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        capture.startBackgroundThread()
        if (binding.surfaceView.isActivated && shouldProceedWithOnResume) {
            binding.surfaceView.holder.surface?.let { capture.setupCamera(it, this@CameraActivity) }
        } else if (!binding.surfaceView.isActivated) {
            binding.surfaceView.holder.addCallback(surfaceViewCallBack)
        }
        shouldProceedWithOnResume = !shouldProceedWithOnResume
    }

    override fun onPause() {
        super.onPause()
        capture.stopBackgroundThread()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.closeCamera()
    }

    private fun wasCameraPermissionWasGiven(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (binding.surfaceView.isActivated) {
                surfaceViewCallBack.surfaceCreated(
                    binding.surfaceView.holder
                )
            }
        } else {
            Toast.makeText(
                this,
                "Camera permission is needed to run this application",
                Toast.LENGTH_LONG
            )
                .show()
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", this.packageName, null)
                startActivity(intent)
            }
        }
    }

    /**
     * Surface Texture Listener
     */
    private val surfaceViewCallBack = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            if (wasCameraPermissionWasGiven()) {
                capture.setupCamera(holder.surface, this@CameraActivity)
                capture.connectCamera()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.e("CameraActivity", "format is $format, width is $width, height is $height")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.e("CameraActivity", "SurfaceView Destroyed")
        }

    }

    fun takePhoto(view: View) {
        capture.takePhoto(windowManager.defaultDisplay.rotation)
    }

    override fun takePhotoDataListener(orientation: Int, data: ByteArray) {
        var bitmap: Bitmap? = BitmapFactory.decodeByteArray(data, 0, data.size) ?: return

        if (orientation != 0) {
            var matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            Log.e(TAG, "jpeg orientation is " + orientation.toFloat())
            bitmap = bitmap?.let { Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true) }
        }

        binding.captureImgView.let {
            it.post { it.setImageBitmap(bitmap) }
        }
    }

    override fun previewNV21DataListener(data: ByteArray) {
        // TODO: add to LinkedBlockingQueue for produce and consume
    }

    fun startRecord(view: View) {}
    fun stopRecord(view: View) {}

}
