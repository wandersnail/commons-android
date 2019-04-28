package com.snail.commons.utils

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.*
import android.net.Uri
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * 时间: 2017/10/10 15:34
 * 作者: zengfansheng
 * 功能: Bitmap、Drawable、图片背景相关
 */

object ImageUtils {
    /**
     * 给ImageView设置圆角背景
     */
    @JvmStatic 
    fun setRoundBackground(iv: ImageView, resid: Int, radius: Float) {
        iv.setImageDrawable(getRoundBitmap(iv.context, resid, radius))
    }

    /**
     * 生成圆角背景
     */
    @JvmStatic 
    fun getRoundBitmap(context: Context, resid: Int, radius: Float): RoundedBitmapDrawable {
        return getRoundBitmap(context, BitmapFactory.decodeResource(context.resources, resid), radius)
    }

    /**
     * 生成圆角背景
     */
    @JvmStatic 
    fun getRoundBitmap(context: Context, bitmap: Bitmap, radius: Float): RoundedBitmapDrawable {
        //创建RoundedBitmapDrawable对象
        val roundImg = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
        roundImg.setAntiAlias(true) //抗锯齿        
        roundImg.cornerRadius = radius //设置圆角半径
        return roundImg
    }

    /**
     * 获取网络bitmap图像
     */
    @JvmStatic 
    fun getNetBitmap(url: String): Bitmap? {
        var bitmap: Bitmap?
        try {
            val myFileUrl = URL(url)
            val conn = myFileUrl.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connect()
            val inputStream = conn.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap = null
        }
        return bitmap
    }

