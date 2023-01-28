package jp.co.yumemi.android.code_check.utils

import android.media.Image
import java.nio.ByteBuffer


object YUVUtil {

    private var yBuffer: ByteBuffer? = null
    private var uBuffer: ByteBuffer? = null
    private var vBuffer: ByteBuffer? = null
    private var nv21: ByteArray? = null
    private var nv12: ByteArray? = null
    private var retNv12Byte: ByteArray? = null
    private var width: Int? = 0
    private var height: Int? = 0

    var isMirrorHorizontal: Boolean = false
    var isMirrorVertical: Boolean = false

    fun yuv420888toNV12(image: Image): ByteArray? {
        if (width == 0 && height == 0) {
            width = image.width
            height = image.height
            nv12 = ByteArray(width!! * height!! * 3 / 2)
            retNv12Byte = ByteArray(width!! * height!! * 3 / 2)
        } else if (width != image.width || height != image.height) {
            nv12 = ByteArray(width!! * height!! * 3 / 2)
            retNv12Byte = ByteArray(width!! * height!! * 3 / 2)
            if (width != image.width) width = image.width
            if (height != image.height) height = image.height
        }

        yBuffer = image.planes[0].buffer
        uBuffer = image.planes[1].buffer

        val ySize: Int = yBuffer!!.capacity()
        val uSize: Int = uBuffer!!.capacity()

        yBuffer!!.get(nv12, 0, ySize)
        uBuffer!!.get(nv12, ySize, uSize)

        retNv12Byte = rotateYUV420Degree90(nv12!!, image.width, image.height)
        if (isMirrorHorizontal) retNv12Byte = mirrorHorizontalYUV420(retNv12Byte!!, image.height, image.width)
        if (isMirrorVertical) retNv12Byte = mirrorVerticalYUV420(retNv12Byte!!, image.height, image.width)
        return retNv12Byte
    }

    private fun rotateYUV420Degree90(
        src: ByteArray,
        imageWidth: Int,
        imageHeight: Int
    ): ByteArray {
        val dest = ByteArray(width!! * height!! * 3 / 2)
        // Rotate the Y luma
        var i = 0
        for (x in 0 until imageWidth) {
            for (y in imageHeight - 1 downTo 0) {
                dest[i] = src[y * imageWidth + x]
                i++
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1
        var x = imageWidth - 1
        while (x > 0) {
            for (y in 0 until imageHeight / 2) {
                dest[i] = src[imageWidth * imageHeight + y * imageWidth + x]
                i--
                dest[i] = src[imageWidth * imageHeight + y * imageWidth + (x - 1)]
                i--
            }
            x -= 2
        }

        return dest
    }

    private fun rotateYUV420Degree90(
        src: ByteArray,
        dest: ByteArray,
        imageWidth: Int,
        imageHeight: Int
    ) {
        // Rotate the Y luma
        var i = 0
        for (x in 0 until imageWidth) {
            for (y in imageHeight - 1 downTo 0) {
                dest[i] = src[y * imageWidth + x]
                i++
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1
        var x = imageWidth - 1
        while (x > 0) {
            for (y in 0 until imageHeight / 2) {
                dest[i] = src[imageWidth * imageHeight + y * imageWidth + x]
                i--
                dest[i] = src[imageWidth * imageHeight + y * imageWidth + (x - 1)]
                i--
            }
            x -= 2
        }
    }

    private fun mirrorHorizontalYUV420(src: ByteArray, width: Int, height: Int): ByteArray {
        val dest = ByteArray(YUVUtil.width!! * YUVUtil.height!! * 3 / 2)
        var index = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val oldY = y
                val oldX = width - 1 - x
                val oldIndex = oldY * width + oldX
                dest[index++] = src[oldIndex]
            }
        }

        for (y in 0 until height step 2) {
            for (x in 0 until width step 2) {
                val oldY = y
                val oldX = width - 1 - (x + 1)
                val vuY = height + oldY / 2
                val vuX = oldX
                val vuIndex = vuY * width + vuX
                dest[index++] = src[vuIndex]
                dest[index++] = src[vuIndex + 1]
            }
        }
        return dest
    }

    private fun mirrorHorizontalYUV420(src: ByteArray, dest: ByteArray, width: Int, height: Int) {
        var index = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val oldY = y
                val oldX = width - 1 - x
                val oldIndex = oldY * width + oldX
                dest[index++] = src[oldIndex]
            }
        }

        for (y in 0 until height step 2) {
            for (x in 0 until width step 2) {
                val oldY = y
                val oldX = width - 1 - (x + 1)
                val vuY = height + oldY / 2
                val vuX = oldX
                val vuIndex = vuY * width + vuX
                dest[index++] = src[vuIndex]
                dest[index++] = src[vuIndex + 1]
            }
        }
    }

    private fun mirrorVerticalYUV420(src: ByteArray, width: Int, height: Int): ByteArray {
        val dest = ByteArray(YUVUtil.width!! * YUVUtil.height!! * 3 / 2)
        var index = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val oldY = height - 1 - y
                val oldX = x
                val oldIndex = oldY * width + oldX
                dest[index++] = src[oldIndex]
            }
        }

        for (y in 0 until height step 2) {
            for (x in 0 until width step 2) {
                val oldY = height - 1 - (y + 1)
                val oldX = x
                val vuY = height + oldY / 2
                val vuX = oldX
                val vuIndex = vuY * width + vuX
                dest[index++] = src[vuIndex]
                dest[index++] = src[vuIndex + 1]
            }
        }
        return dest
    }

    private fun mirrorVerticalYUV420(src: ByteArray, dest: ByteArray, width: Int, height: Int) {
        var index = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val oldY = height - 1 - y
                val oldX = x
                val oldIndex = oldY * width + oldX
                dest[index++] = src[oldIndex]
            }
        }

        for (y in 0 until height step 2) {
            for (x in 0 until width step 2) {
                val oldY = height - 1 - (y + 1)
                val oldX = x
                val vuY = height + oldY / 2
                val vuX = oldX
                val vuIndex = vuY * width + vuX
                dest[index++] = src[vuIndex]
                dest[index++] = src[vuIndex + 1]
            }
        }
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