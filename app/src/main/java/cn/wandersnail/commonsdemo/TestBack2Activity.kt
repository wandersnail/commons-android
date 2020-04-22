package cn.wandersnail.commonsdemo

import android.os.Bundle
import android.widget.TextView
import cn.wandersnail.commons.base.AppHolder

/**
 *
 *
 * date: 2020/4/22 18:42
 * author: zengfansheng
 */
class TestBack2Activity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "TestBack2Activity"
        setContentView(tv)
    }

    override fun onBackPressed() {
        AppHolder.getInstance().finishAllWithout(MainActivity::class.java.name)
    }
}