package com.qfleng.um.fragment

import android.os.Bundle
import android.os.Handler
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.qfleng.um.BaseActivity

/**
 * Created by Duke
 */

abstract class BaseFragment : Fragment() {

    fun <T : View> Fragment.bindView(@IdRes res: Int): Lazy<T> {
        return lazy { rootView.findViewById<T>(res) }
    }


    protected var handler = Handler()

    lateinit var rootView: View
    val baseActivity by lazy { activity as BaseActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = createView(inflater, container, savedInstanceState)
        return rootView
    }


    abstract fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
}
