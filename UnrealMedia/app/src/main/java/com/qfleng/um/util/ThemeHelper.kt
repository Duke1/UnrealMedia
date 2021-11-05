package com.qfleng.um.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

/**
 * 主题相关
 */
object ThemeHelper {


    val isLollipop: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP


    fun getStatusBarHeight(ctx: Context): Int {
        val resourceId = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")

        return if (resourceId > 0) ctx.resources.getDimensionPixelSize(resourceId) else 0
    }

    fun fitsSystemWindows(view: View) {
        val lp: ViewGroup.LayoutParams = view.layoutParams as ViewGroup.LayoutParams

        if (lp is ViewGroup.MarginLayoutParams) {
            lp.topMargin = getStatusBarHeight(view.context)
            view.layoutParams = lp
        }
    }

    fun setTranslucentStatusBar(activity: Activity) {
        if (isLollipop) {
            //透明状态栏
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //透明导航栏
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    fun setStatusBarColor(activity: Activity, statusColor: Int) {
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//暂时只是M以上，不兼容LOLLIPOP
        //            Window window = activity.getWindow();
        //            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        //            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //
        //            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        //            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //            //设置状态栏颜色
        //            window.setStatusBarColor(statusColor);
        //
        //            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        //            View mChildView = mContentView.getChildAt(0);
        //            if (mChildView != null) {
        //                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
        //                ViewCompat.setFitsSystemWindows(mChildView, true);
        //            }
        //        }

        if (isLollipop) {
            val window = activity.window
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            //            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏颜色
            window.statusBarColor = statusColor

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }

}