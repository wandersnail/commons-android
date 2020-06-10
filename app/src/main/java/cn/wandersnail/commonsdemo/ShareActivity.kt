package cn.wandersnail.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import cn.wandersnail.commons.util.SysShareUtils
import cn.wandersnail.fileselector.FileSelector
import kotlinx.android.synthetic.main.activity_share.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * 描述:
 * 时间: 2018/9/28 09:50
 * 作者: zengfansheng
 */
class ShareActivity : BaseActivity() {
    private val fileSelector = FileSelector()
    private val imageFiles = ArrayList<File>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        fileSelector.setRoot(Environment.getExternalStorageDirectory())
        fileSelector.setSelectionMode(FileSelector.FILES_ONLY)
        fileSelector.setMultiSelectionEnabled(true)
        btnSelectImage.setOnClickListener {
            fileSelector.setOnFileSelectListener { _, paths -> tvImagePath.text = paths[0] }
            fileSelector.select(this, 0)
        }
        btnSelectImages.setOnClickListener {
            fileSelector.setOnFileSelectListener { _, paths ->
                tvImagePaths.text = ""
                paths.forEach { path ->
                    tvImagePaths.append("$path\n")
                    imageFiles.add(File(path))
                }
            }
            fileSelector.select(this, 1)
        }
        btnSelectFile.setOnClickListener {
            fileSelector.setOnFileSelectListener { _, paths -> tvFilePath.text = paths[0] }
            fileSelector.select(this, 2)
        }
        btnSelectVideo.setOnClickListener {
            fileSelector.setOnFileSelectListener { _, paths -> tvVideoPath.text = paths[0] }
            fileSelector.select(this, 3)
        }
        btnShareText.setOnClickListener { 
            val text = etText.text.toString().trim()
            if (text.isNotEmpty()) {
                SysShareUtils.shareText(this, "分享到", text)
            }
        }
        btnShareImage.setOnClickListener { 
            SysShareUtils.shareImage(this, "分享到", File(tvImagePath.text.toString()))
        }
        btnShareImages.setOnClickListener {
            SysShareUtils.shareImages(this, "分享到", imageFiles)
        }
        btnShareVideo.setOnClickListener {
            SysShareUtils.shareVideo(this, "分享到", File(tvVideoPath.text.toString()))
        }
        btnShareFile.setOnClickListener {
            SysShareUtils.shareFile(this, "分享到", File(tvFilePath.text.toString()))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileSelector.onActivityResult(requestCode, resultCode, data)
    }
}