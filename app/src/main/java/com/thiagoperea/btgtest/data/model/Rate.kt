package com.thiagoperea.btgtest.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Rate(
    @PrimaryKey val quote: String,
    @ColumnInfo val rateValue: Double
)