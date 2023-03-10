package com.ganeshgfx.projectmanagement.database

import android.content.Context
import androidx.room.*
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.Task

@Database(
    entities = [Project::class,Task::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converter::class)
abstract class ProjectDatabase : RoomDatabase() {
    abstract fun projectDao() : ProjectDAO
    companion object{
        @Volatile
        private var INSTANCE : ProjectDatabase? = null
        fun getDatabase(context: Context):ProjectDatabase{
            if(INSTANCE == null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ProjectDatabase::class.java,
                        "projectDB"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}