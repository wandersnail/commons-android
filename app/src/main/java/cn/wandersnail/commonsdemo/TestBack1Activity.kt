package cn.wandersnail.commonsdemo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import cn.wandersnail.commons.util.UiUtils

/**
 *
 *
 * date: 2020/4/22 18:42
 * author: zengfansheng
 */
class TestBack1Activity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "TestBack1Activity"
        tv.setOnClickListener { 
            startActivity(Intent(this, TestBack2Activity::class.java))
        }
        tv.setPadding(UiUtils.dp2px(10f), UiUtils.dp2px(10f), UiUtils.dp2px(10f), UiUtils.dp2px(10f))
        setContentView(tv)
    }
}