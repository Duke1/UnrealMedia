package com.qfleng.um.audio.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.text.Layout
import android.text.StaticLayout
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.animation.OvershootInterpolator
import android.widget.TextView

import com.qfleng.um.audio.LrcInfo
import android.text.TextPaint
import com.qfleng.um.audio.LrcRow
import com.qfleng.um.util.dpToPx
import com.qfleng.um.util.dpToPxF


class LrcView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : TextView(context, attrs, defStyleAttr) {

    companion object {
        const val MSG_LRC_SLIDE = 1

        const val DEBUG: Boolean = false
    }

    private var width: Float = 0.toFloat()                   //歌词视图宽度
    private var height: Float = 0.toFloat()                 //歌词视图高度
    private val notCurrentPaint = Paint()     //非当前画笔对象
    private val textDefaultHeight = 65      //文本高度
    private val textMaxSize = 50f
    private val txtSize = 40f        //文本大小
    private var index = 0              //list集合下标
    private var infos: LrcInfo? = null              //歌词信息

    var textPaint: TextPaint = TextPaint()

    var lightColor: Int = Color.BLACK
    var normalColor: Int = Color.GRAY

    private var lrcScrollLineAbove: Float = 0f
    private var lrcScrollLineBelow: Float = 0f
    var lrcHeight: Int = 0 //歌词高度


    var mIsMoved: Boolean = false
    var mUserIsTouch: Boolean = false
    var mLastScrollY: Float = 0.0f
    lateinit var mVelocityTracker: VelocityTracker


    var pMatrix: Matrix = Matrix()


