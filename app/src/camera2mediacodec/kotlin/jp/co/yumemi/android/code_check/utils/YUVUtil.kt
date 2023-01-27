package jp.co.yumemi.android.code_check.utils

import android.media.Image
import java.nio.ByteBuffer


object YUVUtil {

    private var yBuffer: ByteBuffer? = null
    private var uBuffer: ByteBuffer? = null
    private var vBuffer: ByteBuffer? = null
    private var nv12: ByteArray? = null
    private var nv12rotate90: ByteArray? = null
    private var nv21: ByteArray? = null
    private var width: Int? = 0
    private var height: Int? = 0

    fun yuv420888toNV12(image: Image): ByteArray? {
        if (width == 0 && height == 0) {
            width = image.width
            height = image.height
            nv12 = ByteArray(width!! * height!! * 3 / 2)
            nv12rotate90 = ByteArray(width!! * height!! * 3 / 2)
        } else if (width != image.width || height != image.height) {
            nv12 = ByteArray(width!! * height!! * 3 / 2)
            nv12rotate90 = ByteArray(width!! * height!! * 3 / 2)
            if(width != image.width) width = image.width
            if(height != image.height) height = image.height
        }

        yBuffer = image.planes[0].buffer
        uBuffer = image.planes[1].buffer

        val ySize: Int = yBuffer!!.capacity()
        val uSize: Int = uBuffer!!.capacity()

        yBuffer!!.get(nv12, 0, ySize)
        uBuffer!!.get(nv12, ySize, uSize)

        rotateYUV420Degree90(nv12!!, image.width, image.height)
        //return mirrorYUV420(nv12rotate90!!, image.height, image.width)
        return nv12rotate90
    }

    private fun rotateYUV420Degree90(
        data: ByteArray,
        imageWidth: Int,
        imageHeight: Int
    ) {
        val yuv = nv12rotate90!!
        // Rotate the Y luma
        var i = 0
        for (x in 0 until imageWidth) {
            for (y in imageHeight - 1 downTo 0) {
                yuv[i] = data[y * imageWidth + x]
                i++
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1
        var x = imageWidth - 1
        while (x > 0) {
            for (y in 0 until imageHeight / 2) {
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + x]
                i--
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + (x - 1)]
                i--
            }
            x -= 2
        }
    }

    fun mirrorYUV420(data: ByteArray, width: Int, height: Int): ByteArray {

        var startPos: Int = 0
        var left: Int = 0
        var right: Int = 0
        var temp: Byte

        for (i in 0 until height) {
            left = startPos
            right = startPos + width -1
            while(left < right) {
                temp = data[left]
                data[left] = data[right]
                data[right] = temp
                left++
                right--
            }
            startPos += width
        }

        val offset = width * height
        startPos = 0

        for (i in 0 until height/2) {
            left = offset + startPos
            right = offset + startPos + width - 2
            while (left < right) {
                temp = data[left]
                data[left] = data[right]
                data[right] = temp
                left++
                right--

                temp = data[left]
                data[left] = data[right]
                data[right] = temp
                left++
                right--
            }
            startPos += width
        }

        return data
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