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

package www.thecodemonks.techbytes.repo

import kotlinx.coroutines.flow.Flow
import org.jsoup.Jsoup
import www.thecodemonks.techbytes.db.ArticleDatabase
import www.thecodemonks.techbytes.model.Article

class Repo(private val db: ArticleDatabase) : ArticleRepository {

    // insert or update article
    override suspend fun upsertArticle(article: Article) = db.getArticleDao().upsert(article)

    // get saved article
    override fun getSavedArticle(): Flow<List<Article>> = db.getArticleDao().getSavedArticle()

    // delete article
    override suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    // crawl data from ny times by selecting Xpath elements
    override fun crawlFromNYTimes(url: String): List<Article> {

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
            if (!image.isNullOrEmpty() || !title.isNullOrEmpty() ||
                !description.isNullOrEmpty() || !author.isNullOrEmpty() ||
                !source.isNullOrEmpty()
            ) {
                val article = Article(
                    title,
                    description,
                    image,
                    author,
                    source
                )
                // add iterated articles to list
                articles.add(article)
            }
        }
        return articles
    }
}

interface ArticleRepository {
    suspend fun upsertArticle(article: Article)
    fun getSavedArticle(): Flow<List<Article>>
    suspend fun deleteArticle(article: Article)
    fun crawlFromNYTimes(url: String): List<Article>
}
