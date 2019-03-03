package cn.zfs.commonsdemo

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.snail.commons.base.BaseHolder
import com.snail.commons.base.BaseListAdapter
import com.snail.commons.helper.WifiHelper
import com.snail.commons.utils.ToastUtils
import com.snail.commons.utils.UiUtils
import kotlinx.android.synthetic.main.activity_connect_wifi.*

/**
 * 描述:
 * 时间: 2018/10/25 20:59
 * 作者: zengfansheng
 */
class ConnectWifiActivity : BaseActivity() {
    private val scanResultList = ArrayList<ScanResult>()
    private val adapter = ListAdapter(this, scanResultList)
    private var helper: WifiHelper? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_wifi)
        title = "连接WiFi"
        lv.adapter = adapter
        helper = WifiHelper(this)
        progressDialog = ProgressDialog(this)
        refreshLayout.setOnRefreshListener {
            scanResultList.clear()
            adapter.notifyDataSetChanged()
            helper?.startScan(5000, object : WifiHelper.OnScanCallback {
                override fun onScanResult(scanResult: ScanResult) {
                    scanResultList.add(scanResult)
                    adapter.notifyDataSetChanged()
                }

                override fun onScanStop() {
                    refreshLayout.isRefreshing = false
                }
            })
        }
        lv.setOnItemClickListener { _, _, position, _ ->
            val result = scanResultList[position]
            val wificipher = WifiHelper.getWificipher(result)
            if (wificipher == WifiHelper.WIFICIPHER_NOPASS) {
                connect(WifiHelper.createWifiConfiguration(result.SSID, "", wificipher))
            } else {
                val et = EditText(this)
                et.layoutParams = ViewGroup.LayoutParams(-1, -2)
                AlertDialog.Builder(this).setView(et).setNegativeButton("取消", null)
                    .setPositiveButton("确定") { _, _ ->
                        connect(WifiHelper.createWifiConfiguration(result.SSID, et.text.toString().trim(), wificipher))
                    }.show()
            }
        }
    }
    
    private fun connect(cfg: WifiConfiguration) {
        progressDialog?.show()
        helper?.addNetwork(cfg, 10000, object : WifiHelper.OnConnectCallback {
            override fun onSuccess() {
                progressDialog?.dismiss()
                ToastUtils.showShort("连接成功")
            }

            override fun onFail() {
                progressDialog?.dismiss()
                ToastUtils.showShort("连接失败")
            }
        })
    }

    private inner class ListAdapter(context: Context, data: MutableList<ScanResult>) :
        BaseListAdapter<ScanResult>(context, data) {
        override fun getHolder(position: Int): BaseHolder<ScanResult> {
            return object : BaseHolder<ScanResult>() {
                private var tv: TextView? = null

                override fun setData(data: ScanResult, position: Int) {
                    tv!!.text = data.SSID
                }

                override fun createConvertView(): View {
                    val view = LinearLayout(context)
                    view.layoutParams = AbsListView.LayoutParams(-1, UiUtils.dp2px(50f).toInt())
                    view.gravity = Gravity.CENTER_VERTICAL
                    view.setPadding(UiUtils.dp2px(20f).toInt(), 0, 0, 0)
                    tv = TextView(context)
                    view.addView(tv)
                    return view
                }
            }
        }
    }
}