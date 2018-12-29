package com.snail.commons.entity

import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.database.Cursor
import android.net.Uri
import com.snail.commons.entity.DownloadMgrPro.RequestPro
import java.lang.reflect.Method

/**
 * Get download info
 *  * [.getStatusById] get download status
 *  * [.getDownloadBytes] get downloaded byte, total byte
 *  * [.getBytesAndStatus] get downloaded byte, total byte and download status
 *  * [.getFileName] get download file name
 *  * [.getUri] get download uri
 *  * [.getReason] get failed code or paused reason
 *  * [.getPausedReason] get paused reason
 *  * [.getErrorCode] get failed error code
 *
 * Operate download
 *
 *  * [.isExistPauseAndResumeMethod] whether exist pauseDownload and resumeDownload method in
 * [DownloadManager]
 *  * [.pauseDownload] pause download. need pauseDownload(long...) method in [DownloadManager]
 *  * [.resumeDownload] onShow download. need resumeDownload(long...) method in [DownloadManager]
 *
 * RequestPro
 *
 *  * [RequestPro.setNotiClass] set noti class
 *  * [RequestPro.setNotiExtras] set noti extras
 */
class DownloadMgrPro(private val downloadManager: DownloadManager) {

    /**
     * get download status
     */
    fun getStatusById(downloadId: Long): Int {
        return getInt(downloadId, DownloadManager.COLUMN_STATUS)
    }

    /**
     * get downloaded byte, total byte
     *
     * @return a int array with two elements
     *
     *  * result[0] represents downloaded bytes, This will initially be -1.
     *  * result[1] represents total bytes, This will initially be -1.
     *
     */
    fun getDownloadBytes(downloadId: Long): IntArray {
        val bytesAndStatus = getBytesAndStatus(downloadId)
        return intArrayOf(bytesAndStatus[0], bytesAndStatus[1])
    }

