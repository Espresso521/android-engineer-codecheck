# 株式会社ゆめみ Android エンジニアコードチェック課題

## 概要

 1. GitHub のリポジトリを検索するアプリを完備させる

 2. Camera２とMediaCodecとMediaRecordreとAudioRecordとAudioTrack、Androidに基づいてのマルチメディア技術のアプリを完備させる

## アプリ仕様

GitHub のリポジトリを検索するアプリ

<img src="docs/githubRepoSearch.gif" width="320">

Camera２、MediaCodec、AudioRecord、AudioTrackの効果

<img src="docs/camera2mediacodec.gif" width="320">

MediaRecorderの効果

<img src="docs/recordplayer.gif" width="320">

    I upgraded my pixel 7pro's system and noticed a problem with the Encode and Decode functions.
    I suspect it's an issue with the camera data return, which was fine before upgrading the phone system.
    I took other phones for testing and both Encode and Decode functioned fine, suggesting it was a problem after the phone upgrade.
    I haven't found how to fix the problem, so if you find a problem with the codecs on the latest phones and know how to fix it, please let me know.
    Thanks.

### 動作

1. 何かしらのキーワードを入力
2. GitHub API（`search/repositories`）でリポジトリを検索し、結果一覧を概要（リポジトリ名）で表示
3. 特定の結果を選択したら、該当リポジトリの詳細（リポジトリ名、オーナーアイコン、プロジェクト言語、Star 数、Watcher 数、Fork 数、Issue 数）を表示

