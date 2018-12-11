package cn.zfs.commonsdemo

import android.app.AlertDialog
import android.content.Intent
import android.net.wifi.ScanResult
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import com.snail.commons.entity.WifiHelper
import com.snail.commons.utils.NetworkUtils
import com.snail.commons.utils.SystemUtils
import kotlinx.android.synthetic.main.activity_static_ip.*
import java.net.InetAddress

/**
 * 描述:
 * 时间: 2018/6/12 12:51
 * 作者: zengfansheng
 */
class StaticIpActivity : BaseActivity(), WifiHelper.OnScanCallback {
    private val ssidList = ArrayList<String>()
    private var adapter: ArrayAdapter<String>? = null
    private var wifiHelper: WifiHelper? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_static_ip)   
        title = "设置静态IP"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            wifiHelper = WifiHelper(this)
            initViews()
        } else {
            finish()
        }
    }

    
    private fun initViews() {
        refreshLayout.setOnRefreshListener {
            ssidList.clear()
            adapter?.notifyDataSetChanged()
            wifiHelper?.startScan(5000, this)
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ssidList)
        lv.adapter = adapter
        lv.setOnItemClickListener { _, _, position, _ -> 
            wifiHelper?.clearCurrentNetwork()
            val wifiCfg = WifiHelper.createWifiConfiguration(ssidList[position], "12345678", WifiHelper.WIFICIPHER_WPA2)
            wifiHelper!!.addNetwork(wifiCfg, 10000, null)
            NetworkUtils.setStaticIpConfiguration(wifiHelper!!.wifiManager, wifiCfg, InetAddress.getByName("10.5.5.9"), 24,
                    InetAddress.getByName("10.5.5.1"), InetAddress.getAllByName("8.8.8.8"))
        }
        if (!SystemUtils.isLocationEnabled(this)) {
            AlertDialog.Builder(this).setMessage("需要打开位置服务").setPositiveButton("OK") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }.show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (SystemUtils.isLocationEnabled(this)) {
            wifiHelper?.startScan(5000, this)
            refreshLayout.isRefreshing = true
        }
    }

    override fun onScanResult(scanResult: ScanResult) {
        runOnUiThread {
            ssidList.add(scanResult.SSID)
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onScanStop() {
        runOnUiThread {
            refreshLayout.isRefreshing = false
        }
    }
    
    override fun onDestroy() {
        wifiHelper?.clearCurrentNetwork()
        super.onDestroy()
    }
}