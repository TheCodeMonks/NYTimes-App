package www.thecodemonks.techbytes.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_post_category.view.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.model.Category

class CategoryAdapter(val category: MutableList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedItem: Int = -1


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return category.size
    }

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryViewHolder, position: Int) {

        holder.itemView.apply {

            item_category_title.text = category[position].title

            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(category[position])

                    if (selectedItem == position) {
                        selectedItem = RecyclerView.NO_POSITION
                        notifyDataSetChanged()
                        return@setOnClickListener

                    }

                    selectedItem = position
                    notifyDataSetChanged()

                }
            }


            when (selectedItem) {
                position -> {
                    item_category_title.setTextColor(
                        ContextCompat.getColor(
                            item_category_title.context,
                            R.color.white
                        )
                    )

                    item_category_title.background.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.design_default_color_primary
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                }

                RecyclerView.NO_POSITION -> {
                    item_category_title.setTextColor(
                        ContextCompat.getColor(
                            item_category_title.context,
                            R.color.black
                        )
                    )

                    item_category_title.background.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.blue_smoke
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                }
                else -> {
                    item_category_title.setTextColor(
                        ContextCompat.getColor(
                            item_category_title.context,
                            R.color.black
                        )
                    )

                    item_category_title.background.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.blue_smoke
                        ), PorterDuff.Mode.SRC_ATOP
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


}