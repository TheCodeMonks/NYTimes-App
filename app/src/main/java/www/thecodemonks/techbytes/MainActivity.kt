package www.thecodemonks.techbytes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import www.thecodemonks.techbytes.adapter.RecyclerViewAdapter
import www.thecodemonks.techbytes.model.Article

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread {

            val url = "https://www.nytimes.com/section/business/smallbusiness"
            val document = Jsoup.connect(url).get()


            val articles = mutableListOf<Article>()

            // Path of articles in web
            val articleHTML = document.getElementById("stream-panel")
                .select("div").first().select("ol")
                .select("div").select("div").select("a")

            for (item in articleHTML) {

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

            val adapter = RecyclerViewAdapter(articles)
            article_rv.rootView.post {
                article_rv.adapter = adapter
                article_rv.layoutManager = LinearLayoutManager(this)
            }

            adapter.setOnItemClickListener {
                val intent = Intent(this, NewsDetailsActivity::class.java)
                intent.putExtra("source", it.source)
                startActivity(intent)
            }

        }.start()


    }
}