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

package www.thecodemonks.techbytes.ui.details

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_article_details.*
import www.thecodemonks.techbytes.R
import www.thecodemonks.techbytes.model.Article
import www.thecodemonks.techbytes.ui.base.BaseActivity
import www.thecodemonks.techbytes.ui.viewmodel.ArticleViewModel
import www.thecodemonks.techbytes.utils.Constants


class ArticleDetailsFragment : Fragment(R.layout.fragment_article_details) {
    private lateinit var viewModel: ArticleViewModel
    val args: ArticleDetailsFragmentArgs by navArgs()
    private var completeUrl: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        // init viewModel
        viewModel = (activity as BaseActivity).viewModel

        // receive bundle here
        val bundle = args.article
        completeUrl = Constants.URL.plus(bundle.source)

        // webView with url has param
        web_view.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                webViewClient = webViewClient
            }
            loadUrl(completeUrl)
        }

        btn_saved_article.setOnClickListener {
            val article = Article(
                bundle.title,
                bundle.description,
                bundle.image,
                bundle.author,
                bundle.source
            )
            viewModel.upsertArticle(article).also {
                Toast.makeText(activity, getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.share_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        return when (item.itemId) {
            R.id.action_share -> {
                val shareMsg = getString(
                    R.string.share_message,
                    args.article.title,
                    completeUrl
                )

                val intent = ShareCompat.IntentBuilder.from(requireActivity())
                    .setType("text/plain")
                    .setText(shareMsg)
                    .intent

                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}