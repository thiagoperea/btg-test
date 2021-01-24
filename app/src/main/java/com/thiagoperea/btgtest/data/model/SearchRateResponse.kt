package com.thiagoperea.btgtest.data.model

import com.google.gson.annotations.SerializedName

data class SearchRateResponse(
    @SerializedName("quotes") val quotesMap: Map<String, Double>
)