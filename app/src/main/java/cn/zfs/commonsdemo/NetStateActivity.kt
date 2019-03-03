package cn.zfs.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.snail.commons.helper.WifiHelper
import com.snail.commons.utils.NetworkUtils
import com.snail.commons.utils.SystemUtils
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
        title = "网络及位置服务状态"
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
        tvNetAvailable.text = if (NetworkUtils.isNetworkAvailable(this)) "是" else "否"
        tvCurrentWifi.text = if (NetworkUtils.isCurrentNetworkWifi(this)) "是" else "否"
        tvLocationEnabled.text = if (SystemUtils.isLocationEnabled(this)) "是" else "否"
        tvGpsEnabled.text = if (SystemUtils.isGPSEnabled(this)) "是" else "否"
    }
}