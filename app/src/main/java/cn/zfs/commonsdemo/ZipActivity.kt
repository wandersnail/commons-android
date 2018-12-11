package cn.zfs.commonsdemo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import cn.zfs.fileselector.FileSelector
import com.zfs.commons.annotation.RunThread
import com.zfs.commons.annotation.ThreadType
import com.zfs.commons.entity.ZipHelper
import com.zfs.commons.interfaces.Callback
import com.zfs.commons.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_zip.*
import java.io.File

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
            fileSelector.setMultiSelect(true)
            fileSelector.setSelectFile(true)
            fileSelector.select(this)
        }
        btnSelectFile.setOnClickListener {
            selectType = 1
            fileSelector.setMultiSelect(true)
            fileSelector.setSelectFile(true)
            fileSelector.select(this)
        }
        btnUnzipTo.setOnClickListener {
            selectType = 2
            fileSelector.setMultiSelect(false)
            fileSelector.setSelectFile(false)
            fileSelector.select(this)
        }
        btnZipTo.setOnClickListener {
            selectType = 3
            fileSelector.setMultiSelect(false)
            fileSelector.setSelectFile(false)
            fileSelector.select(this)
        }
        fileSelector.setOnFileSelectListener {
            tvPaths.text = ""
            when (selectType) {
                0 -> {
                    files.clear()
                    it.forEach { path ->
                        tvPaths.append("$path\n")
                        files.add(File(path))
                    }
                }
                1 -> {
                    files.clear()
                    it.forEach { path ->
                        tvPaths.append("$path\n")
                        files.add(File(path))
                    }
                }
                2 -> {
                    loadDialog.show()
                    ZipHelper.unzip().addZipFiles(files).setTargetDir(it[0]).execute(object : Callback<Boolean> {
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
                    ZipHelper.zip().addSourceFiles(files).setLevel(9).setTarget(it[0], "test").execute(object : Callback<File> {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileSelector.onActivityResult(requestCode, resultCode, data)
    }
}