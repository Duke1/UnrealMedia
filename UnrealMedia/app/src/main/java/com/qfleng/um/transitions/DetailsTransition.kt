package com.qfleng.um.transitions

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.util.AttributeSet


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class DetailsTransition @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : TransitionSet(context,attrs) {


    init  {
        ordering = ORDERING_TOGETHER
        addTransition(ChangeBounds()).addTransition(ChangeTransform()).addTransition(ChangeImageTransform())
    }
}
