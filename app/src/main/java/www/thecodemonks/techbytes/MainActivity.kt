package www.thecodemonks.techbytes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import www.thecodemonks.techbytes.adapter.CategoryAdapter
import www.thecodemonks.techbytes.adapter.NewsAdapter
import www.thecodemonks.techbytes.model.Article
import www.thecodemonks.techbytes.model.Category
import www.thecodemonks.techbytes.utils.Utils
import www.thecodemonks.techbytes.viewmodel.NewsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init show viewModel
        viewModel = this.let { ViewModelProvider(this).get(NewsViewModel::class.java) }

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
            sourceObserver(it.source)
        }

    }


    private fun sourceObserver(source: String?) {

        source?.let {
            viewModel.getTopic(it).observe(this, Observer {

                Thread {
                    val url = source
                    val document = Jsoup.connect(url).get()
                    val articles = mutableListOf<Article>()

                    // Path of articles in web
                    val articleHTML = document.getElementById("stream-panel")
                        .select("div").first().select("ol")
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
                        article_rv.layoutManager = LinearLayoutManager(this)

                    }

                    // submit articles list to adapter
                    adapter.differ.submitList(articles)

                    // onclick open news details activity
                    adapter.setOnItemClickListener {
                        val intent = Intent(this, NewsDetailsActivity::class.java)
                        intent.putExtra("source", it.source)
                        startActivity(intent)
                    }

                }.start()

            })
        }
    }
}