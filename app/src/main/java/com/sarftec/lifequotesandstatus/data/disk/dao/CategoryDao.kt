package com.sarftec.lifequotesandstatus.data.disk.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarftec.lifequotesandstatus.model.CATEGORY_TABLE
import com.sarftec.lifequotesandstatus.model.Category

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categories: List<Category>)

    @Query("select * from $CATEGORY_TABLE")
    suspend fun categories() : List<Category>
}