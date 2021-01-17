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
package www.thecodemonks.techbytes.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.databinding.FragmentBookmarksBinding
import www.thecodemonks.techbytes.model.Article
import www.thecodemonks.techbytes.ui.adapter.NewsAdapter
import www.thecodemonks.techbytes.ui.base.BaseFragment
import www.thecodemonks.techbytes.ui.viewmodel.ArticleViewModel
import www.thecodemonks.techbytes.utils.SpacesItemDecorator
import www.thecodemonks.techbytes.utils.setVisible

@AndroidEntryPoint
class BookmarksFragment : BaseFragment<FragmentBookmarksBinding, ArticleViewModel>() {

    override val viewModel: ArticleViewModel by activityViewModels()
    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeArticles()
        articleItemOnClick()
        swipeToDeleteArticle()
    }

    private fun articleItemOnClick() {
        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_bookmarksFragment_to_articleDetailsFragment,
                bundle
            )
        }
    }

    private fun observeArticles() {
        // get saved articles from room db
        viewModel.getSavedArticle().observe(viewLifecycleOwner) {
            val articles = it ?: emptyList()
            binding.emptyStateLayout.setVisible(articles.isEmpty())
            newsAdapter.differ.submitList(articles)
        }
    }

    private fun swipeToDeleteArticle() {
        // init item touch callback for swipe action
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // get item position & delete article
                val position = viewHolder.adapterPosition
                val item = newsAdapter.differ.currentList[position]
                // todo extension item to Article
                val article = Article(
                    item.title,
                    item.description,
                    item.image,
                    item.author,
                    item.source
                )

                viewModel.deleteArticle(article)

                // todo extension snack bar
                Snackbar.make(
                    binding.bookmarkRootView,
                    "Article deleted successfully",
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction("Undo") {
                        viewModel.upsertArticle(article)
                    }
                    show()
                }
            }
        }

        // attach swipe callback to rv
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.bookmarkRv)
        }
    }

    private fun initViews() = with(binding) {

        newsAdapter = NewsAdapter().also {
            bookmarkRv.adapter = it
            bookmarkRv.addItemDecoration(SpacesItemDecorator(16))
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBookmarksBinding.inflate(inflater, container, false)
}
