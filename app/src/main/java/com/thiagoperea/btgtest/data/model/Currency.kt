package com.thiagoperea.btgtest.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Currency(
    @PrimaryKey val symbol: String,
    @ColumnInfo val fullName: String,
    @ColumnInfo var isFavorite: Boolean = false
)