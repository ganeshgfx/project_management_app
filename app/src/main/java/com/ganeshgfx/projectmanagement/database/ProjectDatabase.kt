package com.ganeshgfx.projectmanagement.database

import android.content.Context
import androidx.room.*
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.Task
import com.ganeshgfx.projectmanagement.models.User

@Database(
    entities = [Project::class,Task::class,User::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converter::class)
abstract class ProjectDatabase : RoomDatabase() {
    abstract fun projectDao() : ProjectDAO
    abstract fun taskDao() : TaskDAO
    abstract fun userDao() : UserDAO
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