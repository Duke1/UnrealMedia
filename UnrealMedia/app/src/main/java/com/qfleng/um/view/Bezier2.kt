package com.qfleng.um.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class Bezier2 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val mPaint: Paint
    private var centerX: Int = 0
    private var centerY: Int = 0

    private val start: PointF
    private val end: PointF
    private val control1: PointF
    private val control2: PointF
    private var mode = true


    internal var rectF1 = RectF(30f, 0f, 130f, 30f)
    internal var rectF2 = RectF(140f, 0f, 240f, 30f)

    init {

        mPaint = Paint()
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 8f
        mPaint.style = Paint.Style.STROKE
        mPaint.textSize = 60f

        start = PointF(0f, 0f)
        end = PointF(0f, 0f)
        control1 = PointF(0f, 0f)
        control2 = PointF(0f, 0f)

    }

    fun setMode(mode: Boolean) {
        this.mode = mode
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2
        centerY = h / 2

        // 初始化数据点和控制点的位置
        start.x = (centerX - 200).toFloat()
        start.y = centerY.toFloat()
        end.x = (centerX + 200).toFloat()
        end.y = centerY.toFloat()
        control1.x = centerX.toFloat()
        control1.y = (centerY - 100).toFloat()
        control2.x = centerX.toFloat()
        control2.y = (centerY - 100).toFloat()

    }

    private fun inPoint(pointF: PointF, x: Float, y: Float): Boolean {
        return RectF(pointF.x - 2, pointF.y - 2, pointF.x + 2, pointF.y + 2).contains(x, y)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        // 根据触摸位置更新控制点，并提示重绘
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (rectF1.contains(event.x, event.y))
                setMode(true)
            else if (rectF2.contains(event.x, event.y))
                setMode(false)
            MotionEvent.ACTION_MOVE -> if (!rectF1.contains(event.x, event.y) && !rectF2.contains(event.x, event.y)) {
                if (mode) {
                    control1.x = event.x
                    control1.y = event.y
                } else {
                    control2.x = event.x
                    control2.y = event.y
                }
            }
        }
        invalidate()
        return true
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        drawPath(canvas)
    }

    private fun drawPath(canvas: Canvas) {

        mPaint.color = Color.parseColor("#33ff11ff")
        canvas.drawRect(rectF1, mPaint)
        canvas.drawRect(rectF2, mPaint)

        //drawCoordinateSystem(canvas);

        // 绘制数据点和控制点
        mPaint.color = Color.GRAY
        mPaint.strokeWidth = 20f
        canvas.drawPoint(start.x, start.y, mPaint)
        canvas.drawPoint(end.x, end.y, mPaint)
        canvas.drawPoint(control1.x, control1.y, mPaint)
        canvas.drawPoint(control2.x, control2.y, mPaint)

        // 绘制辅助线
        mPaint.strokeWidth = 4f
        canvas.drawLine(start.x, start.y, control1.x, control1.y, mPaint)
        canvas.drawLine(control1.x, control1.y, control2.x, control2.y, mPaint)
        canvas.drawLine(control2.x, control2.y, end.x, end.y, mPaint)

        // 绘制贝塞尔曲线
        mPaint.color = Color.RED
        mPaint.strokeWidth = 8f

        val path = Path()

        path.moveTo(start.x, start.y)
        path.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)

        canvas.drawPath(path, mPaint)
    }
}