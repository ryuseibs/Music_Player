```markdown
# 🎵 ミュージックアプリ開発進捗まとめ
🚀 **Android ミュージックプレイヤーアプリの開発進捗と実装ポイントの記録**

## ✅ 開発進捗
### 1️⃣ プロジェクトのセットアップ
- Android Studio でプロジェクトを作成し、`Git` で管理開始
- `Jetpack Compose` を導入し、UI を Kotlin コードで作成
- `ViewModel` を導入し、データ管理を `StateFlow` で行う設計

---

### 2️⃣ 音楽再生機能の実装
✅ **`MediaPlayer` を利用してローカルの音楽ファイルを再生**  
✅ **「再生・一時停止・次の曲・前の曲」の基本操作を実装**  
✅ **「次の曲」「前の曲」でも自動で再生が切り替わるように制御**  

#### 🎵 **実装ポイント**
```kotlin
fun playCurrentTrack() {
    mediaPlayer?.release()
    val songPath = _songList.value.getOrNull(_currentSongIndex.value)?.filePath ?: return
    mediaPlayer = MediaPlayer().apply {
        setDataSource(songPath)
        prepare()
        start()

        setOnCompletionListener {
            nextTrack()
        }
    }

    _isPlaying.value = true
}
```
---

### 3️⃣ シークバー（再生位置の表示 & 移動）の実装
✅ **`Slider` を導入して、現在の再生位置をリアルタイムで更新**  
✅ **`viewModelScope.launch {}` を使い、`500ms` ごとに `currentPosition` を更新**  
✅ **`seekTo(position)` を実装し、スライダーの移動で再生位置が変更可能に**

#### 🎵 **実装ポイント**
```kotlin
viewModelScope.launch {
    while (_isPlaying.value) {
        _currentPosition.value = mediaPlayer?.currentPosition ?: 0
        delay(500)
    }
}
```
---

### 4️⃣ アートワークの表示
✅ **`MediaMetadataRetriever` を使って MP3 に埋め込まれたアートワークを取得**

#### 🎵 **実装ポイント**
```kotlin
private fun getEmbeddedAlbumArt(context: Context, filePath: String): String? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(filePath)
        val art = retriever.embeddedPicture
        retriever.release()

        if (art != null) {
            val file = File(context.cacheDir, "album_art_${filePath.hashCode()}.jpg")
            FileOutputStream(file).use { fos -> fos.write(art) }
            file.absolutePath
        } else null
    } catch (e: Exception) {
        retriever.release()
        null
    }
}
```
---

### 📂 ディレクトリ構成
```plaintext
Music_Player/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/music_player/
│   │   │   │   ├── PlayerActivity.kt
│   │   │   │   ├── PlayerScreen.kt
│   │   │   │   ├── MusicViewModel.kt
│   │   │   │   ├── MusicRepository.kt
│   │   │   │   ├── PermissionManager.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   ├── layout/
│   │   │   │   ├── values/
│   │   │   ├── AndroidManifest.xml
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
├── README.md
```

---

## 📌 今後の予定
✅ **iOS 9 のミュージックアプリ風の UI 調整**  
✅ **アートワークのアニメーション（フェードイン・ズーム・回転など）を追加**  
✅ **ボタンのデザインや配置を iOS 9 風に整える**

---
```


