package jp.co.yumemi.android.code_check.data

import android.graphics.Bitmap

data class Mp4FileMetadata(
    val bitmap: Bitmap?,
    val name: String,
    val path: String,
    val duration: String?,
    val width: String?,
    val height: String?,
    val fps: String?
)