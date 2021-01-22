package com.example.expandcollapsestack

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.expanded_view.view.*


class MainActivity : AppCompatActivity() {

    private lateinit var allViews: ArrayList<View>
    var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        allViews = arrayListOf(flCreditAmount, flEMISelection, flBankSelection, flLast)
        slideUp(allViews[currentIndex]) {}
        btnCTA.text = "Go to ${currentIndex + 1}"
        allViews[currentIndex].tvExpanded.visibility = View.VISIBLE

        btnCTA.setOnClickListener {
            currentIndex++
            if (allViews.size <= currentIndex) {
                return@setOnClickListener
            }
            if (!allViews[currentIndex].isVisible) {
                try {
                    decreaseHeight(allViews[currentIndex - 1])
                } catch (ex: Exception) {
                }
                slideUp(allViews[currentIndex]) {
                    animatedViewSwitcher(
                        view = allViews[currentIndex - 1],
                        intermediateOperation = {
                            allViews[currentIndex - 1].tvExpanded.visibility = View.GONE
                            allViews[currentIndex - 1].tvCollapsed.visibility = View.VISIBLE
                        }
                    )
                }
                animatedViewSwitcher(
                    view = btnCTA,
                    intermediateOperation = {
                        btnCTA.text = "CTA Go To ${currentIndex + 1}"
                    },
                )
                return@setOnClickListener
            }
        }
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

    private fun decreaseHeight(view: View) {
        val anim = ValueAnimator.ofInt(view.height, dpToPx(this, 100))
        anim.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams: ViewGroup.LayoutParams = view.layoutParams
            layoutParams.height = `val`
            view.layoutParams = layoutParams
        }
        anim.duration = 500
        anim.start()
    }

    private fun animatedViewSwitcher(
        view: View,
        intermediateOperation: () -> Unit = {},
        onEnd: () -> Unit = {}
    ) {
        view.apply {
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