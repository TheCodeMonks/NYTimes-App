package www.thecodemonks.techbytes.db

import androidx.lifecycle.LiveData
import androidx.room.*
import www.thecodemonks.techbytes.model.Article

@Dao
interface ArticleDao {

    // insert or update article
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article)

    // get all article from db
    @Query("SELECT * FROM article")
    fun getSavedArticle(): LiveData<List<Article>>


    // delete article from db
    @Delete
    suspend fun deleteArticle(article: Article)

}