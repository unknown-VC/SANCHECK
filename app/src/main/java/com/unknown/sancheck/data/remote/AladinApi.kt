package com.unknown.sancheck.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface AladinApi {

    @GET("ItemLookUp.aspx")
    suspend fun itemLookUp(
        @Query("ttbkey") ttbKey: String,
        @Query("itemIdType") itemIdType: String = "ISBN13",
        @Query("ItemId") itemId: String,
        @Query("output") output: String = "js",
        @Query("Version") version: String = "20131101",
        @Query("Cover") cover: String = "Big"
    ): AladinSearchResponse

    @GET("ItemSearch.aspx")
    suspend fun itemSearch(
        @Query("ttbkey") ttbKey: String,
        @Query("Query") query: String,
        @Query("QueryType") queryType: String = "Keyword",
        @Query("MaxResults") maxResults: Int = 20,
        @Query("start") start: Int = 1,
        @Query("SearchTarget") searchTarget: String = "Book",
        @Query("output") output: String = "js",
        @Query("Version") version: String = "20131101",
        @Query("Cover") cover: String = "Big"
    ): AladinSearchResponse
}
