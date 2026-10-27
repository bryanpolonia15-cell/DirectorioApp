package com.bryan.directorio.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    /** Lista de favoritos; el Flow emite de nuevo cada vez que la tabla cambia. */
    @Query("SELECT * FROM favoritos ORDER BY name ASC")
    fun observeAll(): Flow<List<FavoriteUserEntity>>

    /** IDs marcados como favoritos (para pintar la estrella en la lista). */
    @Query("SELECT id FROM favoritos")
    fun observeIds(): Flow<List<Int>>

    @Query("SELECT * FROM favoritos WHERE id = :id")
    suspend fun getById(id: Int): FavoriteUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: FavoriteUserEntity)

    @Query("DELETE FROM favoritos WHERE id = :id")
    suspend fun deleteById(id: Int)
}
