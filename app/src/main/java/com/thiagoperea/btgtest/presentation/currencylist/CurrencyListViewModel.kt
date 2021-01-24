package com.thiagoperea.btgtest.presentation.currencylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thiagoperea.btgtest.data.model.Currency
import com.thiagoperea.btgtest.repository.currency.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrencyListViewModel(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _listState = MutableLiveData<CurrencyListState>()
    val listState: LiveData<CurrencyListState> = _listState

    var fullList: List<Currency>? = null
    var filteredList: List<Currency>? = null

    fun loadCurrencyList() {
        viewModelScope.launch(Dispatchers.Main) {
            _listState.postValue(CurrencyListState.Loading)

            repository.getCurrencyList(
                onSuccess = { currencyList ->
                    fullList = orderByFavorite(currencyList)
                    filteredList = fullList
                    _listState.postValue(CurrencyListState.Success(fullList))
                },
                onError = {
                    _listState.postValue(CurrencyListState.Error)
                }
            )
        }
    }

    fun filterList(query: String) {
        filteredList = fullList?.filter {
            it.fullName.contains(query, true) || it.symbol.contains(query, true)
        }?.toMutableList()

        _listState.postValue(CurrencyListState.SearchCompleted(filteredList))
    }

    fun switchFavorite(currency: Currency) {
        currency.isFavorite = !currency.isFavorite

        viewModelScope.launch(Dispatchers.Main) {
            repository.updateCurrency(currency)

            filteredList = orderByFavorite(fullList)
            _listState.postValue(CurrencyListState.SearchCompleted(filteredList))
        }
    }

    fun orderByFavorite(list: List<Currency>?): List<Currency>? {
        return list?.sortedByDescending { it.isFavorite }
    }

    fun filterListBySymbol() {
        var orderedList = filteredList?.sortedBy { it.symbol }
        orderedList = orderByFavorite(orderedList)
        filteredList = orderedList

        _listState.postValue(CurrencyListState.SearchCompleted(filteredList))
    }

    fun filterListByName() {
        var orderedList = filteredList?.sortedBy { it.fullName }
        orderedList = orderByFavorite(orderedList)
        filteredList = orderedList

        _listState.postValue(CurrencyListState.SearchCompleted(filteredList))
    }
}