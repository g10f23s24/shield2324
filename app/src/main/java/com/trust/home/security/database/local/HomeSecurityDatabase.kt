package com.trust.home.security.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.trust.home.security.database.entity.Face
import com.trust.home.security.database.entity.User

@Database(entities = [Face::class, User::class], version = 1, exportSchema = false)
abstract class HomeSecurityDatabase : RoomDatabase() {
    abstract fun daoJob(): DAOJob

    companion object {
        private const val DB_NAME = "job-database"

        @Volatile
        private var INSTANCE: HomeSecurityDatabase? = null

        fun getDatabase(context: Context): HomeSecurityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HomeSecurityDatabase::class.java,
                    DB_NAME
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}