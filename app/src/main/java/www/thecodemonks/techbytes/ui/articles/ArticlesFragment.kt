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

package www.thecodemonks.techbytes.ui.articles

import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_articles.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.model.Category
import www.thecodemonks.techbytes.ui.adapter.CategoryAdapter
import www.thecodemonks.techbytes.ui.adapter.NewsAdapter
import www.thecodemonks.techbytes.ui.base.BaseActivity
import www.thecodemonks.techbytes.ui.viewmodel.ArticleViewModel
import www.thecodemonks.techbytes.utils.Constants.NY_BUSINESS
import www.thecodemonks.techbytes.utils.Constants.NY_EDUCATION
import www.thecodemonks.techbytes.utils.Constants.NY_SCIENCE
import www.thecodemonks.techbytes.utils.Constants.NY_SPACE
import www.thecodemonks.techbytes.utils.Constants.NY_SPORTS
import www.thecodemonks.techbytes.utils.Constants.NY_TECH
import www.thecodemonks.techbytes.utils.Constants.NY_YOURMONEY
import www.thecodemonks.techbytes.utils.SpacesItemDecorator


class ArticlesFragment : Fragment(R.layout.fragment_articles) {

    private lateinit var viewModel: ArticleViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var category: MutableList<Category>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // init article rv
        setUpArticleRV()

        // init show viewModel
        viewModel = (activity as BaseActivity).viewModel

        // add category list
        category = mutableListOf(
            Category("Business", NY_BUSINESS),
            Category("Education", NY_EDUCATION),
            Category("Science", NY_SCIENCE),
            Category("Space", NY_SPACE),
            Category("Sports", NY_SPORTS),
            Category("Tech", NY_TECH),
            Category("Your money", NY_YOURMONEY)
        )

        // attach category list to adapter
        categoryAdapter = CategoryAdapter(category)
        category_rv.rootView.post {
            category_rv.adapter = categoryAdapter
            category_rv.addItemDecoration(SpacesItemDecorator(16))
        }

        // observe changes on topic change for list
        viewModel.currentTopic.observe(viewLifecycleOwner, Observer {
            startCrawlObserver(it.toString())
        })


        // onclick to select source & post value to liveData
        categoryAdapter.setOnItemClickListener {
            viewModel.currentTopic.value = it.source
        }


        // pass bundle onclick
        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_articlesFragment_to_articleDetailsFragment,
                bundle
            )
        }


    }

    private fun startCrawlObserver(url: String?) {
        progress_view.visibility = View.VISIBLE
        url?.let { currentTopic ->
            viewModel.crawlFromNYTimes(currentTopic).observe(viewLifecycleOwner, Observer { list ->
                newsAdapter.differ.submitList(list)
                progress_view.visibility = View.GONE
            })
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        return when (item.itemId) {
            R.id.action_one -> {
                findNavController().navigate(R.id.action_articlesFragment_to_bookmarksFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpArticleRV() {
        newsAdapter = NewsAdapter()
        article_rv.apply {
            adapter = newsAdapter
            addItemDecoration(SpacesItemDecorator(16))
            article_rv.visibility = View.VISIBLE
        }
    }

}