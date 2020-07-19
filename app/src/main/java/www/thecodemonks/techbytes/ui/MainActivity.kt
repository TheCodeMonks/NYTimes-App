package www.thecodemonks.techbytes.ui

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.adapter.CategoryAdapter
import www.thecodemonks.techbytes.adapter.NewsAdapter
import www.thecodemonks.techbytes.db.ArticleDatabase
import www.thecodemonks.techbytes.model.Category
import www.thecodemonks.techbytes.repo.Repo
import www.thecodemonks.techbytes.utils.Utils
import www.thecodemonks.techbytes.viewmodel.NewsViewModel
import www.thecodemonks.techbytes.viewmodel.NewsViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var category: ArrayList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // init article rv
        setUpArticleRV()

        // init show viewModel
        val repo = Repo(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(repo)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)


        // add category list
        category = ArrayList()
        category.add(Category("Business", Utils.NY_BUSINESS))
        category.add(Category("Education", Utils.NY_EDUCATION))
        category.add(Category("Science", Utils.NY_SCIENCE))
        category.add(Category("Space", Utils.NY_SPACE))
        category.add(Category("Sports", Utils.NY_SPORTS))
        category.add(Category("Tech", Utils.NY_TECH))
        category.add(Category("Your money", Utils.NY_YOURMONEY))


        // attach category list to adapter
        categoryAdapter = CategoryAdapter(category)
        category_rv.rootView.post {
            category_rv.adapter = categoryAdapter
            category_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }

        // observe changes on topic change for list
        viewModel.currentTopic.observe(this, Observer {
            startCrawlObserver(it.toString())
        })


        // onclick to select source & post value to liveData
        categoryAdapter.setOnItemClickListener {
            viewModel.currentTopic.value = it.source
        }


        // pass data onclick
        newsAdapter.setOnItemClickListener { articleLink ->
            val intent = Intent(this@MainActivity, NewsDetailsActivity::class.java)
            intent.putExtra("title", articleLink.title)
            intent.putExtra("description", articleLink.description)
            intent.putExtra("image", articleLink.image)
            intent.putExtra("author", articleLink.author)
            intent.putExtra("source", articleLink.source)
            startActivity(intent)
        }


    }

    private fun startCrawlObserver(url: String?) {
        progress_view.visibility = View.VISIBLE
        url?.let { currentTopic ->
            viewModel.crawlFromNY(currentTopic).observe(this@MainActivity, Observer { list ->
                newsAdapter.differ.submitList(list)
                progress_view.visibility = View.GONE
            })
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.

        return when (item.itemId) {
            R.id.action_one -> {
                startActivity(Intent(this, BookmarkActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpArticleRV() {
        newsAdapter = NewsAdapter()
        article_rv.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            article_rv.visibility = View.VISIBLE

        }
    }
}