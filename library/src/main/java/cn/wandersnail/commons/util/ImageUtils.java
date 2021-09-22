package cn.wandersnail.commons.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * date: 2019/8/7 17:33
 * author: zengfansheng
 */
public class ImageUtils {
    /**
     * 给ImageView设置圆角背景
     */
    public static void setRoundBackground(@NonNull ImageView iv, int resid, float radius) {
        iv.setImageDrawable(getRoundBitmap(iv.getContext(), resid, radius));
    }

    /**
     * 生成圆角背景
     */
    public static RoundedBitmapDrawable getRoundBitmap(@NonNull Context context, int resid, float radius) {
        return getRoundBitmap(context, BitmapFactory.decodeResource(context.getResources(), resid), radius);
    }

    /**
     * 生成圆角背景
     */
    public static RoundedBitmapDrawable getRoundBitmap(@NonNull Context context, @NonNull Bitmap bitmap, float radius) {
        //创建RoundedBitmapDrawable对象
        RoundedBitmapDrawable roundImg = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundImg.setAntiAlias(true); //抗锯齿        
        roundImg.setCornerRadius(radius); //设置圆角半径
        return roundImg;
    }

    /**
     * 获取网络bitmap图像
     */
    public static Bitmap getNetBitmap(@NonNull String url) {
        Bitmap bitmap;
        try {
            URL myFileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
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
    public static Bitmap getBitmap(@NonNull String path) {
        try {
            InputStream fis = new FileInputStream(path);
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
    public static Bitmap getBitmap(@NonNull String path, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 设置为true只获取图片大小
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // 返回为空
            BitmapFactory.decodeFile(path, opts);
            int width = opts.outWidth;
            int height = opts.outHeight;
            float scaleWidth = 0f;
            float scaleHeight = 0f;
            if (width > w || height > h) {
                // 缩放
                scaleWidth = width * 1f / w;
                scaleHeight = height * 1f / h;
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
     * 根据图片字节数据加载bitmap
     *
     * @param bytes 图片字节数据
     * @param w     宽
     * @param h     高
     */
    public static Bitmap getBitmap(@NonNull byte[] bytes, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            int width = opts.outWidth;
            int height = opts.outHeight;
            float scaleWidth = 0f;
            float scaleHeight = 0f;
            if (width > w || height > h) {
                // 缩放
                scaleWidth = width * 1f / w;
                scaleHeight = height * 1f / h;
            }
            opts.inJustDecodeBounds = false;
            float scale = Math.max(scaleWidth, scaleHeight);
            opts.inSampleSize = (int) scale;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree 旋转的角度
     */
    public static int readPictureDegree(@NonNull String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int attributeInt = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (attributeInt) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
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
    public static Intent getCropImageIntent(@NonNull Context context, @NonNull File srcFile, int aspectX, int aspectY,
                                            int outX, int outY, @NonNull File outFile, @NonNull Bitmap.CompressFormat outFormat) {
        Uri uri;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".WSFileProvider", srcFile);
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
    public static Intent getCropImageIntent(@NonNull Uri uri, int aspectX, int aspectY, int outX, int outY,
                                            @NonNull File outFile, @NonNull Bitmap.CompressFormat outFormat) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.putExtra("crop", "true"); // crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("scale", true); //去黑边
        intent.putExtra("scaleUpIfNeeded", true); //去黑边
        intent.putExtra("aspectX", aspectX); // 这两项为裁剪框的比例.x:y
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outX); //图片输出大小
        intent.putExtra("outputY", outY);
        intent.putExtra("output", Uri.fromFile(outFile));
        intent.putExtra("return-items", false); //若为false则表示不返回数据
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputFormat", outFormat.toString()); // 返回格式
        return intent;
    }

    /**
     * 对图片模糊处理
     */
    public static Bitmap blur(@NonNull Context context, @NonNull Bitmap bitmap, float radius) {
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
    public static Bitmap toBitamp(@NonNull Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (!bitmap.isRecycled()) {
                return bitmap;
            }
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
    public static Bitmap zoom(@NonNull Bitmap bitmap, float newWidth, float newHeight) {
        // 获取这个图片的宽和高 
        float width = bitmap.getWidth() * 1f;
        float height = bitmap.getHeight() * 1f;
        // 创建操作图片用的matrix对象 
        Matrix matrix = new Matrix();
        // 计算宽高缩放率 
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        // 缩放图片动作 
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, (int) width, (int) height, matrix, true);
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
     * 根据ImageView获适当的压缩的宽和高
     *
     * @return int[0]为宽，int[1]为高。
     */
    public static int[] getSize(@NonNull ImageView iv) {
        int[] wh = new int[2];
        DisplayMetrics displayMetrics = iv.getContext().getResources().getDisplayMetrics();
        ViewGroup.LayoutParams lp = iv.getLayoutParams();
        int width = iv.getWidth(); // 获取imageview的实际宽度
        if (width <= 0) {
            width = lp.width; // 获取imageview在layout中声明的宽度
        }
        if (width <= 0) {
            width = getImageViewFieldValue(iv, "mMaxWidth");
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }

        int height = iv.getHeight(); // 获取imageview的实际高度
        if (height <= 0) {
            height = lp.height; // 获取imageview在layout中声明的宽度
        }
        if (height <= 0) {
            height = getImageViewFieldValue(iv, "mMaxHeight"); // 检查最大值
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        wh[0] = width;
        wh[1] = height;
        return wh;
    }

    /**
     * ImageView转黑白
     */
    public static void toMonochrome(@NonNull ImageView iv) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0f);
        ColorFilter filter = new ColorMatrixColorFilter(matrix);
        iv.setColorFilter(filter);
    }

    /**
     * 保存bitmap到文件
     *
     * @param photoFile 文件
     * @param format    保存的图片格式
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void toFile(@NonNull Bitmap bitmap, @NonNull File photoFile, int quality, Bitmap.CompressFormat format) {
        FileOutputStream fos = null;
        try {
            photoFile.delete();
            fos = new FileOutputStream(photoFile);
            if (bitmap.compress(format, quality, fos)) {
                fos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * 旋转图片
     */
    public static Bitmap rotate(@NonNull Bitmap bitmap, float degrees) {
        //旋转图片 动作   
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        // 创建新的图片   
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 将方形bitmap转换为圆形bitmap
     */
    public static Bitmap toCircle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = -0xbdbdbe;
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();
        canvas.drawCircle(x / 2f, x / 2f, x / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 给view添加点击波纹
     *
     * @param color         水波颜色
     * @param allBackground true: 即使是ImageView，也设置到background。false: 如果是ImageView，则优先设置到src，如果drawable不存在才设置到background
     * @param recursive     如果第一个参数是ViewGroup类型，是否让子view也添加波纹
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void enableRipple(@NonNull View view, int color, boolean allBackground, boolean recursive) {
        enableRipple(view, ColorStateList.valueOf(color), allBackground, recursive);
    }

    /**
     * 给view添加波纹效果
     *
     * @param color         水波颜色
     * @param allBackground true: 即使是ImageView，也设置到background。false: 如果是ImageView，则优先设置到src，如果drawable不存在才设置到background
     * @param recursive     如果第一个参数是ViewGroup类型，是否让子view也添加波纹
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void enableRipple(@NonNull View view, @NonNull ColorStateList color, boolean allBackground, boolean recursive) {
        enableRipple(view, color, allBackground);
        if (recursive && view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                enableRipple(viewGroup.getChildAt(i), color, allBackground, true);
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private static void enableRipple(View view, ColorStateList color, boolean background) {
        if (background || !(view instanceof ImageView) || ((ImageView) view).getDrawable() == null) {
            if (view.getBackground() != null) {
                view.setBackground(new RippleDrawable(color, view.getBackground(), view.getBackground()));
            }
        } else {
            ImageView iv = (ImageView) view;
            iv.setImageDrawable(new RippleDrawable(color, iv.getDrawable(), iv.getDrawable()));
        }
    }
}