    /**
     * get downloaded byte, total byte and download status
     *
     * @return a int array with three elements
     *
     *  * result[0] represents downloaded bytes, This will initially be -1.
     *  * result[1] represents total bytes, This will initially be -1.
     *  * result[2] represents download status, This will initially be 0.
     *
     */
    fun getBytesAndStatus(downloadId: Long): IntArray {
        val bytesAndStatus = intArrayOf(-1, -1, 0)
        val query = DownloadManager.Query().setFilterById(downloadId)
        var c: Cursor? = null
        try {
            c = downloadManager.query(query)
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            }
        } finally {
            c?.close()
        }
        return bytesAndStatus
    }

    /**
     * pause download
     *
     * @param ids the IDs of the downloads to be paused
     * @return the number of downloads actually paused, -1 if exception or method not exist
     */
    fun pauseDownload(vararg ids: Long): Int {
        initPauseMethod()
        if (pauseDownload == null) {
            return -1
        }

        try {
            return pauseDownload!!.invoke(downloadManager, ids as Any) as Int
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }

    /**
     * onShow download
     *
     * @param ids the IDs of the downloads to be resumed
     * @return the number of downloads actually resumed, -1 if exception or method not exist
     */
    fun resumeDownload(vararg ids: Long): Int {
        initResumeMethod()
        if (resumeDownload == null) {
            return -1
        }

        try {
            return resumeDownload!!.invoke(downloadManager, ids) as Int
        } catch (e: Exception) {
            //accept all exception, include ClassNotFoundException, NoSuchMethodException, InvocationTargetException, NullPointException
            e.printStackTrace()
        }

        return -1
    }

    /**
     * get download file name
     */
    fun getFileName(downloadId: Long): String? {
        return getString(downloadId, COLUMN_LOCAL_FILENAME)
    }

    /**
     * get download uri
     */
    fun getUri(downloadId: Long): String? {
        return getString(downloadId, DownloadManager.COLUMN_URI)
    }

    /**
     * get failed code or paused reason
     * @return if status of downloadId is [DownloadManager.STATUS_PAUSED], return
     * [.getPausedReason]
     * if status of downloadId is [DownloadManager.STATUS_FAILED], return [.getErrorCode]
     * if status of downloadId is neither [DownloadManager.STATUS_PAUSED] nor
     * [DownloadManager.STATUS_FAILED], return 0
     */
    fun getReason(downloadId: Long): Int {
        return getInt(downloadId, DownloadManager.COLUMN_REASON)
    }

    /**
     * get paused reason
     * @return if status of downloadId is [DownloadManager.STATUS_PAUSED], return one of
     * [DownloadManager.PAUSED_WAITING_TO_RETRY]<br></br>
     * [DownloadManager.PAUSED_WAITING_FOR_NETWORK]<br></br>
     * [DownloadManager.PAUSED_QUEUED_FOR_WIFI]<br></br>
     * [DownloadManager.PAUSED_UNKNOWN]
     * else return [DownloadManager.PAUSED_UNKNOWN]
     */
    fun getPausedReason(downloadId: Long): Int {
        return getInt(downloadId, DownloadManager.COLUMN_REASON)
    }

    /**
     * get failed error code
     */
    fun getErrorCode(downloadId: Long): Int {
        return getInt(downloadId, DownloadManager.COLUMN_REASON)
    }

    class RequestPro
    /**
     * @param uri the HTTP URI to download.
     */
    (uri: Uri) : Request(uri) {

        /**
         * set noti class, only init once
         *
         * @param className full class name
         */
        fun setNotiClass(className: String) {
            synchronized(this) {

                if (!isInitNotiClass) {
                    isInitNotiClass = true
                    try {
                        setNotiClass = Request::class.java.getMethod(METHOD_NAME_SET_NOTI_CLASS, CharSequence::class.java)
                    } catch (e: Exception) {
                        // accept all exception
                        e.printStackTrace()
                    }

                }
            }

            if (setNotiClass != null) {
                try {
                    setNotiClass!!.invoke(this, className)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        /**
         * set noti extras, only init once
         *
         * @param extras
         */
        fun setNotiExtras(extras: String) {
            synchronized(this) {

                if (!isInitNotiExtras) {
                    isInitNotiExtras = true
                    try {
                        setNotiExtras = Request::class.java.getMethod(METHOD_NAME_SET_NOTI_EXTRAS, CharSequence::class.java)
                    } catch (e: Exception) {
                        // accept all exception
                        e.printStackTrace()
                    }

                }
            }

            if (setNotiExtras != null) {
                try {
                    setNotiExtras!!.invoke(this, extras)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        companion object {
            const val METHOD_NAME_SET_NOTI_CLASS = "setNotiClass"
            const val METHOD_NAME_SET_NOTI_EXTRAS = "setNotiExtras"

            private var isInitNotiClass = false
            private var isInitNotiExtras = false

            private var setNotiClass: Method? = null
            private var setNotiExtras: Method? = null
        }
    }

    /**
     * get string column
     */
    private fun getString(downloadId: Long, columnName: String): String? {
        val query = DownloadManager.Query().setFilterById(downloadId)
        var result: String? = null
        var c: Cursor? = null
        try {
            c = downloadManager.query(query)
            if (c != null && c.moveToFirst()) {
                result = c.getString(c.getColumnIndex(columnName))
            }
        } finally {
            c?.close()
        }
        return result
    }

    /**
     * get int column
     */
    private fun getInt(downloadId: Long, columnName: String): Int {
        val query = DownloadManager.Query().setFilterById(downloadId)
        var result = -1
        var c: Cursor? = null
        try {
            c = downloadManager.query(query)
            if (c != null && c.moveToFirst()) {
                result = c.getInt(c.getColumnIndex(columnName))
            }
        } finally {
            c?.close()
        }
        return result
    }

    companion object {

        val CONTENT_URI = Uri.parse("content://downloads/my_downloads")
        /** represents downloaded file above api 11  */
        const val COLUMN_LOCAL_FILENAME = "local_filename"
        /** represents downloaded file below api 11  */
        const val COLUMN_LOCAL_URI = "local_uri"

        const val METHOD_NAME_PAUSE_DOWNLOAD = "pauseDownload"
        const val METHOD_NAME_RESUME_DOWNLOAD = "resumeDownload"

        private var isInitPauseDownload = false
        private var isInitResumeDownload = false

        private var pauseDownload: Method? = null
        private var resumeDownload: Method? = null

        /**
         * whether exist pauseDownload and resumeDownload method in [DownloadManager]
         */
        val isExistPauseAndResumeMethod: Boolean
            get() {
                initPauseMethod()
                initResumeMethod()
                return pauseDownload != null && resumeDownload != null
            }

        private fun initPauseMethod() {
            if (isInitPauseDownload) {
                return
            }

            isInitPauseDownload = true
            try {
                pauseDownload = DownloadManager::class.java.getMethod(METHOD_NAME_PAUSE_DOWNLOAD, LongArray::class.java)
            } catch (e: Exception) {
                // accept all exception
                e.printStackTrace()
            }

        }

        private fun initResumeMethod() {
            if (isInitResumeDownload) {
                return
            }

            isInitResumeDownload = true
            try {
                resumeDownload = DownloadManager::class.java.getMethod(METHOD_NAME_RESUME_DOWNLOAD, LongArray::class.java)
            } catch (e: Exception) {
                // accept all exception
                e.printStackTrace()
            }
        }
    }
}
