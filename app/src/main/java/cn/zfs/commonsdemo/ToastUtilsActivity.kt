package cn.zfs.commonsdemo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import com.snail.commons.utils.ToastUtils
import com.snail.commons.utils.UiUtils
import kotlinx.android.synthetic.main.activity_toast_utils.*

/**
 *
 *
 * date: 2019/1/7 21:03
 * author: zengfansheng
 */
class ToastUtilsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toast_utils)
        btnDefault.setOnClickListener { 
            ToastUtils.reset()
            ToastUtils.showShort("这是一个正常的Toast")
        }

        btnGravity.setOnClickListener {
            ToastUtils.reset()
            ToastUtils.setGravity(Gravity.TOP, UiUtils.dp2px(50f).toInt(), UiUtils.dp2px(50f).toInt())
            ToastUtils.showShort("这是一个带位置及偏移Toast")
        }

        btnMargin.setOnClickListener {
            ToastUtils.reset()
            ToastUtils.setMargin(0.2f, 0.1f)
            ToastUtils.showShort("带Margin的Toast")
        }
        
        btnCustomView.setOnClickListener {
            ToastUtils.reset()
            val tv = TextView(this)
            tv.text = "自定义View的Toast"
            tv.setTextColor(Color.MAGENTA)
            tv.setBackgroundColor(0x66FF4499)
            tv.setPadding(30, 16, 30, 16)
            ToastUtils.setView(tv)
            ToastUtils.showShort()
        }
    }
}