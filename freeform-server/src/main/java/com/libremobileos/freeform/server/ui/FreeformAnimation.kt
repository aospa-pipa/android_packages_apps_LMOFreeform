package com.libremobileos.freeform.server.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator

object FreeformAnimation {
    fun moveInScreenAnimator(start: Int, end: Int, dur: Long, moveX: Boolean, window: FreeformWindow) {
        AnimatorSet().apply {
            play(
                ValueAnimator.ofInt(start, end).apply {
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
