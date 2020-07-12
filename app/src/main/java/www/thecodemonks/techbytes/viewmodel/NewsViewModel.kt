package www.thecodemonks.techbytes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewsViewModel : ViewModel() {

    var selectedSource: MutableLiveData<String> = MutableLiveData()


    // selected source
    fun getTopic(source: String): MutableLiveData<String> {
        selectedSource.postValue(source)
        return selectedSource
    }


}