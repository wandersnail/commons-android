package cn.wandersnail.commonsdemo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import cn.wandersnail.commons.helper.ZipHelper
import cn.wandersnail.commons.util.ToastUtils
import cn.wandersnail.commonsdemo.databinding.ActivityZipBinding
import cn.wandersnail.fileselector.FileSelector
import java.io.File

/**
 * 描述:
 * 时间: 2018/12/11 08:54
 * 作者: zengfansheng
 */
class ZipActivity : BaseViewBindingActivity<ActivityZipBinding>() {
    private var selectType = 0
    private var fileSelector = FileSelector()
    private var files = ArrayList<File>()

    override fun getViewBindingClass(): Class<ActivityZipBinding> {
        return ActivityZipBinding::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loadDialog = ProgressDialog(this)
        loadDialog.setCancelable(false)
        fileSelector.setRoot(Environment.getExternalStorageDirectory())
        binding.btnSelectZip.setOnClickListener {
            selectType = 0
            fileSelector.setFilenameFilter { dir, name ->
                File(dir, name).isDirectory || name.endsWith(".zip", true)
            }
            fileSelector.setMultiSelectionEnabled(true)
            fileSelector.setSelectionMode(FileSelector.FILES_ONLY)
            fileSelector.select(this, 0)
        }
        binding.btnSelectFile.setOnClickListener {
            selectType = 1
            fileSelector.setFilenameFilter(null)
            fileSelector.setMultiSelectionEnabled(true)
            fileSelector.setSelectionMode(FileSelector.FILES_AND_DIRECTORIES)
            fileSelector.select(this, 1)
        }
        binding.btnUnzipTo.setOnClickListener {
            selectType = 2
            fileSelector.setFilenameFilter(null)
            fileSelector.setMultiSelectionEnabled(false)
            fileSelector.setSelectionMode(FileSelector.DIRECTORIES_ONLY)
            fileSelector.select(this, 2)
        }
        binding.btnZipTo.setOnClickListener {
            selectType = 3
            fileSelector.setFilenameFilter(null)
            fileSelector.setMultiSelectionEnabled(false)
            fileSelector.setSelectionMode(FileSelector.DIRECTORIES_ONLY)
            fileSelector.select(this, 3)
        }
        fileSelector.setOnFileSelectListener { _, paths ->
            binding.tvPaths.text = ""
            when (selectType) {
                0 -> {
                    files.clear()
                    paths.forEach { path ->
                        binding.tvPaths.append("$path\n")
                        files.add(File(path))
                    }
                }
                1 -> {
                    files.clear()
                    paths.forEach { path ->
                        binding.tvPaths.append("$path\n")
                        files.add(File(path))
                    }
                }
                2 -> {
                    loadDialog.show()
                    ZipHelper.unzip().addZipFiles(files).setTargetDir(paths[0]).execute { obj ->
                        loadDialog.dismiss()
                        files.clear()
                        binding.tvPaths.text = ""
                        ToastUtils.showShort("解压${if (obj == true) "成功" else "失败"}")
                    }
                }
                3 -> {
                    loadDialog.show()
                    ZipHelper.zip().addSourceFiles(files).setLevel(9).setTarget(paths[0], "test").execute { obj ->
                        loadDialog.dismiss()
                        files.clear()
                        binding.tvPaths.text = ""
                        ToastUtils.showShort("压缩${if (obj != null) "成功" else "失败"}")
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileSelector.onActivityResult(requestCode, resultCode, data)
    }
}