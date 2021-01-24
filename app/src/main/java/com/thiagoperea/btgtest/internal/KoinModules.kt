package com.thiagoperea.btgtest.internal

import androidx.room.Room
import com.thiagoperea.btgtest.data.internal.BtgTestDatabase
import com.thiagoperea.btgtest.presentation.conversion.ConversionViewModel
import com.thiagoperea.btgtest.presentation.currencylist.CurrencyListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ConversionViewModel(get()) }
    viewModel { CurrencyListViewModel(get()) }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            BtgTestDatabase::class.java,
            BtgTestDatabase.NAME
        ).build()
    }

    factory { get<BtgTestDatabase>().currencyDao() }
    factory { get<BtgTestDatabase>().rateDao() }
}