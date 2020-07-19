/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2020 Spikey Sanju
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package www.thecodemonks.techbytes.ui.viewmodel

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
                    val article = Article(
                        title,
                        description,
                        image,
                        author,
                        source
                    )
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