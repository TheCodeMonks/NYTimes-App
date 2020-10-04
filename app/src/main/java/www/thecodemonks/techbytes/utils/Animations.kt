/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2020 Spikey Sanju
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package www.thecodemonks.techbytes.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation


/**
 * Provides various view animations.
 *
 * Author : [https://github.com/ch8n]
 * website : [https://chetangupta.net]
 * Creation Date : 4-08-2020
 */
object Animations {

    fun fadeOutFadeIn(v: View, onFadeInStart: () -> Unit) {
        val fadeOut = ObjectAnimator
            .ofFloat(v, View.ALPHA, 1f, 0f)
            .setDuration(200)

        val fadeIn = ObjectAnimator
            .ofFloat(v, View.ALPHA, 0f, 1f)
            .setDuration(200)

        fadeIn.addListener(object : AnimationCallback() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                onFadeInStart.invoke()
            }
        })

        val animatorSet = AnimatorSet();
        animatorSet.playSequentially(
            fadeOut,
            fadeIn
        );
        animatorSet.start()

    }

    fun collapse(view: View) {
        val initialHeight = view.measuredHeight
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                } else {
                    view.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        animation.duration = 500L
        view.startAnimation(animation)
    }


    fun expand(view: View) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight = view.measuredHeight
        view.layoutParams.height = 0
        view.visibility = View.VISIBLE
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                view.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                view.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        animation.duration = 500L
        view.startAnimation(animation)
    }

    private abstract class AnimationCallback : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?) {

        }

        override fun onAnimationCancel(animation: Animator?) {

        }

        override fun onAnimationRepeat(animation: Animator?) {

        }
    }

}