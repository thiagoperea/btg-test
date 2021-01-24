package com.thiagoperea.btgtest.presentation.currencylist

import com.thiagoperea.btgtest.data.model.Currency

sealed class CurrencyListState {

    object Loading : CurrencyListState()

    data class Success(val currencyList: List<Currency>?) : CurrencyListState()

    data class SearchCompleted(val filteredList: List<Currency>?) : CurrencyListState()

    object Error : CurrencyListState()
}