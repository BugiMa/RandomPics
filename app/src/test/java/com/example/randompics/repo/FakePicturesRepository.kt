package com.example.randompics.repo

import com.example.randompics.data.remote.Pictures
import retrofit2.Response
import kotlin.random.Random

class FakePicturesRepository: IPicturesRepository {

    override suspend fun getPics(page: Int, limit: Int): Response<Pictures> {
        return Response.success(Pictures())
    }

    fun getPics(page: Int, limit: Int, isSuccessful: Boolean): Response<Pictures> {
        return if (isSuccessful) {
            val min = (page - 1) * limit // page starts from 1
            val max = page * limit
            val pictures = Pictures()
            for (i in min..max) {
                pictures.add(createPicture(i))
            }
            Response.success(pictures)
        } else {
            Response.error(404, null)
        }
    }



    private fun createPicture(i: Int): Pictures.Picture {
        return Pictures.Picture(
            id = "$i",
            author = "author_$i",
            url = "url_$i",
            download_url = "download_url_$i",
            width = Random(i).nextInt(0,9000),
            height = Random(i).nextInt(0,9000)
        )
    }
}