/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.hardware.camera2.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.audio.AudioRecorder
import jp.co.yumemi.android.code_check.audio.AudioTracker
import jp.co.yumemi.android.code_check.camera.CameraCapture
import jp.co.yumemi.android.code_check.camera.ICameraTakePhotoListener
import jp.co.yumemi.android.code_check.codec.VideoDecoder
import jp.co.yumemi.android.code_check.codec.VideoEncoder
import jp.co.yumemi.android.code_check.databinding.ActivityCameraBinding
import jp.co.yumemi.android.code_check.utils.YUVUtil
import jp.kotaku.camera.activity.BankSettingActivity
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class CameraActivity : AppCompatActivity(R.layout.activity_camera), ICameraTakePhotoListener {

    private val TAG = CameraActivity::class.simpleName

    val CAMERA_REQUEST_RESULT = 1

    private var isEncodeSurfaceviewCreated: Boolean = false

    @Inject
    lateinit var capture: CameraCapture

    @Inject
    lateinit var videoEncoder: VideoEncoder

    @Inject
    lateinit var videoDecoder: VideoDecoder

    @Inject
    lateinit var audioRecorder: AudioRecorder

    @Inject
    lateinit var audioTracker: AudioTracker

    private lateinit var binding: ActivityCameraBinding
    private lateinit var rotateThumbDrawable: RotateDrawable
    private lateinit var solidThumb: Drawable
    private lateinit var thumbAnimator: ObjectAnimator

    @RequiresPermission(value = "android.permission.RECORD_AUDIO")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSeekBar()

        if (!wasCameraPermissionWasGiven()) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                ), CAMERA_REQUEST_RESULT
            )
        }
        binding.surfaceView.holder.addCallback(surfaceViewCallBack)
        binding.codecSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) startEncode()
            else pauseEncode()
        }
        binding.recordSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) capture.startRecord()
            else capture.stopRecord()
        }
        binding.horizontalMirrorSwitch.setOnCheckedChangeListener { _, isChecked ->
            YUVUtil.isMirrorHorizontal = isChecked
        }
        binding.verticalMirrorSwitch.setOnCheckedChangeListener { _, isChecked ->
            YUVUtil.isMirrorVertical = isChecked
        }
        binding.saveH264Frame.setOnCheckedChangeListener { _, isChecked ->
            videoEncoder.enableH264Save(isChecked)
            videoDecoder.enableH264Save(isChecked)
        }
        binding.saveG711Data.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) audioRecorder.createAudioRecordAndStart()
            else audioRecorder.stopRecord()
        }
        binding.zoomSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                capture.handleZoom(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {//
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {//
            }
        })
        binding.fab.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                if (binding.saveG711Data.isChecked) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Disable [Save PCM/G711 Data] Switch",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                if (audioTracker.isPlaying) {
                    Toast.makeText(
                        this@CameraActivity, "AudioTrack is Playing Now", Toast.LENGTH_LONG
                    ).show()
                    return
                }

                if (TextUtils.isEmpty(AudioRecorder.pcmFileName)) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Enable [Save PCM/G711 Data] Switch to save pcm file first",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                audioTracker.createAudioTrackAndPlay()
            }
        })

        binding.fabFileList.setOnClickListener {
            startActivity(
                Intent(
                    this@CameraActivity,
                    RecordFileListActivity::class.java
                )
            )
        }

        binding.bankWebView.setOnClickListener {
            startActivity(
                Intent(
                    this@CameraActivity,
                    BankSettingActivity::class.java
                )
            )
        }
    }

    private fun initSeekBar() {
        rotateThumbDrawable = (AppCompatResources.getDrawable(
            applicationContext, R.drawable.rotate_thumb_1
        ) as RotateDrawable?)!!
        solidThumb =
            AppCompatResources.getDrawable(applicationContext, R.drawable.shape_thumb_round_1)!!

        binding.zoomSeekbar.thumb = rotateThumbDrawable
        thumbAnimator = ObjectAnimator.ofInt(rotateThumbDrawable, "level", 0, 10000)
        thumbAnimator.setDuration(1000)
        thumbAnimator.setRepeatCount(ValueAnimator.INFINITE)
        thumbAnimator.setInterpolator(LinearInterpolator())
        thumbAnimator.start()
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        capture.startBackgroundThread()
    }

    override fun onStop() {
        super.onStop()
        capture.stopBackgroundThread()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.closeCamera()
        stopEncode()
        thumbAnimator.cancel()
    }

    private fun wasCameraPermissionWasGiven(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (isEncodeSurfaceviewCreated) {
                surfaceViewCallBack.surfaceCreated(
                    binding.surfaceView.holder
                )
            }
        } else {
            Toast.makeText(
                this, "Camera permission is needed to run this application", Toast.LENGTH_LONG
            ).show()
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.CAMERA
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
            isEncodeSurfaceviewCreated = true
            if (wasCameraPermissionWasGiven()) {
                capture.setupCamera(holder.surface, this@CameraActivity)
                capture.connectCamera()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.e("CameraActivity", "Encode width is $width, height is $height")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            isEncodeSurfaceviewCreated = false
            Log.e("CameraActivity", "Encode SurfaceView Destroyed")
        }

    }

    fun takePhoto(view: View) {
        capture.takePhoto(windowManager.defaultDisplay.rotation)
    }

    private fun startEncode() {
        if (videoEncoder.isPaused()) {
            videoEncoder.onResume()
        } else {
            if (isEncodeSurfaceviewCreated) {
                videoEncoder.initConfig(1920, 1080)
                videoDecoder.initConfig(1080, 1920, binding.surfaceViewDecode.holder.surface)
                videoEncoder.start()
                videoDecoder.start()
            }
        }
    }

    private fun pauseEncode() {
        videoEncoder.onPause()
    }

    private fun stopEncode() {
        videoEncoder.stop()
        videoDecoder.stop()
    }

    override fun takePhotoDataListener(orientation: Int, data: ByteArray) {
        var bitmap: Bitmap? = BitmapFactory.decodeByteArray(data, 0, data.size) ?: return

        if (orientation != 0) {
            var matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            bitmap =
                bitmap?.let { Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true) }
        }

        binding.captureImgView.let {
            it.post { it.setImageBitmap(bitmap) }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setTitle("アプリを終了しますか？")
            .setPositiveButton("はい") { _, _ -> finish() }
            .setNegativeButton("いいえ") { dialog, _ -> dialog.dismiss() }.show()
    }

}
