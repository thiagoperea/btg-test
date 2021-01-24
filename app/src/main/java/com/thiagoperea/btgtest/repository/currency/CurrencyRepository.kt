package com.thiagoperea.btgtest.repository.currency

import com.thiagoperea.btgtest.data.model.Currency

interface CurrencyRepository {

    suspend fun searchRate(
        originSymbol: String?,
        destinySymbol: String?,
        onSuccess: (Pair<Double, Double>) -> Unit,
        onError: () -> Unit
    )

    suspend fun getCurrencyList(
        onSuccess: (List<Currency>) -> Unit,
        onError: () -> Unit
    )

    suspend fun updateCurrency(currency: Currency)
}
