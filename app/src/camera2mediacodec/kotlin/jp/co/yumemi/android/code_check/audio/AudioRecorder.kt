package jp.co.yumemi.android.code_check.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ActivityContext
import jp.co.yumemi.android.code_check.utils.FileUtils
import jp.kotaku.camera.utils.G711Converter
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class AudioRecorder @Inject constructor(
    @ActivityContext val context: Context
) {

    private val TAG = this::class.simpleName

    private var isRecording = false

    // 缓冲区字节大小
    private var bufferSizeInBytes = 0

    lateinit var audioRecord: AudioRecord
    lateinit var audioRecordThread: ReadThread

    @RequiresPermission(value = "android.permission.RECORD_AUDIO")
    fun createAudioRecordAndStart() {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSizeInBytes
        )

        audioRecordThread = ReadThread()
        audioRecordThread.init(bufferSizeInBytes)
        audioRecordThread.enableFileSave(true)
        audioRecordThread.start()
    }

    fun stopRecord() {
        audioRecord.stop()
        audioRecord.release()
        audioRecordThread.stopRecord()
    }

    inner class ReadThread : Thread() {
        private var outputStream: BufferedOutputStream? = null
        private var outputG711Stream: BufferedOutputStream? = null
        private var needSaveFile = false
        private var audioDataSize: Int = 0

        fun init(bufferSize: Int) {
            audioDataSize = bufferSize
            audioRecord.startRecording()
            isRecording = true
        }

        fun enableFileSave(saveFile: Boolean) {
            this.needSaveFile = saveFile

            if (saveFile) {
                createFile()
                createG711File()
            }
        }

        fun stopRecord() {
            isRecording = false
            outputStream?.flush()
            outputG711Stream?.flush()
            outputStream?.close()
            outputG711Stream?.close()
        }

        override fun run() {
            super.run()
            if (audioDataSize == 0) {
                Log.e(TAG, "audioDataSize is 0, can not start the record thread")
                return
            }

            val buffer = ByteArray(audioDataSize)
            while (isRecording) {
                val readSize = audioRecord.read(buffer, 0, audioDataSize)
                if (readSize > 0) {
                    saveToOriginalFile(buffer)
                    saveToG711File(buffer)
                } else {
                    val errorStr = when (readSize) {
                        AudioRecord.ERROR_INVALID_OPERATION -> "ERROR_INVALID_OPERATION"
                        AudioRecord.ERROR_BAD_VALUE -> "ERROR_BAD_VALUE"
                        AudioRecord.ERROR_DEAD_OBJECT -> "ERROR_DEAD_OBJECT"
                        AudioRecord.ERROR -> "ERROR"
                        else -> "Unknown Error"
                    }
                    Log.e(TAG, "audioRecord.read error is $errorStr")
                }
            }
        }

        private fun createFile() {
            val file = File(
                FileUtils.getFileDir(context), "audio_${TAG}_${System.currentTimeMillis()}.pcm"
            )
            if (file.exists()) {
                file.delete()
            }
            try {
                outputStream = BufferedOutputStream(FileOutputStream(file))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun createG711File() {
            val file = File(
                FileUtils.getFileDir(context), "audio_${TAG}_${System.currentTimeMillis()}.g711"
            )
            if (file.exists()) {
                file.delete()
            }
            try {
                outputG711Stream = BufferedOutputStream(FileOutputStream(file))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun saveToG711File(srcData: ByteArray) {
            if (needSaveFile) {
                try {
                    val res = ByteArray(srcData.size / 2)
                    G711Converter.encode(srcData, 0, srcData.size, res)
                    outputG711Stream!!.write(res, 0, res.size)
                } catch (e: IOException) {
                    Log.e(TAG, "IOException : " + e.printStackTrace())
                }
            }
        }

        private fun saveToOriginalFile(srcData: ByteArray) {
            if (needSaveFile) {
                try {
                    outputStream!!.write(srcData, 0, srcData.size)
                } catch (e: IOException) {
                    Log.e(TAG, "IOException : " + e.printStackTrace())
                }
            }
        }

    }

}