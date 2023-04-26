package jp.co.yumemi.android.code_check

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.RotateDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.databinding.ActivityMediaplayerBinding
import java.util.*

@AndroidEntryPoint
class MediaPlayerActivity : AppCompatActivity(R.layout.activity_mediaplayer) {
    companion object {
        private val TAG = ActivityMediaplayerBinding::class.java.simpleName
        const val filePathKey = "filePath"
        const val fileNameKey = "fileName"
        fun createIntent(context: Context): Intent {
            return Intent(
                context,
                MediaPlayerActivity::class.java
            )
        }
    }

    private lateinit var binding: ActivityMediaplayerBinding
    private lateinit var thumbAnimator: ObjectAnimator

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var timer: Timer
    private lateinit var mediaMetadataRetriever: MediaMetadataRetriever

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediaplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filePath = intent.extras?.getString(filePathKey)
        binding.mediaPlaySFView.holder.addCallback(surfaceViewCallBack)
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener {
                binding.playControlImageView.setBackgroundResource(R.mipmap.play_icon)
                binding.positionSeekbar.progress = binding.positionSeekbar.max
                binding.playTimeFirstTextView.text = getPlayTime(mediaPlayer.duration)
                timer.cancel()
            }
            setScreenOnWhilePlaying(true)
            setDataSource(filePath)
            prepare()
        }

        mediaMetadataRetriever = MediaMetadataRetriever().apply {
            setDataSource(filePath)
        }

        binding.closeImageView.setOnClickListener {
            timer.cancel()
            finish()
        }
        binding.fileNameTextView.text = intent.extras?.getString(fileNameKey)
        binding.playTimeSecondTextView.text =
            StringBuilder("/").append(getPlayTime(mediaPlayer.duration)).toString()
        binding.playControlImageView.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                it.setBackgroundResource(R.mipmap.play_icon)
                mediaPlayer.pause()
                stopSeekTimer()
            } else {
                it.setBackgroundResource(R.mipmap.pause_icon)
                mediaPlayer.start()
                startSeekTimer()
            }
        }

        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        initSeekBar()
    }

    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val screenH = windowManager.currentWindowMetrics.bounds.height()
            binding.mediaPlaySFView.adjustSize(screenH * 9 / 16, screenH)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        mediaPlayer.release()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val screenW = windowManager.currentWindowMetrics.bounds.width()
        val screenH = windowManager.currentWindowMetrics.bounds.height()
        val orientation = when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> "ORIENTATION_PORTRAIT"
            Configuration.ORIENTATION_LANDSCAPE -> "ORIENTATION_LANDSCAPE"
            else -> "Configuration.ORIENTATION_UNDEFINED"
        }
        Log.d(TAG, "orientation is $orientation, screenWidth is $screenW, screenHeight is $screenH")
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
            && screenW * 16 < screenH * 9
        ) {
            binding.mediaPlaySFView.adjustSize(screenW, screenW * 16 / 9)
        } else {
            binding.mediaPlaySFView.adjustSize(screenH * 9 / 16, screenH)
        }
    }

    private val surfaceViewCallBack = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            mediaPlayer.setSurface(holder.surface)
            mediaPlayer.start()
            startSeekTimer()
            binding.playControlImageView.setBackgroundResource(R.mipmap.pause_icon)
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.e(TAG, "Surface width is $width, height is $height")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.e(TAG, "SurfaceView Destroyed")
            mediaPlayer.pause()
            stopSeekTimer()
            binding.playControlImageView.setBackgroundResource(R.mipmap.play_icon)
        }
    }

    private fun initSeekBar() {
        val rotateThumbDrawable = (AppCompatResources.getDrawable(
            applicationContext, R.drawable.rotate_thumb_1
        ) as RotateDrawable?)!!

        binding.positionSeekbar.max = mediaPlayer.duration
        binding.positionSeekbar.thumb = rotateThumbDrawable
        thumbAnimator = ObjectAnimator.ofInt(rotateThumbDrawable, "level", 0, 10000)
        thumbAnimator.duration = 1000
        thumbAnimator.repeatCount = ValueAnimator.INFINITE
        thumbAnimator.interpolator = LinearInterpolator()
        thumbAnimator.start()

        binding.positionSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            private var startPreviewTime = 0L
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.playTimeFirstTextView.text = getPlayTime(progress)
                    val currentTime = System.currentTimeMillis()
                    if (progress % 5 == 0 && (currentTime - startPreviewTime) > 500) {
                        startPreviewTime = currentTime
                        binding.previewImageView.load(mediaMetadataRetriever.getFrameAtTime(progress * 1000L))
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                binding.previewImageView.visibility = View.VISIBLE
                stopSeekTimer()
                startPreviewTime = 0
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                startSeekTimer()
                seekBar?.let { mediaPlayer.seekTo(it.progress) }
                binding.previewImageView.visibility = View.GONE
            }
        })
    }

    private fun startSeekTimer() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                try {
                    if (mediaPlayer.isPlaying) {
                        val currentPosition = mediaPlayer.currentPosition
                        runOnUiThread {
                            binding.positionSeekbar.progress = currentPosition
                            binding.playTimeFirstTextView.text = getPlayTime(currentPosition)
                        }
                    }
                } catch (ex: IllegalStateException) {
                    // media player maybe released
                    ex.printStackTrace()
                }
            }
        }, 0, 1000)
    }

    private fun stopSeekTimer() {
        timer.cancel()
    }

    private fun getPlayTime(time: Int): String {
        val duration = time.div(1000.0).plus(0.5).toInt()
        val hour = duration / 3600
        val minute = duration / 60
        val second = duration % 60
        return if (hour > 0) {
            String.format("%02d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", minute, second)
        }
    }

}