package com.faizi.pedometerdemo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Step::class], version = 1, exportSchema = false)
abstract class StepRoomDatabase : RoomDatabase() {

    abstract fun stepDao(): StepDao

    private class StepDatabaseCallback (
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val stepDao = database.stepDao()

                    // Delete all content here.
                    stepDao.deleteAll()

                    // Add sample words
                    var step = Step(null, 200)
                    stepDao.insert(step)
                    step = Step(null, 300)
                    stepDao.insert(step)

                    step = Step(null, 400)
                    stepDao.insert(step)
                }
            }
        }

    }

    companion object {
        @Volatile
        private var INSTANCE: StepRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): StepRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder (
                    context.applicationContext,
                    StepRoomDatabase::class.java,
                    "step_database"
                )
                    .addCallback(StepDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}