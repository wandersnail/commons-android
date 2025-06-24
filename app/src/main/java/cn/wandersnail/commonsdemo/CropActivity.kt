package cn.wandersnail.commonsdemo

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import cn.wandersnail.commons.util.FileUtils
import cn.wandersnail.commons.util.ImageUtils
import cn.wandersnail.commons.util.ToastUtils
import cn.wandersnail.commonsdemo.databinding.ActivityCropBinding
import java.io.File

/**
 *
 *
 * date: 2019/11/29 14:22
 * author: zengfansheng
 */
class CropActivity : BaseViewBindingActivity<ActivityCropBinding>() {
    override fun getViewBindingClass(): Class<ActivityCropBinding> {
        return ActivityCropBinding::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var uri: Uri?
        var outPath: String? = null
        val cropLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                if (File(outPath!!).exists()) {
                    ToastUtils.showShort("剪裁完成")
                    binding.tvOutPath.visibility = View.VISIBLE
                }
            }
        }
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data?.data != null) {
                uri = it.data!!.data!!
                binding.tvPath.text = "源图片：${FileUtils.getFileRealPath(this, uri!!) ?: uri}"
                binding.btnCrop.visibility = View.VISIBLE
                val output = File(Environment.getExternalStorageDirectory(), "${Environment.DIRECTORY_PICTURES}/IMG_${System.currentTimeMillis()}.png")
                val cropImageIntent = ImageUtils.getCropImageIntent(
                    uri!!,
                    1,
                    1,
                    400,
                    400,
                    output,
                    Bitmap.CompressFormat.PNG
                )
                outPath = output.absolutePath
                binding.tvOutPath.text = "剪裁完成图片：${output.absolutePath}"
                cropLauncher.launch(cropImageIntent)
            }
        }
        binding.btn.setOnClickListener {
            if (!doSelect(launcher, Intent.ACTION_OPEN_DOCUMENT)) {
                if (!doSelect(launcher, Intent.ACTION_GET_CONTENT)) {
                    ToastUtils.showShort("当前系统缺少文件选择组件")
                }
            }
        }
    }

    private fun doSelect(launcher: ActivityResultLauncher<Intent>, action: String): Boolean {
        return try {
            //type和category都必须写，否则无法调起，还会抛异常
            val intent = Intent(action)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            launcher.launch(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}