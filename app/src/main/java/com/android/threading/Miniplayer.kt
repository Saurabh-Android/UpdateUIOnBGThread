package com.android.threading

import android.content.Context
import android.util.AttributeSet
import android.view.View
import kotlin.jvm.JvmOverloads
import android.view.ViewGroup
import androidx.customview.widget.ViewDragHelper
import androidx.core.view.ViewCompat
import android.view.View.MeasureSpec

class Miniplayer @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {
    private val mDragHelper: ViewDragHelper
    private var mHeaderView: View? = null
    private var mDescView: View? = null
    private var mDragRange = 0
    private var mTop = 0
    private var mDragOffset = 0f
    override fun onFinishInflate() {
        super.onFinishInflate()
        mHeaderView = findViewById(R.id.video_layout)
        mDescView = findViewById(R.id.desc)
    }

    fun maximize() {
        smoothSlideTo(0f)
    }

    fun minimize() {
        smoothSlideTo(1f)
    }

    fun smoothSlideTo(slideOffset: Float): Boolean {
        val topBound = paddingTop
        val y = (topBound + slideOffset * mDragRange).toInt()
        if (mDragHelper.smoothSlideViewTo(mHeaderView!!, mHeaderView!!.left, y)) {
            ViewCompat.postInvalidateOnAnimation(this)
            return true
        }
        return false
    }

    private inner class DragHelperCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child === mHeaderView
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            mTop = top
            mDragOffset = top.toFloat() / mDragRange
            mHeaderView!!.pivotX = mHeaderView!!.width.toFloat()
            mHeaderView!!.pivotY = mHeaderView!!.height.toFloat()
            mHeaderView!!.scaleX = 1 - mDragOffset / 2
            mHeaderView!!.scaleY = 1 - mDragOffset / 2
            mDescView!!.alpha = 1 - mDragOffset
            requestLayout()
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            var top = paddingTop
            if (yvel > 0 || yvel == 0f && mDragOffset > 0.5f) {
                top += mDragRange
            }
            mDragHelper.settleCapturedViewAt(releasedChild.left, top)
            invalidate()
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return mDragRange
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val topBound = paddingTop
            val bottomBound = height - mHeaderView!!.height - mHeaderView!!.paddingBottom
            return Math.min(Math.max(top, topBound), bottomBound)
        }
    }

    override fun computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        val maxHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(
            resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
            resolveSizeAndState(maxHeight, heightMeasureSpec, 0)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mDragRange = height - mHeaderView!!.height
        mHeaderView!!.layout(0, mTop, r, mTop + mHeaderView!!.measuredHeight)
        mDescView!!.layout(0, mTop + mHeaderView!!.measuredHeight, r, mTop + b)
    }

    init {
        mDragHelper = ViewDragHelper.create(this, 2f, DragHelperCallback())
    }
}