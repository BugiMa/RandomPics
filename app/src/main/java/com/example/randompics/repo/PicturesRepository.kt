package com.example.randompics.repo

import com.example.randompics.api.LoremPicsumApi
import com.example.randompics.data.remote.Pictures
import retrofit2.Response
import javax.inject.Inject

class PicturesRepository @Inject constructor(
    private val api: LoremPicsumApi
): IPicturesRepository {
    override suspend fun getPics(page: Int, limit: Int): Response<Pictures> {

        return api.getPics(page, limit)
    }
}