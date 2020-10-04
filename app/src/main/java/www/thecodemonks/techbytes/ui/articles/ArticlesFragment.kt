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
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_articles.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.model.Category
import www.thecodemonks.techbytes.ui.adapter.CategoryAdapter
import www.thecodemonks.techbytes.ui.adapter.NewsAdapter
import www.thecodemonks.techbytes.ui.base.BaseActivity
import www.thecodemonks.techbytes.ui.viewmodel.ArticleViewModel
import www.thecodemonks.techbytes.utils.Animations
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
            article_rv.animate().alpha(0f)
                .withStartAction {
                    progress_view.isVisible = true
                    progress_view.animate().alpha(1f)
                }
                .withEndAction {
                    viewModel.crawlFromNYTimes(it.toString())
                }
        })

        // observe the articles
        viewModel.articles.observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
            progress_view.animate().alpha(0f)
                .withEndAction {
                    article_rv.animate().alpha(1f)
                    progress_view.isVisible = false
                }
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

        viewModel.networkObserver.observe(this, Observer { isConnected ->
            if (isResumed) {
                if (isConnected) {
                    container_network_status.setOnlineBehaviour()
                } else {
                    container_network_status.setOfflineBehaviour()
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu, menu)

        // Set the item state
        lifecycleScope.launch {
            val isChecked = viewModel.readDataStore.first()
            val item = menu.findItem(R.id.action_night_mode)
            item.isChecked = isChecked
            setUIMode(item, isChecked)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        return when (item.itemId) {
            R.id.action_bookmark -> {
                findNavController().navigate(R.id.action_articlesFragment_to_bookmarksFragment)
                true
            }

            R.id.action_night_mode -> {
                item.isChecked = !item.isChecked
                setUIMode(item, item.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUIMode(item: MenuItem, isChecked: Boolean) {
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            viewModel.saveToDataStore(true)
            item.setIcon(R.drawable.ic_night)

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            viewModel.saveToDataStore(false)
            item.setIcon(R.drawable.ic_day)

        }
    }

    private fun setUpArticleRV() {
        newsAdapter = NewsAdapter()
        article_rv.apply {
            adapter = newsAdapter
            addItemDecoration(SpacesItemDecorator(16))
        }
    }


    private val networkAutoDismissHandler = Handler()

    private fun LinearLayout.setOnlineBehaviour() {

        fun applyTheme() {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorStatusConnected
                )
            )
            val onlineDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_internet_on)
            text_network_status.setCompoundDrawablesWithIntrinsicBounds(
                onlineDrawable,
                null,
                null,
                null
            )
            text_network_status.text = getString(R.string.text_connectivity)
        }

        if (!isVisible) {
            //play expanding animation
            Animations.expand(container_network_status)
            applyTheme()
        } else {
            //play fade out and in animation
            Animations.fadeOutFadeIn(text_network_status) {
                //on fadeInStarted
                applyTheme()
            }
        }

        networkAutoDismissHandler.postDelayed({
            if (viewModel.networkObserver.value == true) {
                Animations.collapse(this)
            }
        }, 3000)

    }

    private fun LinearLayout.setOfflineBehaviour() {
        networkAutoDismissHandler.removeCallbacksAndMessages(null)

        fun applyTheme() {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorStatusNotConnected
                )
            )
            val onlineDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_internet_off)
            text_network_status.setCompoundDrawablesWithIntrinsicBounds(
                onlineDrawable,
                null,
                null,
                null
            )
            text_network_status.text = getString(R.string.text_no_connectivity)
        }


        if (!isVisible) {
            //play expanding animation
            Animations.expand(container_network_status)
            applyTheme()
        } else {
            //play fade out and in animation
            Animations.fadeOutFadeIn(text_network_status) {
                //on fadeInStarted
                applyTheme()
            }
        }


    }

}