package com.example.randompics.api

import com.example.randompics.data.remote.Pictures
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LoremPicsumApi {

    @GET ("/v2/list")
    suspend fun getPics(
        @Query("page") page : Int,
        @Query("limit") limit: Int
    ): Response<Pictures>

}