package com.faizi.pedometerdemo

import androidx.lifecycle.LiveData

class StepRepository(private val stepDao: StepDao) {

    val allSteps: LiveData<List<Step>> = stepDao.getAlphabetizedSteps()

    suspend fun insert(step: Step) {
        stepDao.insert(step)
    }
}