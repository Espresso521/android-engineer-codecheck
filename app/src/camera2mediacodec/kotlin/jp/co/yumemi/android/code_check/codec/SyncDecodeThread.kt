package jp.co.yumemi.android.code_check.codec

import android.content.Context
import android.media.MediaCodec
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.yumemi.android.code_check.data.EncodeDecodeDataRepo
import jp.co.yumemi.android.code_check.utils.FileUtils
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import javax.inject.Inject

class SyncDecodeThread @Inject constructor(
    @ApplicationContext val context: Context,
    var encodeDecodeDataRepo: EncodeDecodeDataRepo
) : Thread() {

    private val TAG = SyncDecodeThread::class.simpleName

    private var isDecoding = false

    private lateinit var mCodeC: MediaCodec

    var needSaveFile = false
    private var outputStream: BufferedOutputStream? = null

    fun init(mCodeC: MediaCodec) {
        this.mCodeC = mCodeC
    }

    override fun run() {
        super.run()

        val bufferInfo = MediaCodec.BufferInfo()
        var byteBuffer : ByteBuffer?

        while (isDecoding) {

            val data = encodeDecodeDataRepo.getCodecEncodeData()
            saveToFile(data)
            val inputBufferIndex = mCodeC.dequeueInputBuffer(10_000)

            if (inputBufferIndex >= 0) {
                byteBuffer = mCodeC.getInputBuffer(inputBufferIndex)
                if (byteBuffer == null) {
                    continue
                }
                byteBuffer.clear()
                byteBuffer.put(data, 0, data.size)
                mCodeC.queueInputBuffer(inputBufferIndex, 0, data.size, 0, 0)
            } else {
                sleep(10);
                continue;
            }

            val outIndex: Int = mCodeC.dequeueOutputBuffer(bufferInfo, 0)
            if(outIndex >=0) {
                mCodeC.releaseOutputBuffer(outIndex, true)
            } else {
                val errorMsg = when (outIndex) {
                    MediaCodec.INFO_TRY_AGAIN_LATER -> "MediaCodec.INFO_TRY_AGAIN_LATER"
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> "MediaCodec.INFO_OUTPUT_FORMAT_CHANGED"
                    else -> "Unknown"
                }
                Log.e(TAG, "Error when Decoding: $errorMsg")
            }
        }
        mCodeC.stop()
        mCodeC.release()
        outputStream?.flush()
        outputStream?.close()
    }

    fun startDecode() {
        isDecoding = true
    }

    fun stopDecode() {
        isDecoding = false
    }

    fun enableFileSave(saveFile: Boolean) {
        this.needSaveFile = saveFile

        if (saveFile) {
            createFile()
        }
    }

    private fun saveToFile(singleData: ByteArray) {
        if (needSaveFile) {
            try {
                outputStream!!.write(singleData, 0, singleData.size)
            } catch (e: IOException) {
                Log.e(TAG, "IOException : " + e.printStackTrace())
            }
        }
    }

    private fun createFile() {
        val file = File(
            FileUtils.getFileDir(context), "codec_decoder_${System.currentTimeMillis()}.h264"
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
}