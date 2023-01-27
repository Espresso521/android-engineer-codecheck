package jp.co.yumemi.android.code_check.utils

import android.media.Image
import java.nio.ByteBuffer


object YUVUtil {

    private var yBuffer: ByteBuffer? = null
    private var uBuffer: ByteBuffer? = null
    private var vBuffer: ByteBuffer? = null
    private var nv12: ByteArray? = null
    private var nv21: ByteArray? = null
    private var width: Int? = 0
    private var height: Int? = 0

    fun yuv420888toNV12(image: Image): ByteArray? {
        if (width == 0 && height == 0) {
            width = image.width
            height = image.height
            nv12 = ByteArray(width!! * height!! * 3 / 2)
        } else if (width != image.width || height != image.height) {
            nv12 = ByteArray(width!! * height!! * 3 / 2)
        }

        yBuffer = image.planes[0].buffer
        uBuffer = image.planes[1].buffer

        val ySize: Int = yBuffer!!.capacity()
        val uSize: Int = uBuffer!!.capacity()

        yBuffer!!.get(nv12, 0, ySize)
        uBuffer!!.get(nv12, ySize, uSize)
        return nv12
    }

    fun yuv420888toNV21(image: Image): ByteArray? {
        if (width == 0 && height == 0) {
            width = image.width
            height = image.height
            nv21 = ByteArray(width!! * height!! * 3 / 2)
        } else if (width != image.width || height != image.height) {
            nv21 = ByteArray(width!! * height!! * 3 / 2)
        }

        yBuffer = image.planes[0].buffer
        vBuffer = image.planes[2].buffer

        val ySize: Int = yBuffer!!.capacity()
        val vSize: Int = vBuffer!!.capacity()

        yBuffer!!.get(nv21, 0, ySize)
        vBuffer!!.get(nv21, ySize, vSize)
        return nv21
    }
}