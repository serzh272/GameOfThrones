package ru.skillbranch.gameofthrones.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

fun <T> mutableLiveData(defaultValue: T? = null):MutableLiveData<T>{
    val data = MutableLiveData<T>()
    if (defaultValue != null){
        data.value = defaultValue
    }
    return data
}

fun <T, A, B> LiveData<A>.combineAndCompute(other: LiveData<B>, onChange: (A, B) -> T):MediatorLiveData<T>{
    var source1emmited = false
    var source2emmited = false

    val result = MediatorLiveData<T>()
    val mergeF = {
        val source1Value = this.value
        val source2Value = other.value
        if (source1emmited && source2emmited){
            result.value = onChange.invoke(source1Value!!, source2Value!!)
        }
    }
    result.addSource(this){source1emmited = true; mergeF.invoke()}
    result.addSource(other){source2emmited = true; mergeF.invoke()}
    return result
}