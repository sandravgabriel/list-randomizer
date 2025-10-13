package de.gabriel.listrandomizer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.Update
import androidx.room.Delete

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ItemEntry)

    @Query("SELECT * FROM item WHERE id = :id")
    fun getItem(id: Int): Flow<ItemEntry?>

    @Query("SELECT * FROM item ORDER BY name ASC")
    fun getAllItems(): Flow<List<ItemEntry>>

    @Query("SELECT * FROM item WHERE (:genre IS NULL OR genre = :genre) AND (:playerCount IS NULL OR (minPlayer <= :playerCount AND maxPlayer >= :playerCount)) ORDER BY name ASC")
    fun getFilteredItems(genre: String?, playerCount: Int?): Flow<List<ItemEntry>>

    @Query("SELECT DISTINCT genre FROM item WHERE genre IS NOT NULL AND genre != '' ORDER BY genre ASC")
    fun getAllGenres(): Flow<List<String>>

    @Update
    suspend fun update(item: ItemEntry)

    @Delete
    suspend fun delete(item: ItemEntry)
}