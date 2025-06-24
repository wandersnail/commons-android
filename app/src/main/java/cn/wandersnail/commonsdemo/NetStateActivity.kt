package cn.wandersnail.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import cn.wandersnail.commons.helper.WifiHelper
import cn.wandersnail.commons.util.NetworkUtils
import cn.wandersnail.commons.util.SystemUtils
import cn.wandersnail.commonsdemo.databinding.ActivityNetStateBinding

/**
 * 描述:
 * 时间: 2018/10/24 22:34
 * 作者: zengfansheng
 */
class NetStateActivity : BaseViewBindingActivity<ActivityNetStateBinding>() {
    override fun getViewBindingClass(): Class<ActivityNetStateBinding> {
        return ActivityNetStateBinding::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val helper = WifiHelper(this)
        binding.btnGoConnect.setOnClickListener {
            if (helper.isWifiEnabled) {
                startActivity(Intent(this, ConnectWifiActivity::class.java))
            } else {
                WifiHelper(this).navigationToWifiSettings()
            }
        }
        binding.btnGoLocation.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        Log.i("NetStateActivity", "ip: " + helper.currentIpAddress + ", gateway:" + helper.ipAddressFromHotspot + 
                ", serverAddress: " + helper.serverIpAddress)
    }

    override fun onResume() {
        super.onResume()
        binding.tvNetAvailable.text = if (NetworkUtils.isNetworkAvailable(this)) "是" else "否"
        binding.tvCurrentWifi.text = if (NetworkUtils.isCurrentNetworkWifi(this)) "是" else "否"
        binding.tvLocationEnabled.text = if (SystemUtils.isLocationEnabled(this)) "是" else "否"
        binding.tvGpsEnabled.text = if (SystemUtils.isGPSEnabled(this)) "是" else "否"
    }
}