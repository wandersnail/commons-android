package com.snail.commons.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.annotation.NonNull;

import java.io.File;

/**
 * date: 2019/8/6 14:58
 * author: zengfansheng
 */
public class FileDownloadHelper {
    public static final String MIME_TYPE_BINARY = "application/octet-stream";
    public static final String MIME_TYPE_APK = "application/vnd.android.package-archive";
    private final Context context;
    private final DownloadManager downloadManager;
    private final DownloadManagerPro downloadManagerPro;
    private long downloadId = -1;
    private boolean downloading;
    private final ContentObserver observer;
    private int status;
    private final File targetFile;
    private boolean isSucceeded;
    private final String description;
    private final String mimeType;
    private final String url;
    private final String title;
    private Callback callback;

    public FileDownloadHelper(@NonNull Context context, String mimeType, @NonNull String url, String title, @NonNull String savePath) {
        this(context, mimeType, url, title, "", savePath);
    }

    public FileDownloadHelper(@NonNull Context context, String mimeType, @NonNull String url, String title, String description, @NonNull String savePath) {
        this.context = context.getApplicationContext();
        this.mimeType = mimeType;
        this.url = url;
        this.title = title;
        this.description = description;
        targetFile = new File(savePath);
        observer = new DownloadChangeObserver();
        downloadManager = (DownloadManager) this.context.getSystemService(Context.DOWNLOAD_SERVICE);
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
        //如果文件存在，先删除
        targetFile.delete();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //7.0以上的系统适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresDeviceIdle(false);
            request.setRequiresCharging(false);
        }
        request.setTitle(title);
        if (!TextUtils.isEmpty(description)) {
            request.setDescription(description);
        }
        request.setDestinationUri(Uri.fromFile(targetFile));
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setMimeType(mimeType);
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
                }
            }
        }
    }

    private synchronized void unregisterObserver() {
        context.getContentResolver().unregisterContentObserver(observer);
        downloading = false;
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
         * 下载完成
         *
         * @param status 下载状态。[DownloadManager.STATUS_RUNNING]等
         */
        void onStateChange(int status);
    }
}
