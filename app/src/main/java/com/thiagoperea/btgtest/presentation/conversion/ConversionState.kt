package com.thiagoperea.btgtest.presentation.conversion

sealed class ConversionState {

    object Loading : ConversionState()

    object Success : ConversionState()

    object Error : ConversionState()
}