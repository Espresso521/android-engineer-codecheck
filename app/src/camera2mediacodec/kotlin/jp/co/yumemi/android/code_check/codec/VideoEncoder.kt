package jp.co.yumemi.android.code_check.codec

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoEncoder @Inject constructor() {

    private val TAG = VideoEncoder::class.simpleName

    @Inject
    lateinit var syncEncodeThread: SyncEncodeThread

    var mCodeC: MediaCodec? = null
    lateinit var mVideoFormat: MediaFormat
    var mWidth = 0
    var mHeight = 0

    /**
     * 初始化配置参数
     */
    fun initConfig(width: Int, height: Int) {
        mWidth = width
        mHeight = height
        mVideoFormat = MediaFormat.createVideoFormat("video/avc", width, height)
        mVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25)
        mVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)
        mVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 3)
        mVideoFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
        mVideoFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        )
        mCodeC = MediaCodec.createEncoderByType(mVideoFormat.getString(MediaFormat.KEY_MIME)!!)
        //此处不能用mOutputSurface，会configure失败
        mCodeC!!.configure(mVideoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mCodeC!!.start()
        syncEncodeThread.init(mCodeC!!)
        Log.e(TAG, "Encoder codec started!")
    }

    fun start() {
        syncEncodeThread.startEncode()
    }

    fun stop() {
        syncEncodeThread.stopEncode()
    }

}