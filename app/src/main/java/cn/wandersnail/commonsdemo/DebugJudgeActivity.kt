package cn.wandersnail.commonsdemo

import android.content.Intent
import android.os.Bundle
import cn.wandersnail.commons.util.SystemUtils
import cn.wandersnail.fileselector.FileSelector
import kotlinx.android.synthetic.main.activity_debug_judge.*
import java.io.File

/**
 * 判断是否是debug包
 *
 * date: 2019/4/27 10:53
 * author: zengfansheng
 */
class DebugJudgeActivity : BaseActivity() {
    private var fileSelector = FileSelector()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug_judge)
        fileSelector.setFilenameFilter { dir, name ->
            File(dir, name).isDirectory || name.endsWith(".apk", true)
        }
        fileSelector.setMultiSelectionEnabled(false)
        fileSelector.setSelectionMode(FileSelector.FILES_ONLY)        
        fileSelector.setOnFileSelectListener { _, paths -> tvApkState.text = if (SystemUtils.isDebugApk(this@DebugJudgeActivity, paths[0])) "选择的apk是debug包" else "选择的apk是release包" }
        tvAppState.text = if (SystemUtils.isRunInDebug(this)) "当前app是debug包" else "当前app是release包"
        btnSelect.setOnClickListener { fileSelector.select(this, 0) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileSelector.onActivityResult(requestCode, resultCode, data)
    }
}