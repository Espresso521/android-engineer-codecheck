package jp.co.yumemi.android.code_check.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * repository information data class
 */
@Parcelize
data class RepoInfo(
    val name: String,
    val fullName: String,
    val loginName: String,
    val ownerIconUrl: String,
    val language: String,
    var description: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
    val createdTime: String,
) : Parcelable