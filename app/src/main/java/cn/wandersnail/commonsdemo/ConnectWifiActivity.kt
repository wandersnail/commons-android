package cn.wandersnail.commonsdemo

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
import cn.wandersnail.commons.helper.WifiHelper
import cn.wandersnail.commons.util.ToastUtils
import cn.wandersnail.commons.util.UiUtils
import cn.wandersnail.commonsdemo.databinding.ActivityConnectWifiBinding
import cn.wandersnail.widget.listview.BaseListAdapter
import cn.wandersnail.widget.listview.BaseViewHolder

/**
 * 描述:
 * 时间: 2018/10/25 20:59
 * 作者: zengfansheng
 */
class ConnectWifiActivity : BaseViewBindingActivity<ActivityConnectWifiBinding>() {
    private val scanResultList = ArrayList<ScanResult>()
    private var helper: WifiHelper? = null
    private var progressDialog: ProgressDialog? = null

    override fun getViewBindingClass(): Class<ActivityConnectWifiBinding> {
        return ActivityConnectWifiBinding::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "连接WiFi"
        val adapter = ListAdapter(this, scanResultList)
        binding.lv.adapter = adapter
        helper = WifiHelper(this)
        progressDialog = ProgressDialog(this)
        binding.refreshLayout.setOnRefreshListener {
            startScan(adapter)
        }
        binding.lv.setOnItemClickListener { _, _, position, _ ->
            val result = scanResultList[position]
            val wificipher = WifiHelper.getWifiCipher(result)
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
        binding.refreshLayout.isRefreshing = true
        startScan(adapter)        
    }

    private fun startScan(adapter: ListAdapter) {
        scanResultList.clear()
        adapter.notifyDataSetChanged()
        helper?.startScan(10000) { scanResults ->
            binding.refreshLayout.isRefreshing = false
            scanResultList.clear()
            scanResultList.addAll(scanResults)
            adapter.notifyDataSetChanged()
        }
    }

    private fun connect(cfg: WifiConfiguration) {
        progressDialog?.show()
        helper?.addNetwork(cfg, 10000, object : WifiHelper.ConnectCallback {
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
        override fun createViewHolder(position: Int): BaseViewHolder<ScanResult> {
            return object : BaseViewHolder<ScanResult> {
                private var tv: TextView? = null

                override fun onBind(item: ScanResult, position: Int) {
                    tv!!.text = item.SSID
                }

                override fun createView(): View {
                    val view = LinearLayout(context)
                    view.layoutParams = AbsListView.LayoutParams(-1, UiUtils.dp2px(50f))
                    view.gravity = Gravity.CENTER_VERTICAL
                    view.setPadding(UiUtils.dp2px(20f), 0, 0, 0)
                    tv = TextView(context)
                    view.addView(tv)
                    return view
                }
            }
        }
    }
}