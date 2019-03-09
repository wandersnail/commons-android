package cn.zfs.commonsdemo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.snail.commons.annotation.RunThread
import com.snail.commons.annotation.ThreadType
import com.snail.commons.helper.ZipHelper
import com.snail.commons.interfaces.Callback
import com.snail.commons.utils.ToastUtils
import com.snail.fileselector.FileSelector
import com.snail.fileselector.OnFileSelectListener
import kotlinx.android.synthetic.main.activity_zip.*
import java.io.File
import java.io.FilenameFilter

/**
 * 描述:
 * 时间: 2018/12/11 08:54
 * 作者: zengfansheng
 */
class ZipActivity : BaseActivity() {
    private var selectType = 0
    private var fileSelector = FileSelector()
    private var files = ArrayList<File>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zip)     
        title = "解压缩"
        val loadDialog = ProgressDialog(this)
        loadDialog.setCancelable(false)
        fileSelector.setRoot(Environment.getExternalStorageDirectory())
        btnSelectZip.setOnClickListener { 
            selectType = 0
            fileSelector.setFilenameFilter(FilenameFilter { dir, name -> 
                dir.isDirectory || name.endsWith(".zip", true)
            })
            fileSelector.setMultiSelectionEnabled(true)
            fileSelector.setSelectionMode(FileSelector.FILES_ONLY)
            fileSelector.select(this)
        }
        btnSelectFile.setOnClickListener {
            selectType = 1
            fileSelector.setFilenameFilter(null)
            fileSelector.setMultiSelectionEnabled(true)
            fileSelector.setSelectionMode(FileSelector.FILES_AND_DIRECTORIES)
            fileSelector.select(this)
        }
        btnUnzipTo.setOnClickListener {
            selectType = 2
            fileSelector.setFilenameFilter(null)
            fileSelector.setMultiSelectionEnabled(false)
            fileSelector.setSelectionMode(FileSelector.DIRECTORIES_ONLY)
            fileSelector.select(this)
        }
        btnZipTo.setOnClickListener {
            selectType = 3
            fileSelector.setFilenameFilter(null)
            fileSelector.setMultiSelectionEnabled(false)
            fileSelector.setSelectionMode(FileSelector.DIRECTORIES_ONLY)
            fileSelector.select(this)
        }
        fileSelector.setOnFileSelectListener(object : OnFileSelectListener {
            override fun onFileSelect(paths: List<String>) {
                tvPaths.text = ""
                when (selectType) {
                    0 -> {
                        files.clear()
                        paths.forEach { path ->
                            tvPaths.append("$path\n")
                            files.add(File(path))
                        }
                    }
                    1 -> {
                        files.clear()
                        paths.forEach { path ->
                            tvPaths.append("$path\n")
                            files.add(File(path))
                        }
                    }
                    2 -> {
                        loadDialog.show()
                        ZipHelper.unzip().addZipFiles(files).setTargetDir(paths[0]).execute(object : Callback<Boolean> {
                            @RunThread(ThreadType.MAIN)
                            override fun onCallback(obj: Boolean?) {
                                loadDialog.dismiss()
                                files.clear()
                                tvPaths.text = ""
                                ToastUtils.showShort("解压${if (obj == true) "成功" else "失败"}")
                            }
                        })
                    }
                    3 -> {
                        loadDialog.show()
                        ZipHelper.zip().addSourceFiles(files).setLevel(9).setTarget(paths[0], "test").execute(object : Callback<File> {
                            @RunThread(ThreadType.MAIN)
                            override fun onCallback(obj: File?) {
                                loadDialog.dismiss()
                                files.clear()
                                tvPaths.text = ""
                                ToastUtils.showShort("压缩${if (obj != null) "成功" else "失败"}")
                            }
                        })
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileSelector.onActivityResult(requestCode, resultCode, data)
    }
}