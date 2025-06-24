package cn.wandersnail.commonsdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cn.wandersnail.commons.helper.SysFileChooser
import cn.wandersnail.commonsdemo.databinding.ActivitySysFilesBinding

/**
 *
 *
 * date: 2019/3/2 23:09
 * author: zengfansheng
 */
class SysFileChooserActivity : BaseViewBindingActivity<ActivitySysFilesBinding>() {
    private val fileChooser = SysFileChooser()

    override fun getViewBindingClass(): Class<ActivitySysFilesBinding> {
        return ActivitySysFilesBinding::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnFiles.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                type = "video/*"
//                addCategory(Intent.CATEGORY_OPENABLE)
//                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, chkMulti.isChecked)
//                putExtra(Intent.EXTRA_LOCAL_ONLY, true)
//                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/*"))
//            }
//            startActivityForResult(Intent.createChooser(intent, "选择文件"), 200)
            val options = SysFileChooser.Options()
            options.allowMultiple = binding.chkMulti.isChecked
            options.mimeTypes = arrayOf(SysFileChooser.MIME_TYPE_VIDEO, SysFileChooser.MIME_TYPE_AUDIO)
            fileChooser.choose(this, options)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = fileChooser.getRealPathsFromResultData(this, requestCode, resultCode, data)
        binding.tvPaths.text = ""
        list.forEach {
            binding.tvPaths.append("$it\n")
        }
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
//            val clipData = items!!.clipData
//            Log.e("onActivityResult", "$clipData")
//            if (clipData != null) {
//                val count = clipData.itemCount
//                for (i in 0 until count) {
//                    val item = clipData.getItemAt(i)
//                    Log.e("onActivityResult", FileUtils.getFileRealPath(this, item.uri))
//                }
//            } else if (items.items != null) {
//                Log.e("onActivityResult", FileUtils.getFileRealPath(this, items.items!!))
//            }
//            tvPaths.text = "uri = ${items!!.items}\n"
//            tvPaths.append("真实 = ${FileUtils.getFileRealPath(this, items.items!!)}\n")
//            tvPaths.append("文件是否存在 = ${File(FileUtils.getFileRealPath(this, items.items!!)).exists()}\n")
        }
    }
}