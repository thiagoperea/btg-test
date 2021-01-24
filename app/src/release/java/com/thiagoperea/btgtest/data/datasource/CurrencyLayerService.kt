package com.thiagoperea.btgtest.data.datasource

import com.thiagoperea.btgtest.data.model.CurrencyListResponse
import com.thiagoperea.btgtest.data.model.SearchRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyLayerService {

    @GET("live")
    suspend fun searchRate(
        @Query("currencies") currencies: String,
        @Query("access_key") accessKey: String
    ): SearchRateResponse

    @GET("list")
    suspend fun getCurrencyList(
        @Query("access_key") accessKey: String
    ): CurrencyListResponse
}