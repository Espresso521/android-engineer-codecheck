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

    private fun getRecordFileList(folder: File?): List<Mp4FileMetadata> {
        val fileList = ArrayList<Mp4FileMetadata>()
        folder?.listFiles()?.forEach {
            if (it.name.endsWith(".mp4")) fileList.add(getMp4FileMetadata(it))
        }
        return fileList
    }

    private fun getMp4FileMetadata(item: File): Mp4FileMetadata {
        MediaMetadataRetriever().run {
            setDataSource(item.path)
            val duration = extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong()!!.div(1000.0)
            val fps = extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)
                ?.toInt()
                ?.div(duration)?.toInt().toString()

            return Mp4FileMetadata(
                frameAtTime,
                item.name,
                item.path,
                floor(duration + 0.5).toInt().toString(),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH),
                extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT),
                fps,
                (item.length()/(1024*1024)).toString()
            )
        }
    }

}
