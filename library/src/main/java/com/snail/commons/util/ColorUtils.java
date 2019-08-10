package com.snail.commons.util;

import android.content.res.ColorStateList;
import android.graphics.Color;

/**
 * date: 2019/8/6 23:14
 * author: zengfansheng
 */
public class ColorUtils {
    /**
     * 取过渡色
     *
     * @param offset 取值范围：0 ~ 1
     */
    public static int getColor(int startColor, int endColor, float offset) {
        int aa = (startColor >> 24) & 0xff;
        int ra = (startColor >> 16) & 0xff;
        int ga = (startColor >> 8) & 0xff;
        int ba = startColor & 0xff;
        int ab = (endColor >> 24) & 0xff;
        int rb = (endColor >> 16) & 0xff;
        int gb = (endColor >> 8) & 0xff;
        int bb = endColor & 0xff;
        int a = (int) (aa + (ab - aa) * offset);
        int r = (int) (ra + (rb - ra) * offset);
        int g = (int) (ga + (gb - ga) * offset);
        int b = (int) (ba + (bb - ba) * offset);
        return Color.argb(a, r, g, b);
    }

    /**
     * @param normal   正常时的颜色
     * @param pressed  按压时的颜色
     */
    public static ColorStateList createColorStateList(int normal, int pressed) {
        //normal一定要最后
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[0]
        };
        return new ColorStateList(states, new int[]{pressed, normal});
    }
    
    /**
     * @param normal   正常时的颜色
     * @param pressed  按压时的颜色
     * @param disabled 不可用时的颜色
     */
    public static ColorStateList createColorStateList(int normal, int pressed, int disabled) {
        //normal一定要最后
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled},
                new int[0]
        };
        return new ColorStateList(states, new int[]{disabled, pressed, normal});
    }

    /**
     * @param normal   正常时的颜色
     * @param pressed  按压时的颜色
     * @param selected 选中时的颜色
     * @param disabled 不可用时的颜色
     */
    public static ColorStateList createColorStateList(int normal, int pressed, int selected, int disabled) {
        //normal一定要最后
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled},
                new int[]{android.R.attr.state_selected, android.R.attr.state_enabled},
                new int[0]
        };
        return new ColorStateList(states, new int[]{disabled, pressed, selected, normal});
    }

    /**
     * Color转换为颜色字符串，格式：#ffffffff
     */
    public static String toHexColor(int color) {
        byte[] bs = new byte[4];
        bs[0] = (byte) (color >> 24);
        bs[1] = (byte) (color >> 16);
        bs[2] = (byte) (color >> 8);
        bs[3] = (byte) color;
        return StringUtils.toHex(bs);
    }

    /**
     * 判断颜色是否深色
     */
    public static boolean isColorDark(int color) {
        return 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255 >= 0.5;
    }
}
