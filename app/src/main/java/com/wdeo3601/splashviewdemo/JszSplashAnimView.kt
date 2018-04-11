package com.wdeo3601.splashviewdemo

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView


/**
 * Created by wendong on 2018/4/10 0010.
 * Email:       wdeo3601@163.com
 * Description:
 */
class JszSplashAnimView : ImageView {

    var mWidth = 0
    var mHeight = 0

    private lateinit var mPaint: Paint
    private lateinit var mSplashBgBitmap: Bitmap
    private lateinit var mSplashFgBitmap: Bitmap
    private lateinit var mBgSrcRect: Rect
    private lateinit var mBgDestRect: Rect

    private lateinit var mFgSrcRect: Rect
    private lateinit var mFgDestRect: Rect

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mPaint = Paint()
        //获取图片资源
        mSplashBgBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_splash_bg)
        mSplashFgBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_splash_fg)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        Log.e("-------------", "onMeasure:${mWidth}--${mHeight}")
    }

    override fun onDraw(canvas: Canvas?) {
        //画背景图
        if (!::mBgSrcRect.isInitialized)
            mBgSrcRect = Rect(0, 0, mSplashBgBitmap.width, mHeight)
        if (!::mBgDestRect.isInitialized)
            mBgDestRect = Rect(0, 0, mWidth, mHeight)
        canvas!!.drawBitmap(mSplashBgBitmap, mBgSrcRect, mBgDestRect, mPaint)

        //画前景图
        if (!::mFgSrcRect.isInitialized)
            mFgSrcRect = Rect(0, 0, mSplashFgBitmap.width, mSplashFgBitmap.height)
        if (!::mFgDestRect.isInitialized) {
            val xSpace = (mWidth - mSplashFgBitmap.width) / 2
            val yTopSpace = (143 * resources.displayMetrics.density + 0.5f).toInt()
            mFgDestRect = Rect(xSpace
                    , yTopSpace
                    , mSplashFgBitmap.width + xSpace
                    , mSplashFgBitmap.height + yTopSpace)
        }
        canvas.drawBitmap(mSplashFgBitmap, mFgSrcRect, mFgDestRect, mPaint)
    }

    fun startAnimation() {
        // 使用ValueAnimator创建一个过程
        Log.e("-------------", "startAnimation")

        //当view布局完成后（获取宽高再执行动画）
        post {
            var endFloat = mSplashBgBitmap.height - mHeight
            if (endFloat <= 0)
                endFloat = 0
            val valueAnimator = ValueAnimator.ofFloat(0f, endFloat.toFloat())
            valueAnimator.duration = 1000
            valueAnimator.interpolator = AccelerateDecelerateInterpolator()
            valueAnimator.addUpdateListener { animator ->
                // 不断重新计算上下左右位置
                val fraction = animator.animatedValue as Float
                val currentTop = fraction.toInt()
                Log.e("-------------", "currentTop" + currentTop.toString() + "\nheight" + height)
                if (!::mBgSrcRect.isInitialized) {
                    mBgSrcRect = Rect(0, 0, mSplashBgBitmap.width, mHeight)
                }
                mBgSrcRect.top = currentTop
                mBgSrcRect.bottom = mHeight + currentTop
                // 重绘
                postInvalidate()
            }
            valueAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (::onAnimEnd.isInitialized) {
                        onAnimEnd.invoke()
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            valueAnimator.start()
        }

    }

    lateinit var onAnimEnd: () -> Unit

}