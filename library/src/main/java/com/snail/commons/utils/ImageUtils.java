package com.snail.commons.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 时间: 2017/10/10 15:34
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能: Bitmap、Drawable、图片背景相关
 */

public class ImageUtils {
    /**
     * 将方形bitmap转换为圆形bitmap
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();
        canvas.drawCircle(x / 2, x / 2, x / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static void setRoundBackground(ImageView iv, int resid, float radius) {
        iv.setImageDrawable(getRoundBitmap(iv.getContext(), resid, radius));
    }

    public static RoundedBitmapDrawable getRoundBitmap(Context context, int resid, float radius) {
        return getRoundBitmap(context, BitmapFactory.decodeResource(context.getResources(), resid), radius);
    }

    public static RoundedBitmapDrawable getRoundBitmap(Context context, Bitmap bitmap, float radius) {
        //创建RoundedBitmapDrawable对象
        RoundedBitmapDrawable roundImg = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundImg.setAntiAlias(true);//抗锯齿        
        roundImg.setCornerRadius(radius);//设置圆角半径
        return roundImg;
    }

    /**
     * 对图片模糊处理
     */
    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float radius) {
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        scriptIntrinsicBlur.setRadius(radius);
        scriptIntrinsicBlur.setInput(input);
        scriptIntrinsicBlur.forEach(output);
        output.copyTo(bitmap);
        return bitmap;
    }

    /**
     * drawable转bitmap
     */
    public static Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 缩放bitmap
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高 
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象 
        Matrix matrix = new Matrix();
        // 计算宽高缩放率 
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作 
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
    }

    /**
     * 根据ImageView获适当的压缩的宽和高
     *
     * @return int[0]为宽，int[1]为高。
     */
    public static int[] getImageViewSize(ImageView imageView) {
        int[] wh = new int[2];
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width = imageView.getWidth();// 获取imageview的实际宽度
        if (width <= 0) {
            width = lp.width;// 获取imageview在layout中声明的宽度
        }
        if (width <= 0) {
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }

        int height = imageView.getHeight();// 获取imageview的实际高度
        if (height <= 0) {
            height = lp.height;// 获取imageview在layout中声明的宽度
        }
        if (height <= 0) {
            height = getImageViewFieldValue(imageView, "mMaxHeight");// 检查最大值
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        wh[0] = width;
        wh[1] = height;
        return wh;
    }

    //通过反射获取imageview的某个属性值
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取网络bitmap图像
     */
    public static Bitmap getNetBitmap(String url) {
        Bitmap bitmap;
        try {
            URL myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 根据文件路径加载bitmap
     *
     * @param path 文件绝对路径
     */
    public static Bitmap getBitmap(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件路径加载bitmap
     *
     * @param path 文件绝对路径
     * @param w    宽
     * @param h    高
     */
    public static Bitmap getBitmap(String path, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 设置为ture只获取图片大小
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // 返回为空
            BitmapFactory.decodeFile(path, opts);
            int width = opts.outWidth;
            int height = opts.outHeight;
            float scaleWidth = 0.f, scaleHeight = 0.f;
            if (width > w || height > h) {
                // 缩放
                scaleWidth = ((float) width) / w;
                scaleHeight = ((float) height) / h;
            }
            opts.inJustDecodeBounds = false;
            float scale = Math.max(scaleWidth, scaleHeight);
            opts.inSampleSize = (int) scale;
            return BitmapFactory.decodeFile(path, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存bitmap到文件，保存成JPG格式
     *
     * @param photoFile 文件
     */
    public static void saveBitmapToFile(Bitmap bitmap, File photoFile, int quality) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (bitmap != null) {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (Exception e) {
            photoFile.delete();
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存bitmap到文件
     *
     * @param photoFile 文件
     * @param format    保存的图片格式
     */
    public static void saveBitmapToFile(Bitmap bitmap, File photoFile, Bitmap.CompressFormat format, int quality) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (bitmap != null) {
                if (bitmap.compress(format, quality, fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (Exception e) {
            photoFile.delete();
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param cornerRadii 圆角大小，dp
     */
    public static StateListDrawable createDrawableSelecor(int normal, int pressed, float cornerRadii,
                                                          boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, createDrawable(pressed, cornerRadii, leftTop, rightTop, leftBottom, rightBottom));
        drawable.addState(new int[]{}, createDrawable(normal, cornerRadii, leftTop, rightTop, leftBottom, rightBottom));
        return drawable;
    }

    /**
     * @param color       背景色
     * @param cornerRadii 圆角大小
     * @param leftTop     左上是否圆角
     * @param rightTop    右上是否圆角
     * @param leftBottom  左下是否圆角
     * @param rightBottom 右下是否圆角
     */
    public static Drawable createDrawable(int color, float cornerRadii, boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(new float[]{
                leftTop ? cornerRadii : 0, leftTop ? cornerRadii : 0,
                rightTop ? cornerRadii : 0, rightTop ? cornerRadii : 0,
                rightBottom ? cornerRadii : 0, rightBottom ? cornerRadii : 0,
                leftBottom ? cornerRadii : 0, leftBottom ? cornerRadii : 0});
        drawable.setColor(color);
        return drawable;
    }

    /**
     * 创建带边框背景
     * @param fillColor    背景色
     * @param frameWidth   边框亮度
     * @param frameColor   边框颜色
     * @param cornerRadius 圆角
     */
    public static GradientDrawable createDrawable(int fillColor, int frameWidth, int frameColor, int cornerRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(cornerRadius);
        drawable.setColor(fillColor);
        drawable.setStroke(frameWidth, frameColor);
        return drawable;
    }
    
    /**
     * @param normal  正常时的Drawable
     * @param pressed 按压时的Drawable
     * @param disable 不可用时的Drawable
     */
    public static StateListDrawable createStateListDrawable(Drawable normal, Drawable pressed, Drawable disable) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, disable);
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
        drawable.addState(new int[]{}, normal);//normal一定要最后
        return drawable;
    }

    /**
     * @param normal   正常时的Drawable
     * @param pressed  按压时的Drawable
     * @param selected 被选中时的Drawable
     * @param disabled 不可用时的Drawable
     */
    public static StateListDrawable createStateListDrawable(Drawable normal, Drawable pressed, Drawable selected, Drawable disabled) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_enabled}, disabled);
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
        drawable.addState(new int[]{android.R.attr.state_selected}, selected);
        drawable.addState(new int[]{}, normal);//normal一定要最后
        return drawable;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree 旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * 旋转图片
     */
    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        //旋转图片 动作   
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片   
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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
    public static Intent getCropImageIntent(@NonNull Context context, @NonNull File srcFile, int aspectX, int aspectY, int outX, int outY, @NonNull File outFile, Bitmap.CompressFormat outFormat) {
        Uri uri;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", srcFile);
        } else {
            uri = Uri.fromFile(srcFile);
        }
        return getCropImageIntent(uri, aspectX, aspectY, outX, outY, outFile, outFormat);
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
    public static Intent getCropImageIntent(@NonNull Uri uri, int aspectX, int aspectY, int outX, int outY, @NonNull File outFile, Bitmap.CompressFormat outFormat) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("scale", true);//去黑边
        intent.putExtra("scaleUpIfNeeded", true);//去黑边
        intent.putExtra("aspectX", aspectX);// 这两项为裁剪框的比例.x:y
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outX);//图片输出大小
        intent.putExtra("outputY", outY);
        intent.putExtra("output", Uri.fromFile(outFile));
        intent.putExtra("return-data", false);//若为false则表示不返回数据
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputFormat", outFormat.toString());// 返回格式
        return intent;
    }

    /**
     * 给view设置新背景，并回收旧的背景图片<br>
     * <font color=red>注意：需要确定以前的背景不被使用</font>
     */
    public static void setAndRecycleBackground(View v, int resID) {
        // 获得ImageView当前显示的图片
        Bitmap bitmap1 = null;
        if (v.getBackground() != null) {
            try {
                //若是可转成bitmap的背景，手动回收
                bitmap1 = ((BitmapDrawable) v.getBackground()).getBitmap();
            } catch (ClassCastException e) {
                //若无法转成bitmap，则解除引用，确保能被系统GC回收
                v.getBackground().setCallback(null);
            }
        }
        // 根据原始位图和Matrix创建新的图片
        v.setBackgroundResource(resID);
        // bitmap1确认即将不再使用，强制回收，这也是我们经常忽略的地方
        if (bitmap1 != null && !bitmap1.isRecycled()) {
            bitmap1.recycle();
        }
    }

    /**
     * 给view设置新背景，并回收旧的背景图片<br>
     * <font color=red>注意：需要确定以前的背景不被使用</font>
     */
    public static void setAndRecycleBackground(View v, BitmapDrawable imageDrawable) {
        // 获得ImageView当前显示的图片
        Bitmap bitmap1 = null;
        if (v.getBackground() != null) {
            try {
                //若是可转成bitmap的背景，手动回收
                bitmap1 = ((BitmapDrawable) v.getBackground()).getBitmap();
            } catch (ClassCastException e) {
                //若无法转成bitmap，则解除引用，确保能被系统GC回收
                v.getBackground().setCallback(null);
            }
        }
        // 根据原始位图和Matrix创建新的图片
        v.setBackground(imageDrawable);
        // bitmap1确认即将不再使用，强制回收，这也是我们经常忽略的地方
        if (bitmap1 != null && !bitmap1.isRecycled()) {
            bitmap1.recycle();
        }
    }

    /**
     * 释放背景图片资源
     */
    public static void releaseBitmapAndBg(View root) {
        root.setBackgroundResource(0);
        if (root instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                view.setBackgroundResource(0);
                if (view instanceof ImageView) {
                    ((ImageView) view).setImageBitmap(null);
                }
            }
        } else if (root instanceof ImageView) {
            ((ImageView) root).setImageBitmap(null);
        }
    }
}
