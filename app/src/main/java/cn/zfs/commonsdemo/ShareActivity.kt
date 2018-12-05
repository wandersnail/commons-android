package cn.zfs.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import cn.zfs.fileselector.FileSelector
import com.zfs.commons.utils.ShareUtils
import kotlinx.android.synthetic.main.activity_share.*
import java.io.File

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
        fileSelector.setSelectFile(true)
        fileSelector.setMultiSelect(true)
        fileSelector.setFilenameFilter { _, name ->
            !name.startsWith(".")
        }
        btnSelectImage.setOnClickListener { _ ->
            fileSelector.setOnFileSelectListener {
                tvImagePath.text = it[0]
            }
            fileSelector.select(this)
        }
        btnSelectImages.setOnClickListener { _ ->
            fileSelector.setOnFileSelectListener {
                tvImagePaths.text = ""
                it.forEach { path ->
                    tvImagePaths.append("$path\n")
                    imageFiles.add(File(path))
                }
            }
            fileSelector.select(this)
        }
        btnSelectVideo.setOnClickListener { _ ->
            fileSelector.setOnFileSelectListener {
                tvVideoPath.text = it[0]
            }
            fileSelector.select(this)
        }
        btnShareText.setOnClickListener { 
            val text = etText.text.toString().trim()
            if (!text.isEmpty()) {
                ShareUtils.shareText(this, "分享到", text)
            }
        }
        btnShareImage.setOnClickListener { 
            ShareUtils.shareImage(this, "分享到", File(tvImagePath.text.toString()))
        }
        btnShareImages.setOnClickListener {
            ShareUtils.shareImages(this, "分享到", imageFiles)
        }
        btnShareVideo.setOnClickListener {
            ShareUtils.shareVideo(this, "分享到", File(tvVideoPath.text.toString()))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileSelector.onActivityResult(requestCode, resultCode, data)
    }
}