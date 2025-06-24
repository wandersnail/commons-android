package cn.wandersnail.commonsdemo

import android.os.Bundle
import cn.wandersnail.commons.util.Logger
import cn.wandersnail.commons.util.NetworkUtils
import cn.wandersnail.commons.util.SystemUtils
import cn.wandersnail.commonsdemo.databinding.ActivityDeviceInfoBinding
import com.bun.miitmdid.core.MdidSdkHelper

/**
 *
 *
 * date: 2021/9/24 14:02
 * author: zengfansheng
 */
class DeviceInfoActivity : BaseViewBindingActivity<ActivityDeviceInfoBinding>() {
    override fun getViewBindingClass(): Class<ActivityDeviceInfoBinding> {
        return ActivityDeviceInfoBinding::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvMac.text = NetworkUtils.getMacAddress(this)
        try {
            MdidSdkHelper.InitSdk(applicationContext, true) { _, supplier ->
                Logger.d("DeviceInfoActivity", "oaid = ${supplier?.oaid}，aaid = ${supplier?.aaid}，vaid = ${supplier?.vaid}")
                binding.tvOaid.text = supplier?.oaid
            }
        } catch (e: Exception) {
            Logger.e("DeviceInfoActivity", "oaid获取失败：${e.message ?: e.javaClass.name}")
        }
        binding.tvImei.text = SystemUtils.getImei(this)
    }
}