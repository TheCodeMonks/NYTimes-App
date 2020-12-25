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
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.databinding.ItemPostCategoryBinding
import www.thecodemonks.techbytes.model.Category

class CategoryAdapter(private val category: MutableList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedItem: Int = -1

    inner class CategoryViewHolder(val binding: ItemPostCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val binding =
            ItemPostCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return category.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.apply {

            itemCategoryTitle.text = category[position].title

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
                    itemCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            itemCategoryTitle.context,
                            R.color.white
                        )
                    )

                    MyDrawableCompat.setColorFilter(
                        itemCategoryTitle.background,
                        ContextCompat.getColor(root.context, R.color.design_default_color_primary)
                    )
                }
                else -> {
                    itemCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            itemCategoryTitle.context,
                            R.color.categoryText
                        )
                    )
                    MyDrawableCompat.setColorFilter(
                        itemCategoryTitle.background,
                        ContextCompat.getColor(root.context, R.color.blue_smoke)
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
