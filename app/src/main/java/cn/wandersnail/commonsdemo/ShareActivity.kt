package cn.wandersnail.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import cn.wandersnail.commons.util.SysShareUtils
import cn.wandersnail.commonsdemo.databinding.ActivityShareBinding
import cn.wandersnail.fileselector.FileSelector
import java.io.File

/**
 * 描述:
 * 时间: 2018/9/28 09:50
 * 作者: zengfansheng
 */
class ShareActivity : BaseViewBindingActivity<ActivityShareBinding>() {
    private val fileSelector = FileSelector()
    private val imageFiles = ArrayList<File>()

    override fun getViewBindingClass(): Class<ActivityShareBinding> {
        return ActivityShareBinding::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileSelector.setRoot(Environment.getExternalStorageDirectory())
        fileSelector.setSelectionMode(FileSelector.FILES_ONLY)
        fileSelector.setMultiSelectionEnabled(true)
        binding.btnSelectImage.setOnClickListener {
            fileSelector.setOnFileSelectListener { _, paths -> binding.tvImagePath.text = paths[0] }
            fileSelector.select(this, 0)
        }
        binding.btnSelectImages.setOnClickListener {
            fileSelector.setOnFileSelectListener { _, paths ->
                binding.tvImagePaths.text = ""
                paths.forEach { path ->
                    binding.tvImagePaths.append("$path\n")
                    imageFiles.add(File(path))
                }
            }
            fileSelector.select(this, 1)
        }
        binding.btnSelectFile.setOnClickListener {
            fileSelector.setOnFileSelectListener { _, paths -> binding.tvFilePath.text = paths[0] }
            fileSelector.select(this, 2)
        }
        binding.btnSelectVideo.setOnClickListener {
            fileSelector.setOnFileSelectListener { _, paths -> binding.tvVideoPath.text = paths[0] }
            fileSelector.select(this, 3)
        }
        binding.btnShareText.setOnClickListener {
            val text = binding.etText.text.toString().trim()
            if (text.isNotEmpty()) {
                SysShareUtils.shareText(this, "分享到", text)
            }
        }
        binding.btnShareImage.setOnClickListener {
            SysShareUtils.shareImage(this, "分享到", File(binding.tvImagePath.text.toString()))
        }
        binding.btnShareImages.setOnClickListener {
            SysShareUtils.shareImages(this, "分享到", imageFiles)
        }
        binding.btnShareVideo.setOnClickListener {
            SysShareUtils.shareVideo(this, "分享到", File(binding.tvVideoPath.text.toString()))
        }
        binding.btnShareFile.setOnClickListener {
            SysShareUtils.shareFile(this, "分享到", File(binding.tvFilePath.text.toString()))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileSelector.onActivityResult(requestCode, resultCode, data)
    }
}