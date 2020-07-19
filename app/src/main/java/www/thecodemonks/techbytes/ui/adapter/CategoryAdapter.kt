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

package www.thecodemonks.techbytes.ui.adapter

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_post_category.view.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.model.Category


class CategoryAdapter(private val category: MutableList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedItem: Int = -1

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return category.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.itemView.apply {

            item_category_title.text = category[position].title

            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(category[position])

                    if (selectedItem == position) {
                        notifyItemChanged(position)
                        return@setOnClickListener

                    }

                    selectedItem = position
                    notifyDataSetChanged()

                }
            }

            // if item selected then change it's state color
            when (selectedItem) {
                position -> {
                    item_category_title.setTextColor(
                        ContextCompat.getColor(
                            item_category_title.context,
                            R.color.white
                        )
                    )

                    MyDrawableCompat.setColorFilter(
                        item_category_title.background,
                        ContextCompat.getColor(context, R.color.design_default_color_primary)
                    )

                }
                else -> {
                    item_category_title.setTextColor(
                        ContextCompat.getColor(
                            item_category_title.context,
                            R.color.black
                        )
                    )
                    MyDrawableCompat.setColorFilter(
                        item_category_title.background,
                        ContextCompat.getColor(context, R.color.blue_smoke)
                    )

                }
            }

        }

    }


    // on item click listener
    private var onItemClickListener: ((Category) -> Unit)? = null
    fun setOnItemClickListener(listener: (Category) -> Unit) {
        onItemClickListener = listener
    }


    // check if android version is greater than Q -> color filter else use set color filter
    object MyDrawableCompat {
        fun setColorFilter(drawable: Drawable, color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
            } else {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

}