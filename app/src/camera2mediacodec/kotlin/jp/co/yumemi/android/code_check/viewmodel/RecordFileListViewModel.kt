package jp.co.yumemi.android.code_check.viewmodel

import android.media.MediaMetadataRetriever
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.yumemi.android.code_check.data.Mp4FileMetadata
import jp.co.yumemi.android.code_check.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class RecordFileListViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) :
    ViewModel() {

    private var _recordFileList: MutableLiveData<List<Mp4FileMetadata>> = MutableLiveData(listOf())
    val recordFileList: LiveData<List<Mp4FileMetadata>> = _recordFileList

    fun doRefreshRecordFileList(folder: File?) {
        viewModelScope.launch(ioDispatcher) {
            _recordFileList.postValue(getRecordFileList(folder))
        }
    }

    private fun getRecordFileList(folder: File?) : List<Mp4FileMetadata> {
        val fileList = ArrayList<Mp4FileMetadata>()
        folder?.listFiles()?.forEach {
            if (it.name.endsWith(".mp4")) fileList.add(getMp4FileMetadata(it))
        }
        return fileList
    }

    private fun getMp4FileMetadata(item: File): Mp4FileMetadata {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(item.path)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLong()!! / 1000.0
        return Mp4FileMetadata(
            retriever.frameAtTime,
            item.name,
            item.path,
            floor(duration + 0.5).toInt().toString(),
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH),
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        )
    }

}
