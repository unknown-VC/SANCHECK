package com.unknown.sancheck.data.remote

import com.google.gson.annotations.SerializedName

data class AladinSearchResponse(
    @SerializedName("version") val version: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("totalResults") val totalResults: Int = 0,
    @SerializedName("startIndex") val startIndex: Int = 0,
    @SerializedName("itemsPerPage") val itemsPerPage: Int = 0,
    @SerializedName("item") val items: List<AladinItem> = emptyList()
)

data class AladinItem(
    @SerializedName("title") val title: String = "",
    @SerializedName("author") val author: String = "",
    @SerializedName("pubDate") val pubDate: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("isbn13") val isbn13: String = "",
    @SerializedName("isbn") val isbn: String = "",
    @SerializedName("priceStandard") val priceStandard: Int = 0,
    @SerializedName("cover") val cover: String = "",
    @SerializedName("publisher") val publisher: String = "",
    @SerializedName("categoryName") val categoryName: String = "",
    @SerializedName("subInfo") val subInfo: SubInfo? = null
)

data class SubInfo(
    @SerializedName("subTitle") val subTitle: String = "",
    @SerializedName("originalTitle") val originalTitle: String = "",
    @SerializedName("itemPage") val itemPage: Int = 0
)
