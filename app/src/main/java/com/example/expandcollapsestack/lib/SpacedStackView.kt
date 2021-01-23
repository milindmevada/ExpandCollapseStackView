package com.example.expandcollapsestack.lib

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import com.example.expandcollapsestack.R
import kotlinx.android.synthetic.main.expanded_view.view.*
import kotlinx.android.synthetic.main.spaced_stack_view.view.*

class SpacedStackView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init {
        inflateView()
    }

    var mView: View? = null
    var mAdapter: SpacedStackViewAdapter? = null

    private fun inflateView() {
        mView = inflate(context, R.layout.spaced_stack_view, this)
    }

    fun setAdapter(adapter: SpacedStackViewAdapter) {
        mAdapter = adapter
        val size = mAdapter?.getItemCount() ?: 0
        if (size < 2 || size > 4) {
            throw Exception("View cannot have items  < 2 or > 4")
        }
        initView()
    }

    var currentIndex = 0

    private fun initView() {
        val view = mAdapter?.onBindView(this, 0)
        llStack.addView(view)
        val ctaView = mAdapter?.onBindCTA(this, 0)
        flCTA.addView(ctaView)
    }

    fun goToNextItem() {
        currentIndex++
        val size = mAdapter?.getItemCount() ?: 0
        if (size <= currentIndex) {
            return
        }
        val nextView = mAdapter?.onBindView(this, currentIndex)
        nextView?.visibility = View.INVISIBLE
        try {
            decreaseHeight(llStack.getChildAt(currentIndex - 1))
        } catch (ex: Exception) {
        }
        nextView?.let {
            llStack.addView(it)
            slideUp(nextView) {
                animatedViewSwitcher(
                    view = llStack.getChildAt(currentIndex - 1),
                    intermediateOperation = {
                        llStack.getChildAt(currentIndex - 1).tvExpanded.visibility = View.GONE
                        llStack.getChildAt(currentIndex - 1).tvCollapsed.visibility = View.VISIBLE
                    }
                )
            }
            animatedViewSwitcher(
                view = flCTA,
                intermediateOperation = {
                    flCTA.removeAllViews()
                    flCTA.addView(mAdapter?.onBindCTA(this, currentIndex))
                },
            )
        }
    }

    private fun decreaseHeight(view: View) {
        val anim = ValueAnimator.ofInt(view.height, dpToPx(context, 100))
        anim.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = `val`
            view.layoutParams = layoutParams
        }
        anim.duration = 500
        anim.start()
    }

    //TODO: Fix the issue to expand view for when middle item in tapped
    fun expandViewAt(index: Int) {
        if (currentIndex == index) {
            return
        }
        currentIndex = index
        val totalSize = mAdapter?.getItemCount() ?: 0
        for (i in index + 1 until totalSize) {
            try {
                llStack.removeViewAt(i)
                invalidate()
            } catch (ex: Exception) {
            }
        }
        increaseHeight(llStack.getChildAt(index))
        animatedViewSwitcher(
            view = flCTA,
            intermediateOperation = {
                flCTA.removeAllViews()
                flCTA.addView(mAdapter?.onBindCTA(this, currentIndex))
            },
        )
    }

    private fun increaseHeight(view: View) {
        val anim = ValueAnimator.ofInt(dpToPx(context, 100), (view.parent as View).height)
        anim.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = `val`
            view.layoutParams = layoutParams
        }
        anim.duration = 500
        anim.start()
    }

    private fun slideUp(view: View, onEnd: () -> Unit) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            clRoot.height.toFloat(),  // fromYDelta
            0F
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                onEnd.invoke()
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }
        })
    }

    private fun animatedViewSwitcher(
        view: View?,
        intermediateOperation: () -> Unit = {},
        onEnd: () -> Unit = {}
    ) {
        view?.apply {
            animate()
                .alpha(0f)
                .setDuration(250)
                .withEndAction {
                    intermediateOperation.invoke()
                    animate()
                        .alpha(1f)
                        .setDuration(250)
                        .withEndAction {
                            onEnd.invoke()
                        }
                }
        }
    }

    fun dpToPx(
        context: Context,
        dp: Int
    ): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        )
            .toInt()
    }
}