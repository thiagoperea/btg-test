package com.thiagoperea.btgtest.presentation.conversion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thiagoperea.btgtest.internal.convertToDouble
import com.thiagoperea.btgtest.repository.currency.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConversionViewModel(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _searchRateState = MutableLiveData<ConversionState>()
    val searchRateState: LiveData<ConversionState> = _searchRateState

    private val _conversionValue = MutableLiveData<Double>()
    val conversionValue: LiveData<Double> = _conversionValue

    var rateUsd = 0.0
    var rateDestiny = 0.0

    fun searchRate(
        origin: String?,
        destiny: String?,
        currentValue: String? = null
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            _searchRateState.postValue(ConversionState.Loading)

            currencyRepository.searchRate(
                origin,
                destiny,
                onSuccess = { (rateUsd, rateDestiny) ->
                    this@ConversionViewModel.rateUsd = rateUsd
                    this@ConversionViewModel.rateDestiny = rateDestiny
                    _searchRateState.postValue(ConversionState.Success)
                    if (currentValue != null) {
                        convertValue(currentValue.convertToDouble() / 100)
                    }
                },
                onError = {
                    _searchRateState.postValue(ConversionState.Error)
                }
            )
        }
    }

    fun convertValue(value: Double) {
        if (value == 0.0) {
            _conversionValue.postValue(value)
        }

        val convertedToUsd = value / rateUsd
        val convertedToDestiny = convertedToUsd * rateDestiny

        _conversionValue.postValue(convertedToDestiny)
    }
}