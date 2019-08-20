package cn.zfs.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.snail.commons.util.EncryptUtils
import com.snail.commons.util.SignUtils
import com.snail.fileselector.FileSelector
import kotlinx.android.synthetic.main.activity_md5.*
import kotlin.concurrent.thread

/**
 * 描述:
 * 时间: 2018/8/29 20:40
 * 作者: zengfansheng
 */
class MD5Activity : BaseActivity() {
    private val fileSelector = FileSelector()
    private var selectType = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_md5)
        fileSelector.setRoot(Environment.getExternalStorageDirectory())
        fileSelector.setSelectionMode(FileSelector.FILES_ONLY)
        fileSelector.setOnFileSelectListener { paths ->
            if (selectType == 0) {
                val md5 = EncryptUtils.getFileMD5Code(paths[0])
                val sha1 = EncryptUtils.getFileSHA1Code(paths[0])
                val separator = etSeparator.text.toString()
                tvMd5.text = if (separator.isEmpty()) md5 else EncryptUtils.addSeparator(md5 ?: "", separator)
                tvSha1.text = if (separator.isEmpty()) sha1 else EncryptUtils.addSeparator(sha1 ?: "", separator)
            } else if (selectType == 1) {
                val signInfo = SignUtils.getSignatureFromApk(this@MD5Activity, paths[0])
                tvSignInfo.text = "hashCode: ${signInfo?.hashCode}\nmd5: ${signInfo?.md5}\nsha1: ${EncryptUtils.addSeparator(signInfo!!.sha1 ?: "", ":")}"
            }
        }
        btnCalcFile.setOnClickListener {
            selectType = 0
            fileSelector.select(this)
        }
        btnCalc.setOnClickListener { 
            val text = et.text.toString()
            if (text.isNotEmpty()) {
                val code = EncryptUtils.getMD5Code(text)
                val sha1 = EncryptUtils.getSHA1Code(text)
                val separator = etSeparator.text.toString()
                tvMd5.text = if (separator.isEmpty()) code else EncryptUtils.addSeparator(code ?: "", separator)
                tvSha1.text = if (separator.isEmpty()) sha1 else EncryptUtils.addSeparator(sha1 ?: "", separator)
            }
        }
        btnApk.setOnClickListener { 
            selectType = 1
            fileSelector.select(this)
        }
        btnApp.setOnClickListener {
            val signInfo = SignUtils.getSignatureInstalled(this)
            tvSignInfo.text = "hashCode: ${signInfo?.hashCode}\nmd5: ${signInfo?.md5}\nsha1: ${EncryptUtils.addSeparator(signInfo!!.sha1 ?: "", ":")}"
        }
        thread {
            App.instance?.observable?.notifyObservers("coming")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        btnCalc.postDelayed({
            fileSelector.onActivityResult(requestCode, resultCode, data)
        }, 200)
    }
}