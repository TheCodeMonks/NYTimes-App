package www.thecodemonks.techbytes.model
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "article"
)
data class Article(
    @PrimaryKey
    @ColumnInfo(name = "title")
    val title: String = " ",
    @ColumnInfo(name = "description")
    val description: String? = null,
    @ColumnInfo(name = "image")
    val image: String? = null,
    @ColumnInfo(name = "author")
    val author: String? = null,
    @ColumnInfo(name = "source")
    val source: String? = null
)