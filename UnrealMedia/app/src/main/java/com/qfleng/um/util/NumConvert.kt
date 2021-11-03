package com.qfleng.um.util

import android.content.Context
import android.util.TypedValue

/**
 * Created by Duke
 */


fun dpToPx(ctx: Context, value: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, ctx.resources.displayMetrics).toInt()
}

fun dpToPxF(ctx: Context, value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, ctx.resources.displayMetrics)
}