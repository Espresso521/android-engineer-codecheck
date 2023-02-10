package jp.co.yumemi.android.code_check.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioTracker @Inject constructor() {
    private val TAG = this::class.simpleName

    private var bufferSizeInBytes = 0

    lateinit var audioTrack: AudioTrack

    var isPlaying = false

    fun createAudioTrackAndPlay() {
        if (isPlaying) return
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioTrack.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            44100,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSizeInBytes,
            AudioTrack.MODE_STREAM
        )

        audioTrack.play()
        PlayThread().start()
    }

    inner class PlayThread : Thread() {
        override fun run() {
            super.run()
            isPlaying = true
            val buffer = ByteArray(2048)
            val inputStream = FileInputStream(AudioRecorder.pcmFileName)
            Log.d(TAG, "Play file is ${AudioRecorder.pcmFileName}")
            var readNumber = inputStream.read(buffer)
            try {
                while (readNumber > 0) {
                    val ret = audioTrack.write(buffer, 0, readNumber)
                    if (ret < 0) {
                        val errorStr = when (ret) {
                            AudioTrack.ERROR_INVALID_OPERATION -> "ERROR_INVALID_OPERATION"
                            AudioTrack.ERROR_BAD_VALUE -> "ERROR_BAD_VALUE"
                            AudioTrack.ERROR_DEAD_OBJECT -> "ERROR_DEAD_OBJECT"
                            AudioTrack.ERROR -> "ERROR"
                            else -> "Unknown Error"
                        }
                        Log.e(TAG, "audioRecord.read error is $errorStr")
                    }
                    readNumber = inputStream.read(buffer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inputStream.close()
                audioTrack.stop()
                audioTrack.release()
                isPlaying = false
            }
        }
    }
}