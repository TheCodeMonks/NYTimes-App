package www.thecodemonks.techbytes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import www.thecodemonks.techbytes.repo.Repo

class NewsViewModelProviderFactory(private val repo: Repo) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(repo) as T
    }
}