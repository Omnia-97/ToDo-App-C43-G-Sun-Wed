package com.route.todoappc43gsunwed.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.route.todoappc43gsunwed.database.dao.TaskDao
import com.route.todoappc43gsunwed.database.typeConverter.TaskDateConverter

@Database(version = 2, entities = [Task::class], exportSchema = false)
@TypeConverters(TaskDateConverter::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao

    //       Software Development -> Problems
    //                                        Solutions
    //   Task   -   Employee
    //   Creational Design Patterns
    companion object {
        private val databaseName = "TaskDatabase"
        private var INSTANCE: TaskDatabase? = null
        fun getInstance(context: Context): TaskDatabase {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context, TaskDatabase::class.java, databaseName)
                    .allowMainThreadQueries() // X      Coroutines  -> Thread
                    .fallbackToDestructiveMigration(true) // X
                    .build()
            return INSTANCE!!
        }
    }
    // Migration
}
