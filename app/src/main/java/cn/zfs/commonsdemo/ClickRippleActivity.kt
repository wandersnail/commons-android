package cn.zfs.commonsdemo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.snail.commons.helper.GradientDrawableBuilder
import com.snail.commons.helper.SolidDrawableBuilder
import com.snail.commons.util.ImageUtils
import com.snail.commons.util.UiUtils
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
        val builder = GradientDrawableBuilder()
        builder.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT)
        builder.setNormalColors(intArrayOf(0xff00AFEC.toInt(), 0xff00AFEC.toInt(), 0xff00D8F7.toInt()))
        
        val builder1 = SolidDrawableBuilder()
        builder1.setNormalColor(0xff00AFEC.toInt())
        builder1.round(UiUtils.dp2pxF(8f))
        builder1.round(UiUtils.dp2pxF(20f), UiUtils.dp2pxF(12f), UiUtils.dp2pxF(4f))
        builder1.roundLeftTop(UiUtils.dp2pxF(12f), UiUtils.dp2pxF(20f))
        btnOutSec.background = builder1.build()
        ivOutSrc.setImageDrawable(createDrawable())
        ivOutBg.background = createDrawable()
        tvOutUnclickable.background = builder.build()
        tvInner.setOnClickListener {  }
        ivOutBg.setOnClickListener {  }
        ivOutSrc.setOnClickListener {  }
        view.setOnClickListener {  }
        ImageUtils.enableRipple(root, 0xff00574B.toInt(), false, true)
        UiUtils.setTextColor(root, Color.WHITE)
    }
    
    private fun createDrawable(): Drawable {
        val drawable = StateListDrawable()
        drawable.addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(0xff00BAA8.toInt()))
        drawable.addState(intArrayOf(), ColorDrawable(0xff008577.toInt()))
        return drawable
    }
}