package jp.co.yumemi.android.code_check.utils

import android.content.Context
import android.media.Image
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class FileUtils {
    companion object {

        fun getFileDir(context: Context): File? {
            var filesDir = context.getExternalFilesDir(null)
            if (filesDir == null) {
                filesDir = context.filesDir
            }
            return filesDir
        }

        fun getMediaFileDirOld(context: Context?): File {
            val fileDir = getFileDir(
                context!!
            )!!
            val wavFileDir = File(fileDir, "media")
            if (!wavFileDir.exists()) {
                wavFileDir.mkdirs()
            }
            return wavFileDir
        }

        fun saveJpeg(context: Context, id: String, captureJpegName: String, image: Image) {
            val buffer: ByteBuffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            val fileOutputStream =
                FileOutputStream(File(getMediaFileDirOld(context), captureJpegName))
            try {
                fileOutputStream.write(bytes)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fileOutputStream.close()
            }
        }
    }
}