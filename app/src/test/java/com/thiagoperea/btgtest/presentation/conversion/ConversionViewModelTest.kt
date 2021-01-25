package com.thiagoperea.btgtest.presentation.conversion

import androidx.lifecycle.Observer
import com.thiagoperea.btgtest.BaseUnitTest
import com.thiagoperea.btgtest.repository.currency.CurrencyRepository
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class ConversionViewModelTest : BaseUnitTest() {

    private fun createMocks(): Pair<ConversionViewModel, CurrencyRepository> {
        val repository = mockk<CurrencyRepository>(relaxed = true)
        val viewModel = ConversionViewModel(repository)
        return Pair(viewModel, repository)
    }

    @Test
    fun `searchRate on success`() {
        //arrange
        val (viewModel, repository) = createMocks()
        val lambdaSlot = slot<(Pair<Double, Double>) -> Unit>()
        val observerMock = mockk<Observer<ConversionState>>(relaxed = true)
        viewModel.searchRateState.observeForever(observerMock)

        //act
        viewModel.searchRate(null, null)

        //assert
        coVerify {
            repository.searchRate(any(), any(), capture(lambdaSlot), any())
        }

        lambdaSlot.captured.invoke(Pair(2.0, 3.0))

        assertEquals(viewModel.rateUsd, 2.0, 1.0)
        assertEquals(viewModel.rateDestiny, 3.0, 1.0)

        coVerifySequence {
            observerMock.onChanged(ofType(ConversionState.Loading::class))
            observerMock.onChanged(ofType(ConversionState.Success::class))
        }
    }

    @Test
    fun `searchRate on error`() {
        //arrange
        val (viewModel, repository) = createMocks()
        val lambdaSlot = slot<() -> Unit>()
        val observerMock = mockk<Observer<ConversionState>>(relaxed = true)
        viewModel.searchRateState.observeForever(observerMock)

        //act
        viewModel.searchRate(null, null)

        //assert
        coVerify {
            repository.searchRate(any(), any(), any(), capture(lambdaSlot))
        }

        lambdaSlot.captured.invoke()

        coVerifySequence {
            observerMock.onChanged(ofType(ConversionState.Loading::class))
            observerMock.onChanged(ofType(ConversionState.Error::class))
        }
    }

    @Test
    fun `converValue with zero`() {
        //arrange
        val (viewModel, _) = createMocks()
        val observerMock = mockk<Observer<Double>>(relaxed = true)
        viewModel.conversionValue.observeForever(observerMock)

        //act
        viewModel.convertValue(0.0)

        //assert
        observerMock.onChanged(0.0)
    }

    @Test
    fun `convertValue with above zero`() {
        //arrange
        val (viewModel, _) = createMocks()
        viewModel.rateUsd = 2.0
        viewModel.rateDestiny = 4.0
        val observerMock = mockk<Observer<Double>>(relaxed = true)
        viewModel.conversionValue.observeForever(observerMock)

        //act
        viewModel.convertValue(2.0)

        //assert
        observerMock.onChanged(4.0)
    }

}