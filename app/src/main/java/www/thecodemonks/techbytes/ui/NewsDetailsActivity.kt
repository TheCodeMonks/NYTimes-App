package www.thecodemonks.techbytes.ui

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_news_details.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.db.ArticleDatabase
import www.thecodemonks.techbytes.model.Article
import www.thecodemonks.techbytes.repo.Repo
import www.thecodemonks.techbytes.utils.Utils
import www.thecodemonks.techbytes.viewmodel.NewsViewModel
import www.thecodemonks.techbytes.viewmodel.NewsViewModelProviderFactory

class NewsDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel
    private var title: String? = null
    private var description: String? = null
    private var image: String? = null
    private var author: String? = null
    private var source: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)


        // init viewModel
        val repo = Repo(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(repo)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)


        // url source
        title = intent.extras?.getString("title")
            ?: throw IllegalArgumentException("`Title` must be non-null")
        description = intent.extras?.getString("description")
            ?: throw IllegalArgumentException("`Description` must be non-null")
        image = intent.extras?.getString("image")
            ?: throw IllegalArgumentException("`Image` must be non-null")
        author = intent.extras?.getString("author")
            ?: throw IllegalArgumentException("`Author` must be non-null")
        source = intent.extras?.getString("source")
            ?: throw IllegalArgumentException("`Source` must be non-null")
        val completeUrl = Utils.URL.plus(source)


        // init webView with url has param
        web_view.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                webViewClient = webViewClient
            }
            loadUrl(completeUrl)
        }

        btn_saved_article.setOnClickListener {
            val article = Article(title!!, description, image, author, source)
            viewModel.upsertArticle(article).also {
                Toast.makeText(this, "Article saved successfully!", Toast.LENGTH_SHORT).show()
            }
        }


    }
}