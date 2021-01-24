package com.thiagoperea.btgtest.data.internal

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thiagoperea.btgtest.data.internal.dao.CurrencyDao
import com.thiagoperea.btgtest.data.internal.dao.RateDao
import com.thiagoperea.btgtest.data.model.Currency
import com.thiagoperea.btgtest.data.model.Rate

@Database(
    entities = [Currency::class, Rate::class],
    version = 1
)
abstract class BtgTestDatabase : RoomDatabase() {

    companion object {
        const val NAME = "btg-test-db"
    }

    abstract fun currencyDao(): CurrencyDao

    abstract fun rateDao(): RateDao
}