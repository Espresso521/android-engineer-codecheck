package jp.co.yumemi.android.code_check.utils

import android.media.Image
import java.nio.ByteBuffer


object YUVUtil {

    private var yBuffer: ByteBuffer? = null
    private var uBuffer: ByteBuffer? = null
    private var vBuffer: ByteBuffer? = null

    fun yuv420888toNV12(image: Image): ByteArray {
        yBuffer = image.planes[0].buffer
        uBuffer = image.planes[1].buffer
        vBuffer = image.planes[2].buffer

        val ySize: Int = yBuffer!!.capacity()
        val uSize: Int = uBuffer!!.capacity()

        val nv12 = ByteArray(image.width * image.height * 3 / 2)
        yBuffer!!.get(nv12, 0, ySize)
        uBuffer!!.get(nv12, ySize, uSize)
        return nv12
    }

    fun yuv420888toNV21(image: Image): ByteArray {
        yBuffer = image.planes[0].buffer
        uBuffer = image.planes[1].buffer
        vBuffer = image.planes[2].buffer

        val ySize: Int = yBuffer!!.capacity()
        val vSize: Int = vBuffer!!.capacity()

        val nv21 = ByteArray(image.width * image.height * 3 / 2)
        yBuffer!!.get(nv21, 0, ySize)
        vBuffer!!.get(nv21, ySize, vSize)
        return nv21
    }
}