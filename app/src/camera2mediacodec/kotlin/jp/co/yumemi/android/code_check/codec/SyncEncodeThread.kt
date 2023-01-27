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

class SyncEncodeThread @Inject constructor(
    @ApplicationContext val context: Context,
    var encodeDecodeDataRepo: EncodeDecodeDataRepo
) : Thread() {

    private val TAG = SyncEncodeThread::class.simpleName

    private var configByte: ByteArray? = null
    lateinit var mVideoSps: ByteArray
    lateinit var mVideoPps: ByteArray
    private var outputStream: BufferedOutputStream? = null
    private var mIsEncoding = false
    var needSaveFile = false

    private lateinit var mCodeC: MediaCodec


    fun init(mCodeC: MediaCodec) {
        this.mCodeC = mCodeC
    }

    override fun run() {
        super.run()

        val bufferInfo = MediaCodec.BufferInfo()
        while (mIsEncoding) {

            val data = encodeDecodeDataRepo.getCameraPreviewData()
            val inputBufferIndex = mCodeC.dequeueInputBuffer(10_000)
            if (inputBufferIndex >= 0) {
                val inputBuffer: ByteBuffer? = mCodeC.getInputBuffer(inputBufferIndex)
                inputBuffer?.clear()
                inputBuffer?.put(data)
                mCodeC.queueInputBuffer(inputBufferIndex, 0, data.size, 0, 0)
            }

            var outputIndex = mCodeC.dequeueOutputBuffer(bufferInfo, 10_000)
            if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                sleep(10)
            } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                //获取数据的配置头信息
                var byteBuffer = mCodeC.outputFormat.getByteBuffer("csd-0")
                mVideoSps = ByteArray(byteBuffer!!.remaining())
                byteBuffer.get(mVideoSps, 0, mVideoSps.size)

                byteBuffer = mCodeC.outputFormat.getByteBuffer("csd-1")
                mVideoPps = ByteArray(byteBuffer!!.remaining())
                byteBuffer.get(mVideoPps, 0, mVideoPps.size)
            } else if (outputIndex > 0) {//图像数据流到达
                var singleData = byteArrayOf()
                while (outputIndex >= 0) {//循环读取
                    val outputBuffer = mCodeC.getOutputBuffer(outputIndex)
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
                    mCodeC.releaseOutputBuffer(outputIndex, false)
                    outputIndex = mCodeC.dequeueOutputBuffer(bufferInfo, 1_000)
                }
                encodeDecodeDataRepo.h264EncodeDataListener(singleData)
                saveToFile(singleData)
            }
        }
        mCodeC.stop()
        mCodeC.release()
        outputStream?.flush()
        outputStream?.close()
    }

    fun startEncode() {
        mIsEncoding = true
    }

    fun stopEncode() {
        mIsEncoding = false
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

    private fun byteMerger(bt1: ByteArray, bt2: ByteArray): ByteArray {
        val bt3 = ByteArray(bt1.size + bt2.size)
        System.arraycopy(bt1, 0, bt3, 0, bt1.size)
        System.arraycopy(bt2, 0, bt3, bt1.size, bt2.size)
        return bt3
    }

    private fun createFile() {
        val file = File(
            FileUtils.getFileDir(context), "codec_${System.currentTimeMillis()}.h264"
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