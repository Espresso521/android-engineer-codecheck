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

### 動作

1. 何かしらのキーワードを入力
2. GitHub API（`search/repositories`）でリポジトリを検索し、結果一覧を概要（リポジトリ名）で表示
3. 特定の結果を選択したら、該当リポジトリの詳細（リポジトリ名、オーナーアイコン、プロジェクト言語、Star 数、Watcher 数、Fork 数、Issue 数）を表示

