package com.snail.commons.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.snail.commons.utils.UiUtils

/**
 * Created by zengfs on 2016/1/23.
 * fragment基类
 */
abstract class BaseFragment : Fragment() {
    protected var rootView: View? = null

    protected abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null)
            rootView = inflater.inflate(layoutId, container, false)
        UiUtils.removeFromContainer(rootView!!)
        return rootView
    }
}
