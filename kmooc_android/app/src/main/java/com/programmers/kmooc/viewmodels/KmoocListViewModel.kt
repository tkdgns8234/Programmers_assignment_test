package com.programmers.kmooc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.repositories.KmoocRepository
import java.util.Collections.addAll

class KmoocListViewModel(private val repository: KmoocRepository) : ViewModel() {
    private val _lectureListLiveData = MutableLiveData<LectureList>()
    val lectureListLiveData: LiveData<LectureList> get() = _lectureListLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> get() = _progressLiveData

    fun list() {
        _progressLiveData.postValue(true)
        repository.list { lectureList ->
            _lectureListLiveData.postValue(lectureList)
            _progressLiveData.postValue(false)
        }
    }

    fun next() {
        _progressLiveData.postValue(true)
        val currentLectureList = lectureListLiveData.value ?: LectureList.EMPTY
        repository.next(currentLectureList) { lectureList ->
            val newList = currentLectureList.lectures.toMutableList()
            newList.addAll(lectureList.lectures)
            lectureList.lectures = newList
            _lectureListLiveData.postValue(lectureList)
            _progressLiveData.postValue(false)
        }
    }
}

class KmoocListViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocListViewModel::class.java)) {
            return KmoocListViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}