package com.thiagoperea.btgtest.presentation.currencylist

import androidx.lifecycle.Observer
import com.thiagoperea.btgtest.BaseUnitTest
import com.thiagoperea.btgtest.data.model.Currency
import com.thiagoperea.btgtest.repository.currency.CurrencyRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrencyListViewModelTest : BaseUnitTest() {

    private fun createMocks(): Pair<CurrencyListViewModel, CurrencyRepository> {
        val repository = mockk<CurrencyRepository>(relaxed = true)
        val viewModel = CurrencyListViewModel(repository)
        return Pair(viewModel, repository)
    }

    private fun createResponse(): List<Currency> {
        return listOf(
            Currency("SYM", "Symbol", false),
            Currency("TST", "Test", false),
            Currency("OTH", "Other", true)
        )
    }

    @Test
    fun `loadCurrencyList on success`() {
        //arrange
        val (viewModel, repository) = createMocks()
        val lambdaSlot = slot<(List<Currency>) -> Unit>()
        val observerMock = mockk<Observer<CurrencyListState>>(relaxed = true)
        viewModel.listState.observeForever(observerMock)

        //act
        viewModel.loadCurrencyList()

        //assert
        coVerify {
            repository.getCurrencyList(capture(lambdaSlot), any())
        }

        lambdaSlot.captured.invoke(createResponse())

        assertEquals(viewModel.fullList?.size, 3)
        assertTrue(viewModel.fullList?.get(0)?.isFavorite!!)

        coVerifySequence {
            observerMock.onChanged(ofType(CurrencyListState.Loading::class))
            observerMock.onChanged(ofType(CurrencyListState.Success::class))
        }
    }

    @Test
    fun `loadCurrencyList on error`() {
        //arrange
        val (viewModel, repository) = createMocks()
        val lambdaSlot = slot<() -> Unit>()
        val observerMock = mockk<Observer<CurrencyListState>>(relaxed = true)
        viewModel.listState.observeForever(observerMock)

        //act
        viewModel.loadCurrencyList()

        //assert
        coVerify {
            repository.getCurrencyList(any(), capture(lambdaSlot))
        }

        lambdaSlot.captured.invoke()

        coVerifySequence {
            observerMock.onChanged(ofType(CurrencyListState.Loading::class))
            observerMock.onChanged(ofType(CurrencyListState.Error::class))
        }
    }

    @Test
    fun `filterList should filter`() {
        //arrange
        val (viewModel, _) = createMocks()
        val observerMock = mockk<Observer<CurrencyListState>>(relaxed = true)
        viewModel.listState.observeForever(observerMock)

        //act
        viewModel.fullList = createResponse()
        viewModel.filterList("Sy")

        //assert
        assertEquals(viewModel.filteredList?.size, 1)
        assertTrue(viewModel.filteredList?.get(0)?.symbol == "SYM")

        verify { observerMock.onChanged(ofType(CurrencyListState.SearchCompleted::class)) }
    }

    @Test
    fun `switchFavorite call state`() {
        //arrange
        val (viewModel, repository) = createMocks()
        val currency = Currency("TST", "Test", false)
        val slot = slot<Currency>()
        val observerMock = mockk<Observer<CurrencyListState>>(relaxed = true)
        viewModel.listState.observeForever(observerMock)

        //act
        viewModel.switchFavorite(currency)

        //assert
        coVerify {
            repository.updateCurrency(capture(slot))
        }

        assertTrue(slot.captured.isFavorite)

        coVerify {
            observerMock.onChanged(ofType(CurrencyListState.SearchCompleted::class))
        }
    }

    @Test
    fun `orderByFavorite should order`() {
        //arrange
        val (viewModel, _) = createMocks()
        var listToOrder: List<Currency>? = createResponse()

        //act
        listToOrder = viewModel.orderByFavorite(listToOrder)

        //assert
        assertEquals(listToOrder?.get(0)?.isFavorite, true)
        assertEquals(listToOrder?.size, 3)
    }

    @Test
    fun `filterListBySymbol should call state`() {
        //arrange
        val (viewModel, _) = createMocks()
        val observerMock = mockk<Observer<CurrencyListState>>(relaxed = true)
        viewModel.listState.observeForever(observerMock)

        //act
        viewModel.filteredList = createResponse()
        viewModel.filterListBySymbol()

        //assert
        assertEquals(viewModel.filteredList?.size, 3)
        assertEquals(viewModel.filteredList?.get(0)?.symbol, "OTH")

        coVerify {
            observerMock.onChanged(ofType(CurrencyListState.SearchCompleted::class))
        }
    }

    @Test
    fun `filterListByName should call state`() {
        //arrange
        val (viewModel, _) = createMocks()
        val observerMock = mockk<Observer<CurrencyListState>>(relaxed = true)
        viewModel.listState.observeForever(observerMock)

        //act
        viewModel.filteredList = createResponse()
        viewModel.filterListByName()

        //assert
        assertEquals(viewModel.filteredList?.size, 3)
        assertEquals(viewModel.filteredList?.get(0)?.fullName, "Other")

        coVerify {
            observerMock.onChanged(ofType(CurrencyListState.SearchCompleted::class))
        }
    }
}