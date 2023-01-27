package jp.co.yumemi.android.code_check.codec

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import jp.co.yumemi.android.code_check.camera.ICameraPreviewDataListener
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoEncoder @Inject constructor() : ICameraPreviewDataListener {

    private val TAG = VideoEncoder::class.simpleName

    @Inject
    lateinit var syncEncodeThread: SyncEncodeThread

    private var mCodeC: MediaCodec? = null
    private lateinit var mVideoFormat: MediaFormat

    // buffer 1 seconds data
    var nv12buffer = LinkedBlockingQueue<ByteArray>(25)

    /**
     * 初始化配置参数
     */
    fun initConfig(width: Int, height: Int) {
        mVideoFormat = MediaFormat.createVideoFormat("video/avc", width, height)
        mVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25)
        mVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)
        mVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 3)
        mVideoFormat.setInteger(
            MediaFormat.KEY_BITRATE_MODE,
            MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR
        );
        mVideoFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        )
        mCodeC = MediaCodec.createEncoderByType(mVideoFormat.getString(MediaFormat.KEY_MIME)!!)
        //此处不能用mOutputSurface，会configure失败
        mCodeC!!.configure(mVideoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mCodeC!!.start()
        syncEncodeThread.init(mCodeC!!, nv12buffer)
        Log.e(TAG, "Encoder codec started!")
    }

    fun start() {
        syncEncodeThread.startEncode()
        syncEncodeThread.start()
    }

    fun stop() {
        syncEncodeThread.stopEncode()
    }

    override fun previewNV21DataListener(data: ByteArray) {
        // TODO: add to LinkedBlockingQueue for produce and consume
    }

    override fun previewNV12DataListener(data: ByteArray) {
        nv12buffer.offer(data)
    }

}