package jp.co.yumemi.android.code_check.data

import jp.co.yumemi.android.code_check.camera.ICameraPreviewDataListener
import jp.co.yumemi.android.code_check.codec.IEncoderDataListener
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncodeDecodeDataRepo @Inject constructor() : ICameraPreviewDataListener, IEncoderDataListener {

    private var cameraPreviewBuffer = LinkedBlockingQueue<ByteArray>(25)
    private var codecEncodeBuffer = LinkedBlockingQueue<ByteArray>(25)

    fun getCameraPreviewData() : ByteArray {
        return cameraPreviewBuffer.take()
    }

    fun getCodecEncodeData() : ByteArray {
        return cameraPreviewBuffer.take()
    }

    override fun previewNV21DataListener(data: ByteArray) {
        //
    }

    override fun previewNV12DataListener(data: ByteArray) {
        cameraPreviewBuffer.offer(data)
    }

    override fun h264EncodeDataListener(data: ByteArray) {
        codecEncodeBuffer.offer(data)
    }
}