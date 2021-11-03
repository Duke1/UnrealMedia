package com.qfleng.um.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

import com.qfleng.um.util.dpToPx


/**
 * Created by Duke .
 */
class RecyclerListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    private val mLayoutManager = LinearLayoutManager(context)


    init {

        setHasFixedSize(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager = mLayoutManager
    }

    override fun getLayoutManager(): LinearLayoutManager? {
        return mLayoutManager
    }

    class SimpleListItemDecoration(protected var context: Context, span: Float, color: Int = Color.BLACK) : RecyclerView.ItemDecoration() {
        protected var mPaint: Paint

        protected var mSpanSize = 0
        protected var enableSpanLine = false
        protected var spanMarginLeft: Int = 0
        protected var spanMarginTop: Int = 0
        protected var spanMarginRight: Int = 0
        protected var spanMarginBottom: Int = 0

        protected var place = PLACE_MIDDLE

        private var mode = LinearLayoutManager.VERTICAL


        init {
            mSpanSize = dpToPx(context, span)

            mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            mPaint.color = color
            mPaint.style = Paint.Style.FILL
            mPaint.isAntiAlias = true
        }

        override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            if (enableSpanLine)
                drawVertical(canvas, parent)
        }

        /**
         * @param left   dp
         * @param top    dp
         * @param right  dp
         * @param bottom dp
         * @return
         */
        fun spanLineMargin(left: Float, top: Float, right: Float, bottom: Float): SimpleListItemDecoration {
            this.spanMarginLeft = dpToPx(context, left)
            this.spanMarginRight = dpToPx(context, right)
            this.spanMarginTop = dpToPx(context, top)
            this.spanMarginBottom = dpToPx(context, bottom)

            return this
        }

        fun enableSpanLine(): SimpleListItemDecoration {
            enableSpanLine = true
            return this
        }

        /**
         * @param place PLACE_TOP|PLACE_TOP|PLACE_TOP
         */
        fun spanEnablePlace(place: Int): SimpleListItemDecoration {
            this.place = place
            return this
        }

        /**
         * @param mode LinearLayoutManager.HORIZONTAL or LinearLayoutManager.VERTICAL
         * @return
         */
        fun setMode(mode: Int): SimpleListItemDecoration {
            this.mode = mode
            return this
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            if (LinearLayoutManager.HORIZONTAL == mode) {
                getHorizontalItemOffsets(outRect, view, parent, state)
            } else {
                getVerticalItemOffsets(outRect, view, parent, state)
            }
        }

        fun getVerticalItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            val position = parent.getChildAdapterPosition(view)

            if (0 == position) {
                outRect.set(0, if (PLACE_TOP == place and PLACE_TOP) mSpanSize else 0, 0, 0)
            } else if (position == parent.adapter!!.itemCount - 1) {
                outRect.set(0, if (PLACE_MIDDLE == place and PLACE_MIDDLE) mSpanSize else 0, 0, if (PLACE_BOTTOM == place and PLACE_BOTTOM) mSpanSize else 0)
            } else {
                outRect.set(0, if (PLACE_MIDDLE == place and PLACE_MIDDLE) mSpanSize else 0, 0, 0)
            }
        }

        fun getHorizontalItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            val position = parent.getChildAdapterPosition(view)

            if (0 == position) {
                outRect.set(if (PLACE_LEFT == place and PLACE_LEFT) mSpanSize else 0, 0, 0, 0)
            } else if (position == parent.adapter!!.itemCount - 1) {
                outRect.set(if (PLACE_MIDDLE == place and PLACE_MIDDLE) mSpanSize else 0, 0, if (PLACE_RIGHT == place and PLACE_RIGHT) mSpanSize else 0, 0)
            } else {
                outRect.set(if (PLACE_MIDDLE == place and PLACE_MIDDLE) mSpanSize else 0, 0, 0, 0)
            }
        }

        protected fun drawVertical(canvas: Canvas, parent: RecyclerView) {
            val left = parent.paddingLeft + spanMarginLeft
            val right = parent.measuredWidth - parent.paddingRight + spanMarginRight
            val childSize = parent.childCount
            for (i in 0 until childSize) {
                val child = parent.getChildAt(i)
                val layoutParams = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + layoutParams.bottomMargin + spanMarginTop
                val bottom = top + mSpanSize + spanMarginBottom
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
            }
        }
    }


    class SimpleGridItemDecoration
    /**
     * @param ctx
     * @param span      dip
     * @param spanCount
     */
    (ctx: Context, span: Float, internal var mSpanCount: Int) : RecyclerView.ItemDecoration() {

        internal var mSpanSize = 0


        init {
            mSpanSize = dpToPx(ctx, span)
        }

        override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            val position = parent.getChildAdapterPosition(view)
            val column = position % mSpanCount//每行中的position

            outRect.left = column * mSpanSize / mSpanCount // column * ((1f / spanCount) * spacing)
            outRect.right = mSpanSize - (column + 1) * mSpanSize / mSpanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)

            if (position >= mSpanCount) {
                outRect.top = mSpanSize
            }
        }


    }

    companion object {
        val PLACE_TOP = 0x1
        val PLACE_MIDDLE = PLACE_TOP shl 1
        val PLACE_BOTTOM = PLACE_TOP shl 2
        val PLACE_LEFT = PLACE_TOP shl 3
        val PLACE_RIGHT = PLACE_TOP shl 4
    }

}
