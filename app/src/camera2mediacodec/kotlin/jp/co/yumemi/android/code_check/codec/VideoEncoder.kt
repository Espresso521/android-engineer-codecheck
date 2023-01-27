package jp.co.yumemi.android.code_check.codec

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoEncoder @Inject constructor(var syncEncodeThread: SyncEncodeThread) {

    private val TAG = VideoEncoder::class.simpleName

    /**
     * 初始化配置参数
     */
    fun initConfig(width: Int, height: Int) {
        // we rotate 90 degree to the camera preview data, so change the width and height
        val mVideoFormat: MediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, height, width)
        mVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 25)
        mVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2)
        mVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 3)
        mVideoFormat.setInteger(
            MediaFormat.KEY_BITRATE_MODE,
            MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR
        );
        mVideoFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        )
        val mCodeC = MediaCodec.createEncoderByType(mVideoFormat.getString(MediaFormat.KEY_MIME)!!)
        //此处不能用mOutputSurface，会configure失败
        mCodeC.configure(mVideoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mCodeC.start()
        syncEncodeThread.init(mCodeC)
        Log.e(TAG, "Encoder codec started!")
    }

    fun start() {
        //syncEncodeThread.enableFileSave(true)
        syncEncodeThread.startEncode()
        syncEncodeThread.start()
    }

    fun stop() {
        syncEncodeThread.stopEncode()
    }

}