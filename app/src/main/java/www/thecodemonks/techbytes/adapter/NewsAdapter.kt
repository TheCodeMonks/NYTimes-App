package www.thecodemonks.techbytes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_post_article.view.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.model.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsVH>() {


    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.source == newItem.source
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsVH {

        return NewsVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_post_article,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NewsVH, position: Int) {

        val item = differ.currentList[position]
        holder.itemView.apply {

            if (item.title.isNullOrBlank() || item.description.isNullOrBlank() || item.image.isNullOrBlank()) {

                item_article_title.visibility = View.GONE
                item_post_description.visibility = View.GONE
                item_post_author.visibility = View.GONE
                item_article_image.visibility = View.GONE

            } else {
                item_article_title.text = item.title
                item_post_description.text = item.description
                item_post_author.text = item.author.toString().ifBlank { "Unknown" }
                item_article_image.load(item.image) {
                    crossfade(true)
                    crossfade(200)
                    transformations(
                        RoundedCornersTransformation(
                            12f,
                            12f,
                            12f,
                            12f
                        )
                    )
                }
            }

            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }

        }

    }

    inner class NewsVH(itemView: View) : RecyclerView.ViewHolder(itemView)


    // on item click listener
    private var onItemClickListener: ((Article) -> Unit)? = null
    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

}