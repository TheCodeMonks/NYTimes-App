package www.thecodemonks.techbytes

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_news_details.*
import www.thecodemonks.techbytes.utils.Utils

class NewsDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)


        // url source
        val source = intent.extras?.getString("source")
        val completeUrl = Utils.URL.plus(source)


        // init webView with url has param
        web_view.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                webViewClient = webViewClient
            }
            loadUrl(completeUrl)
        }


    }
}