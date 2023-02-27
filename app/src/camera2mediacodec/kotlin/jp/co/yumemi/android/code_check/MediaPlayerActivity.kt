package jp.co.yumemi.android.code_check

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.databinding.ActivityMediaplayerBinding

@AndroidEntryPoint
class MediaPlayerActivity : AppCompatActivity(R.layout.activity_mediaplayer) {

    private lateinit var binding: ActivityMediaplayerBinding

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediaplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mediaPlaySFView.holder.addCallback(surfaceViewCallBack)
        mediaPlayer = MediaPlayer().apply {
            val filePath = intent.extras?.getString("filePath")
            setDataSource(filePath)
            prepare()
        }
    }

    private val surfaceViewCallBack = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            mediaPlayer.setSurface(holder.surface)
            mediaPlayer.start()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.e("MediaPlayerActivity", "Encode width is $width, height is $height")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.e("MediaPlayerActivity", "Encode SurfaceView Destroyed")
        }
    }

}