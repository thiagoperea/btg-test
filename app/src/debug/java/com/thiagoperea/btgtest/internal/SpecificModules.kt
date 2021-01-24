package com.thiagoperea.btgtest.internal

import com.thiagoperea.btgtest.repository.currency.CurrencyRepository
import com.thiagoperea.btgtest.repository.currency.LocalCurrencyRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<CurrencyRepository> { LocalCurrencyRepository() }
}

val dataSourceModule = module { }