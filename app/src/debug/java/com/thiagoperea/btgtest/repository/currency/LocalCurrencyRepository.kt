package com.thiagoperea.btgtest.repository.currency

import com.thiagoperea.btgtest.data.model.Currency
import kotlinx.coroutines.delay
import kotlin.random.Random

class LocalCurrencyRepository : CurrencyRepository {

    override suspend fun searchRate(
        originSymbol: String?,
        destinySymbol: String?,
        onSuccess: (Pair<Double, Double>) -> Unit,
        onError: () -> Unit
    ) {
        sleep()
        if (wasSuccess()) {
            onSuccess(Pair(2.0, 3.0))
        } else {
            onError()
        }
    }

    override suspend fun getCurrencyList(
        onSuccess: (List<Currency>) -> Unit,
        onError: () -> Unit
    ) {
        sleep()
        if (wasSuccess()) {
            val dataset = listOf(
                Currency("JPY", "Yen Japonês"),
                Currency("USD", "Dolar Americano"),
                Currency("BRL", "Real Brasileiro")
            )
            onSuccess(dataset)
        } else {
            onError()
        }
    }

    override suspend fun updateCurrency(currency: Currency) {
        // nothing to do
    }

    private suspend fun sleep() = delay(Random.nextLong(1000, 2000))

    private fun wasSuccess() = Random.nextInt(0, 10) <= 4
}