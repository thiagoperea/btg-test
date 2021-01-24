package com.thiagoperea.btgtest.internal

import com.thiagoperea.btgtest.data.datasource.CurrencyLayerService
import com.thiagoperea.btgtest.repository.currency.CurrencyRepository
import com.thiagoperea.btgtest.repository.currency.RemoteCurrencyRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val API_URL = "http://api.currencylayer.com/"
const val NAMED_API_KEY = "api.key"

val repositoryModule = module {
    single<CurrencyRepository> {
        RemoteCurrencyRepository(get(), get(), get(), get(named(NAMED_API_KEY)))
    }
}

val dataSourceModule = module {
    single(named(NAMED_API_KEY)) { PropertiesReader.getApiKey(get()) }

    single {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyLayerService::class.java)
    }
}