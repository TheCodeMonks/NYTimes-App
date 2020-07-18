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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.adapter.CategoryAdapter
import www.thecodemonks.techbytes.adapter.NewsAdapter
import www.thecodemonks.techbytes.db.ArticleDatabase
import www.thecodemonks.techbytes.model.Article
import www.thecodemonks.techbytes.model.Category
import www.thecodemonks.techbytes.repo.Repo
import www.thecodemonks.techbytes.utils.Utils
import www.thecodemonks.techbytes.viewmodel.NewsViewModel
import www.thecodemonks.techbytes.viewmodel.NewsViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // init show viewModel
        val repo = Repo(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(repo)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)


        // add category
        val category = ArrayList<Category>()
        category.add(Category("Business", Utils.NY_BUSINESS))
        category.add(Category("Education", Utils.NY_EDUCATION))
        category.add(Category("Science", Utils.NY_SCIENCE))
        category.add(Category("Space", Utils.NY_SPACE))
        category.add(Category("Sports", Utils.NY_SPORTS))
        category.add(Category("Tech", Utils.NY_TECH))
        category.add(Category("Your money", Utils.NY_YOURMONEY))

        // attach category list to adapter
        val adapter = CategoryAdapter(category)
        category_rv.rootView.post {
            category_rv.adapter = adapter
            category_rv.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }

        // onclick select source & observe live data change
        adapter.setOnItemClickListener {
            progress_view.visibility = View.VISIBLE
            sourceObserver(it.source)
        }


    }

    private fun sourceObserver(url: String?) {


        GlobalScope.launch(Dispatchers.Main) {
            url?.let { url ->
                viewModel.getTopic(url).observe(this@MainActivity, Observer { topic ->
                    val document = Jsoup.connect(topic).get()
                    val articles = mutableListOf<Article>()

                    // Path of articles in web
                    val articleHTML = document.getElementById("stream-panel")
                        .select("div").select("ol")
                        .select("div").select("div").select("a")

                    articleHTML.forEach { item ->
                        // iterate each article to get content
                        val image = item.select("div").select("figure")
                            .select("div").select("img").attr("src")
                        val title = item.select("h2").text()
                        val description = item.select("p").text()
                        val author = item.select("div").select("p").select("span").text()
                        val source = item.attr("href")

                        if (!image.isNullOrEmpty() || !title.isNullOrEmpty() || !description.isNullOrEmpty() || !author.isNullOrEmpty() || !source.isNullOrEmpty()) {
                            val article = Article(title, description, image, author, source)
                            articles.add(article)
                        }
                    }

                    // init adapter
                    val adapter = NewsAdapter()
                    article_rv.rootView.post {
                        article_rv.adapter = adapter
                        article_rv.layoutManager = LinearLayoutManager(this@MainActivity)
                        article_rv.visibility = View.VISIBLE
                        progress_view.visibility = View.GONE
                    }

                    // submit articles list to adapter
                    adapter.differ.currentList.clear()
                    adapter.differ.submitList(articles)

                    // onclick open news details activity
                    adapter.setOnItemClickListener { articleLink ->
                        val intent = Intent(this@MainActivity, NewsDetailsActivity::class.java)
                        intent.putExtra("title", articleLink.title)
                        intent.putExtra("description", articleLink.description)
                        intent.putExtra("image", articleLink.image)
                        intent.putExtra("author", articleLink.author)
                        intent.putExtra("source", articleLink.source)
                        startActivity(intent)
                    }
                })
            }

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
}