    /**
     * 根据文件路径加载bitmap
     *
     * @param path 文件绝对路径
     */
    @JvmStatic 
    fun getBitmap(path: String): Bitmap? {
        try {
            val fis = FileInputStream(path)
            return BitmapFactory.decodeStream(fis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 根据文件路径加载bitmap
     *
     * @param path 文件绝对路径
     * @param w    宽
     * @param h    高
     */
    @JvmStatic 
    fun getBitmap(path: String, w: Int, h: Int): Bitmap? {
        try {
            val opts = BitmapFactory.Options()
            // 设置为ture只获取图片大小
            opts.inJustDecodeBounds = true
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888
            // 返回为空
            BitmapFactory.decodeFile(path, opts)
            val width = opts.outWidth
            val height = opts.outHeight
            var scaleWidth = 0f
            var scaleHeight = 0f
            if (width > w || height > h) {
                // 缩放
                scaleWidth = width.toFloat() / w
                scaleHeight = height.toFloat() / h
            }
            opts.inJustDecodeBounds = false
            val scale = Math.max(scaleWidth, scaleHeight)
            opts.inSampleSize = scale.toInt()
            return BitmapFactory.decodeFile(path, opts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * @param cornerRadii 圆角大小，dp
     */
    @JvmOverloads 
    @JvmStatic
    @Deprecated(message = "使用SolidDrawableBuilder")
    fun createDrawable(normal: Int, pressed: Int, cornerRadii: Float, leftTop: Boolean = true, rightTop: Boolean = true,
                       leftBottom: Boolean = true, rightBottom: Boolean = true): StateListDrawable {
        val drawable = StateListDrawable()
        drawable.addState(intArrayOf(android.R.attr.state_pressed), createDrawable(pressed, cornerRadii, leftTop, rightTop, leftBottom, rightBottom))
        drawable.addState(intArrayOf(), createDrawable(normal, cornerRadii, leftTop, rightTop, leftBottom, rightBottom))
        return drawable
    }

    /**
     * @param color       背景色
     * @param cornerRadii 圆角大小
     * @param leftTop     左上是否圆角
     * @param rightTop    右上是否圆角
     * @param leftBottom  左下是否圆角
     * @param rightBottom 右下是否圆角
     */
    @JvmOverloads 
    @JvmStatic
    @Deprecated(message = "使用SolidDrawableBuilder")
    fun createDrawable(color: Int, cornerRadii: Float, leftTop: Boolean = true, rightTop: Boolean = true, 
                                     leftBottom: Boolean = true, rightBottom: Boolean = true): Drawable {
        val drawable = GradientDrawable()
        drawable.cornerRadii = floatArrayOf(if (leftTop) cornerRadii else 0f, if (leftTop) cornerRadii else 0f, if (rightTop) cornerRadii else 0f, 
                if (rightTop) cornerRadii else 0f, if (rightBottom) cornerRadii else 0f, if (rightBottom) cornerRadii else 0f, 
                if (leftBottom) cornerRadii else 0f, if (leftBottom) cornerRadii else 0f)
        drawable.setColor(color)
        return drawable
    }

    /**
     * 创建带边框背景
     * @param fillColor    背景色
     * @param frameWidth   边框亮度
     * @param frameColor   边框颜色
     * @param cornerRadius 圆角
     */
    @JvmStatic
    @Deprecated(message = "使用SolidDrawableBuilder")
    fun createDrawable(fillColor: Int, frameWidth: Int, frameColor: Int, cornerRadius: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.cornerRadius = cornerRadius.toFloat()
        drawable.setColor(fillColor)
        drawable.setStroke(frameWidth, frameColor)
        return drawable
    }

    /**
     * @param normal   正常时的Drawable
     * @param pressed  按压时的Drawable
     * @param selected 被选中时的Drawable
     * @param disabled 不可用时的Drawable
     */
    @JvmOverloads 
    @JvmStatic
    @Deprecated(message = "使用GradientDrawableBuilder")
    fun createStateListDrawable(normal: Drawable, pressed: Drawable, selected: Drawable? = null, disabled: Drawable? = null): StateListDrawable {
        val drawable = StateListDrawable()
        if (disabled != null) {
            drawable.addState(intArrayOf(-android.R.attr.state_enabled), disabled)
        }
        drawable.addState(intArrayOf(android.R.attr.state_pressed), pressed)
        if (selected != null) {
            drawable.addState(intArrayOf(android.R.attr.state_selected), selected)
        }
        drawable.addState(intArrayOf(), normal) //normal一定要最后
        return drawable
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree 旋转的角度
     */
    @JvmStatic 
    fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    

    /**
     * 生成系统剪裁Intent
     *
     * @param srcFile   要剪裁的图片
     * @param aspectX   裁剪框的宽比
     * @param aspectY   裁剪框的高比
     * @param outX      图片输出宽度，像素
     * @param outY      图片输出高度，像素
     * @param outFile   图片保存路径
     * @param outFormat 图片输出格式
     */
    @JvmStatic 
    fun getCropImageIntent(context: Context, srcFile: File, aspectX: Int, aspectY: Int, outX: Int, outY: Int, outFile: File, outFormat: Bitmap.CompressFormat): Intent {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, context.packageName + ".fileprovider", srcFile)
        } else {
            Uri.fromFile(srcFile)
        }
        // 判断版本大于等于7.0
        return getCropImageIntent(uri, aspectX, aspectY, outX, outY, outFile, outFormat)
    }

    /**
     * 生成系统剪裁Intent
     *
     * @param uri       要剪裁的图片uri
     * @param aspectX   裁剪框的宽比
     * @param aspectY   裁剪框的高比
     * @param outX      图片输出宽度，像素
     * @param outY      图片输出高度，像素
     * @param outFile   图片保存路径
     * @param outFormat 图片输出格式
     */
    @JvmStatic 
    fun getCropImageIntent(uri: Uri, aspectX: Int, aspectY: Int, outX: Int, outY: Int, outFile: File, outFormat: Bitmap.CompressFormat): Intent {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.putExtra("crop", "true") // crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("scale", true) //去黑边
        intent.putExtra("scaleUpIfNeeded", true) //去黑边
        intent.putExtra("aspectX", aspectX) // 这两项为裁剪框的比例.x:y
        intent.putExtra("aspectY", aspectY)
        intent.putExtra("outputX", outX) //图片输出大小
        intent.putExtra("outputY", outY)
        intent.putExtra("output", Uri.fromFile(outFile))
        intent.putExtra("return-items", false) //若为false则表示不返回数据
        intent.putExtra("noFaceDetection", true)
        intent.putExtra("outputFormat", outFormat.toString()) // 返回格式
        return intent
    }   
}

/*############################################ 扩展函数 #########################################*/

/**
 * 对图片模糊处理
 */
fun Bitmap.blur(context: Context, radius: Float): Bitmap {
    val rs = RenderScript.create(context)
    val input = Allocation.createFromBitmap(rs, this)
    val output = Allocation.createTyped(rs, input.type)
    val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    scriptIntrinsicBlur.setRadius(radius)
    scriptIntrinsicBlur.setInput(input)
    scriptIntrinsicBlur.forEach(output)
    output.copyTo(this)
    return this
}

/**
 * drawable转bitmap
 */
fun Drawable.toBitamp(): Bitmap {
    if (this is BitmapDrawable) {
        return bitmap
    }
    val w = intrinsicWidth
    val h = intrinsicHeight
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, w, h)
    draw(canvas)
    return bitmap
}

/**
 * 缩放bitmap
 */
fun Bitmap.zoom(newWidth: Double, newHeight: Double): Bitmap {
    // 获取这个图片的宽和高 
    val width = width.toFloat()
    val height = height.toFloat()
    // 创建操作图片用的matrix对象 
    val matrix = Matrix()
    // 计算宽高缩放率 
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    // 缩放图片动作 
    matrix.postScale(scaleWidth, scaleHeight)
    return Bitmap.createBitmap(this, 0, 0, width.toInt(), height.toInt(), matrix, true)
}

//通过反射获取imageview的某个属性值
private fun getImageViewFieldValue(any: Any, fieldName: String): Int {
    var value = 0
    try {
        val field = ImageView::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        val fieldValue = field.getInt(any)
        if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
            value = fieldValue
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return value
}

/**
 * 根据ImageView获适当的压缩的宽和高
 *
 * @return int[0]为宽，int[1]为高。
 */
fun ImageView.getSize(): IntArray {
    val wh = IntArray(2)
    val displayMetrics = context.resources.displayMetrics
    val lp = layoutParams
    var width = width // 获取imageview的实际宽度
    if (width <= 0) {
        width = lp.width // 获取imageview在layout中声明的宽度
    }
    if (width <= 0) {
        width = getImageViewFieldValue(this, "mMaxWidth")
    }
    if (width <= 0) {
        width = displayMetrics.widthPixels
    }

    var height = height // 获取imageview的实际高度
    if (height <= 0) {
        height = lp.height // 获取imageview在layout中声明的宽度
    }
    if (height <= 0) {
        height = getImageViewFieldValue(this, "mMaxHeight") // 检查最大值
    }
    if (height <= 0) {
        height = displayMetrics.heightPixels
    }
    wh[0] = width
    wh[1] = height
    return wh
}

/**
 * ImageView转黑白
 */
fun ImageView.toMonochrome() {
    val matrix = ColorMatrix()
    matrix.setSaturation(0f)
    val filter = ColorMatrixColorFilter(matrix)
    colorFilter = filter
}

/**
 * 保存bitmap到文件
 *
 * @param photoFile 文件
 * @param format    保存的图片格式
 */
@JvmOverloads
fun Bitmap.toFile(photoFile: File, quality: Int = 100, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG) {
    FileOutputStream(photoFile).use {
        if (compress(format, quality, it)) {
            it.flush()
        }
        photoFile.delete()
    }
}

/**
 * 旋转图片
 */
fun Bitmap.rotate(degrees: Float): Bitmap {
    //旋转图片 动作   
    val matrix = Matrix()
    matrix.postRotate(degrees)
    // 创建新的图片   
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

/**
 * 将方形bitmap转换为圆形bitmap
 */
fun Bitmap.toCircle(bitmap: Bitmap): Bitmap {
    val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val color = -0xbdbdbe
    val rect = Rect(0, 0, bitmap.width, bitmap.height)
    val paint = Paint()
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    val x = bitmap.width
    canvas.drawCircle((x / 2).toFloat(), (x / 2).toFloat(), (x / 2).toFloat(), paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)
    return output
}

/**
 * 给view添加点击波纹
 *
 * @param color 水波颜色
 * @param allBackground true: 即使是ImageView，也设置到background。false: 如果是ImageView，则优先设置到src，如果drawable不存在才设置到background
 * @param recursive 如果第一个参数是ViewGroup类型，是否让子view也添加波纹
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.enableRipple(color: Int, allBackground: Boolean = false, recursive: Boolean = false) {
    enableRipple(ColorStateList.valueOf(color), allBackground, recursive)
}

/**
 * 给view添加波纹效果
 *
 * @param color 水波颜色
 * @param allBackground true: 即使是ImageView，也设置到background。false: 如果是ImageView，则优先设置到src，如果drawable不存在才设置到background
 * @param recursive 如果第一个参数是ViewGroup类型，是否让子view也添加波纹
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.enableRipple(color: ColorStateList, allBackground: Boolean = false, recursive: Boolean = false) {
    enableRipple(this, color, allBackground)
    if (recursive && this is ViewGroup) {
        for (i in 0 until childCount) {
            enableRipple(color, allBackground, true)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private fun enableRipple(view: View, color: ColorStateList, background: Boolean) {
    if (background || view !is ImageView || view.drawable == null) {
        if (view.background != null) {
            view.background = RippleDrawable(color, view.background, view.background)
        }
    } else {
        view.setImageDrawable(RippleDrawable(color, view.drawable, view.drawable))
    }
}