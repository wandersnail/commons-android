package cn.wandersnail.commonsdemo

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.wandersnail.commons.helper.PermissionsRequester
import cn.wandersnail.commons.observer.Observe
import cn.wandersnail.commons.poster.PosterDispatcher
import cn.wandersnail.commons.poster.ThreadMode
import cn.wandersnail.commons.util.Logger
import cn.wandersnail.commons.util.ToastUtils
import com.snail.widget.listview.BaseListAdapter
import com.snail.widget.listview.BaseViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), MyObserver {
    private var requester: PermissionsRequester? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = arrayListOf(
            "储存信息获取", "md5和sha1算法", "系统分享", "网络及位置服务状态", "解压缩", "点击波纹", "Toast", "系统文件选择器", "debug包判断",
            "系统下载并安装APP", "文件操作"
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
            FileOperateActivity::class.java
        )
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
        requester!!.setCallback {
            if (it.isNotEmpty()) {
                ToastUtils.showShort("部分权限被拒绝，可能造成某些功能无法使用")
            }
        }
        requester!!.checkAndRequest(list)
        thread {
            try {
                val method = javaClass.getMethod("test")
                PosterDispatcher(Executors.newCachedThreadPool(), ThreadMode.POSTING).post(method, MyRunOn::class.java) {
                    method.invoke(this@MainActivity)
                }
            } catch (e: Exception) {
            }
        }
        App.instance?.observable?.registerObserver(this)
    }

    override fun onDestroy() {
        App.instance?.observable?.unregisterObserver(this)
        super.onDestroy()
    }

    @Observe
    override fun onChanged(o: Any?) {
        ToastUtils.showShort("$o, 主线程: ${Looper.getMainLooper() == Looper.myLooper()}")
    }

    @Observe(ThreadMode.MAIN)
    override fun coming() {
        ToastUtils.showShort("coming, 主线程: ${Looper.getMainLooper() == Looper.myLooper()}")
    }

    @MyRunOn(ThreadMode.MAIN)
    fun test() {
        ToastUtils.showShort("主线程: ${Looper.getMainLooper() == Looper.myLooper()}")
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
