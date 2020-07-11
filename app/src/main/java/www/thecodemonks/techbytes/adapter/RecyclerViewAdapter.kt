package www.thecodemonks.techbytes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_post_article.view.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.model.Article

class RecyclerViewAdapter(val articles: MutableList<Article>) :
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post_article, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.itemView.item_article_title.text = articles[position].title
        holder.itemView.item_post_description.text = articles[position].description
        holder.itemView.item_post_author.text = ("by" + " " + articles[position].author)
        holder.itemView.item_article_image.load(articles[position].image) {
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


        // on item click
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(articles[position]) }
        }

    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // on item click listener
    private var onItemClickListener: ((Article) -> Unit)? = null
    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}