package jp.co.yumemi.android.code_check.camera

interface ICameraTakePhotoListener {
    fun takePhotoDataListener(orientation: Int, data: ByteArray)
}