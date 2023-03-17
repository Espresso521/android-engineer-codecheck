package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.data.Mp4FileMetadata
import jp.co.yumemi.android.code_check.databinding.ActivityRecordFileListBinding
import jp.co.yumemi.android.code_check.ui.adapter.FileListAdapter
import jp.co.yumemi.android.code_check.utils.FileUtils
import jp.co.yumemi.android.code_check.viewmodel.RecordFileListViewModel
import javax.inject.Inject

@AndroidEntryPoint
class RecordFileListActivity : AppCompatActivity(R.layout.activity_record_file_list) {

    private lateinit var binding: ActivityRecordFileListBinding

    @Inject
    lateinit var adapter: FileListAdapter

    private val recordFileListViewModel: RecordFileListViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecordFileListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter.run {
            onItemClickListener = object : FileListAdapter.OnItemClickListener {
                override fun itemClick(fileMetadata: Mp4FileMetadata) {
                    Log.e("huze", "click file $fileMetadata")
                    val intent = MediaPlayerActivity.createIntent(this@RecordFileListActivity)

                    intent.putExtras(Bundle().apply {
                        putString(MediaPlayerActivity.filePathKey, fileMetadata.path)
                        putString(MediaPlayerActivity.fileNameKey, fileMetadata.name)
                    })
                    startActivity(intent)
                }
            }
            recordFileListViewModel.recordFileList.observe(this@RecordFileListActivity) {
                if(it.isEmpty()) return@observe
                submitList(it)
                binding.progressBar.visibility = View.GONE
                binding.titleInfoTextView.text = "Total: ${it.size} files"
            }
        }

        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(this@RecordFileListActivity)
            it.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        recordFileListViewModel.doRefreshRecordFileList(FileUtils.getMediaFileDirOld(this@RecordFileListActivity))
    }
}