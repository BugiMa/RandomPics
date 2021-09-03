package com.example.randompics.repo

import com.example.randompics.data.remote.Pictures
import retrofit2.Response

interface IPicturesRepository {

    suspend fun getPics(page: Int, limit: Int): Response<Pictures>
}