package com.snail.java.network.callback

import com.snail.java.network.TaskInfo

/**
 *
 *
 * date: 2019/2/28 20:26
 * author: zengfansheng
 */
interface MultiTaskListener<T : TaskInfo> : TaskListener<T>, MultiProgressListener