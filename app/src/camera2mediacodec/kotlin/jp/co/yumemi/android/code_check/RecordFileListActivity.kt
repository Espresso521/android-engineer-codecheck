package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jp.co.yumemi.android.code_check.databinding.ActivityRecordFileListBinding
import jp.co.yumemi.android.code_check.ui.adapter.FileListAdapter
import jp.co.yumemi.android.code_check.utils.FileUtils
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class RecordFileListActivity : AppCompatActivity(R.layout.activity_record_file_list) {

    private lateinit var binding: ActivityRecordFileListBinding

    @Inject
    lateinit var adapter: FileListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecordFileListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fileList = ArrayList<File>()
        FileUtils.getMediaFileDir(this)?.listFiles()?.forEach {
            Log.e("huze", "file name is ${it.name}")
            if(it.name.endsWith(".mp4")) fileList.add(it)
        }

        adapter.run {
            onItemClickListener = object : FileListAdapter.OnItemClickListener {
                override fun itemClick(fileInfo: File) {
                    Log.e("huze", "click file $fileInfo")
                }
            }
            submitList(fileList)
        }

        binding.titleInfoTextView.text = "Total: ${fileList.size} files"

        binding.recyclerView.let {
            it.layoutManager = LinearLayoutManager(this@RecordFileListActivity)
            it.adapter = adapter
        }
    }
}