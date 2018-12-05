package cn.zfs.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import cn.zfs.fileselector.FileSelector
import com.zfs.commons.utils.EncryptUtils
import kotlinx.android.synthetic.main.activity_md5.*

/**
 * 描述:
 * 时间: 2018/8/29 20:40
 * 作者: zengfansheng
 */
class MD5Activity : BaseActivity() {
    private val fileSelector = FileSelector()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_md5)
        fileSelector.setRoot(Environment.getExternalStorageDirectory())
        fileSelector.setSelectFile(true)
        fileSelector.setFilenameFilter { _, name ->
            !name.startsWith(".")
        }
        fileSelector.setOnFileSelectListener {
            val md5 = EncryptUtils.getFileMD5Code(it[0])
            val sha1 = EncryptUtils.getFileSHA1Code(it[0])
            val separator = etSeparator.text.toString()
            tvMd5.text = if (separator.isEmpty()) md5 else EncryptUtils.addSeparator(md5, separator)
            tvSha1.text = if (separator.isEmpty()) sha1 else EncryptUtils.addSeparator(sha1, separator)
        }
        btnCalcFile.setOnClickListener {
            fileSelector.select(this)
        }
        btnCalc.setOnClickListener { 
            val text = et.text.toString()
            if (!text.isEmpty()) {
                val code = EncryptUtils.getMD5Code(text)
                val sha1 = EncryptUtils.getSHA1Code(text)
                val separator = etSeparator.text.toString()
                tvMd5.text = if (separator.isEmpty()) code else EncryptUtils.addSeparator(code, separator)
                tvSha1.text = if (separator.isEmpty()) sha1 else EncryptUtils.addSeparator(sha1, separator)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileSelector.onActivityResult(requestCode, resultCode, data)
    }
}