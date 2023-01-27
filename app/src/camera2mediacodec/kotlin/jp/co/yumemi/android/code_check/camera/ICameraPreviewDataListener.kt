package jp.co.yumemi.android.code_check.camera

interface ICameraPreviewDataListener {
    fun previewNV21DataListener(data: ByteArray)
    fun previewNV12DataListener(data: ByteArray)
}