package cn.zfs.commonsdemo

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.snail.commons.helper.PermissionsRequester
import com.snail.commons.utils.Logger
import com.snail.commons.utils.ToastUtils
import com.snail.widget.listview.BaseListAdapter
import com.snail.widget.listview.BaseViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var requester: PermissionsRequester? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = arrayListOf("储存信息获取", "md5和sha1算法", "系统分享", "网络及位置服务状态", "解压缩", "点击波纹", "Toast", "文件操作", "debug包判断",
            "系统下载并安装APP")
        val clsArr = arrayListOf(StorageActivity::class.java, MD5Activity::class.java, ShareActivity::class.java, NetStateActivity::class.java, 
                ZipActivity::class.java, ClickRippleActivity::class.java, ToastUtilsActivity::class.java, FilesOprateActivity::class.java,
                DebugJudgeActivity::class.java, ApkDownloadActivity::class.java)
        lv.adapter = object : BaseListAdapter<String>(this, data) {
            override fun createViewHolder(position: Int): BaseViewHolder<String> {
                return object : BaseViewHolder<String>() {
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
            val intent = Intent(this, clsArr[position])
            intent.putExtra("title", data[position])
            startActivity(intent)
        }
        Logger.setPrintLevel(Logger.ALL)
        requester = PermissionsRequester(this)
        val list = ArrayList<String>()
        list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        list.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        list.add(Manifest.permission.ACCESS_FINE_LOCATION)
        list.add(Manifest.permission.ACCESS_NETWORK_STATE)
        requester?.checkAndRequest(list)
        requester?.setOnRequestResultListener(object : PermissionsRequester.OnRequestResultListener {
            override fun onRequestResult(refusedPermissions: MutableList<String>) {
                if (refusedPermissions.isNotEmpty()) {
                    ToastUtils.showShort("部分权限被拒绝，可能造成某些功能无法使用")
                }
            }
        })
        
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        requester?.onActivityResult(requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requester?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