    internal var msgHandler: Handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_LRC_SLIDE -> {

                    smoothScrollTo()
                }
            }
        }
    }

    fun setLrcList(infos: LrcInfo) {
        this.infos = infos

    }

    init {
        //非高亮部分
        notCurrentPaint.isAntiAlias = true
        notCurrentPaint.textAlign = Paint.Align.CENTER


        isFocusable = true     //设置可对焦
        //显示歌词部分


        mVelocityTracker = VelocityTracker.obtain()

        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = dpToPxF(context, 20f)
        textPaint.isAntiAlias = true
        //currentPaint!!.textSize = textMaxSize
        textPaint.typeface = Typeface.SERIF
    }

    fun setTextColor(lightColor: Int, normalColor: Int) {
        this.lightColor = lightColor
        this.normalColor = normalColor

        postInvalidate()
    }

    /**
     * 绘画歌词
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }



        notCurrentPaint!!.textSize = txtSize
        notCurrentPaint!!.typeface = Typeface.DEFAULT

        var staticLayout: StaticLayout
        var text: String?
        var textHeight: Int = textDefaultHeight
        var lrcRow: LrcRow

        try {

            text = ""

            canvas.save()
            canvas.concat(pMatrix)

            //高亮部分
            textPaint.color = lightColor
            lrcRow = infos!!.lrcLists[index]
            canvas.save()
            staticLayout = StaticLayout(lrcRow.content, textPaint, canvas.width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
            textHeight = staticLayout.height
            canvas.translate(width / 2, height / 2 - (textHeight / 2))
            staticLayout.draw(canvas)
            lrcRow.tag = textHeight as Object
            canvas.restore()

            var centerOffsetY = staticLayout.height / 2

            textPaint.color = normalColor
            var tempY = height / 2 - centerOffsetY
            //分割线之上内容
            for (i in index - 1 downTo 0) {
                lrcRow = infos!!.lrcLists[i]
                text = lrcRow.content
                staticLayout = StaticLayout(text, textPaint, canvas.width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
                textHeight = staticLayout.height
                //向上移
                tempY = tempY - textHeight

                lrcRow.tag = textHeight as Object

                canvas.save()
                canvas.translate(width / 2, tempY /*- (textHeight / 2)*/)
                staticLayout.draw(canvas)
                canvas.restore()
            }
            tempY = height / 2 + centerOffsetY
            //分割线之下内容
            for (i in index + 1 until infos!!.lrcLists.size) {
                lrcRow = infos!!.lrcLists[i]
                text = lrcRow.content
                staticLayout = StaticLayout(text, textPaint, canvas.width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
                textHeight = staticLayout.height

                lrcRow.tag = textHeight as Object

                canvas.save()
                canvas.translate(width / 2, tempY /*- (textHeight / 2)*/)
                staticLayout.draw(canvas)
                canvas.restore()

                //往下移
                tempY = tempY + textHeight
            }

            canvas.restore()



            if (0 == lrcHeight) {
                if (null == infos) return
                for (row in infos!!.lrcLists) {
                    lrcHeight += row.tag as Int
                }
                lrcHeight += dpToPx(context, 20f)
            }

            //lrc滚动数据
            lrcScrollLineAbove = 0f
            for (i in index downTo 0) {
                lrcScrollLineAbove += infos!!.lrcLists[i].tag as Int
            }
            lrcScrollLineBelow = 0f
            for (i in index until infos!!.lrcLists.size) {
                lrcScrollLineBelow += infos!!.lrcLists[i].tag as Int
            }


        } catch (e: Exception) {
            text = ""
        }

        if (DEBUG) {
            val tp = TextPaint()
            tp.color = Color.GREEN
            tp.style = Paint.Style.FILL
            tp.textSize = dpToPxF(context, 10f)
            tp.isAntiAlias = true

            var sBuilder = StringBuilder()
            notCurrentPaint!!.color = Color.argb(210, 0, 248, 0)
            canvas.drawLine(0f, height / 2, width, height / 2, notCurrentPaint)


            var matrixArray = FloatArray(9, { 0f })
            pMatrix.getValues(matrixArray)
            sBuilder.append("trans: " + matrixArray[Matrix.MTRANS_Y])
            sBuilder.append("\r\n")

            if (null != infos && null != infos!!.lrcLists)
                sBuilder.append(String.format("size: { %d , %d }", canvas.width, textHeight * infos!!.lrcLists.size))

            staticLayout = StaticLayout(sBuilder, tp, canvas.width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
            staticLayout.draw(canvas)

            Log.e("LrcView", "staticLayout-> " + staticLayout.height)
        }

    }

    /**
     * 当view大小改变的时候调用的方法
     */

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.width = w.toFloat()
        this.height = h.toFloat()
    }

    fun setIndex(index: Int) {
        if (!mUserIsTouch) {
            this.index = index
            postInvalidate()
        }
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP -> {
            }
            else -> {
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {


        mVelocityTracker.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_CANCEL -> actionCancel(event)
            MotionEvent.ACTION_DOWN -> actionDown(event)
            MotionEvent.ACTION_MOVE -> actionMove(event)
            MotionEvent.ACTION_UP -> actionUp(event)
            else -> {
            }
        }
        invalidate()
        return true
    }

    private fun actionUp(event: MotionEvent) {
        msgHandler.sendEmptyMessageDelayed(MSG_LRC_SLIDE, 2000)
    }


    private fun actionMove(event: MotionEvent) {
        val tracker = mVelocityTracker

        val offsetY = event.y - this.mLastScrollY

        mIsMoved = true

        var matrixArray = FloatArray(9, { 0f })
        pMatrix.getValues(matrixArray)
        val dy: Float = matrixArray[Matrix.MTRANS_Y]

        if (dy + offsetY <= -lrcScrollLineBelow)
            pMatrix.setTranslate(0f, (-lrcScrollLineBelow))
        else if (dy + offsetY > lrcScrollLineAbove)
            pMatrix.setTranslate(0f, lrcScrollLineAbove)
        else
            pMatrix.postTranslate(0f, offsetY)

        this.mLastScrollY = event.y

        if (DEBUG)
            Log.e("LrcView", "MotionEvent->" + pMatrix)
    }

    private fun actionDown(event: MotionEvent) {
        msgHandler.removeMessages(MSG_LRC_SLIDE)

        mUserIsTouch = true
        mLastScrollY = event.y
    }

    private fun actionCancel(event: MotionEvent) {
        actionUp(event)
    }


    private fun smoothScrollTo() {
        var matrixArray = FloatArray(9, { 0f })
        pMatrix.getValues(matrixArray)

        val dy: Float = matrixArray[Matrix.MTRANS_Y]

        val animator = ValueAnimator.ofFloat(dy, 0f)
        animator.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
            val y = animation.animatedValue as Float
            pMatrix.setTranslate(0f, y)
            invalidate()
        })

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                pMatrix.reset()

                mUserIsTouch = false
                invalidate()
            }
        })
        animator.duration = 640
        animator.interpolator = OvershootInterpolator(0.5f)

        animator.start()
    }
}