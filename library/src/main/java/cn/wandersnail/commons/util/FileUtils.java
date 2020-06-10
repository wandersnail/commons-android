package cn.wandersnail.commons.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

/**
 * date: 2019/8/7 11:50
 * author: zengfansheng
 */
public class FileUtils {
    /**
     * 格式化文件大小，根据文件大小不同使用不同单位
     *
     * @param size   文件大小
     * @param format 数字的格式
     * @return 字符串形式的大小，包含单位(B,KB,MB,GB,TB,PB)
     */
    public static String formatFileSize(long size, DecimalFormat format) {
        if (format == null) {
            format = new DecimalFormat("#.00");
        }
        if (size < 1024) {
            return size + " B";
        } else if (size < 1048576) {
            return format.format((size / 1024d)) + " KB";
        } else if (size < 1073741824) {
            return format.format((size / 1048576d)) + " MB";
        } else if (size < 1099511627776L) {
            return format.format((size / 1073741824d)) + " GB";
        } else if (size < 1125899906842624L) {
            return format.format((size / 1099511627776d)) + " TB";
        } else if (size < 1152921504606846976L) {
            return format.format((size / 1125899906842624d)) + " PB";
        } else {
            return "size: out of range";
        }
    }

    /**
     * 格式化文件大小，根据文件大小不同使用不同单位
     *
     * @param size 文件大小
     * @return 字符串形式的大小，包含单位(B,KB,MB,GB,TB,PB)
     */
    public static String formatFileSize(long size) {
        return formatFileSize(size, null);
    }

    /**
     * 返回去掉扩展名的文件名
     */
    public static String deleteSuffix(String fileName) {
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    /**
     * 获取扩展名
     *
     * @param s 路径或后缀
     * @return 不存在后缀时返回null
     */
    public static String getSuffix(String s) {
        if (s != null && s.contains(".")) {
            return s.substring(s.lastIndexOf("."));
        }
        return null;
    }

    /**
     * 从路径中获取文件名，包含扩展名
     *
     * @param path 路径
     * @return 如果所传参数是合法路径，截取文件名，如果不是返回原值
     */
    public static String getFileName(String path) {
        return getFileName(path, false);
    }

    /**
     * 从路径中获取文件名
     *
     * @param path          路径
     * @param withoutSuffix true不包含扩展名，false包含
     * @return 如果所传参数是合法路径，截取文件名，如果不是返回原值
     */
    public static String getFileName(String path, boolean withoutSuffix) {
        if ((path.contains("/") || path.contains("\\"))) {
            int beginIndex = path.lastIndexOf("\\");
            String fileName = path;
            if (beginIndex != -1) {
                fileName = fileName.substring(beginIndex + 1);
            }
            beginIndex = fileName.lastIndexOf("/");
            if (beginIndex != -1) {
                fileName = fileName.substring(beginIndex + 1);
            }
            return withoutSuffix ? deleteSuffix(fileName) : fileName;
        }
        return withoutSuffix ? deleteSuffix(path) : path;
    }

    /**
     * 检查是否有同名文件，有则在自动在文件名后加当前时间的毫秒值
     */
    public static File checkAndRename(@NonNull File target) {
        Objects.requireNonNull(target, "target is null");
        if (target.exists()) {
            String fileName = target.getName();
            if (fileName.contains(".")) {
                String sub = fileName.substring(0, fileName.lastIndexOf("."));
                fileName = fileName.replace(sub, StringUtils.randomUuid() + "_" + sub);
            } else {
                fileName = StringUtils.randomUuid() + "_" + fileName;
            }
            return new File(target.getParent(), fileName);
        }
        return target;
    }

    /**
     * 去掉字符串中重复部分字符串
     *
     * @param dup  重复部分字符串
     * @param strs 要去重的字符串
     * @return 按参数先后顺序返回一个字符串数组
     */
    public static String[] removeDuplicate(@NonNull String dup, @NonNull String... strs) {
        String[] out = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            out[i] = strs[i].replace(dup + "+", "");
        }
        return out;
    }

