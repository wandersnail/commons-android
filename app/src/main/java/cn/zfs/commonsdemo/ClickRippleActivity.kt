package cn.zfs.commonsdemo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.snail.commons.utils.ImageUtils
import com.snail.commons.utils.UiUtils
import kotlinx.android.synthetic.main.activity_click_ripple.*

/**
 *
 *
 * date: 2018/12/30 10:57
 * author: zengfansheng
 */
class ClickRippleActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_click_ripple)        
        tvInner.background = createDrawable()
        btnOutSec.background = createDrawable()
        ivOutSrc.setImageDrawable(createDrawable())
        ivOutBg.background = createDrawable()
        tvOutUnclickable.background = createDrawable()
        tvInner.setOnClickListener {  }
        ivOutBg.setOnClickListener {  }
        ivOutSrc.setOnClickListener {  }
        view.setOnClickListener {  }
        ImageUtils.enableRipple(root, 0xff00574B.toInt(), true, true)
        UiUtils.setTextColor(root, Color.WHITE)
    }
    
    private fun createDrawable(): Drawable {
        val drawable = StateListDrawable()
        drawable.addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(0xff00BAA8.toInt()))
        drawable.addState(intArrayOf(), ColorDrawable(0xff008577.toInt()))
        return drawable
    }
}