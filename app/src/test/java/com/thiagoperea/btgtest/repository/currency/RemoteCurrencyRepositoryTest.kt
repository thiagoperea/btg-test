package com.thiagoperea.btgtest.repository.currency

import com.thiagoperea.btgtest.BaseUnitTest
import com.thiagoperea.btgtest.data.datasource.CurrencyLayerService
import com.thiagoperea.btgtest.data.internal.dao.CurrencyDao
import com.thiagoperea.btgtest.data.internal.dao.RateDao
import com.thiagoperea.btgtest.data.model.Currency
import com.thiagoperea.btgtest.data.model.CurrencyListResponse
import com.thiagoperea.btgtest.data.model.Rate
import com.thiagoperea.btgtest.data.model.SearchRateResponse
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@ExperimentalCoroutinesApi
class RemoteCurrencyRepositoryTest : BaseUnitTest() {

    private data class Fields(
        val service: CurrencyLayerService,
        val currencyDao: CurrencyDao,
        val rateDao: RateDao,
        val apiKey: String
    )

    private fun createMocks(): Pair<RemoteCurrencyRepository, Fields> {
        val service = mockk<CurrencyLayerService>(relaxed = true)
        val currencyDao = mockk<CurrencyDao>(relaxed = true)
        val rateDao = mockk<RateDao>(relaxed = true)

        val fields = Fields(service, currencyDao, rateDao, "123")

        val repository = RemoteCurrencyRepository(
            fields.service,
            fields.currencyDao,
            fields.rateDao,
            fields.apiKey,
            dispatcher
        )

        return Pair(repository, fields)
    }

    @Test
    fun `searchRate from cache`() = runBlocking {
        //arrange
        val (repository, fields) = createMocks()
        val rateMock = Rate("test", 1.0)
        coEvery { fields.rateDao.get(any()) } returns rateMock

        //act
        repository.searchRate("A", "B", {}, {})

        //assert
        coVerifySequence {
            fields.rateDao.get(eq("USDA"))
            fields.rateDao.get(eq("USDB"))
        }
    }

    @Test
    fun `searchRate from internet`() = runBlocking {
        //arrange
        val (repository, fields) = createMocks()
        val response = SearchRateResponse(
            mapOf(Pair("TEST", 2.0))
        )
        coEvery { fields.rateDao.get(any()) } returns null
        coEvery { fields.service.searchRate(any(), any()) } returns response

        //act
        repository.searchRate("A", "B", {}, {})

        //assert
        coVerifySequence {
            fields.rateDao.get(eq("USDA"))
            fields.rateDao.get(eq("USDB"))

            fields.service.searchRate(eq("A,B"), eq("123"))

            fields.rateDao.addAll(any(), any())
        }
    }

    @Test(expected = RuntimeException::class)
    fun `searchRate error no response`() = runBlocking {
        //arrange
        val (repository, fields) = createMocks()
        coEvery { fields.rateDao.get(any()) } returns null
        coEvery { fields.service.searchRate(any(), any()) } returns SearchRateResponse(mapOf())

        //act
        repository.searchRate("A", "B", {}, {})

        //assert
        coVerifySequence {
            fields.rateDao.get(eq("USDA"))
            fields.rateDao.get(eq("USDB"))

            fields.service.searchRate(eq("A,B"), eq("123"))
        }
    }

    @Test(expected = RuntimeException::class)
    fun `searchRate error on call`() = runBlocking {
        //arrange
        val (repository, fields) = createMocks()
        var onErrorCalled = false
        coEvery { fields.rateDao.get(any()) } throws RuntimeException()

        //act
        repository.searchRate("A", "B", {}, {
            onErrorCalled = true
        })

        //assert
        assertTrue(onErrorCalled)
    }

    @Test
    fun `getCurrencyList from cache`() = runBlocking {
        //arrange
        val (repository, fields) = createMocks()
        val response = listOf(Currency("TST", "Test", true))
        coEvery { fields.currencyDao.getAll() } returns response

        //act
        repository.getCurrencyList({ list ->
            //assert
            assertEquals(list.size, 1)
            assertEquals(list[0].symbol, "TST")
        }, {})

        //assert
        coVerifySequence {
            fields.currencyDao.getAll()
        }
    }

    @Test
    fun `getCurrencyList from internet`() = runBlocking {
        //arrange
        val (repository, fields) = createMocks()
        val response = CurrencyListResponse(
            mapOf(Pair("TST", "Test"))
        )
        coEvery { fields.currencyDao.getAll() } returns listOf()
        coEvery { fields.service.getCurrencyList(any()) } returns response

        //act
        repository.getCurrencyList({ list ->
            //assert
            assertEquals(list.size, 1)
            assertEquals(list[0].symbol, "TST")
        }, {})

        //assert
        coVerifySequence {
            fields.currencyDao.getAll()
            fields.currencyDao.addAll(any())
        }
    }

    @Test(expected = RuntimeException::class)
    fun `getCurrencyList error on call`() = runBlocking {
        //arrange
        val (repository, fields) = createMocks()
        var onErrorCalled = false
        coEvery { fields.currencyDao.getAll() } throws RuntimeException()

        //act
        repository.getCurrencyList({}, {
            onErrorCalled = true
        })

        //assert
        assertTrue(onErrorCalled)
    }
}