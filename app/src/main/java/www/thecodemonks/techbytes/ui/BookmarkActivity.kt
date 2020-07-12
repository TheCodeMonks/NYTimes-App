package www.thecodemonks.techbytes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_bookmark.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.adapter.NewsAdapter
import www.thecodemonks.techbytes.db.ArticleDatabase
import www.thecodemonks.techbytes.model.Article
import www.thecodemonks.techbytes.repo.Repo
import www.thecodemonks.techbytes.viewmodel.NewsViewModel
import www.thecodemonks.techbytes.viewmodel.NewsViewModelProviderFactory

class BookmarkActivity : AppCompatActivity() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        setUpRV()

        // init viewModelProvider
        val repo = Repo(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(repo)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)


        // get saved articles from room db
        viewModel.getSavedArticle().observe(this, Observer {
            newsAdapter.differ.submitList(it)
        })


        // init item touch callback for swipe action
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // get item position & delete article
                val position = viewHolder.adapterPosition
                val item = newsAdapter.differ.currentList[position]
                val article = Article(
                    item.title,
                    item.description,
                    item.image,
                    item.author,
                    item.source
                )
                viewModel.deleteArticle(article)
                Snackbar.make(bookmark_layout, "Article deleted successfully", Snackbar.LENGTH_LONG)
                    .apply {
                        setAction("Undo") {
                            viewModel.upsertArticle(article)
                        }
                        show()
                    }
            }
        }

        // attach swipe callback to rv
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(bookmark_rv)
        }

        newsAdapter.setOnItemClickListener {
            val intent = Intent(this, NewsDetailsActivity::class.java)
            intent.putExtra("title", it.title)
            intent.putExtra("description", it.description)
            intent.putExtra("image", it.image)
            intent.putExtra("author", it.author)
            intent.putExtra("source", it.source)
            startActivity(intent)
        }

    }


    private fun setUpRV() {
        newsAdapter = NewsAdapter()
        bookmark_rv.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@BookmarkActivity)
        }
    }

}