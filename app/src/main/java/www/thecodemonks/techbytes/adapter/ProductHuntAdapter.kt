package www.thecodemonks.techbytes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_post_article.view.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.model.ProductHunt

class ProductHuntAdapter(val products: MutableList<ProductHunt>) :
    RecyclerView.Adapter<ProductHuntAdapter.ProductVH>() {

    inner class ProductVH(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        return ProductVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_post_article,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductVH, position: Int) {

        val item = products[position]
        holder.itemView.apply {

            item_article_title.text = item.title
            item_post_description.text = item.tag
            item_post_author.text = item.votes
            item_article_image.load(item.image) {
                crossfade(true)
                crossfade(200)
                transformations(
                    RoundedCornersTransformation(
                        8f,
                        8f,
                        8f,
                        8f
                    )
                )


            }

            // on item click
            setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }

    }


    // on item click listener
    private var onItemClickListener: ((ProductHunt) -> Unit)? = null
    fun setOnItemClickListener(listener: (ProductHunt) -> Unit) {
        onItemClickListener = listener
    }

}