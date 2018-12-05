package com.zfs.commons.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

public class FileUtils {

    /**
     * 格式化文件大小，根据文件大小不同使用不同单位
     * @param size 文件大小
     * @return 字符串形式的大小，包含单位(B,KB,MB,GB,TB,PB)
     */
    public static String formatFileSize(long size) {
        return formatFileSize(size, null);
    }

    /**
     * 格式化文件大小，根据文件大小不同使用不同单位
     * @param size 文件大小
     * @return 字符串形式的大小，包含单位(B,KB,MB,GB,TB,PB)
     */
    public static String formatFileSize(long size, DecimalFormat format) {
        if (format == null) {
            format = new DecimalFormat("#.00");
        }
        if (size < 1024L) {
            return size + "B";
        } else if (size < 1048576L) {
            return format.format(size / 1024f) + "KB";
        } else if (size < 1073741824L) {
            return format.format(size / 1048576f) + "MB";
        } else if (size < 1099511627776L) {
            return format.format(size / 1073741824f) + "GB";
        } else if (size < 1125899906842624L) {
            return format.format(size / 1099511627776f) + "TB";
        } else if (size < 1152921504606846976L) {
            return format.format(size / 1125899906842624f) + "PB";
        }
        return "size: out of range";
    }

    /**
     * 从路径中获取文件名，包含扩展名
     * @param path 路径
     * @return 如果所传参数是合法路径，截取文件名，如果不是返回原值
     */
    public static String getFileName(String path) {
        if (path != null && (path.contains("/")||path.contains("\\"))) {
            String fileName = path.trim();
            int beginIndex;
            if ((beginIndex=fileName.lastIndexOf("\\")) != -1) {
                fileName = fileName.substring(beginIndex+1);
            }
            if ((beginIndex=fileName.lastIndexOf("/")) != -1) {
                fileName = fileName.substring(beginIndex+1);
            }
            return fileName;
        }
        return path;
    }

    /**
     * 从路径中获取文件名，不包含扩展名
     * @param path 路径
     * @return 如果所传参数是合法路径，截取文件名，如果不是返回原值
     */
    public static String getFileNameWithoutSuffix(String path) {
        if (path != null && (path.contains("/")||path.contains("\\"))) {
            String fileName = path.trim();
            int beginIndex;
            if ((beginIndex=fileName.lastIndexOf("\\")) != -1) {
                fileName = fileName.substring(beginIndex+1);
            }
            if ((beginIndex=fileName.lastIndexOf("/")) != -1) {
                fileName = fileName.substring(beginIndex+1);
            }
            return deleteSuffix(fileName);
        }
        return deleteSuffix(path);
    }

