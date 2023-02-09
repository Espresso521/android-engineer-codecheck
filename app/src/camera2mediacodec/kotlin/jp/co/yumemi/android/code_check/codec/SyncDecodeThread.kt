package jp.co.yumemi.android.code_check.codec

import android.content.Context
import android.media.MediaCodec
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.yumemi.android.code_check.data.EncodeDecodeDataRepo
import java.nio.ByteBuffer
import javax.inject.Inject

class SyncDecodeThread @Inject constructor(
    @ApplicationContext context: Context,
    var encodeDecodeDataRepo: EncodeDecodeDataRepo
) : SyncBaseThread(context) {

    companion object {
        var frameCount: Long = 1
    }

    private var isDecoding = false

    private lateinit var mCodeC: MediaCodec

    fun init(mCodeC: MediaCodec) {
        this.mCodeC = mCodeC
    }

    override fun run() {
        super.run()

        val bufferInfo = MediaCodec.BufferInfo()
        var byteBuffer: ByteBuffer?

        try {
            while (isDecoding) {
                val data = encodeDecodeDataRepo.getCodecEncodeData()
                saveToFile(data)
                val inputBufferIndex = mCodeC.dequeueInputBuffer(0)

                if (inputBufferIndex >= 0) {
                    byteBuffer = mCodeC.getInputBuffer(inputBufferIndex)
                    byteBuffer?.clear()
                    byteBuffer?.put(data, 0, data.size)
                    mCodeC.queueInputBuffer(
                        inputBufferIndex,
                        0,
                        data.size,
                        frameCount++ * 1000000 / 30,
                        0
                    )
                }

                var outIndex: Int = mCodeC.dequeueOutputBuffer(bufferInfo, 0)
                while (outIndex >= 0) {
                    mCodeC.releaseOutputBuffer(outIndex, true);
                    outIndex = mCodeC.dequeueOutputBuffer(bufferInfo, 0);
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startDecode() {
        isDecoding = true
    }

    fun stopDecode() {
        isDecoding = false
        mCodeC.stop()
        mCodeC.release()
        outputStream?.flush()
        outputStream?.close()
    }
}