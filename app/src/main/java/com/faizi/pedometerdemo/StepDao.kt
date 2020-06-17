package com.faizi.pedometerdemo

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StepDao {

    @Query("SELECT * FROM step_table ORDER BY step ASC")
    fun getAlphabetizedSteps(): LiveData<List<Step>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(step: Step)

    @Query("DELETE FROM step_table")
    suspend fun deleteAll()
}