package cn.wandersnail.commonsdemo

import android.content.Intent
import android.os.Bundle
import cn.wandersnail.commons.poster.MethodInfo
import cn.wandersnail.commons.util.FileUtils
import cn.wandersnail.commons.util.ToastUtils
import cn.wandersnail.fileselector.FileSelector
import kotlinx.android.synthetic.main.activity_file_operate.*
import java.io.File
import kotlin.concurrent.thread

/**
 * date: 2019/8/8 12:28
 * author: zengfansheng
 */
class FileOperateActivity : BaseActivity() {
    private var path = ""
    private val fileSelector = FileSelector()
    private val fileSelector2 = FileSelector()
    private var copy = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_operate)
        fileSelector.setOnFileSelectListener { _, paths ->
            path = paths[0]
            tvPath.text = path
        }
        fileSelector2.setSelectionMode(FileSelector.DIRECTORIES_ONLY)
        fileSelector2.setOnFileSelectListener { _, paths ->
            if (copy) {
                FileUtils.copy(File(path), File(paths[0], FileUtils.getFileName(path)))
            } else {
                FileUtils.move(File(path), File(paths[0], FileUtils.getFileName(path)))
            }
            ToastUtils.showShort(if (copy) "复制完成: ${paths[0]}" else "移动完成: ${paths[0]}")
        }
        btnSelectFile.setOnClickListener { 
            fileSelector.setSelectionMode(FileSelector.FILES_ONLY)
            fileSelector.select(this, 0)
        }
        btnSelectDir.setOnClickListener {
            fileSelector.setSelectionMode(FileSelector.DIRECTORIES_ONLY)
            fileSelector.select(this, 1)
        }
        btnCopy.setOnClickListener { 
            if (path.isNotEmpty()) {
                copy = true
                fileSelector2.select(this, 2)
            }
        }
        btnMove.setOnClickListener {
            if (path.isNotEmpty()) {
                copy = false
                fileSelector2.select(this, 3)
            }
        }
        btnClear.setOnClickListener { 
            if (path.isNotEmpty()) {
                FileUtils.emptyDir(File(path))
            }
        }
        btnDelectDir.setOnClickListener { 
            if (path.isNotEmpty()) {
                FileUtils.deleteDir(File(path))
            }
        }
        thread {
            App.instance?.observable?.notifyObservers("onChanged", MethodInfo.Parameter(Any::class.java, "大风来了"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileSelector.onActivityResult(requestCode, resultCode, data)
        fileSelector2.onActivityResult(requestCode, resultCode, data)
    }
}
