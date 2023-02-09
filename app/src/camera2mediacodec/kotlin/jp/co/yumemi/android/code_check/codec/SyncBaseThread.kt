package jp.co.yumemi.android.code_check.codec

import android.content.Context
import android.util.Log
import jp.co.yumemi.android.code_check.utils.FileUtils
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

abstract class SyncBaseThread(val context: Context) : Thread() {

    private val TAG = this::class.simpleName

    protected var outputStream: BufferedOutputStream? = null
    var needSaveFile = false
    fun enableFileSave(saveFile: Boolean) {
        this.needSaveFile = saveFile

        if (saveFile) {
            createFile()
        }
    }

    protected fun saveToFile(singleData: ByteArray) {
        if (needSaveFile) {
            try {
                outputStream!!.write(singleData, 0, singleData.size)
            } catch (e: IOException) {
                Log.e(TAG, "IOException : " + e.printStackTrace())
            }
        }
    }

    private fun createFile() {
        val file = File(
            FileUtils.getFileDir(context), "codec_${TAG}_${System.currentTimeMillis()}.h264"
        )
        if (file.exists()) {
            file.delete()
        }
        try {
            outputStream = BufferedOutputStream(FileOutputStream(file))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}