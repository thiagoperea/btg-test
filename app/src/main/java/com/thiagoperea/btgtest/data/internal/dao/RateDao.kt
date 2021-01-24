package com.thiagoperea.btgtest.data.internal.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thiagoperea.btgtest.data.model.Rate

@Dao
interface RateDao {

    @Query("SELECT * FROM Rate WHERE quote = :quote LIMIT 1")
    fun get(quote: String): Rate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(vararg rate: Rate)
}