    /**
     * 根据路径或文件名获取MimeType
     *
     * @param path 文件路径
     */
    public static String getMimeType(@NonNull Context context, @NonNull String path) {
        String mime = null;       
        String suffix = getSuffix(path).toLowerCase(Locale.ENGLISH);
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("mimetype.json");
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String json = new String(bytes);
            JSONObject jsonObject = new JSONObject(json);
            mime = jsonObject.getString(suffix);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mime == null) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                mmr.setDataSource(path);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (Exception e) {
                e.printStackTrace();
                mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
            }
        }        
        return mime == null ? "*/*" : mime;
    }

    /**
     * 获取随机UUID文件名
     *
     * @param fileName 原文件名
     * @return 生成的文件名
     */
    public static String generateRandonFileName(@NonNull String fileName) {
        return StringUtils.randomUuid() + getSuffix(fileName);
    }

    /**
     * 使用GZIP压缩数据
     */
    public static byte[] compressByGZIP(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            GZIPOutputStream out = new GZIPOutputStream(baos);
            out.write(bytes);
            out.finish();
            out.flush();
            out.close();
            return baos.toByteArray();
        } catch (Exception e) {
            return bytes;
        }
    }

    /**
     * 使用GZIP压缩文件
     *
     * @param src    待压缩文件
     * @param target 压缩后文件
     */
    public static void compressByGZIP(@NonNull File src, @NonNull File target) {
        compressByGZIP(src, target, 40960);
    }

    /**
     * 使用GZIP压缩文件
     *
     * @param src        待压缩文件
     * @param target     压缩后的文件
     * @param bufferSize 读取流时的缓存大小
     */
    public static void compressByGZIP(@NonNull File src, @NonNull File target, int bufferSize) {
        if (!src.exists()) return;
        FileInputStream fis = null;
        GZIPOutputStream out = null;
        try {
            fis = new FileInputStream(src);
            out = new GZIPOutputStream(new FileOutputStream(target));
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.finish();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis, out);
        }
    }

    /**
     * 从流保存到文件，不会关闭输入流
     *
     * @param target 目标文件
     */
    public static void toFile(@NonNull InputStream inputStream, @NonNull File target) {
        toFile(inputStream, target, 40960);
    }

    /**
     * 从流保存到文件，不会关闭输入流
     *
     * @param target     目标文件
     * @param bufferSize 读取流时的缓存大小
     */
    public static void toFile(@NonNull InputStream inputStream, @NonNull File target, int bufferSize) {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(target));
            BufferedInputStream in = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 复制文件
     * @param src 源文件
     * @param target 目标文件
     */
    public static void copyFile(@NonNull File src, @NonNull File target) {
        copyFile(src, target, 40960);
    }

    /**
     * 复制文件
     * @param src 源文件
     * @param target 目标文件
     * @param bufferSize 读取流时的缓存大小
     */
    public static void copyFile(@NonNull File src, @NonNull File target, int bufferSize) {
        if (!src.exists()) return;
        BufferedInputStream fis = null;
        BufferedOutputStream fos = null;
        try {
            fis = new BufferedInputStream(new FileInputStream(src));
            fos = new BufferedOutputStream(new FileOutputStream(target));
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis, fos);
        }
    }

    /**
     * 复制文件夹
     * @param srcDir 源文件夹
     * @param targetDir 目标文件夹
     */
    public static void copyDir(@NonNull File srcDir, @NonNull File targetDir) {
        copyDir(srcDir, targetDir, 40960);
    }

    /**
     * 复制文件夹
     * @param srcDir 源文件夹
     * @param targetDir 目标文件夹
     * @param bufferSize 读取流时的缓存大小
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyDir(@NonNull File srcDir, @NonNull File targetDir, int bufferSize) {
        //目标目录新建源文件夹
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        // 获取源文件夹当前下的文件或目录   
        File[] files = srcDir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    copyFile(file, new File(targetDir, file.getName()), bufferSize);
                } else {
                    copyDir(file, new File(targetDir, file.getName()), bufferSize);
                }
            }
        }
    }

    /**
     * 复制文件或文件夹
     * @param src 源文件或文件夹
     * @param target 目标文件或文件夹
     */
    public static void copy(@NonNull File src, @NonNull File target) {
        copy(src, target, 40960);
    }
    
    /**
     * 复制文件或文件夹
     * @param src 源文件或文件夹
     * @param target 目标文件或文件夹
     * @param bufferSize 读取流时的缓存大小
     */
    public static void copy(@NonNull File src, @NonNull File target, int bufferSize) {
        if (src.exists()) {
            if (src.isFile()) {
                copyFile(src, target, bufferSize);
            } else {
                copyDir(src, target, bufferSize);
            }
        }
    }

    /**
     * 获取文件或文件夹大小
     */
    public static long getSize(@NonNull File file) {
        if (file.exists()) {
            if (file.isFile()) {
                return file.length();
            } else {
                long size = 0;
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        if (f.isFile()) {
                            size += f.length();
                        } else {
                            size += getSize(f);
                        }
                    }
                }
                return size;
            }
        } else {
            return 0;
        }
    }

    /**
     * 删除文件夹，包含自身
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteDir(@NonNull File dir) {
        emptyDir(dir);
        dir.delete();
    }

    /**
     * 删除文件夹内所有文件
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void emptyDir(@NonNull File dir) {
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    deleteDir(file);
                }
            }
        }
    }

    /*
     * 比较大小并删除源
     */
    private static boolean compareAndDeleteSrc(@NonNull File src, @NonNull File target) {
        //如果文件存在，并且大小与源文件相等，则写入成功，删除源文件或文件夹
        if (src.exists()) {
            if (src.isFile()) {
                if (target.exists() && target.length() == src.length()) {
                    return src.delete();
                }
            } else {
                if (getSize(src) == getSize(target)) {
                    deleteDir(src);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 移动文件或文件夹
     *
     * @param target 目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
     * @return 移动成功返回true, 否则返回false
     */
    public static boolean move(@NonNull File src, @NonNull File target) {
        return move(src, target, 40960, true);
    }

    /**
     * 移动文件或文件夹
     *
     * @param target     目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
     * @param bufferSize 读取流时的缓存大小
     * @return 移动成功返回true, 否则返回false
     */
    public static boolean move(@NonNull File src, @NonNull File target, int bufferSize) {
        return move(src, target, bufferSize, true);
    }

    /**
     * 移动文件或文件夹
     *
     * @param target  目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
     * @param replace 当有重名文件时是否替换。传false时，自动在重命名
     * @return 移动成功返回true, 否则返回false
     */
    public static boolean move(@NonNull File src, @NonNull File target, boolean replace) {
        return move(src, target, 40960, replace);
    }

    /**
     * 移动文件或文件夹
     *
     * @param target     目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
     * @param bufferSize 读取流时的缓存大小
     * @param replace    当有重名文件时是否替换。传false时，自动在重命名
     * @return 移动成功返回true, 否则返回false
     */
    public static boolean move(@NonNull File src, @NonNull File target, int bufferSize, boolean replace) {
        if (!src.exists()) return false;
        if (!replace) {
            target = checkAndRename(target);
        }
        if (src.isFile()) {
            copyFile(src, target, bufferSize);
        } else {
            copyDir(src, target, bufferSize);
        }
        return compareAndDeleteSrc(src, target);
    }

    /**
     * 兼容Android7.0以上，获取Intent传递的File的Uri
     */
    public static Uri toUri(@NonNull File file, @NonNull Context context) {
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = context.getPackageName() + ".fileprovider";
            return FileProvider.getUriForFile(context, authority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 给Intent设置兼容Android7.0的数据和类型
     */
    public static void setIntentDataAndType(@NonNull File file, @NonNull Context context, @NonNull Intent intent, @NonNull String type, boolean writeable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(toUri(file, context), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeable) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }

    /**
     * 获取文件真实路径
     */
    public static String getFileRealPath(@NonNull Context context, @NonNull Uri uri) {
        return getRealPathFromUri(context, uri);
    }

    /**
     * 获取文件真实路径
     *
     * @param path 可能是content://或file://或真实路径
     */
    public static String getFileRealPath(@NonNull Context context, @NonNull String path) {
        return getRealPathFromUri(context, Uri.parse(path));
    }

    private static String getRealPathFromUri(Context context, Uri uri) {
        // DocumentProvider
        try {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    //只处理主存储，外置的可能性太多
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                    String id = DocumentsContract.getDocumentId(uri);
                    if (id.startsWith("raw:")) {
                        return id.substring(4);
                    }
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {// MediaProvider
                    String[] split = DocumentsContract.getDocumentId(uri).split(":");
                    String type = split[0];
                    Uri contentUri = null;
                    switch (type) {
                        case "image":
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "video":
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "audio":
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            break;
                    }
                    return getDataColumn(context, contentUri, BaseColumns._ID + "=?", new String[]{split[1]});
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) { // MediaStore (and general)
                // Return the remote address
                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                } else {
                    return getDataColumn(context, uri, null, null);
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) { // File
                return uri.getPath();
            } else {
                return getRealPathFromURIDB(context, uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets real path from uri.
     *
     * @param uri the content uri
     * @return the real path from uri
     */
    private static String getRealPathFromURIDB(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            String realPath = null;
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                realPath = cursor.getString(index);
            }
            cursor.close();
            return realPath == null ? uri.getPath() : realPath;
        }
    }

    /**
     * Gets items column.
     *
     * @param uri           the uri
     * @param selection     the selection
     * @param selectionArgs the selection args
     * @return the items column
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String[] projection = new String[]{MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            String realPath = null;
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                realPath = cursor.getString(index);
            }
            cursor.close();
            return realPath;
        }
        return null;
    }

    /**
     * Is external storage document boolean.
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * Is downloads document boolean.
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * Is media document boolean.
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Is google photos uri boolean.
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 根据文件获取content uri
     *
     * @param context 上下文
     * @param baseUri 父uri
     * @param file    文件
     */
    private static Uri getContentUri(Context context, Uri baseUri, File file) {
        Cursor cursor = context.getContentResolver().query(baseUri, new String[]{BaseColumns._ID}, MediaStore.MediaColumns.DATA + "=? ", new String[]{file.getAbsolutePath()}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else if (file.exists()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            context.getContentResolver().insert(baseUri, values);
        }
        return null;
    }

    /**
     * 获取视频文件的content uri
     */
    public static Uri getVideoContentUri(@NonNull Context context, @NonNull File file) {
        return getContentUri(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, file);
    }

    /**
     * 获取图片文件的content uri
     */
    public static Uri getImageContentUri(@NonNull Context context, @NonNull File file) {
        return getContentUri(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, file);
    }

    /**
     * 获取音频文件的content uri
     */
    public static Uri getAudioContentUri(@NonNull Context context, @NonNull File file) {
        return getContentUri(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, file);
    }
}
