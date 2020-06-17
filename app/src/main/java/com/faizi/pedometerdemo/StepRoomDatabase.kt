package com.faizi.pedometerdemo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Step::class], version = 1, exportSchema = false)
abstract class StepRoomDatabase : RoomDatabase() {

    abstract fun stepDao(): StepDao

    companion object {

        @Volatile
        private var INSTANCE: StepRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): StepRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepRoomDatabase::class.java,
                    "step_database"
                ).build()
                INSTANCE = instance
                return  instance
            }
        }
    }
}