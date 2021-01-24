package com.thiagoperea.btgtest.data.internal.dao

import androidx.room.*
import com.thiagoperea.btgtest.data.model.Currency

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM Currency")
    fun getAll(): List<Currency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(vararg currency: Currency)

    @Update
    fun update(currency: Currency)
}