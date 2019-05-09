package cn.zfs.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.snail.commons.entity.WifiHelper
import com.snail.commons.utils.isCurrentNetworkWifi
import com.snail.commons.utils.isGPSEnabled
import com.snail.commons.utils.isLocationEnabled
import com.snail.commons.utils.isNetworkAvailable
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
            if (helper.isWifiEnable) {
                startActivity(Intent(this, ConnectWifiActivity::class.java))
            } else {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
        }
        btnGoLocation.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        tvNetAvailable.text = if (isNetworkAvailable()) "是" else "否"
        tvCurrentWifi.text = if (isCurrentNetworkWifi()) "是" else "否"
        tvLocationEnabled.text = if (isLocationEnabled()) "是" else "否"
        tvGpsEnabled.text = if (isGPSEnabled()) "是" else "否"
    }
}