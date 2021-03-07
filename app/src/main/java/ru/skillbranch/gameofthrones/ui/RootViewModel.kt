package ru.skillbranch.gameofthrones.ui

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.gameofthrones.extensions.isNetworkAvailable
import ru.skillbranch.gameofthrones.extensions.mutableLiveData
import ru.skillbranch.gameofthrones.repositories.RootRepository

class RootViewModel(val app:Application) : AndroidViewModel(app) {
    private val repository = RootRepository

    fun syncDataIfNeed(): LiveData<LoadResult<Boolean>> {
        val result: MutableLiveData<LoadResult<Boolean>> =
            mutableLiveData(LoadResult.Loading(false))
        viewModelScope.launch(Dispatchers.IO) {
            var isNeedUpdate = repository.isNeedUpdate()
            if (isNeedUpdate) {
                if (!app.applicationContext.isNetworkAvailable) {
                    result.postValue(LoadResult.Error("Интернет не доступен, приложение может работать некорректно"))
                    return@launch
                }
                repository.sync() //suspend
                result.postValue(LoadResult.Success(true))
            } else {
                delay(10000)
                result.postValue(LoadResult.Success(true))
            }
        }
        return result
    }
}

sealed class LoadResult<T>(
    val data: T?,
    val errorMessage: String? = null
){
    class Success<T>(data:T):LoadResult<T>(data)
    class Loading<T>(data: T? = null):LoadResult<T>(data)
    class Error<T>(message: String, data:T? = null): LoadResult<T>(data, message)
}