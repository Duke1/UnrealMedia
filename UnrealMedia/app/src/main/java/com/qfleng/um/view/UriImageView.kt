package com.qfleng.um.view

import android.content.Context
import android.util.AttributeSet

import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView


/**

 * ps:roundingBorderPadding解决白边问题
 */

class UriImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : SimpleDraweeView(context, attrs, defStyleAttr) {


    fun setCornersRadius(radius: Float) {
        var roundingParams: RoundingParams? = hierarchy.roundingParams
        if (null == roundingParams) {
            roundingParams = RoundingParams.fromCornersRadius(radius)
        } else
            roundingParams.setCornersRadius(radius)

        hierarchy.roundingParams = roundingParams
    }

    fun setBorderWidth(width: Float) {
        val roundingParams = hierarchy.roundingParams ?: return
        roundingParams.borderWidth = width
        hierarchy.roundingParams = roundingParams
    }

    fun setBorderPadding(padding: Float) {
        val roundingParams = hierarchy.roundingParams ?: return
        roundingParams.padding = padding
        hierarchy.roundingParams = roundingParams
    }

    fun setBorderColor(color: Int) {
        val roundingParams = hierarchy.roundingParams ?: return
        roundingParams.borderColor = color
        hierarchy.roundingParams = roundingParams
    }

}
