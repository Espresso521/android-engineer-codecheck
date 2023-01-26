package jp.co.yumemi.android.code_check.camera

interface ICameraDataListener {
    fun takePhotoDataListener(orientation: Int, data: ByteArray)
    fun previewNV21DataListener(data: ByteArray)
}