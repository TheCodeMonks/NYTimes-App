package www.thecodemonks.techbytes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import www.thecodemonks.techbytes.model.Article
import www.thecodemonks.techbytes.repo.Repo
import www.thecodemonks.techbytes.utils.Utils

class NewsViewModel(private val repo: Repo) : ViewModel() {

    var crawledFromNY: MutableLiveData<List<Article>> = MutableLiveData()
    val currentTopic: MutableLiveData<String> by lazy {
        MutableLiveData<String>().defaultTopic(Utils.NY_TECH)
    }

    // save article
    fun upsertArticle(article: Article) = viewModelScope.launch {
        repo.upsertArticle(article)
    }

    // get saved article
    fun getSavedArticle() = repo.getSavedArticle()

    // save article
    fun deleteArticle(article: Article) = viewModelScope.launch {
        repo.deleteArticle(article)
    }

    fun crawlFromNY(url: String): MutableLiveData<List<Article>> {

        viewModelScope.launch(Dispatchers.Main) {
            val document = Jsoup.connect(url).get()
            val articles: MutableList<Article> = mutableListOf()

            // Path of articles present in the web
            val articleHTML = document.getElementById("stream-panel")
                    .select("div").select("ol")
                    .select("div").select("div").select("a")

            // iterate each article to get content
            articleHTML.forEach { item ->
                val image = item.select("div").select("figure")
                        .select("div").select("img").attr("src")
                val title = item.select("h2").text()
                val description = item.select("p").text()
                val author = item.select("div").select("p").select("span").text()
                val source = item.attr("href")

                // check for null content
                if (!image.isNullOrEmpty() || !title.isNullOrEmpty() || !description.isNullOrEmpty() || !author.isNullOrEmpty() || !source.isNullOrEmpty()) {
                    val article = Article(title, description, image, author, source)
                    articles.add(article)
                }
            }

            // post value to after crawling to liveData
            crawledFromNY.value = articles
        }

        return crawledFromNY
    }


    private fun <T : Any?> MutableLiveData<T>.defaultTopic(initialValue: T) = apply { setValue(initialValue) }

}