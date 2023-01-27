package jp.co.yumemi.android.code_check.codec

import android.content.Context
import android.media.MediaCodec
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.yumemi.android.code_check.utils.FileUtils
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class SyncEncodeThread @Inject constructor(@ApplicationContext val context: Context) : Thread() {

    var configByte: ByteArray? = null
    lateinit var mVideoSps: ByteArray
    lateinit var mVideoPps: ByteArray
    private var outputStream: BufferedOutputStream? = null
    var mIsEncoding = false

    init {
        createFile()
    }

    var mCodeC: MediaCodec? = null

    companion object {
        private const val TAG = "SyncEncodeThread"
    }

    fun init(mCodeC: MediaCodec) {
        this.mCodeC = mCodeC
    }

    override fun run() {
        super.run()

        val bufferInfo = MediaCodec.BufferInfo()
        while (mIsEncoding) {//当处于正在编码时进行循环读取数据
            var outputIndex = mCodeC!!.dequeueOutputBuffer(bufferInfo, 10_000)
            if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.e(TAG, "MediaCodec.INFO_TRY_AGAIN_LATER ???")
            } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {//当编码器开始返回数据开始
                //获取数据的配置头信息
                var byteBuffer = mCodeC!!.outputFormat.getByteBuffer("csd-0")
                mVideoSps = ByteArray(byteBuffer!!.remaining())
                byteBuffer.get(mVideoSps, 0, mVideoSps.size)

                byteBuffer = mCodeC!!.outputFormat.getByteBuffer("csd-1")
                mVideoPps = ByteArray(byteBuffer!!.remaining())
                byteBuffer.get(mVideoPps, 0, mVideoPps.size)
            } else if (outputIndex > 0) {//图像数据流到达
                var singleData = byteArrayOf()
                while (outputIndex >= 0) {//循环读取
                    val outputBuffer = mCodeC!!.getOutputBuffer(outputIndex)
                    val outData = ByteArray(bufferInfo.size)
                    outputBuffer!!.get(outData)//读取数据到buffer
                    if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        configByte = ByteArray(bufferInfo.size)
                        configByte = outData
                    } else if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                        if (configByte == null || configByte!!.isEmpty()) {
                            configByte = outData
                        }
                        val head = byteMerger(mVideoSps, mVideoPps)
                        var keyframe = ByteArray(bufferInfo.size + configByte!!.size)
                        configByte = byteMerger(head, configByte!!)
                        val out = byteMerger(configByte!!, outData)
                        keyframe = byteMerger(out, keyframe)
                        if (singleData.isEmpty()) {
                            singleData = keyframe
                        } else {
                            singleData = byteMerger(singleData, keyframe)
                        }
                    } else {
                        singleData = if (singleData.isEmpty()) {
                            outData
                        } else {
                            byteMerger(singleData, outData)
                        }
                    }
                    mCodeC!!.releaseOutputBuffer(outputIndex, false)
                    outputIndex = mCodeC!!.dequeueOutputBuffer(bufferInfo, 1_000)
                    try {
                        outputStream!!.write(singleData, 0, singleData.size)
                    } catch (e: IOException) {
                        Log.e(TAG, "IOException : " + e.printStackTrace())
                    }
                }

            }
        }
        //结束，释放资源
        mCodeC!!.signalEndOfInputStream()
        mCodeC!!.stop()
        mCodeC!!.release()
        mCodeC = null
        outputStream?.flush()
        outputStream?.close()
    }

    fun startEncode() {
        mIsEncoding = true
    }

    fun stopEncode() {
        mIsEncoding = false
    }

    private fun byteMerger(bt1: ByteArray, bt2: ByteArray): ByteArray {
        val bt3 = ByteArray(bt1.size + bt2.size)
        System.arraycopy(bt1, 0, bt3, 0, bt1.size)
        System.arraycopy(bt2, 0, bt3, bt1.size, bt2.size)
        return bt3
    }

    private fun createFile() {
        val file = File(
            FileUtils.getFileDir(context),
            "codec_${System.currentTimeMillis()}.h264"
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