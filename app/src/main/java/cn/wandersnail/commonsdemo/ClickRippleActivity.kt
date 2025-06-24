package cn.wandersnail.commonsdemo

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import cn.wandersnail.commons.helper.GradientDrawableBuilder
import cn.wandersnail.commons.helper.SolidDrawableBuilder
import cn.wandersnail.commons.util.ImageUtils
import cn.wandersnail.commons.util.UiUtils
import cn.wandersnail.commonsdemo.databinding.ActivityClickRippleBinding

/**
 *
 *
 * date: 2018/12/30 10:57
 * author: zengfansheng
 */
class ClickRippleActivity : BaseViewBindingActivity<ActivityClickRippleBinding>() {
    override fun getViewBindingClass(): Class<ActivityClickRippleBinding> {
        return ActivityClickRippleBinding::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvInner.background = createDrawable()
        binding.btnOutSec.background = createDrawable()
        val builder = GradientDrawableBuilder()
        builder.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT)
        builder.setNormalColors(intArrayOf(0xff00AFEC.toInt(), 0xff00AFEC.toInt(), 0xff00D8F7.toInt()))
        
        val builder1 = SolidDrawableBuilder()
        builder1.setNormalColor(0xff00AFEC.toInt())
        builder1.round(UiUtils.dp2pxF(8f))
        builder1.round(UiUtils.dp2pxF(20f), UiUtils.dp2pxF(12f), UiUtils.dp2pxF(4f))
        builder1.roundLeftTop(UiUtils.dp2pxF(12f), UiUtils.dp2pxF(20f))
        binding.btnOutSec.background = builder1.build()
        binding.ivOutSrc.setImageDrawable(createDrawable())
        binding.ivOutBg.background = createDrawable()
        binding.tvOutUnclickable.background = builder.build()
        binding.tvInner.setOnClickListener {  }
        binding.ivOutBg.setOnClickListener {  }
        binding.ivOutSrc.setOnClickListener {  }
        binding.view.setOnClickListener {  }
        ImageUtils.enableRipple(binding.root, 0xff00574B.toInt(), false, true)
        UiUtils.setTextColor(binding.root, Color.WHITE)
    }
    
    private fun createDrawable(): Drawable {
        val drawable = StateListDrawable()
        drawable.addState(intArrayOf(android.R.attr.state_pressed), 0xff00BAA8.toInt().toDrawable())
        drawable.addState(intArrayOf(), 0xff008577.toInt().toDrawable())
        return drawable
    }
}