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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.databinding.FragmentArticlesBinding
import www.thecodemonks.techbytes.model.Category
import www.thecodemonks.techbytes.ui.adapter.CategoryAdapter
import www.thecodemonks.techbytes.ui.adapter.NewsAdapter
import www.thecodemonks.techbytes.ui.base.BaseFragment
import www.thecodemonks.techbytes.ui.viewmodel.ArticleViewModel
import www.thecodemonks.techbytes.utils.Constants.NY_BUSINESS
import www.thecodemonks.techbytes.utils.Constants.NY_EDUCATION
import www.thecodemonks.techbytes.utils.Constants.NY_SCIENCE
import www.thecodemonks.techbytes.utils.Constants.NY_SPACE
import www.thecodemonks.techbytes.utils.Constants.NY_SPORTS
import www.thecodemonks.techbytes.utils.Constants.NY_TECH
import www.thecodemonks.techbytes.utils.Constants.NY_YOURMONEY
import www.thecodemonks.techbytes.utils.NetworkUtils
import www.thecodemonks.techbytes.utils.SpacesItemDecorator
import www.thecodemonks.techbytes.utils.getColorCompat
import www.thecodemonks.techbytes.utils.hide
import www.thecodemonks.techbytes.utils.setDrawableLeft
import www.thecodemonks.techbytes.utils.show

@AndroidEntryPoint
class ArticlesFragment : BaseFragment<FragmentArticlesBinding, ArticleViewModel>() {

    override val viewModel: ArticleViewModel by activityViewModels()

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val category: MutableList<Category> by lazy {
        mutableListOf(
            Category("Business", NY_BUSINESS),
            Category("Education", NY_EDUCATION),
            Category("Science", NY_SCIENCE),
            Category("Space", NY_SPACE),
            Category("Sports", NY_SPORTS),
            Category("Tech", NY_TECH),
            Category("Your money", NY_YOURMONEY)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setup()
    }

    private fun setup() = with(binding) {
        initArticlesRv()
        initCategoryRv()
        observeNetworkConnectivity()
        observeTopics()
        observeArticles()
        categoryItemOnClick()
        articleItemOnClick()
        swipeToRefreshArticles()
    }

    private fun initArticlesRv() = with(binding) {
        // init article rv
        newsAdapter = NewsAdapter().also {
            articleRv.adapter = it
            articleRv.addItemDecoration(SpacesItemDecorator(16))
        }
    }

    private fun initCategoryRv() = with(binding) {
        // init category rv
        categoryAdapter = CategoryAdapter(category).also {
            categoryRv.rootView.post {
                binding.categoryRv.adapter = it
                binding.categoryRv.addItemDecoration(SpacesItemDecorator(16))
            }
        }
    }

    private fun swipeToRefreshArticles() = with(binding) {
        refreshArticles.setOnRefreshListener {
            viewModel.reCrawlFromNYTimes {
                refreshArticles.isRefreshing = true
            }
        }
    }

    private fun observeTopics() = with(binding) {
        // observe changes on topic change for list
        viewModel.currentTopic.observe(viewLifecycleOwner) {
            articleRv.animate().alpha(0f)
                .withStartAction {
                    if (viewModel.networkObserver.value == true) {
                        refreshArticles.isRefreshing = true
                    }
                }
                .withEndAction {
                    viewModel.crawlFromNYTimes(it.toString())
                }
        }
    }

    private fun articleItemOnClick() {
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

    private fun categoryItemOnClick() {
        // onclick to select source & post value to liveData
        categoryAdapter.setOnItemClickListener {
            viewModel.currentTopic.value = it.source
        }
    }

    private fun observeArticles() = with(binding) {
        // observe the articles
        viewModel.articles.observe(viewLifecycleOwner) {
            refreshArticles.isRefreshing = false
            newsAdapter.differ.submitList(it)
            articleRv.animate().alpha(1f)
            articleRv.itemAnimator = null // prevents flickering between article categories
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu, menu)

        // TODO move to viewmodel
        // Set the item state
        lifecycleScope.launch {
            val isChecked = viewModel.uiModeRead.uiMode.first()
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
            R.id.action_about -> {
                findNavController().navigate(R.id.action_articlesFragment_to_aboutFragment)
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

    private fun observeNetworkConnectivity() {
        NetworkUtils.observeConnectivity(requireContext())
            .observe(viewLifecycleOwner) { isConnected ->
                if (isConnected) onConnectivityAvailable() else onConnectivityUnavailable()
            }
    }

    private fun onConnectivityAvailable() = with(binding) {
        textNetworkStatus.apply {
            text = getString(R.string.text_connectivity)
            setDrawableLeft(R.drawable.ic_internet_on)
        }
        containerNetworkStatus.apply {
            setBackgroundColor(
                context.getColorCompat(R.color.colorStatusConnected)
            )
            animate()
                .alpha(1f)
                .setStartDelay(ANIMATION_DURATION)
                .setDuration(ANIMATION_DURATION)
                .setListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            hide()
                        }
                    }
                )
                .start()
        }
    }

    private fun onConnectivityUnavailable() = with(binding) {
        textNetworkStatus.apply {
            text = getString(R.string.text_no_connectivity)
            setDrawableLeft(R.drawable.ic_internet_off)
        }
        containerNetworkStatus.apply {
            show()
            setBackgroundColor(
                context.getColorCompat(R.color.colorStatusNotConnected)
            )
        }
    }

    companion object {
        const val ANIMATION_DURATION = 3000L
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentArticlesBinding.inflate(inflater, container, false)
}
