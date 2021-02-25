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

package www.thecodemonks.techbytes.ui.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import www.thecodemonks.techbytes.datastore.UIModeMutableStore
import www.thecodemonks.techbytes.datastore.UIModeReadStore
import www.thecodemonks.techbytes.model.Article
import www.thecodemonks.techbytes.repo.ArticleRepository
import www.thecodemonks.techbytes.utils.Constants
import www.thecodemonks.techbytes.utils.NetworkManager

class ArticleViewModel @ViewModelInject constructor(
    application: Application,
    private val repo: ArticleRepository,
    private val uiModeEdit: UIModeMutableStore,
    val uiModeRead: UIModeReadStore,
    private val networkManager: NetworkManager,
) : AndroidViewModel(application) {

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>>
        get() = _articles

    val networkObserver = networkManager.observeConnectionStatus

    val currentTopic: MutableLiveData<String> by lazy {
        MutableLiveData<String>().defaultTopic(Constants.NY_TECH)
    }

    // save article
    fun upsertArticle(article: Article) = viewModelScope.launch {
        repo.upsertArticle(article)
    }

    // get saved article
    fun getSavedArticle() = repo.getSavedArticle().asLiveData()

    // save article
    fun deleteArticle(article: Article) = viewModelScope.launch {
        repo.deleteArticle(article)
    }

    private var currentQueryUrl = ""

    // crawl data from NY times
    fun crawlFromNYTimes(url: String) {
        currentQueryUrl = url
        if (networkObserver.value == true) {
            viewModelScope.launch(IO) {
                _articles.postValue(repo.crawlFromNYTimes(url))
            }
        }
    }

    fun reCrawlFromNYTimes(refreshFailed: () -> Unit = {}) {
        if (networkObserver.value == true) {
            viewModelScope.launch(IO) {
                _articles.postValue(repo.crawlFromNYTimes(currentQueryUrl))
            }
        } else {
            refreshFailed.invoke()
        }
    }

    // Save to DataStore
    fun saveToDataStore(isNightMode: Boolean) = viewModelScope.launch(IO) {
        uiModeEdit.saveToDataStore(isNightMode)
    }

    // set default topic when opening
    private fun <T : Any?> MutableLiveData<T>.defaultTopic(initialValue: T) =
        apply { setValue(initialValue) }
}
