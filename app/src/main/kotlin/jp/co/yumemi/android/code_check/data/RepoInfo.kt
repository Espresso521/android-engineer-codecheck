package jp.co.yumemi.android.code_check.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * repository information data class
 */
@Parcelize
data class RepoInfo(
    @SerializedName("name")
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("stargazers_count")
    val stargazersCount: Long,
    @SerializedName("watchers_count")
    val watchersCount: Long,
    @SerializedName("forks")
    val forksCount: Long,
    @SerializedName("open_issues_count")
    val openIssuesCount: Long,
    @SerializedName("created_at")
    val createdTime: String,
    @SerializedName("owner")
    val owner: RepoOwner,
) : Parcelable

@Parcelize
data class RepoOwner(
    @SerializedName("login")
    val loginName: String,
    @SerializedName("avatar_url")
    val ownerIconUrl: String,
) : Parcelable