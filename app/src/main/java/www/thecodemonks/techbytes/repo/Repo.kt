package www.thecodemonks.techbytes.repo

import www.thecodemonks.techbytes.db.ArticleDatabase
import www.thecodemonks.techbytes.model.Article

class Repo(private val db: ArticleDatabase) {

    // upsert article
    suspend fun upsertArticle(article: Article) = db.getArticleDao().upsert(article)

    // get saved article
    fun getSavedArticle() = db.getArticleDao().getSavedArticle()

    // delete article
    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}