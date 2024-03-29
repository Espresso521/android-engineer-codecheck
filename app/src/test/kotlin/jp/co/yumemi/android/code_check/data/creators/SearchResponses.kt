package jp.co.yumemi.android.code_check.data.creators

/**
 * Simulates a successful response json.
 */
const val SearchSuccessJSON = """
{
total_count: 241827,
incomplete_results: false,
items: [
{
id: 3432266,
node_id: "MDEwOlJlcG9zaXRvcnkzNDMyMjY2",
name: "kotlin",
full_name: "JetBrains/kotlin",
private: false,
owner: {
login: "JetBrains",
id: 878437,
node_id: "MDEyOk9yZ2FuaXphdGlvbjg3ODQzNw==",
avatar_url: "https://avatars.githubusercontent.com/u/878437?v=4",
gravatar_id: "",
url: "https://api.github.com/users/JetBrains",
html_url: "https://github.com/JetBrains",
followers_url: "https://api.github.com/users/JetBrains/followers",
following_url: "https://api.github.com/users/JetBrains/following{/other_user}",
gists_url: "https://api.github.com/users/JetBrains/gists{/gist_id}",
starred_url: "https://api.github.com/users/JetBrains/starred{/owner}{/repo}",
subscriptions_url: "https://api.github.com/users/JetBrains/subscriptions",
organizations_url: "https://api.github.com/users/JetBrains/orgs",
repos_url: "https://api.github.com/users/JetBrains/repos",
events_url: "https://api.github.com/users/JetBrains/events{/privacy}",
received_events_url: "https://api.github.com/users/JetBrains/received_events",
type: "Organization",
site_admin: false
},
html_url: "https://github.com/JetBrains/kotlin",
description: "The Kotlin Programming Language. ",
fork: false,
url: "https://api.github.com/repos/JetBrains/kotlin",
forks_url: "https://api.github.com/repos/JetBrains/kotlin/forks",
keys_url: "https://api.github.com/repos/JetBrains/kotlin/keys{/key_id}",
collaborators_url: "https://api.github.com/repos/JetBrains/kotlin/collaborators{/collaborator}",
teams_url: "https://api.github.com/repos/JetBrains/kotlin/teams",
hooks_url: "https://api.github.com/repos/JetBrains/kotlin/hooks",
issue_events_url: "https://api.github.com/repos/JetBrains/kotlin/issues/events{/number}",
events_url: "https://api.github.com/repos/JetBrains/kotlin/events",
assignees_url: "https://api.github.com/repos/JetBrains/kotlin/assignees{/user}",
branches_url: "https://api.github.com/repos/JetBrains/kotlin/branches{/branch}",
tags_url: "https://api.github.com/repos/JetBrains/kotlin/tags",
blobs_url: "https://api.github.com/repos/JetBrains/kotlin/git/blobs{/sha}",
git_tags_url: "https://api.github.com/repos/JetBrains/kotlin/git/tags{/sha}",
git_refs_url: "https://api.github.com/repos/JetBrains/kotlin/git/refs{/sha}",
trees_url: "https://api.github.com/repos/JetBrains/kotlin/git/trees{/sha}",
statuses_url: "https://api.github.com/repos/JetBrains/kotlin/statuses/{sha}",
languages_url: "https://api.github.com/repos/JetBrains/kotlin/languages",
stargazers_url: "https://api.github.com/repos/JetBrains/kotlin/stargazers",
contributors_url: "https://api.github.com/repos/JetBrains/kotlin/contributors",
subscribers_url: "https://api.github.com/repos/JetBrains/kotlin/subscribers",
subscription_url: "https://api.github.com/repos/JetBrains/kotlin/subscription",
commits_url: "https://api.github.com/repos/JetBrains/kotlin/commits{/sha}",
git_commits_url: "https://api.github.com/repos/JetBrains/kotlin/git/commits{/sha}",
comments_url: "https://api.github.com/repos/JetBrains/kotlin/comments{/number}",
issue_comment_url: "https://api.github.com/repos/JetBrains/kotlin/issues/comments{/number}",
contents_url: "https://api.github.com/repos/JetBrains/kotlin/contents/{+path}",
compare_url: "https://api.github.com/repos/JetBrains/kotlin/compare/{base}...{head}",
merges_url: "https://api.github.com/repos/JetBrains/kotlin/merges",
archive_url: "https://api.github.com/repos/JetBrains/kotlin/{archive_format}{/ref}",
downloads_url: "https://api.github.com/repos/JetBrains/kotlin/downloads",
issues_url: "https://api.github.com/repos/JetBrains/kotlin/issues{/number}",
pulls_url: "https://api.github.com/repos/JetBrains/kotlin/pulls{/number}",
milestones_url: "https://api.github.com/repos/JetBrains/kotlin/milestones{/number}",
notifications_url: "https://api.github.com/repos/JetBrains/kotlin/notifications{?since,all,participating}",
labels_url: "https://api.github.com/repos/JetBrains/kotlin/labels{/name}",
releases_url: "https://api.github.com/repos/JetBrains/kotlin/releases{/id}",
deployments_url: "https://api.github.com/repos/JetBrains/kotlin/deployments",
created_at: "2012-02-13T17:29:58Z",
updated_at: "2022-12-21T13:02:49Z",
pushed_at: "2022-12-21T14:07:53Z",
git_url: "git://github.com/JetBrains/kotlin.git",
ssh_url: "git@github.com:JetBrains/kotlin.git",
clone_url: "https://github.com/JetBrains/kotlin.git",
svn_url: "https://github.com/JetBrains/kotlin",
homepage: "https://kotlinlang.org",
size: 1635160,
stargazers_count: 43400,
watchers_count: 43400,
language: "Kotlin",
has_issues: false,
has_projects: false,
has_downloads: true,
has_wiki: false,
has_pages: false,
has_discussions: false,
forks_count: 5371,
mirror_url: null,
archived: false,
disabled: false,
open_issues_count: 162,
license: null,
allow_forking: true,
is_template: false,
web_commit_signoff_required: false,
topics: [
"compiler",
"gradle-plugin",
"intellij-plugin",
"kotlin",
"kotlin-library",
"maven-plugin",
"programming-language"
],
visibility: "public",
forks: 5371,
open_issues: 162,
watchers: 43400,
default_branch: "master",
score: 1
},
{
id: 91829561,
node_id: "MDEwOlJlcG9zaXRvcnk5MTgyOTU2MQ==",
name: "KotlinUdemy",
full_name: "hussien89aa/KotlinUdemy",
private: false,
owner: {
login: "hussien89aa",
id: 7304399,
node_id: "MDQ6VXNlcjczMDQzOTk=",
avatar_url: "https://avatars.githubusercontent.com/u/7304399?v=4",
gravatar_id: "",
url: "https://api.github.com/users/hussien89aa",
html_url: "https://github.com/hussien89aa",
followers_url: "https://api.github.com/users/hussien89aa/followers",
following_url: "https://api.github.com/users/hussien89aa/following{/other_user}",
gists_url: "https://api.github.com/users/hussien89aa/gists{/gist_id}",
starred_url: "https://api.github.com/users/hussien89aa/starred{/owner}{/repo}",
subscriptions_url: "https://api.github.com/users/hussien89aa/subscriptions",
organizations_url: "https://api.github.com/users/hussien89aa/orgs",
repos_url: "https://api.github.com/users/hussien89aa/repos",
events_url: "https://api.github.com/users/hussien89aa/events{/privacy}",
received_events_url: "https://api.github.com/users/hussien89aa/received_events",
type: "User",
site_admin: false
},
html_url: "https://github.com/hussien89aa/KotlinUdemy",
description: "Learn how to make online games, and apps for Android O, like Pokémon , twitter,Tic Tac Toe, and notepad using Kotlin",
fork: false,
url: "https://api.github.com/repos/hussien89aa/KotlinUdemy",
forks_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/forks",
keys_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/keys{/key_id}",
collaborators_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/collaborators{/collaborator}",
teams_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/teams",
hooks_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/hooks",
issue_events_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/issues/events{/number}",
events_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/events",
assignees_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/assignees{/user}",
branches_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/branches{/branch}",
tags_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/tags",
blobs_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/git/blobs{/sha}",
git_tags_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/git/tags{/sha}",
git_refs_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/git/refs{/sha}",
trees_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/git/trees{/sha}",
statuses_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/statuses/{sha}",
languages_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/languages",
stargazers_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/stargazers",
contributors_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/contributors",
subscribers_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/subscribers",
subscription_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/subscription",
commits_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/commits{/sha}",
git_commits_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/git/commits{/sha}",
comments_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/comments{/number}",
issue_comment_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/issues/comments{/number}",
contents_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/contents/{+path}",
compare_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/compare/{base}...{head}",
merges_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/merges",
archive_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/{archive_format}{/ref}",
downloads_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/downloads",
issues_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/issues{/number}",
pulls_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/pulls{/number}",
milestones_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/milestones{/number}",
notifications_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/notifications{?since,all,participating}",
labels_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/labels{/name}",
releases_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/releases{/id}",
deployments_url: "https://api.github.com/repos/hussien89aa/KotlinUdemy/deployments",
created_at: "2017-05-19T17:24:22Z",
updated_at: "2022-12-19T04:52:00Z",
pushed_at: "2021-03-15T19:58:10Z",
git_url: "git://github.com/hussien89aa/KotlinUdemy.git",
ssh_url: "git@github.com:hussien89aa/KotlinUdemy.git",
clone_url: "https://github.com/hussien89aa/KotlinUdemy.git",
svn_url: "https://github.com/hussien89aa/KotlinUdemy",
homepage: "https://www.udemy.com/the-complete-kotlin-developer-course/?couponCode=UDMEYGITHUB",
size: 2791,
stargazers_count: 1552,
watchers_count: 1552,
language: "Kotlin",
has_issues: true,
has_projects: true,
has_downloads: true,
has_wiki: true,
has_pages: false,
has_discussions: false,
forks_count: 5046,
mirror_url: null,
archived: false,
disabled: false,
open_issues_count: 13,
license: {
key: "mit",
name: "MIT License",
spdx_id: "MIT",
url: "https://api.github.com/licenses/mit",
node_id: "MDc6TGljZW5zZTEz"
},
allow_forking: true,
is_template: false,
web_commit_signoff_required: false,
topics: [
"kotlin",
"kotlin-android"
],
visibility: "public",
forks: 5046,
open_issues: 13,
watchers: 1552,
default_branch: "master",
score: 1
}
]
}
"""

/**
 * Simulates a error response json.
 */
const val SearchErrorJSON = """
{
message: "Validation Failed",
errors: [
{
resource: "Search",
field: "q",
code: "missing"
}
],
documentation_url: "https://docs.github.com/v3/search"
}
"""
