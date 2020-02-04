package com.virajjage.abl_test_viraj_jage.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.virajjage.abl_test_viraj_jage.models.BlackListedItem

@Database(entities = [BlackListedItem::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {


    abstract fun userDBAccess(): UserDBAccess


    companion object {
        @Volatile
        private var instance: UserDatabase? = null
        private val LOCK = Any()
        private var DB_NAME: String = "users.db"

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            UserDatabase::class.java, DB_NAME
        )
            .build()
    }
}