package jp.co.yumemi.android.code_check.codec

import android.content.Context
import android.media.MediaCodec
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.yumemi.android.code_check.data.EncodeDecodeDataRepo
import java.nio.ByteBuffer
import javax.inject.Inject

class SyncEncodeThread @Inject constructor(
    @ApplicationContext context: Context, var encodeDecodeDataRepo: EncodeDecodeDataRepo
) : SyncBaseThread(context) {

    companion object {
        var frameCount: Long = 1
    }

    private var configByte: ByteArray? = null
    lateinit var mVideoSps: ByteArray
    lateinit var mVideoPps: ByteArray
    private var mIsEncoding = false
    var isPause = false

    private lateinit var mCodeC: MediaCodec

    fun init(mCodeC: MediaCodec) {
        this.mCodeC = mCodeC
    }

    override fun run() {
        super.run()
        try {
            val bufferInfo = MediaCodec.BufferInfo()
            while (mIsEncoding) {

                if (isPause) {
                    sleep(500)
                    continue
                }

                val data = encodeDecodeDataRepo.getCameraPreviewData()
                val inputBufferIndex = mCodeC.dequeueInputBuffer(0)
                if (inputBufferIndex >= 0) {
                    val inputBuffer: ByteBuffer? = mCodeC.getInputBuffer(inputBufferIndex)
                    inputBuffer?.clear()
                    inputBuffer?.put(data)
                    mCodeC.queueInputBuffer(
                        inputBufferIndex, 0, data.size, frameCount++ * 1000000 / 30, 0
                    )
                }

                var outputIndex = mCodeC.dequeueOutputBuffer(bufferInfo, 0)
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
                } else if (outputIndex >= 0) {//图像数据流到达
                    var singleData = byteArrayOf()
                    while (outputIndex >= 0) {
                        val outputBuffer = mCodeC.getOutputBuffer(outputIndex)
                        val outData = ByteArray(bufferInfo.size)
                        outputBuffer!!.get(outData)//读取数据到buffer
                        when (bufferInfo.flags) {
                            MediaCodec.BUFFER_FLAG_CODEC_CONFIG -> {
                                configByte = ByteArray(bufferInfo.size)
                                configByte = outData
                            }
                            MediaCodec.BUFFER_FLAG_KEY_FRAME -> {
                                if (configByte == null || configByte!!.isEmpty()) {
                                    configByte = outData
                                }
                                val head = byteMerger(mVideoSps, mVideoPps)
                                var keyframe = ByteArray(bufferInfo.size + configByte!!.size)
                                configByte = byteMerger(head, configByte!!)
                                val out = byteMerger(configByte!!, outData)
                                keyframe = byteMerger(out, keyframe)
                                singleData = if (singleData.isEmpty()) {
                                    keyframe
                                } else {
                                    byteMerger(singleData, keyframe)
                                }
                            }
                            else -> {
                                singleData = if (singleData.isEmpty()) {
                                    outData
                                } else {
                                    byteMerger(singleData, outData)
                                }
                            }
                        }
                        mCodeC.releaseOutputBuffer(outputIndex, false)
                        outputIndex = mCodeC.dequeueOutputBuffer(bufferInfo, 0)
                    }
                    encodeDecodeDataRepo.h264EncodeDataListener(singleData)
                    saveToFile(singleData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startEncode() {
        mIsEncoding = true
    }

    fun stopEncode() {
        mIsEncoding = false
        mCodeC.stop()
        mCodeC.release()
        outputStream?.flush()
        outputStream?.close()
    }

    private fun byteMerger(bt1: ByteArray, bt2: ByteArray): ByteArray {
        val bt3 = ByteArray(bt1.size + bt2.size)
        System.arraycopy(bt1, 0, bt3, 0, bt1.size)
        System.arraycopy(bt2, 0, bt3, bt1.size, bt2.size)
        return bt3
    }

}