package cn.wandersnail.commonsdemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import cn.wandersnail.commons.helper.PermissionsRequester
import cn.wandersnail.commons.poster.Tag
import cn.wandersnail.commons.util.Logger
import cn.wandersnail.commons.util.ToastUtils
import cn.wandersnail.widget.listview.BaseListAdapter
import cn.wandersnail.widget.listview.BaseViewHolder
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), TestObserver {
    private var requester: PermissionsRequester? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = arrayListOf(
            "储存信息获取", "md5和sha1算法", "系统分享", "网络及位置服务状态", "解压缩", "点击波纹", "Toast", "系统文件选择器", "debug包判断",
            "系统下载并安装APP", "文件操作", "观察者模式", "测试返回到首页"
        )
        val clsArr = arrayListOf(
            StorageActivity::class.java,
            MD5Activity::class.java,
            ShareActivity::class.java,
            NetStateActivity::class.java,
            ZipActivity::class.java,
            ClickRippleActivity::class.java,
            ToastUtilsActivity::class.java,
            SysFileChooserActivity::class.java,
            DebugJudgeActivity::class.java,
            ApkDownloadActivity::class.java,
            FileOperateActivity::class.java,
            TestObserverActivity::class.java,
            TestBack1Activity::class.java
        )
        lv.adapter = object : BaseListAdapter<String>(this, data) {
            override fun createViewHolder(position: Int): BaseViewHolder<String> {
                return object : BaseViewHolder<String> {
                    private var tv: TextView? = null

                    override fun onBind(item: String, position: Int) {
                        tv?.text = item
                    }

                    override fun createView(): View {
                        val view = View.inflate(context, android.R.layout.simple_list_item_1, null)
                        tv = view.findViewById(android.R.id.text1)
                        return view
                    }
                }
            }
        }
        lv.setOnItemClickListener { _, _, position, _ ->
            if (data[position] == "崩溃测试") {
                throw RuntimeException("This is a RuntimeException test")
            } else {
                val intent = Intent(this, clsArr[position])
                intent.putExtra("title", data[position])
                startActivity(intent)
            }
        }
        Logger.setPrintLevel(Logger.ALL)
        requester = PermissionsRequester(this)
        val list = ArrayList<String>()
        list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        list.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        list.add(Manifest.permission.ACCESS_FINE_LOCATION)
        list.add(Manifest.permission.ACCESS_NETWORK_STATE)
        requester!!.setCallback {
            if (it.isNotEmpty()) {
                ToastUtils.showShort("部分权限被拒绝，可能造成某些功能无法使用")
            } else {
//                val uriString = MMKV.defaultMMKV().decodeString("uri")
//                if (uriString == null) {
//                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//                    startActivityForResult(intent, 1101)
//                }
            }
        }
        requester!!.checkAndRequest(list)
        App.instance?.observable?.registerObserver(this)
    }

    override fun onDestroy() {
        App.instance?.observable?.unregisterObserver(this)
        super.onDestroy()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        requester?.onActivityResult(requestCode)
        if (requestCode == 1101 && resultCode == Activity.RESULT_OK && data?.data != null) {
            //授予打开的文档树永久性的读写权限
            val takeFlags =
                intent.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            val treeUri = data.data!!
            MMKV.defaultMMKV().encode("uri", treeUri.toString())
            contentResolver.takePersistableUriPermission(data.data!!, takeFlags)
            //使用DocumentFile构建一个根文档，之后的操作可以在该文档上进行
            val file = DocumentFile.fromTreeUri(this, treeUri)!!
            file.createDirectory("logs")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requester?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    
    @Tag("test")
    override fun test(i: Int, f: Float, d: Double, b: Byte, b1: Boolean, c: Char, l: Long, s: Short) {
        ToastUtils.showShort("$i, $f, $d, $b, $b1, $c, $l, $s")
    }
}