    /**
     * 获取扩展名
     * @param s 路径或后缀
     * @return 不存在后缀时返回null
     */
    public static String getSuffix(String s) {
        if (s.contains(".")) {
            return s.substring(s.lastIndexOf("."));
        }
        return null;
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
     * 检查是否有同名文件，有则在自动在文件名后加当前时间的毫秒值
     */
    public static File checkAndRename(File target) {
        if (target.exists()) {
            String fileName = target.getName();
            if (fileName.contains(".")) {
                String sub = fileName.substring(0, fileName.lastIndexOf("."));
                fileName = fileName.replace(sub, sub + "_" + System.currentTimeMillis());
            } else {
                fileName = fileName+"_"+ System.currentTimeMillis();
            }
            return new File(target.getParent(), fileName);
        }
        return target;
    }

    /**
     * 移动文件或文件夹
     * @param src 要移动的文件或文件夹
     * @param target 目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
     * @param replace 当有重名文件时是否替换。传false时，自动在原文件名后加上当前时间的毫秒值
     * @return 移动成功返回true,否则返回false
     */
    public static boolean moveFile(File src, File target, boolean replace) {
        if (src == null || !src.exists() || target == null) {
            return false;
        }
        if (!replace) {
            target = checkAndRename(target);
        }
        copy(src, target);
        return compareAndDeleteSrc(src, target);
    }

    private static boolean compareAndDeleteSrc(File src, File target) {
        //如果文件存在，并且大小与源文件相等，则写入成功，删除源文件
        if (src.isFile()) {
            if (target.exists() && target.length() == src.length()) {
                src.delete();
                return true;
            }
        } else {
            if (getDirSize(src) == getDirSize(target)) {
                deleteDir(src, true);
                return true;
            }
        }
        return false;
    }

    /**
     * 移动文件或文件夹
     * @param src 要移动的文件或文件夹
     * @param target 目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
     * @param replace 当有重名文件时是否替换。传false时，自动在原文件名后加上当前时间的毫秒值
     * @return 移动成功返回true,否则返回false
     */
    public static boolean moveFileFit(File src, File target, boolean replace) {
        if (src == null || !src.exists() || target == null) {
            return false;
        }
        if (!replace) {
            target = checkAndRename(target);
        }
        copyFit(src, target);

        //如果文件存在，并且大小与源文件相等，则写入成功，删除源文件
        return compareAndDeleteSrc(src, target);
    }

    /**
     * 去掉字符串中重复部分字符串
     * @param dup 重复部分字符串
     * @param strs 要去重的字符串
     * @return 按参数先后顺序返回一个字符串数组
     */
    public static String[] removeDuplicate(String dup, String... strs) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i] != null) {
                strs[i] = strs[i].replaceAll(dup+"+", "");
            }
        }
        return strs;
    }

    /**
     * 获取随机UUID文件名
     * @param fileName 原文件名
     * @return 生成的文件名
     */
    public static String generateRandonFileName(String fileName) {
        // 获得扩展名
        int beginIndex = fileName.lastIndexOf(".");
        String ext = "";
        if (beginIndex != -1) {
            ext = fileName.substring(beginIndex);
        }
        return UUID.randomUUID().toString() + ext;
    }

    /**
     * 删除文件夹
     * @param dir 文件夹
     * @param includeSelf 是否包括本身
     */
    public static void deleteDir(File dir, boolean includeSelf) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f, true);
                } else {
                    f.delete();
                }
            }
        }
        if (includeSelf)
            dir.delete();
    }


    /**
     * 删除文件内所有文件，不包含文件夹
     * @param dir 文件夹
     */
    public static void deleteAllFiles(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteAllFiles(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    /**
     * 获取文件夹的大小
     * @param dir 目录
     * @return 所传参数是目录且存在，则返回文件夹大小，否则返回-1
     */
    public static long getDirSize(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            long size = 0;
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        size += getDirSize(file);
                    } else {
                        size += file.length();
                    }
                }
                return size;
            }
            return 0;
        }
        return -1;
    }

    /**
     * 压缩数据
     */
    public static byte[] compress(byte[] data) {
        GZIPOutputStream gzip = null;
        ByteArrayOutputStream baos = null;
        byte[] newData = null;
        try {
            baos = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(baos);
            gzip.write(data);
            gzip.finish();
            gzip.flush();
            newData = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                IOUtils.close(gzip, baos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newData ;
    }

    /**
     * 压缩文件
     * @param source 源文件
     * @param target 目标文件
     */
    public static void compressFile(File source, File target) {
        FileInputStream fin = null;
        FileOutputStream fout = null;
        GZIPOutputStream gzout = null;
        try {
            fin = new FileInputStream(source);
            fout = new FileOutputStream(target);
            gzout = new GZIPOutputStream(fout);
            byte[] buf = new byte[1024];
            int num;
            while ((num = fin.read(buf)) != -1) {
                gzout.write(buf, 0, num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                IOUtils.close(gzout, fout, fin);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
	 * 快速复制文件，不适合Android
	 * @param source 源文件
	 * @param target 目标文件
	 */
    private static void nioCopyFile(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                IOUtils.close(in, out, inStream, outStream);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /*
     * 复制文件夹，不适合Android
     * @param sourceDir 源文件夹
     * @param targetDir 目标文件夹
     */
    private static void copyDir(File sourceDir, File targetDir) {
        //目标目录新建源文件夹
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        // 获取源文件夹当前下的文件或目录   
        File[] files = sourceDir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                nioCopyFile(file, new File(targetDir, file.getName()));
            } else {
                copyDir(file, new File(targetDir, file.getName()));
            }
        }
    }

    /**
     * 复制文件或文件夹，不适合Android
     * @param src 源文件或文件夹
     * @param target 目标文件或文件夹
     */
    public static void copy(String src, String target) {
        copy(new File(src), new File(target));
    }

    /**
     * 复制文件或文件夹，不适合Android
     * @param src 源文件或文件夹
     * @param target 目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
     */
    public static void copy(File src, File target) {
        if (src.isFile()) {
            nioCopyFile(src, target);
        } else {
            copyDir(src, target);
        }
    }

    /**
     * 复制文件，适合Android平台
     */
    public static void copyFile(File srcFile, File targetFile) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(srcFile));
            out = new BufferedOutputStream(new FileOutputStream(targetFile));
            // 缓冲数组   
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            // 刷新此缓冲的输出流   
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                IOUtils.close(in, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制文件或文件夹，适合Android
     * @param src    源文件或文件夹
     * @param target 目标文件或文件夹
     */
    public static void copyFit(File src, File target) {
        if (src.isFile()) {
            copyFile(src, target);
        } else {
            copyDir(src, target);
        }
    }

    /**
     * 序列化对象到文件
     * @param obj 要序列化的对象
     * @param file 保存到的文件
     */
    public static void saveObjectToFile(Serializable obj, File file) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(obj);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件反序列化对象
     * @param file 保存对象的文件
     */
    public static Object getObjectFromFile(File file) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            Object obj = ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件真实路径
     */
    public static String getFileRealPath(Context context, @NonNull Uri uri) {
        return getRealPathFromUri(context, uri);
    }

    /**
     * 获取文件真实路径
     *
     * @param path 可能是content://或file://或真实路径
     */
    public static String getFileRealPath(Context context, @NonNull String path) {
        return getRealPathFromUri(context, Uri.parse(path));
    }
    
    private static String getRealPathFromUri(Context context, Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String[] split = DocumentsContract.getDocumentId(uri).split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                return getDataColumn(context, contentUri, BaseColumns._ID + "=?", new String[]{split[1]});
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        } else {
            return getRealPathFromURIDB(context, uri);
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
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            String realPath = cursor.getString(index);
            cursor.close();
            return realPath;
        }
    }

    /**
     * Gets data column.
     *
     * @param uri           the uri
     * @param selection     the selection
     * @param selectionArgs the selection args
     * @return the data column
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(index);
            }
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
}