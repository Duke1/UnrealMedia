package com.qfleng.um.view

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View


class RecordView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr), View.OnClickListener {


    private val mPaint = Paint()
    private var mCenterX: Int = 0
    private var mCenterY: Int = 0

    private var mCircleRadius: Float = 0.toFloat()                  // 圆的半径
    private var mDifference: Float = 0.toFloat()        // 圆形的控制点与数据点的差值
    private var dc: Float = 0.toFloat()

    private val mData = FloatArray(8)               // 顺时针记录绘制圆形的四个数据点
    private val mCtrl = FloatArray(16)              // 顺时针记录绘制圆形的八个控制点


    private var _width: Int = 0
    private var _height: Int = 0

    @Volatile
    private var mRecording: Boolean = false

    init {


        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 8f
        mPaint.style = Paint.Style.STROKE
        mPaint.textSize = 60f
        mPaint.isAntiAlias = true


        initPoint()


        setOnClickListener(this)
    }

    private fun initCircle() {

        mCircleRadius = _width / 5f
        mDifference = mCircleRadius * C        // 圆形的控制点与数据点的差值
        dc = mCircleRadius - mDifference
    }

    private fun initPoint() {
        // 初始化数据点

        mData[0] = 0f
        mData[1] = mCircleRadius

        mData[2] = mCircleRadius
        mData[3] = 0f

        mData[4] = 0f
        mData[5] = -mCircleRadius

        mData[6] = -mCircleRadius
        mData[7] = 0f

        // 初始化控制点

        mCtrl[0] = mData[0] + mDifference
        mCtrl[1] = mData[1]

        mCtrl[2] = mData[2]
        mCtrl[3] = mData[3] + mDifference

        mCtrl[4] = mData[2]
        mCtrl[5] = mData[3] - mDifference

        mCtrl[6] = mData[4] + mDifference
        mCtrl[7] = mData[5]

        mCtrl[8] = mData[4] - mDifference
        mCtrl[9] = mData[5]

        mCtrl[10] = mData[6]
        mCtrl[11] = mData[7] - mDifference

        mCtrl[12] = mData[6]
        mCtrl[13] = mData[7] + mDifference

        mCtrl[14] = mData[0] - mDifference
        mCtrl[15] = mData[1]
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this._width = w
        this._height = h

        initCircle()
        initPoint()

        mCenterX = w / 2
        mCenterY = h / 2

        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCoordinateSystem(canvas)       // 绘制坐标系

        canvas.translate(mCenterX.toFloat(), mCenterY.toFloat()) // 将坐标系移动到画布中央
        canvas.scale(1f, -1f)                 // 翻转Y轴

        drawAuxiliaryLine(canvas)


        // 绘制贝塞尔曲线
        mPaint.color = Color.RED
        mPaint.strokeWidth = 8f

        val path = Path()
        path.moveTo(mData[0], mData[1])

        path.cubicTo(mCtrl[0], mCtrl[1], mCtrl[2], mCtrl[3], mData[2], mData[3])
        path.cubicTo(mCtrl[4], mCtrl[5], mCtrl[6], mCtrl[7], mData[4], mData[5])
        path.cubicTo(mCtrl[8], mCtrl[9], mCtrl[10], mCtrl[11], mData[6], mData[7])
        path.cubicTo(mCtrl[12], mCtrl[13], mCtrl[14], mCtrl[15], mData[0], mData[1])

        canvas.drawPath(path, mPaint)

    }

    // 绘制辅助线
    private fun drawAuxiliaryLine(canvas: Canvas) {
        // 绘制数据点和控制点
        mPaint.color = Color.GRAY
        mPaint.strokeWidth = 20f

        run {
            var i = 0
            while (i < 8) {
                canvas.drawPoint(mData[i], mData[i + 1], mPaint)
                i += 2
            }
        }

        run {
            var i = 0
            while (i < 16) {
                canvas.drawPoint(mCtrl[i], mCtrl[i + 1], mPaint)
                i += 2
            }
        }


        // 绘制辅助线
        mPaint.strokeWidth = 4f

        var i = 2
        var j = 2
        while (i < 8) {
            canvas.drawLine(mData[i], mData[i + 1], mCtrl[j], mCtrl[j + 1], mPaint)
            canvas.drawLine(mData[i], mData[i + 1], mCtrl[j + 2], mCtrl[j + 3], mPaint)
            i += 2
            j += 4
        }
        canvas.drawLine(mData[0], mData[1], mCtrl[0], mCtrl[1], mPaint)
        canvas.drawLine(mData[0], mData[1], mCtrl[14], mCtrl[15], mPaint)
    }

    // 绘制坐标系
    private fun drawCoordinateSystem(canvas: Canvas) {
        canvas.save()                      // 绘制做坐标系

        canvas.translate(mCenterX.toFloat(), mCenterY.toFloat()) // 将坐标系移动到画布中央
        canvas.scale(1f, -1f)                 // 翻转Y轴

        val fuzhuPaint = Paint()
        fuzhuPaint.color = Color.parseColor("#3304EF72")
        fuzhuPaint.strokeWidth = 5f
        fuzhuPaint.style = Paint.Style.STROKE

        canvas.drawLine(0f, (-_width).toFloat(), 0f, _width.toFloat(), fuzhuPaint)
        canvas.drawLine((-_height).toFloat(), 0f, _height.toFloat(), 0f, fuzhuPaint)

        canvas.restore()
    }

    @Synchronized
    fun record() {
        mRecording = true
        animat(false)
    }

    private fun animat(reverse: Boolean) {
        val valueAnimator = ValueAnimator.ofObject(TypeEvaluator<Float> { fraction, startValue, endValue ->
            Log.e("", "evaluate===$fraction")
            fraction * endValue!!
        }, 0f, dc)
        valueAnimator.addUpdateListener { animation ->
            val dcPiece = animation.animatedValue as Float
            mCtrl[0] = mData[0] + mDifference + dcPiece

            mCtrl[3] = mData[3] + mDifference + dcPiece

            mCtrl[5] = mData[3] - mDifference - dcPiece

            mCtrl[6] = mData[4] + mDifference + dcPiece

            mCtrl[8] = mData[4] - mDifference - dcPiece

            mCtrl[11] = mData[7] - mDifference - dcPiece

            mCtrl[13] = mData[7] + mDifference + dcPiece

            mCtrl[14] = mData[0] - mDifference - dcPiece

            postInvalidate()
        }
        valueAnimator.duration = 300

        if (reverse)
            valueAnimator.reverse()
        else
            valueAnimator.start()
    }

    @Synchronized
    fun stop() {
        mRecording = false
        animat(true)
    }

    override fun onClick(v: View) {
        if (mRecording)
            stop()
        else
            record()
    }

    companion object {


        private val C = 0.551915024494f     // 一个常量，用来计算绘制圆形贝塞尔曲线控制点的位置
    }
}
