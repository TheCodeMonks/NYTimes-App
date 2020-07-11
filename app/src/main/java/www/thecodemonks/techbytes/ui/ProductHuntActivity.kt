package www.thecodemonks.techbytes.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_product_hunt.*
import org.jsoup.Jsoup
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.adapter.ProductHuntAdapter
import www.thecodemonks.techbytes.model.ProductHunt
import www.thecodemonks.techbytes.utils.Utils

class ProductHuntActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_hunt)


        Thread {


            val document = Jsoup.connect(Utils.PRODUCT_HUNT).get()

            val productHunt = mutableListOf<ProductHunt>()

            // this is the path to the list of the newest tech articles
            val articleHTML = document.getElementById("app")
                    .select("div").select("div").select("main")
                    .select("div").select("div").select("div").select("div").select("div")
                    .select("ul").select("li")
            Log.d("ProductHunt:", articleHTML.text())


            for (item in articleHTML) {
                // for each article preview we need to find the path to list items
                val image = item.select("div").select("a").select("div").select("span").text()
                val title = item.select("div").select("a").select("div").select("h3").text()
                val tag = item.select("div").select("a").select("div").select("p").text()
                val votes = item.select("div").select("div").select("button").select("span").select("span").text()
                val source = item.select("div").select("div").select("div").select("a").select("span").text()

                val products = ProductHunt(title, tag, image, votes, source)
                productHunt.add(products)
                Log.d("ProductHunt: ", "\n Image: $image\n, Title: $title\n, Tag: $tag\n, Votes: $votes\n, Source: $source\n, Image: $image")

            }

            val adapter = ProductHuntAdapter(productHunt)
            product_hunt_rv.rootView.post {
                product_hunt_rv.adapter = adapter
                product_hunt_rv.layoutManager = LinearLayoutManager(this)
            }


        }.start()
    }
}