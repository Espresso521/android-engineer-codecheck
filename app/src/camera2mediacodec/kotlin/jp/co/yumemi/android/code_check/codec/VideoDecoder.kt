package jp.co.yumemi.android.code_check.codec

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoDecoder @Inject constructor(var syncDecodeThread: SyncDecodeThread) {

    private val TAG = VideoDecoder::class.simpleName

    /**
     * 初始化配置参数
     */
    fun initConfig(width: Int, height: Int, surface: Surface) {
        val mVideoFormat: MediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        val mCodeC = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mCodeC.configure(mVideoFormat, surface, null, 0)
        mCodeC.start()
        syncDecodeThread.init(mCodeC)
        Log.e(TAG, "Decoder codec started!")
    }

    fun start() {
        syncDecodeThread.startDecode()
        syncDecodeThread.start()
    }

    fun stop() {
        syncDecodeThread.stopDecode()
    }

    fun enableH264Save(isSave : Boolean) {
        syncDecodeThread.enableFileSave(isSave)
    }

}