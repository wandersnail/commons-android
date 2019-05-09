package com.snail.commons.entity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.snail.commons.utils.getFileRealPath

/**
 * 调用系统文件管理选择文件
 *
 * date: 2019/3/3 12:10
 * author: zengfansheng
 */
class SysFileChooser {
    var allowMultiple = false
    var localOnly = true
    
    private fun generateIntent(mimeTyps: Array<String>, title: String): Intent {
        return Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT).apply {
            type = if (mimeTyps.isEmpty() || mimeTyps.size > 1) MIME_TYPE_ALL else mimeTyps[0]
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
            putExtra(Intent.EXTRA_LOCAL_ONLY, localOnly)
            if (mimeTyps.size > 1) {
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTyps)
            }
        }, title)
    }
    
    @JvmOverloads
    fun choose(activity: Activity, mimeTyps: Array<String>, title: String = "") {
        activity.startActivityForResult(generateIntent(mimeTyps, title),
            REQUEST_CODE
        )
    }

    @JvmOverloads
    fun choose(fragment: Fragment, mimeTyps: Array<String>, title: String = "") {
        fragment.startActivityForResult(generateIntent(mimeTyps, title),
            REQUEST_CODE
        )
    }

    /**
     * 从选择结果中获取文件的真实路径
     */
    fun getRealPashsFromResultData(context: Context, requestCode: Int, resultCode: Int, data: Intent?): List<String> {
        val paths = ArrayList<String>()
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val clipData = data.clipData
            if (clipData != null) {
                val count = clipData.itemCount
                for (i in 0 until count) {
                    val item = clipData.getItemAt(i)                    
                    val path = context.getFileRealPath(item.uri)
                    if (path != null) {
                        paths.add(path)
                    }
                }
            } else if (data.data != null) {
                val path = context.getFileRealPath(data.data!!)
                if (path != null) {
                    paths.add(path)
                }
            }
        }
        return paths
    }
    
    companion object {
        private const val REQUEST_CODE = 13342
        
        const val MIME_TYPE_AUDIO = "audio/*"
        const val MIME_TYPE_APPLICATION = "application/*"
        const val MIME_TYPE_IMAGE = "image/*"
        const val MIME_TYPE_VIDEO = "video/*"
        const val MIME_TYPE_TEXT = "text/*"
        const val MIME_TYPE_ALL = "*/*"
    }
}