package cn.wandersnail.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.TextView
import cn.wandersnail.commons.util.EncryptUtils
import cn.wandersnail.commons.util.SignUtils
import cn.wandersnail.commons.util.SignUtils.SignInfo
import cn.wandersnail.commonsdemo.databinding.ActivityMd5Binding
import cn.wandersnail.fileselector.FileSelector
import cn.wandersnail.widget.listview.BaseListAdapter
import cn.wandersnail.widget.listview.BaseViewHolder

/**
 * 描述:
 * 时间: 2018/8/29 20:40
 * 作者: zengfansheng
 */
class MD5Activity : BaseViewBindingActivity<ActivityMd5Binding>() {
    private val fileSelector = FileSelector()
    private var selectType = -1

    override fun getViewBindingClass(): Class<ActivityMd5Binding> {
        return ActivityMd5Binding::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileSelector.setRoot(Environment.getExternalStorageDirectory())
        fileSelector.setSelectionMode(FileSelector.FILES_ONLY)
        fileSelector.setOnFileSelectListener { _, paths ->
            if (selectType == 0) {
                val md5 = EncryptUtils.getFileMD5Code(paths[0])
                val sha1 = EncryptUtils.getFileSHA1Code(paths[0])
                val separator = binding.etSeparator.text.toString()
                binding.tvMd5.text = if (separator.isEmpty()) md5 else EncryptUtils.addSeparator(md5 ?: "", separator)
                binding.tvSha1.text = if (separator.isEmpty()) sha1 else EncryptUtils.addSeparator(sha1 ?: "", separator)
            } else if (selectType == 1) {
                val signInfo = SignUtils.getSignatureFromApk(this@MD5Activity, paths[0])
                binding.tvSignInfo.text = "hashCode: ${signInfo?.hashCode}\nmd5: ${signInfo?.md5}\nsha1: ${EncryptUtils.addSeparator(signInfo!!.sha1 ?: "", ":")}"
            }
        }
        binding.btnCalcFile.setOnClickListener {
            selectType = 0
            fileSelector.select(this, 0)
        }
        binding.btnCalc.setOnClickListener {
            val text = binding.et.text.toString()
            if (text.isNotEmpty()) {
                val code = EncryptUtils.getMD5Code(text)
                val sha1 = EncryptUtils.getSHA1Code(text)
                val separator = binding.etSeparator.text.toString()
                binding.tvMd5.text = if (separator.isEmpty()) code else EncryptUtils.addSeparator(code ?: "", separator)
                binding.tvSha1.text = if (separator.isEmpty()) sha1 else EncryptUtils.addSeparator(sha1 ?: "", separator)
            }
        }
        val adapter: BaseListAdapter<SignInfo> = object : BaseListAdapter<SignInfo>(this) {
            override fun createViewHolder(position: Int): BaseViewHolder<SignInfo> {
                return object : BaseViewHolder<SignInfo> {
                    private var tvPkgName: TextView? = null
                    private var tvSign: TextView? = null

                    override fun onBind(item: SignInfo, position: Int) {
                        tvPkgName?.text = item.pkgName
                        tvSign?.text = item.md5
                    }

                    override fun createView(): View {
                        val view = View.inflate(this@MD5Activity, R.layout.apk_sign_item, null)
                        tvPkgName = view.findViewById(R.id.tvPkgName)
                        tvSign = view.findViewById(R.id.tvSign)
                        return view
                    }
                }
            }
        }
        binding.btnApk.setOnClickListener {
            adapter.refresh(emptyList())
            selectType = 1
            fileSelector.select(this, 2)
        }
        binding.btnApp.setOnClickListener {
            adapter.refresh(emptyList())
            val signInfo = SignUtils.getSignatureInstalled(this)
            binding.tvSignInfo.text = "hashCode: ${signInfo?.hashCode}\nmd5: ${signInfo?.md5}\nsha1: ${EncryptUtils.addSeparator(signInfo!!.sha1 ?: "", ":")}"
        }
        binding.lv.adapter = adapter
        binding.btnAll.setOnClickListener {
            binding.tvSignInfo.text = ""
            adapter.refresh(emptyList())
            val list = SignUtils.getInstalledApkSignature(this)
            adapter.refresh(list)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.btnCalc.postDelayed({
            fileSelector.onActivityResult(requestCode, resultCode, data)
        }, 200)
    }
}