package de.gabriel.listrandomizer.data

import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [ItemEntry::class], version = 1, exportSchema = false)
abstract class ListRandomizerDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object Companion {

        @Volatile
        private var Instance: ListRandomizerDatabase? = null

        fun getDatabase(context: Context): ListRandomizerDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ListRandomizerDatabase::class.java, "item_database")
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}