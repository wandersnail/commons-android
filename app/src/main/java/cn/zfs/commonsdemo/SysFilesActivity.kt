package cn.zfs.commonsdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.snail.commons.utils.FileUtils
import kotlinx.android.synthetic.main.activity_sys_files.*
import java.io.File

/**
 *
 *
 * date: 2019/3/2 23:09
 * author: zengfansheng
 */
class SysFilesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sys_files)
        btnFiles.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("*/*").addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, 200)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            Log.e("dd", "onActivityResult: ${data?.data}")
            Log.e("dd", "onActivityResult 转换后: ${FileUtils.getFileRealPath(this, data!!.data!!)}")
            Log.e("dd", "onActivityResult: ${File(FileUtils.getFileRealPath(this, data.data!!)).exists()}")
            tvPaths.text = "uri = ${data.data}\n"
            tvPaths.append("真实 = ${FileUtils.getFileRealPath(this, data.data!!)}\n")
            tvPaths.append("文件是否存在 = ${File(FileUtils.getFileRealPath(this, data.data!!)).exists()}\n")
        }
    }
}