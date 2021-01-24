package com.thiagoperea.btgtest.data.model

import com.google.gson.annotations.SerializedName

data class CurrencyListResponse(
    @SerializedName("currencies") val currencyList: Map<String, String>
)