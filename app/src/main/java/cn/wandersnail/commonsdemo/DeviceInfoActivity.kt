package cn.wandersnail.commonsdemo

import android.os.Bundle
import cn.wandersnail.commons.util.Logger
import cn.wandersnail.commons.util.NetworkUtils
import cn.wandersnail.commons.util.SystemUtils
import com.bun.miitmdid.core.MdidSdkHelper
import kotlinx.android.synthetic.main.activity_device_info.*

/**
 *
 *
 * date: 2021/9/24 14:02
 * author: zengfansheng
 */
class DeviceInfoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_info)
        tvMac.text = NetworkUtils.getMacAddress(this)
        try {
            MdidSdkHelper.InitSdk(applicationContext, true) { _, supplier ->
                Logger.d("DeviceInfoActivity", "oaid = ${supplier?.oaid}，aaid = ${supplier?.aaid}，vaid = ${supplier?.vaid}")
                tvOaid.text = supplier?.oaid
            }
        } catch (e: Exception) {
            Logger.e("DeviceInfoActivity", "oaid获取失败：${e.message ?: e.javaClass.name}")
        }        
        tvImei.text = SystemUtils.getImei(this)
    }
}