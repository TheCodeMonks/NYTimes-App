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

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecorator(space: Int) : RecyclerView.ItemDecoration() {

    private val space: Int = space.dp

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val orientation = (parent.layoutManager as? LinearLayoutManager)?.orientation
            ?: LinearLayoutManager.VERTICAL
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            when (parent.getChildLayoutPosition(view)) {
                0 -> {
                    outRect.left = space
                    outRect.right = space
                }
                else -> outRect.right = space
            }
        } else {
            when (parent.getChildLayoutPosition(view)) {
                0 -> {
                    outRect.top = space
                    outRect.left = space
                    outRect.right = space
                    outRect.bottom = space
                }
                else -> {
                    outRect.left = space
                    outRect.right = space
                    outRect.bottom = space
                }
            }
        }
    }
}

val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
