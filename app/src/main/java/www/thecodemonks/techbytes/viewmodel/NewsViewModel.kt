package www.thecodemonks.techbytes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import www.thecodemonks.techbytes.model.Article
import www.thecodemonks.techbytes.repo.Repo

class NewsViewModel(private val repo: Repo) : ViewModel() {
    // selected source
    fun getTopic(source: String): MutableLiveData<String> {
        selectedSource.postValue(source)
        return selectedSource
    }


    // save article
    fun upsertArticle(article: Article) = viewModelScope.launch {
        repo.upsertArticle(article)
    }


    // get saved article
    fun getSavedArticle() = repo.getSavedArticle()


    // save article
    fun deleteArticle(article: Article) = viewModelScope.launch {
        repo.deleteArticle(article)
    }


    var selectedSource: MutableLiveData<String> = MutableLiveData()


}