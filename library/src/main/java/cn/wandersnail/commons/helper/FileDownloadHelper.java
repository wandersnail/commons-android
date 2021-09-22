package cn.wandersnail.commons.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.UUID;

/**
 * date: 2019/8/6 14:58
 * author: zengfansheng
 */
@Deprecated
public class FileDownloadHelper {
    public static final String MIME_TYPE_BINARY = "application/octet-stream";
    public static final String MIME_TYPE_APK = "application/vnd.android.package-archive";
    private final Context context;
    private DownloadManager downloadManager;
    private DownloadManagerPro downloadManagerPro;
    private long downloadId = -1;
    private boolean downloading;
    private ContentObserver observer;
    private int status;
    private File targetFile;
    private boolean isSucceeded;
    private final String description;
    private final String mimeType;
    private final String url;
    private final String title;
    private Callback callback;
    private String filename;

    private FileDownloadHelper(Builder builder) {
        context = builder.context.getApplicationContext();
        title = builder.title;
        description = builder.description;
        url = builder.url;
        mimeType = builder.mimeType;
        if (builder.savePath != null && Build.VERSION.SDK_INT < 29) {
            targetFile = new File(builder.savePath);
        }
        init();
    }

    /**
     * @param mimeType 文件类型
     * @param url      文件下载地址
     * @param title    通知栏显示的标题
     * @param savePath 文件保存路径。在Android Q系统此路径无效
     */
    @Deprecated
    public FileDownloadHelper(@NonNull Context context, String mimeType, @NonNull String url, String title, @NonNull String savePath) {
        this(context, mimeType, url, title, "", savePath);
        init();
    }

    /**
     * @param mimeType    文件类型
     * @param url         文件下载地址
     * @param title       通知栏显示的标题
     * @param description 通知栏显示的内容
     * @param savePath    文件保存路径。在Android Q系统此路径无效
     */
    @Deprecated
    public FileDownloadHelper(@NonNull Context context, String mimeType, @NonNull String url, String title, String description, @NonNull String savePath) {
        this.context = context.getApplicationContext();
        this.mimeType = mimeType;
        this.url = url;
        this.title = title;
        this.description = description;
        targetFile = new File(savePath);
        init();
    }

    private void init() {
        observer = new DownloadChangeObserver();
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManagerPro = new DownloadManagerPro(downloadManager);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public synchronized void start() {
        if (downloading) return;
        downloading = true;
        downloadId = -1;
        isSucceeded = false;
        context.getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, observer);
        if (Build.VERSION.SDK_INT < 29) {
            //如果文件存在，先删除
            if (targetFile != null) {
                if (targetFile.exists()) {
                    targetFile.delete();
                } else {
                    targetFile.getParentFile().mkdirs();
                }
            }
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //7.0以上的系统适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresDeviceIdle(false);
            request.setRequiresCharging(false);
        }
        if (!TextUtils.isEmpty(title)) {
            request.setTitle(title);
        }
        if (!TextUtils.isEmpty(description)) {
            request.setDescription(description);
        }
        filename = UUID.randomUUID().toString().replaceAll("-", "");
        Uri uri;
        if (targetFile != null) {
            uri = Uri.fromFile(targetFile);
        } else {
            uri = Uri.fromFile(new File(context.getExternalCacheDir(), filename));
        }
        request.setDestinationUri(uri);
        if (Build.VERSION.SDK_INT < 29) {
            request.setVisibleInDownloadsUi(true);
            request.allowScanningByMediaScanner();
        } else {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
        }        
        if (!TextUtils.isEmpty(mimeType)) {
            request.setMimeType(mimeType);
        }
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadId = downloadManager.enqueue(request); //开始下载
    }

    /**
     * 取消下载
     */
    public void cancel() {
        unregisterObserver();
        if (!isSucceeded) {
            downloadManager.remove(downloadId);
        }
        filename = null;
    }

    private class DownloadChangeObserver extends ContentObserver {
        DownloadChangeObserver() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        public void onChange(boolean selfChange) {
            if (downloadId == -1L) {
                return;
            }
            int status = downloadManagerPro.getStatusById(downloadId);
            if (status == -1) {
                status = DownloadManager.STATUS_FAILED;
            }
            switch (status) {
                case DownloadManager.STATUS_RUNNING:
                    //下载进度，回调
                    if (callback != null) {
                        int[] downloadBytes = downloadManagerPro.getDownloadBytes(downloadId);
                        callback.onProgress(downloadBytes[0], downloadBytes[1]);
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    unregisterObserver();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL: {
                    unregisterObserver();
                    //更新最新进度
                    if (callback != null) {
                        int[] downloadBytes = downloadManagerPro.getDownloadBytes(downloadId);
                        callback.onProgress(downloadBytes[0], downloadBytes[1]);
                    }
                    isSucceeded = true;
                }
            }
            if (FileDownloadHelper.this.status != status) {
                FileDownloadHelper.this.status = status;
                if (callback != null) {
                    callback.onStateChange(status);
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        if (Build.VERSION.SDK_INT >= 29 || targetFile == null) {
                            callback.onCompleted(new File(context.getExternalCacheDir(), filename));
                        } else {
                            callback.onCompleted(targetFile);
                        }
                    }
                }
            }
        }
    }

    private synchronized void unregisterObserver() {
        context.getContentResolver().unregisterContentObserver(observer);
        downloading = false;
    }
    
    public static class Builder {
        private Context context;
        private String description;
        private String mimeType;
        private String url;
        private String title;
        private String savePath;

        /**
         * @param url 下载地址
         */
        public Builder(@NonNull Context context, @NonNull String url) {
            this.context = context;
            this.url = url;
        }

        /**
         * 设置通知栏显示的内容
         */
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * 文件类型
         */
        public Builder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        /**
         * 通知栏显示的标题
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 在Android Q无效
         * @param savePath 下载文件保存路径
         */
        public Builder setSavePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public FileDownloadHelper build() {
            return new FileDownloadHelper(this);
        }
    }

    public interface Callback {
        /**
         * 下载进度
         *
         * @param downloaded 已下载
         * @param total      总大小
         */
        void onProgress(int downloaded, int total);

        /**
         * 下载状态
         *
         * @param status 下载状态。[DownloadManager.STATUS_RUNNING]等
         */
        void onStateChange(int status);

        /**
         * 下载完成
         *
         * @param file 真实保存的文件
         */
        void onCompleted(@NonNull File file);
    }
}
