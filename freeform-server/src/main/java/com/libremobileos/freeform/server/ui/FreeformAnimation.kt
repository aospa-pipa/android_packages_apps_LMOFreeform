package com.libremobileos.freeform.server.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.PathInterpolator

object FreeformAnimation {
    private const val TRANSITION_SCALE = 0.88f
    private val moveInterpolator = DecelerateInterpolator(1.5f)
    private val transitionInterpolator = PathInterpolator(0.2f, 0f, 0f, 1f)

    fun moveInScreenAnimator(start: Int, end: Int, dur: Long, moveX: Boolean, window: FreeformWindow) {
        AnimatorSet().apply {
            play(
                ValueAnimator.ofInt(start, end).apply {
                    interpolator = moveInterpolator
                    addUpdateListener {
                        window.windowParams.apply {
                            if (moveX) x = it.animatedValue as Int
                            else y = it.animatedValue as Int
                        }
                        window.updateWindowLayout()
                    }
                }
            )
            duration = dur
            start()
        }
    }

    fun scaleOut(view: View, duration: Long, onEnd: () -> Unit) {
        view.animate().cancel()
        view.animate()
            .scaleX(TRANSITION_SCALE)
            .scaleY(TRANSITION_SCALE)
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(transitionInterpolator)
            .withEndAction(Runnable { onEnd() })
            .start()
    }

    fun scaleIn(view: View, duration: Long, onEnd: () -> Unit = {}) {
        view.animate().cancel()
        view.scaleX = TRANSITION_SCALE
        view.scaleY = TRANSITION_SCALE
        view.alpha = 0f
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(transitionInterpolator)
            .withEndAction(Runnable { onEnd() })
            .start()
    }

    fun toFullScreen(window: FreeformWindow, dur: Long, listener: Animator.AnimatorListener) {
        AnimatorSet().apply {
            play(
                ValueAnimator.ofInt(window.windowParams.x, 0).apply {
                    addUpdateListener {
                        window.windowParams.x = it.animatedValue as Int
                        window.updateWindowLayout()
                    }
                }
            )
            duration = dur
            start()
        }
        AnimatorSet().apply {
            play(
                ValueAnimator.ofInt(window.windowParams.y, 0).apply {
                    addUpdateListener {
                        window.windowParams.y = it.animatedValue as Int
                        window.updateWindowLayout()
                    }
                }
            )
            duration = dur
            start()
        }
        AnimatorSet().apply {
            play(
                ValueAnimator.ofInt(window.freeformConfig.width, window.defaultDisplayWidth).apply {
                    addUpdateListener {
                        window.freeformRootView.layoutParams = window.freeformRootView.layoutParams.apply {
                            width = it.animatedValue as Int
                        }
                    }
                }
            )
            duration = dur
            start()
        }
        AnimatorSet().apply {
            play(
                ValueAnimator.ofInt(window.freeformConfig.height, window.defaultDisplayHeight).apply {
                    addUpdateListener {
                        window.freeformRootView.layoutParams = window.freeformRootView.layoutParams.apply {
                            height = it.animatedValue as Int
                        }
                    }
                }
            )
            duration = dur
            start()
        }.addListener(listener)
    }
}
