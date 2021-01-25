package com.thiagoperea.btgtest.repository.currency

import android.util.Log
import com.thiagoperea.btgtest.data.datasource.CurrencyLayerService
import com.thiagoperea.btgtest.data.internal.dao.CurrencyDao
import com.thiagoperea.btgtest.data.internal.dao.RateDao
import com.thiagoperea.btgtest.data.model.Currency
import com.thiagoperea.btgtest.data.model.Rate
import com.thiagoperea.btgtest.internal.BtgTestApplication.Companion.APP_TAG
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class RemoteCurrencyRepository(
    private val service: CurrencyLayerService,
    private val currencyDao: CurrencyDao,
    private val rateDao: RateDao,
    private val apiKey: String,
    private val ioDispatcher: CoroutineDispatcher
) : CurrencyRepository {

    /**
     * Busca as taxas dado uma origem [originSymbol] e um destino [destinySymbol]
     */
    override suspend fun searchRate(
        originSymbol: String?,
        destinySymbol: String?,
        onSuccess: (Pair<Double, Double>) -> Unit,
        onError: () -> Unit
    ) = try {
        val originUpperCase = originSymbol?.toUpperCase(Locale.getDefault())
        val destinyUpperCase = destinySymbol?.toUpperCase(Locale.getDefault())

        val (origin, destiny) = withContext(ioDispatcher) {
            val cache = searchRateFromCache(originUpperCase, destinyUpperCase)
            if (cache.first == null || cache.second == null) {
                return@withContext searchRateFromInternet(originUpperCase, destinyUpperCase)
            } else {
                return@withContext cache
            }
        }

        if (origin == null || destiny == null) {
            throw RuntimeException("Response not found")
        }

        onSuccess(Pair(origin.rateValue, destiny.rateValue))
    } catch (error: Exception) {
        Log.e(APP_TAG, "Error ${error.message}")
        onError()
    }

    /**
     * Busca taxas no cache
     */
    private fun searchRateFromCache(
        originUpperCase: String?,
        destinyUpperCase: String?
    ): Pair<Rate?, Rate?> {
        val origin = rateDao.get("USD$originUpperCase")
        val destiny = rateDao.get("USD$destinyUpperCase")
        return Pair(origin, destiny)
    }

    /**
     * Busca taxas na internet e guarda no cache
     */
    private suspend fun searchRateFromInternet(
        originUpperCase: String?,
        destinyUpperCase: String?
    ): Pair<Rate?, Rate?> {
        val response = service.searchRate("$originUpperCase,$destinyUpperCase", apiKey)

        if (response.quotesMap.isEmpty()) {
            return Pair(null, null)
        }

        val originId = "USD$originUpperCase"
        val originRate = response.quotesMap[originId] ?: 0.0
        val origin = Rate(originId, originRate)

        val destinyId = "USD$destinyUpperCase"
        val destinyRate = response.quotesMap[destinyId] ?: 0.0
        val destiny = Rate(destinyId, destinyRate)

        rateDao.addAll(origin, destiny)
        return Pair(origin, destiny)
    }

    /**
     * Busca lista de moedas
     */
    override suspend fun getCurrencyList(
        onSuccess: (List<Currency>) -> Unit,
        onError: () -> Unit
    ) = try {
        val resultList = mutableListOf<Currency>()

        withContext(ioDispatcher) {
            if (!getCurrencyListFromCache(resultList)) {
                getCurrencyListFromInternet(resultList)
            }
        }

        onSuccess(resultList)
    } catch (error: Exception) {
        Log.e(APP_TAG, "Error ${error.message}")
        onError()
    }

    /**
     * Atualiza cache de moedas
     */
    override suspend fun updateCurrency(currency: Currency) {
        withContext(Dispatchers.IO) {
            currencyDao.update(currency)
        }
    }

    /**
     * Retorna true se houver cache
     */
    private fun getCurrencyListFromCache(resultList: MutableList<Currency>): Boolean {
        resultList.addAll(currencyDao.getAll())
        return resultList.isNotEmpty()
    }

    /**
     * Busca lista da internet e salva no cache
     */
    private suspend fun getCurrencyListFromInternet(resultList: MutableList<Currency>) {
        val result = service.getCurrencyList(apiKey)

        resultList.addAll(
            result.currencyList.map { entry ->
                return@map Currency(entry.key, entry.value)
            }
        )

        currencyDao.addAll(*resultList.toTypedArray())
    }
}