package jp.co.yumemi.android.code_check.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * repository information data class
 */
@Parcelize
data class RepoInfo(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable