package cn.wandersnail.commonsdemo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import cn.wandersnail.commons.util.ToastUtils
import cn.wandersnail.commons.util.UiUtils
import cn.wandersnail.commonsdemo.databinding.ActivityToastUtilsBinding
import kotlin.concurrent.thread

/**
 *
 *
 * date: 2019/1/7 21:03
 * author: zengfansheng
 */
class ToastUtilsActivity : BaseViewBindingActivity<ActivityToastUtilsBinding>() {
    override fun getViewBindingClass(): Class<ActivityToastUtilsBinding> {
        return ActivityToastUtilsBinding::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnDefault.setOnClickListener {
            ToastUtils.reset()
            ToastUtils.showShort("这是一个正常的Toast")
        }

        binding.btnGravity.setOnClickListener {
            ToastUtils.reset()
            ToastUtils.setGravity(Gravity.TOP, UiUtils.dp2px(50f), UiUtils.dp2px(50f))
            ToastUtils.showShort("这是一个带位置及偏移Toast")
        }

        binding.btnMargin.setOnClickListener {
            ToastUtils.reset()
            ToastUtils.setMargin(0.2f, 0.1f)
            ToastUtils.showShort("带Margin的Toast")
        }

        binding.btnCustomView.setOnClickListener {
            ToastUtils.reset()
            val tv = TextView(this)
            tv.text = "自定义View的Toast"
            tv.setTextColor(Color.MAGENTA)
            tv.setBackgroundColor(0x66FF4499)
            tv.setPadding(30, 16, 30, 16)
            ToastUtils.setView(tv)
            ToastUtils.showShort()
        }
        binding.btnBackground.setOnClickListener {
            thread {
                ToastUtils.showShort("这是子线程的Toast")
            }
        }
        thread {
            ToastUtils.showShort("这是子线程的Toast")
        }
    }
}