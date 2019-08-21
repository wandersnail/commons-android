package cn.wandersnail.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import cn.wandersnail.commons.helper.WifiHelper
import cn.wandersnail.commons.util.NetworkUtils
import cn.wandersnail.commons.util.SystemUtils
import kotlinx.android.synthetic.main.activity_net_state.*

/**
 * 描述:
 * 时间: 2018/10/24 22:34
 * 作者: zengfansheng
 */
class NetStateActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net_state)
        val helper = WifiHelper(this)
        btnGoConnect.setOnClickListener { 
            if (helper.isWifiEnabled) {
                startActivity(Intent(this, ConnectWifiActivity::class.java))
            } else {
                WifiHelper(this).navigationToWifiSettings()
            }
        }
        btnGoLocation.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        Log.i("NetStateActivity", "ip: " + helper.currentIpAddress + ", gateway:" + helper.ipAddressFromHotspot + 
                ", serverAddress: " + helper.serverIpAddress)
    }

    override fun onResume() {
        super.onResume()
        tvNetAvailable.text = if (NetworkUtils.isNetworkAvailable(this)) "是" else "否"
        tvCurrentWifi.text = if (NetworkUtils.isCurrentNetworkWifi(this)) "是" else "否"
        tvLocationEnabled.text = if (SystemUtils.isLocationEnabled(this)) "是" else "否"
        tvGpsEnabled.text = if (SystemUtils.isGPSEnabled(this)) "是" else "否"
    }
}