package com.faizi.pedometerdemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StepViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StepRepository

    val allSteps: LiveData<List<Step>>

    init {
        val stepsDao = StepRoomDatabase.getDatabase(application, viewModelScope).stepDao()
        repository = StepRepository(stepsDao)
        allSteps = repository.allSteps
    }

    fun insert(step: Step) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(step)
    }
}